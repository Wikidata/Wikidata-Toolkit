package org.wikidata.wdtk.examples;

/*
 * #%L
 * Wikidata Toolkit Examples
 * %%
 * Copyright (C) 2014 - 2016 Wikidata Toolkit Developers
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

import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.datamodel.interfaces.Value;

/**
 * This simple {@link EntityDocumentProcessor} finds all items with a GND
 * identifier (property P227) who are also humans (P31 with value Q5), and
 * extracts for each of them the id, GND value, as well as English and German
 * labels and Wikipedia articles, if any. The results are written to a CSV file
 * "extracted-data.csv". The extracted property can be modified by changing the
 * value for {@link DataExtractionProcessor#extractPropertyId}. The current code
 * only extracts the first value for this property if many are given. The filter
 * condition (P31::Q5) can also be changed in the code.
 *
 * @author Markus Kroetzsch
 *
 */
public class DataExtractionProcessor implements EntityDocumentProcessor {

	static final String extractPropertyId = "P227"; // "GND identifier"
	static final String filterPropertyId = "P31"; // "instance of"
	static final Value filterValue = Datamodel.makeWikidataItemIdValue("Q5"); // "human"

	int itemsWithPropertyCount = 0;
	int itemCount = 0;
	PrintStream out;

	/**
	 * Main method. Processes the whole dump using this processor. To change
	 * which dump file to use and whether to run in offline mode, modify the
	 * settings in {@link ExampleHelpers}.
	 *
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		ExampleHelpers.configureLogging();
		DataExtractionProcessor.printDocumentation();

		DataExtractionProcessor processor = new DataExtractionProcessor();
		ExampleHelpers.processEntitiesFromWikidataDump(processor);
		processor.close();
	}

	public DataExtractionProcessor() throws IOException {
		// open file for writing results:
		out = new PrintStream(
				ExampleHelpers.openExampleFileOuputStream("extracted-data.csv"));
		// write CSV header:
		out.println("ID,Label (en),Label (de),Value,Wikipedia (en),Wikipedia (de)");
	}

	@Override
	public void processItemDocument(ItemDocument itemDocument) {
		this.itemCount++;

		// Check if the item matches our filter conditions:
		if (!itemDocument.hasStatementValue(filterPropertyId, filterValue)) {
			return;
		}

		// Find the first value for this property, if any:
		StringValue stringValue = itemDocument
				.findStatementStringValue(extractPropertyId);

		// If a value was found, write the data:
		if (stringValue != null) {
			this.itemsWithPropertyCount++;
			out.print(itemDocument.getEntityId().getId());
			out.print(",");
			out.print(csvEscape(itemDocument.findLabel("en")));
			out.print(",");
			out.print(csvEscape(itemDocument.findLabel("de")));
			out.print(",");
			out.print(csvEscape(stringValue.getString()));
			out.print(",");
			SiteLink enwiki = itemDocument.getSiteLinks().get("enwiki");
			if (enwiki != null) {
				out.print(csvEscape(enwiki.getPageTitle()));
			} else {
				out.print("\"\"");
			}
			out.print(",");
			SiteLink dewiki = itemDocument.getSiteLinks().get("dewiki");
			if (dewiki != null) {
				out.print(csvEscape(dewiki.getPageTitle()));
			} else {
				out.print("\"\"");
			}
			out.println();
		}

		// Print progress every 100,000 items:
		if (this.itemCount % 100000 == 0) {
			printStatus();
		}
	}

	/**
	 * Escapes a string for use in CSV. In particular, the string is quoted and
	 * quotation marks are escaped.
	 *
	 * @param string
	 *            the string to escape
	 * @return the escaped string
	 */
	private String csvEscape(String string) {
		if (string == null) {
			return "\"\"";
		} else {
			return "\"" + string.replace("\"", "\"\"") + "\"";
		}
	}

	/**
	 * Prints the current status, time and entity count.
	 */
	public void printStatus() {
		System.out.println("Found " + this.itemsWithPropertyCount
				+ " matching items after scanning " + this.itemCount
				+ " items.");
	}

	/**
	 * Prints some basic documentation about this program.
	 */
	public static void printDocumentation() {
		System.out
				.println("********************************************************************");
		System.out.println("*** Wikidata Toolkit: DataExtractionProcessor");
		System.out.println("*** ");
		System.out
				.println("*** This program will download and process dumps from Wikidata.");
		System.out
				.println("*** It will scan the dump to find items with values for property");
		System.out.println("*** " + extractPropertyId
				+ " and print some data for these items to a CSV file. ");
		System.out.println("*** See source code for further details.");
		System.out
				.println("********************************************************************");
	}

	public void close() {
		printStatus();
		this.out.close();
	}
}
