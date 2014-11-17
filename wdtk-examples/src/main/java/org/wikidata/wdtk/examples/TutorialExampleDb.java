package org.wikidata.wdtk.examples;

/*
 * #%L
 * Wikidata Toolkit Examples
 * %%
 * Copyright (C) 2014 Wikidata Toolkit Developers
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

import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Value;
import org.wikidata.wdtk.storage.wdtkbindings.WdtkDatabaseManager;
import org.wikidata.wdtk.util.Timer;

/**
 * This example application applies an {@link EntityDocumentProcessor} to all
 * documents in a Wikidata dump file. By default, the EntityDocumentProcessor is
 * {@link TutorialDocumentProcessor}.
 * <p>
 * In order to use this application, you must have Wikidata dumps in binary
 * format. By default, the application will use the files
 * <tt>wdtkDatabaseFull-20140804.mapdb</tt> and
 * <tt>wdtkDatabaseFull-20140804.mapdb.p</tt> in the current working directory.
 * The file name can be changed in the source code. If you don't have a binary
 * Wikidata dump in this format, you may have to use the {@link TutorialExample}
 * instead.
 * <p>
 * The application provides several methods to iterate over all or some of the
 * data efficiently. By default, it will simply iterate over all entity
 * documents in the dump.
 *
 * @author Markus Kroetzsch
 *
 */
public class TutorialExampleDb {

	/**
	 * If this is set to a value other than 0, then any iteration will stop
	 * after that many seconds. This should be used for testing instead of just
	 * terminating the program, since it ensures that the database is closed
	 * properly.
	 */
	static long testTimeOut = 5;

	/**
	 * The name of your database file. Change as appropriate.
	 */
	final static String dbName = "wdtkDatabase-20140804";
	final static WdtkDatabaseManager wdtkDatabaseManager = new WdtkDatabaseManager(
			dbName);

	/**
	 * A simple timer that we use to measure progress.
	 */
	final static Timer timer = Timer.getNamedTimer("tutorial-example");

	/**
	 * A counter that we use during processing to print some status messages.
	 */
	static int count;

	/**
	 * The document processor that is used to handle documents.
	 */
	static TutorialDocumentProcessor documentProcessor;

	public static void main(String[] args) {
		documentProcessor = new TutorialDocumentProcessor();

		// Process all entity documents:
		processEntityDocuments(null, null);
		// ==Other options==
		// Process all entity documents that have a value for P570:
		// PropertyIdValue searchProperty = Datamodel
		// .makeWikidataPropertyIdValue("P570"); // P570 = date of death
		// processEntityDocuments(searchProperty, null);
		// Process all properties:
		// processPropertyDocuments();

		// Store final results:
		documentProcessor.storeResults();

		// Always close the DB. This is really important, even if we don't write
		// to the DB.
		wdtkDatabaseManager.close();
	}

	/**
	 * Processes all property documents.
	 */
	protected static void processPropertyDocuments() {
		count = 0;
		timer.reset();

		timer.start();
		for (EntityDocument ed : wdtkDatabaseManager.propertyDocuments()) {
			if (!processEntityDocument(ed)) {
				break;
			}
		}
		timer.stop();

		printProcessingStatus();
	}

	/**
	 * Processes all entity documents that have a statement with the given
	 * property and value. Both can be set to null if no such filtering is
	 * desired.
	 *
	 * @param searchProperty
	 *            the property to look for, or null to process all entities
	 * @param searchValue
	 *            the value to look for, or null to process entities with
	 *            arbitrary values for the given property
	 */
	protected static void processEntityDocuments(
			PropertyIdValue searchProperty, Value searchValue) {
		count = 0;
		timer.reset();

		timer.start();
		for (EntityDocument ed : wdtkDatabaseManager.findEntityDocuments(
				searchProperty, searchValue)) {
			if (!processEntityDocument(ed)) {
				break;
			}
		}
		timer.stop();

		printProcessingStatus();
	}

	/**
	 * Processes a single entity document and takes care of printing some
	 * statistics from time to time.
	 *
	 * @param entityDocument
	 *            the entity document to process
	 */
	protected static boolean processEntityDocument(EntityDocument entityDocument) {
		if (entityDocument instanceof ItemDocument) {
			documentProcessor
					.processItemDocument((ItemDocument) entityDocument);
			count++;
		} else if (entityDocument instanceof PropertyDocument) {
			documentProcessor
					.processPropertyDocument((PropertyDocument) entityDocument);
			count++;
		}

		if (count % 10000 == 0) {
			printProcessingStatus();
		}

		if (testTimeOut > 0) {
			timer.stop();
			if (timer.getTotalWallTime() > testTimeOut * 1000000000) {
				System.out.println("*** Aborting iteration after "
						+ testTimeOut + " seconds (test mode)");
				System.out
						.println("*** Set testTimeOut to 0 to deactivate timeout.");
				return false;
			}
			timer.start();
		}

		return true;
	}

	/**
	 * Prints the number of items processed so far.
	 */
	protected static void printProcessingStatus() {
		boolean wasRunning;
		if (timer.isRunning()) {
			timer.stop();
			wasRunning = true;
		} else {
			wasRunning = false;
		}

		System.out.println("Processed " + count + " entities in "
				+ timer.getTotalWallTime() / 1000000000 + "s");

		if (wasRunning) {
			timer.start();
		}
	}

}
