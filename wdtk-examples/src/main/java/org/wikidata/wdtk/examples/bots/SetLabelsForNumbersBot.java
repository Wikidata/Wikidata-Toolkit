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
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.ItemDocumentBuilder;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;
import org.wikidata.wdtk.examples.ExampleHelpers;
import org.wikidata.wdtk.util.WebResourceFetcherImpl;
import org.wikidata.wdtk.wikibaseapi.ApiConnection;
import org.wikidata.wdtk.wikibaseapi.LoginFailedException;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataEditor;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

/**
 * This bot adds a default label to Wikidata items that are about numbers, by
 * simply creating a string for any integer number. Decimal numbers that are not
 * integers are not touched since they usually should have more meaningful
 * labels than the numerical representation. Moreover, the bot checks the class
 * (P31 value) of each item to make sure that only items about numbers are
 * re-labelled.
 * <p>
 * The activity of the bot is logged in the file
 * bot-log-setnumlabels-TIMESTAMP.txt. Note that this log contains all edits
 * that would have been made, even if editing was disabled. Errors are logged to
 * the console only.
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
 * The bot is tried and tested, and has been used on Wikidata to perform its
 * task on over 10,000 items (see <a
 * href="https://www.wikidata.org/wiki/User:Makrobot">User:Makrobot</a>).
 *
 * @author Markus Kroetzsch
 *
 */
public class SetLabelsForNumbersBot implements EntityDocumentProcessor {

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

	/**
	 * List of language codes for those languages where it is meaningful to
	 * label a number with an Arabic numeral string. There are more than those,
	 * of course.
	 */
	final static String[] arabicNumeralLanguages = { "en", "de", "fr", "pt",
			"it", "es", "nl", "da", "ru" };

	/**
	 * Set of Wikidata items that are commonly used to classify numbers that we
	 * would like to edit with this bot. This is relevant since some things that
	 * have numerical values are not intentionally numbers, and hence should not
	 * have a label that is a numerical representation. An example of such a
	 * case is https://www.wikidata.org/wiki/Q2415057 which would not be
	 * correctly labelled as "20".
	 */
	final static Set<ItemIdValue> numberClasses = new HashSet<>();
	static {
		numberClasses.add(Datamodel.makeWikidataItemIdValue("Q12503")); // integer
		numberClasses.add(Datamodel.makeWikidataItemIdValue("Q13366104")); // even
																			// number
		numberClasses.add(Datamodel.makeWikidataItemIdValue("Q13366129")); // odd
																			// number
		numberClasses.add(Datamodel.makeWikidataItemIdValue("Q21199")); // natural
																		// number
	}

	/**
	 * Main method to run the bot.
	 *
	 * @param args
	 * @throws LoginFailedException
	 * @throws IOException
	 * @throws MediaWikiApiErrorException
	 */
	public static void main(String[] args) throws LoginFailedException, IOException {
		ExampleHelpers.configureLogging();
		printDocumentation();

		SetLabelsForNumbersBot bot = new SetLabelsForNumbersBot();
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
		System.out.println("*** Wikidata Toolkit: SetLabelsForNumbersBot");
		System.out.println("*** ");
		System.out
				.println("*** This bot downloads recent Wikidata dumps to locate items about");
		System.out
				.println("*** integer numbers, and it adds default labels for these items in ");
		System.out
				.println("*** several languages, if there is no label for a language yet.");
		System.out
				.println("********************************************************************");
	}

	/**
	 * Constructor.
	 *
	 * @throws LoginFailedException
	 * @throws IOException
	 */
	public SetLabelsForNumbersBot() throws LoginFailedException, IOException {
		WebResourceFetcherImpl
				.setUserAgent("makrobot 0.3.0; Wikidata Toolkit; Java");

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
				ExampleHelpers
						.openExampleFileOuputStream("bot-log-setnumlabels-"
								+ timeStamp + ".txt"));
	}

	@Override
	public void processItemDocument(ItemDocument itemDocument) {
		if (itemDocument.hasStatement("P1181")) {
			if (lacksSomeLanguage(itemDocument)) {
				addLabelForNumbers(itemDocument.getEntityId());
			} else {
				System.out.println("*** Labels already complete for "
						+ itemDocument.getEntityId().getId());
			}
		} // else: ignore items that have no numeric value
	}

	/**
	 * Finishes processing and makes sure that the log file is written.
	 */
	public void finish() {
		this.logfile.close();
	}

	/**
	 * Fetches the current online data for the given item, and adds numerical
	 * labels if necessary.
	 *
	 * @param itemIdValue
	 *            the id of the document to inspect
	 */
	protected void addLabelForNumbers(ItemIdValue itemIdValue) {

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

			// Check if we still have exactly one numeric value:
			QuantityValue number = currentItemDocument
					.findStatementQuantityValue("P1181");
			if (number == null) {
				System.out.println("*** No unique numeric value for " + qid);
				return;
			}

			// Check if the item is in a known numeric class:
			if (!currentItemDocument.hasStatementValue("P31", numberClasses)) {
				System.out
						.println("*** "
								+ qid
								+ " is not in a known class of integer numbers. Skipping.");
				return;
			}

			// Check if the value is integer and build label string:
			String numberString;
			try {
				BigInteger intValue = number.getNumericValue()
						.toBigIntegerExact();
				numberString = intValue.toString();
			} catch (ArithmeticException e) {
				System.out.println("*** Numeric value for " + qid
						+ " is not an integer: " + number.getNumericValue());
				return;
			}

			// Construct data to write:
			ItemDocumentBuilder itemDocumentBuilder = ItemDocumentBuilder
					.forItemId(itemIdValue).withRevisionId(
							currentItemDocument.getRevisionId());
			ArrayList<String> languages = new ArrayList<>(
					arabicNumeralLanguages.length);
			for (String arabicNumeralLanguage : arabicNumeralLanguages) {
				if (!currentItemDocument.getLabels().containsKey(
						arabicNumeralLanguage)) {
					itemDocumentBuilder.withLabel(numberString,
							arabicNumeralLanguage);
					languages.add(arabicNumeralLanguage);
				}
			}

			if (languages.size() == 0) {
				System.out.println("*** Labels already complete for " + qid);
				return;
			}

			logEntityModification(currentItemDocument.getEntityId(),
					numberString, languages);

			dataEditor.editItemDocument(itemDocumentBuilder.build(), false,
					"Set labels to numeric value (Task MB1)", null);
		} catch (MediaWikiApiErrorException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns true if the given item document lacks a label for at least one of
	 * the languages covered.
	 *
	 * @param itemDocument
	 * @return true if some label is missing
	 */
	protected boolean lacksSomeLanguage(ItemDocument itemDocument) {
		for (String arabicNumeralLanguage : arabicNumeralLanguages) {
			if (!itemDocument.getLabels()
					.containsKey(arabicNumeralLanguage)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Logs information about entities changed so far.
	 *
	 * @param entityId
	 *            the id of the modified item
	 * @param numberLabel
	 *            the label written
	 * @param languages
	 *            the list of languages for which the label was set
	 */
	protected void logEntityModification(EntityIdValue entityId,
			String numberLabel, ArrayList<String> languages) {
		modifiedEntities++;

		System.out.println(entityId.getId() + ": adding label " + numberLabel
				+ " for languages " + languages.toString() + " ("
				+ modifiedEntities + " entities modified so far)");

		this.logfile.println(entityId.getId() + "," + numberLabel + ",\""
				+ languages.toString() + "\"");

		if (modifiedEntities % 10 == 0) {
			this.logfile.flush();
		}
	}
}
