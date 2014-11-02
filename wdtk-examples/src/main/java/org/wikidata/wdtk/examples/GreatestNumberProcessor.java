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

import java.io.IOException;
import java.math.BigDecimal;

import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.Value;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

/**
 * This simple {@link EntityDocumentProcessor} finds the greatest number
 * assigned to a certain property. The property can be modified by changing the
 * value for {@link GreatestNumberProcessor#numberPropertyId}.
 *
 * @author Markus Kroetzsch
 *
 */
public class GreatestNumberProcessor implements EntityDocumentProcessor {

	static final String numberPropertyId = "P1101";
	// P1090 is "redshift"
	// P1351 is "number of points/goals scored"
	// P1350 is "number of matches played"
	// P1128 is "employees", P1101 is "floors above ground"
	// P1174 is "visitors per year", P1183 is "seat capacity"

	ItemIdValue largestNumberItem;
	String largestNumberItemLabel;
	BigDecimal largestNumberValue;
	int itemsWithPropertyCount = 0;
	int itemCount = 0;

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
		GreatestNumberProcessor.printDocumentation();

		GreatestNumberProcessor processor = new GreatestNumberProcessor();
		ExampleHelpers.processEntitiesFromWikidataDump(processor);
		processor.printStatus();
	}

	@Override
	public void processItemDocument(ItemDocument itemDocument) {
		this.itemCount++;

		BigDecimal numericValue = null;

		for (StatementGroup statementGroup : itemDocument.getStatementGroups()) {
			switch (statementGroup.getProperty().getId()) {
			case numberPropertyId:
				numericValue = getNumericValue(statementGroup);
			}
		}

		if (numericValue != null) {
			this.itemsWithPropertyCount++;

			if (this.largestNumberValue == null
					|| numericValue.compareTo(this.largestNumberValue) > 0) {
				this.largestNumberValue = numericValue;
				this.largestNumberItem = itemDocument.getItemId();
				MonolingualTextValue label = itemDocument.getLabels().get("en");
				if (label != null) {
					this.largestNumberItemLabel = label.getText();
				} else {
					this.largestNumberItemLabel = this.largestNumberItem
							.getId();
				}
			}
		}

		if (this.itemCount % 100000 == 0) {
			printStatus();
		}
	}

	@Override
	public void processPropertyDocument(PropertyDocument propertyDocument) {
		// Nothing to do
	}

	/**
	 * Prints the current status, time and entity count.
	 */
	public void printStatus() {
		System.out.println("Found " + this.itemsWithPropertyCount
				+ " matching items after scanning " + this.itemCount
				+ " items.");
		if (this.largestNumberValue != null) {
			System.out.println("The items with the greatest number is: "
					+ this.largestNumberItemLabel + " ("
					+ this.largestNumberItem.getId() + ") with number "
					+ this.largestNumberValue);
		} else {
			System.out.println("No number with a specified value found yet.");
		}
	}

	/**
	 * Prints some basic documentation about this program.
	 */
	public static void printDocumentation() {
		System.out
				.println("********************************************************************");
		System.out.println("*** Wikidata Toolkit: GreatestNumberProcessor");
		System.out.println("*** ");
		System.out
				.println("*** This program will download and process dumps from Wikidata.");
		System.out
				.println("*** It will scan the dump to find the item with the greatest value");
		System.out.println("*** for property " + numberPropertyId + ".");
		System.out.println("*** See source code for further details.");
		System.out
				.println("********************************************************************");
	}

	/**
	 * Helper method that extracts a numeric value from the first quantity value
	 * found in a statement of the given statement group. It checks if the
	 * statement has a {@link QuantityValue}.
	 *
	 * @param statementGroup
	 *            the {@link StatementGroup} to extract the value from
	 * @return the number, or null if none was found
	 */
	private BigDecimal getNumericValue(StatementGroup statementGroup) {
		// Iterate over all statements
		for (Statement s : statementGroup.getStatements()) {
			// Find the main claim and check if it has a value
			if (s.getClaim().getMainSnak() instanceof ValueSnak) {
				Value v = ((ValueSnak) s.getClaim().getMainSnak()).getValue();
				// Check if the value is a TimeValue of sufficient precision
				if (v instanceof QuantityValue) {
					return ((QuantityValue) v).getNumericValue();
				}
			}
		}

		return null;
	}

}
