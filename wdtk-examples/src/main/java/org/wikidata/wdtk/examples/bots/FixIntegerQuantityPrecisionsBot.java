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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * <li>Update statements while preserving most of their content</li>
 * <li>Use basic bot configuration features (login, disable editing for test,
 * limited numbers of test edits)</li>
 * </ul>
 * The bot is tried and tested, and has been used on Wikidata to perform its
 * task on several 1,000 items (see <a
 * href="https://www.wikidata.org/wiki/User:Makrobot">User:Makrobot</a>).
 *
 * @author Markus Kroetzsch
 *
 */
public class FixIntegerQuantityPrecisionsBot implements EntityDocumentProcessor {

	/**
	 * List of all integer properties considered by this bot.
	 */
	final static String[] integerProperties = { "P1082", // population
			"P1083", // capacity (seats etc.)
			"P1092", // total produced (product)
			"P1098", // number of speakers
			"P1099", // number of masts
			"P1100", // number of cylinders
			"P1101", // floors above ground
			"P1103", // number of platforms
			"P1104", // number of pages
			"P1110", // attendance (people attending an event)
			"P1111", // votes received
			"P1113", // series length
			"P1114", // quantity (how many?)
			"P1120", // number of deaths
			"P1128", // employees
			"P1129", // national team caps
			"P1132", // number of participants
			"P1139", // floors below ground
			"P1141", // number of processor cores
			"P1164", // cardinality of the group (in mathematics):
			"P1174", // visitors per year
			"P1301", // number of elevators
			"P1314", // number of spans (bridge)
			"P1339", // number of injured
			"P1342", // number of seats
			"P1345", // number of victims
			"P1350", // number of matches played
			"P1355", // wins (of sports matches)
			"P1356", // losses (of sports matches)
			"P1357", // matches/games drawn/tied
			"P1359", // number of points/goals conceded
			"P1373", // daily ridership
			"P1410", // number of seats of the organization in legislature
			"P1418", // number of orbits completed
			"P1436", // collection or exhibition size
			"P1446", // number of missing
			"P1538", // number of households
			"P1539", // female population
			"P1540", // male population
			"P1548", // maximum Strahler number (of rivers etc.)
			"P1561", // number of survivors
			"P1569", // number of edges
			"P1570", // number of vertices
			"P1590", // number of casualties
			"P1603", // number of cases (in medical outbreaks)
			"P1641", // port (-number; in computing)
			"P1658", // number of faces (of a mathematical solid)
			"P1831", // electorate (number of registered voters)
			"P1833", // number of registered users/contributors
			"P1867", // eligible voters
			"P1868", // ballots cast
			"P1872", // minimum number of players
			"P1873", // maximum number of players
			"P1971", // number of children
			"P2021", // Erdős number
			"P2103", // size of team at start
			"P2105", // size of team at finish
			"P2124", // membership
			"P2196", // students count
	};

	final ApiConnection connection;
	final WikibaseDataEditor dataEditor;
	final WikibaseDataFetcher dataFetcher;

	/**
	 * Number of entities modified so far.
	 */
	int modifiedEntities = 0;
	/**
	 * Number of statements modified so far.
	 */
	int modifiedStatements = 0;
	/**
	 * Number of statements modified so far, per property.
	 */
	Map<String, Integer> modifiedStatementsByProperty = new HashMap<>();

	/**
	 * The place to write logging information to.
	 */
	final PrintStream logfile;

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
				.println("*** that use quantity values for integer-valued properties, such as");
		System.out
				.println("*** popluation, and checks if they have a precision of +/-1. In this");
		System.out
				.println("*** case, it fixes their precision to be exact (+/-0).");
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
		// Do not retrieve data that we don't care about here:
		dataFetcher.getFilter().excludeAllLanguages();
		dataFetcher.getFilter().excludeAllSiteLinks();

		// Initialise array to count
		for (String propertyId : integerProperties) {
			this.modifiedStatementsByProperty.put(propertyId, 0);
		}

		String timeStamp = new SimpleDateFormat("yyyyMMdd'T'HHmmss")
				.format(new Date());
		this.logfile = new PrintStream(
				ExampleHelpers.openExampleFileOuputStream("bot-log-fixintprec-"
						+ timeStamp + ".txt"));
	}

	@Override
	public void processItemDocument(ItemDocument itemDocument) {
		for (String propertyId : integerProperties) {
			if (hasPlusMinusOneValues(itemDocument
					.findStatementGroup(propertyId))) {
				fixIntegerPrecisions(itemDocument.getEntityId(), propertyId);
			} // else: ignore items that have no value or only correct values
				// for the property we consider
		}
	}

	/**
	 * Finishes processing and makes sure that the log file is written.
	 */
	public void finish() {
		this.logfile.close();
		System.out.println("### " + modifiedStatements
				+ " statements modified: "
				+ modifiedStatementsByProperty.toString());
	}

	/**
	 * Fetches the current online data for the given item, and fixes the
	 * precision of integer quantities if necessary.
	 *
	 * @param itemIdValue
	 *            the id of the document to inspect
	 * @param propertyId
	 *            id of the property to consider
	 */
	protected void fixIntegerPrecisions(ItemIdValue itemIdValue,
			String propertyId) {

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
					.findStatementGroup(propertyId);
			if (editPropertyStatements == null) {
				System.out.println("*** " + qid
						+ " no longer has any statements for " + propertyId);
				return;
			}

			PropertyIdValue property = Datamodel
					.makeWikidataPropertyIdValue(propertyId);
			List<Statement> updateStatements = new ArrayList<>();
			for (Statement s : editPropertyStatements) {
				QuantityValue qv = (QuantityValue) s.getValue();
				if (qv != null && isPlusMinusOneValue(qv)) {
					QuantityValue exactValue = Datamodel.makeQuantityValue(
							qv.getNumericValue(), qv.getNumericValue(),
							qv.getNumericValue());
					Statement exactStatement = StatementBuilder
							.forSubjectAndProperty(itemIdValue, property)
							.withValue(exactValue).withId(s.getStatementId())
							.withQualifiers(s.getQualifiers())
							.withReferences(s.getReferences())
							.withRank(s.getRank()).build();
					updateStatements.add(exactStatement);
				}
			}

			if (updateStatements.size() == 0) {
				System.out.println("*** " + qid + " quantity values for "
						+ propertyId + " already fixed");
				return;
			}

			logEntityModification(currentItemDocument.getEntityId(),
					updateStatements, propertyId);

			dataEditor.updateStatements(currentItemDocument, updateStatements,
					Collections.<Statement> emptyList(),
					"Set exact values for [[Property:" + propertyId + "|"
							+ propertyId + "]] integer quantities (Task MB2)");

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
	 * @param propertyId
	 */
	protected void logEntityModification(EntityIdValue entityId,
			List<Statement> updateStatements, String propertyId) {
		modifiedEntities++;
		modifiedStatements += updateStatements.size();
		modifiedStatementsByProperty.put(
				propertyId,
				modifiedStatementsByProperty.get(propertyId)
						+ updateStatements.size());

		System.out.println(entityId.getId() + ": fixing "
				+ updateStatements.size() + " statement(s) for " + propertyId
				+ " (" + modifiedEntities + " entities modified so far)");

		this.logfile.println("\n==" + entityId.getId() + "==\n"
				+ updateStatements.toString());

		if (modifiedEntities % 10 == 0) {
			this.logfile.flush();
			System.out.println("### " + modifiedStatements
					+ " statements modified so far: "
					+ modifiedStatementsByProperty.toString());
		}
	}

	/**
	 * Checks if the given value is a number with precision +/-1.
	 *
	 * @param quantityValue
	 * @return
	 */
	protected boolean isPlusMinusOneValue(QuantityValue quantityValue) {
		BigDecimal valueSucc = quantityValue.getNumericValue().add(
				BigDecimal.ONE);
		BigDecimal valuePrec = quantityValue.getNumericValue().subtract(
				BigDecimal.ONE);
		return (quantityValue.getLowerBound().equals(valuePrec)
				&& quantityValue.getUpperBound().equals(valueSucc) && "1"
					.equals(quantityValue.getUnit()));
	}

	/**
	 * Checks if the given statement group contains at least one value of
	 * precision +/-1.
	 *
	 * @param statementGroup
	 * @return
	 */
	protected boolean hasPlusMinusOneValues(StatementGroup statementGroup) {
		if (statementGroup == null) {
			return false;
		}
		for (Statement s : statementGroup) {
			QuantityValue qv = (QuantityValue) s.getValue();
			if (qv != null && isPlusMinusOneValue(qv)) {
				return true;
			}
		}
		return false;
	}
}
