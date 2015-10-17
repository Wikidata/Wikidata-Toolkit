package org.wikidata.wdtk.examples.bots;

/*
 * #%L
 * Wikidata Toolkit Examples
 * %%
 * Copyright (C) 2014 - 2015 Wikidata Toolkit Developers
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.StatementBuilder;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;
import org.wikidata.wdtk.examples.ExampleHelpers;
import org.wikidata.wdtk.util.WebResourceFetcherImpl;
import org.wikidata.wdtk.wikibaseapi.ApiConnection;
import org.wikidata.wdtk.wikibaseapi.LoginFailedException;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataEditor;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

/**
 * This bot adds changes quantity values of properties that are required to use
 * integers (such as population numbers) to be exact if they are now set to
 * +/-1. The latter is the default when editing through the API but not useful
 * there.
 * <p>
 * The activity of the bot is logged in the file
 * bot-log-fixintprec-TIMESTAMP.txt. Note that this log contains all edits that
 * would have been made, even if editing was disabled. Errors are logged to the
 * console only.
 * <p>
 * By default, this program has the actual editing disabled (see source code),
 * so as to avoid accidental modifications. The output will still mention
 * changes that would be done. If you want to do real edits, please respect the
 * bot etiquette and community guidelines.
 * <p>
 * The function of the bot is very basic, but it illustrates some important
 * techniques:
 * <ul>
 * <li>Scan a recent dump for items worth changing</li>
 * <li>Check the online version of each item before really changing it, and use
 * the online revision id to prevent edit conflicts</li>
 * <li>Create data objects for writing</li>
 * <li>Use basic bot configuration features (login, disable editing for test,
 * limited numbers of test edits)</li>
 * </ul>
 *
 * @author Markus Kroetzsch
 *
 */
public class FixIntegerQuantityPrecisionsBot implements EntityDocumentProcessor {

	final ApiConnection connection;
	final WikibaseDataEditor dataEditor;
	final WikibaseDataFetcher dataFetcher;

	/**
	 * Number of entities modified so far.
	 */
	int modifiedEntities = 0;

	/**
	 * The place to write logging information to.
	 */
	final PrintStream logfile;

	final static String editPropertyId = "P1082";
	final static PropertyIdValue editProperty = Datamodel
			.makeWikidataPropertyIdValue(editPropertyId);

	/**
	 * Main method to run the bot.
	 *
	 * @param args
	 * @throws LoginFailedException
	 * @throws IOException
	 * @throws MediaWikiApiErrorException
	 */
	public static void main(String[] args) throws LoginFailedException,
			IOException, MediaWikiApiErrorException {
		ExampleHelpers.configureLogging();
		printDocumentation();

		FixIntegerQuantityPrecisionsBot bot = new FixIntegerQuantityPrecisionsBot();
		ExampleHelpers.processEntitiesFromWikidataDump(bot);
		bot.finish();

		System.out.println("*** Done.");
	}

	/**
	 * Prints some basic documentation about this program.
	 */
	public static void printDocumentation() {
		System.out
				.println("********************************************************************");
		System.out.println("*** Wikidata Toolkit: FixIntegerQuantitiesBot");
		System.out.println("*** ");
		System.out
				.println("*** This bot downloads recent Wikidata dumps to locate items about");
		System.out
				.println("*** that use quantity values for property P1082 (population) and ");
		System.out
				.println("*** checks if they have a precision of +/-1. In this case, it fixes");
		System.out.println("*** their precision to be exact (+/-0).");
		System.out
				.println("********************************************************************");
	}

	/**
	 * Constructor.
	 *
	 * @throws LoginFailedException
	 * @throws IOException
	 */
	public FixIntegerQuantityPrecisionsBot() throws LoginFailedException,
			IOException {
		WebResourceFetcherImpl
				.setUserAgent("makrobot 0.4.0; Wikidata Toolkit; Java");

		connection = ApiConnection.getWikidataApiConnection();
		if (BotSettings.USERNAME != null) {
			connection.login(BotSettings.USERNAME, BotSettings.PASSWORD);
		}
		dataEditor = new WikibaseDataEditor(connection, Datamodel.SITE_WIKIDATA);
		dataEditor.setEditAsBot(BotSettings.EDIT_AS_BOT);
		dataEditor.disableEditing(); // do no actual edits
		// dataEditor.setRemainingEdits(5); // do at most 5 (test) edits

		dataFetcher = new WikibaseDataFetcher(connection,
				Datamodel.SITE_WIKIDATA);

		String timeStamp = new SimpleDateFormat("yyyyMMdd'T'HHmmss")
				.format(new Date());
		this.logfile = new PrintStream(
				ExampleHelpers.openExampleFileOuputStream("bot-log-fixintprec-"
						+ timeStamp + ".txt"));
	}

	@Override
	public void processItemDocument(ItemDocument itemDocument) {
		if (itemDocument.hasStatement(editPropertyId)) {
			fixIntegerPrecisions(itemDocument.getItemId());
		} // else: ignore items that have no value for the property we consider
	}

	@Override
	public void processPropertyDocument(PropertyDocument propertyDocument) {
		// ignore properties
	}

	/**
	 * Finishes processing and makes sure that the log file is written.
	 */
	public void finish() {
		this.logfile.close();
	}

	/**
	 * Fetches the current online data for the given item, and fixes the
	 * precision of integer quantities if necessary.
	 *
	 * @param itemIdValue
	 *            the id of the document to inspect
	 */
	protected void fixIntegerPrecisions(ItemIdValue itemIdValue) {

		String qid = itemIdValue.getId();

		try {
			// Fetch the online version of the item to make sure we edit the
			// current version:
			ItemDocument currentItemDocument = (ItemDocument) dataFetcher
					.getEntityDocument(qid);
			if (currentItemDocument == null) {
				System.out.println("*** " + qid
						+ " could not be fetched. Maybe it has been deleted.");
				return;
			}

			// Get the current statements for the property we want to fix:
			StatementGroup editPropertyStatements = currentItemDocument
					.findStatementGroup(editPropertyId);
			if (editPropertyStatements == null) {
				System.out
						.println("*** " + qid
								+ " no longer has any statements for "
								+ editPropertyId);
				return;
			}

			List<Statement> updateStatements = new ArrayList<>();
			for (Statement s : editPropertyStatements.getStatements()) {
				if (s.getClaim().getMainSnak() instanceof ValueSnak) {
					QuantityValue qv = (QuantityValue) ((ValueSnak) s
							.getClaim().getMainSnak()).getValue();
					BigDecimal valueSucc = qv.getNumericValue().add(
							BigDecimal.ONE);
					BigDecimal valuePrec = qv.getNumericValue().subtract(
							BigDecimal.ONE);
					if (qv.getLowerBound().equals(valuePrec)
							&& qv.getUpperBound().equals(valueSucc)
							&& qv.getUnit().equals("")) {
						QuantityValue exactValue = Datamodel.makeQuantityValue(
								qv.getNumericValue(), qv.getNumericValue(),
								qv.getNumericValue(), "");
						Statement exactStatement = StatementBuilder
								.forSubjectAndProperty(itemIdValue,
										editProperty).withValue(exactValue)
								.withId(s.getStatementId())
								.withQualifiers(s.getClaim().getQualifiers())
								.withReferences(s.getReferences())
								.withRank(s.getRank()).build();
						updateStatements.add(exactStatement);
					}
				}
			}

			if (updateStatements.size() == 0) {
				System.out.println("*** Quantity values already fixed for "
						+ qid);
				return;
			}

			logEntityModification(currentItemDocument.getItemId(),
					updateStatements);

			dataEditor.updateStatements(currentItemDocument, updateStatements,
					Collections.<Statement> emptyList(),
					"Set exact values for integer quantities (Task MB2)");

		} catch (MediaWikiApiErrorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Logs information about entities changed so far.
	 *
	 * @param entityId
	 *            the id of the modified item
	 * @param updateStatements
	 */
	protected void logEntityModification(EntityIdValue entityId,
			List<Statement> updateStatements) {
		modifiedEntities++;

		System.out.println(entityId.getId() + ": fixing "
				+ updateStatements.size() + " statements (" + modifiedEntities
				+ " entities modified so far)");

		this.logfile.println("\n==" + entityId.getId() + "==\n"
				+ updateStatements.toString());

		if (modifiedEntities % 10 == 0) {
			this.logfile.flush();
		}
	}
}
