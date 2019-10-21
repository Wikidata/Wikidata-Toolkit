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
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementDocument;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.TermedDocument;

/**
 * A simple example class that processes EntityDocuments to compute basic
 * statistics that are printed to the standard output. Moreover, it stores
 * further statistics in several files:
 * <ul>
 * <li>The number of uses of each property in the data is counted and stored in
 * CSV files item-property-counts.csv (for statements used on items) and
 * property-property-counts.csv (for statements used on properties).</li>
 * <li>The number of links to each linked site is counted and stored in file
 * site-link-counts.csv.</li>
 * <li>The number of labels, aliases, and descriptions per language is counted
 * and stored in CSV files item-term-counts.csv (for items) and
 * property-term-counts.csv (for properties).</li>
 * </ul>
 *
 * @author Markus Kroetzsch
 *
 */
class EntityStatisticsProcessor implements EntityDocumentProcessor {

	/**
	 * Simple record class to keep track of some usage numbers for one type of
	 * entity.
	 *
	 * @author Markus Kroetzsch
	 *
	 */
	static class UsageStatistics {
		long count = 0;
		long countLabels = 0;
		long countDescriptions = 0;
		long countAliases = 0;
		long countStatements = 0;
		long countReferencedStatements = 0;

		// Maps to store property usage data for each property:
		final HashMap<PropertyIdValue, Integer> propertyCountsMain = new HashMap<>();
		final HashMap<PropertyIdValue, Integer> propertyCountsQualifier = new HashMap<>();
		final HashMap<PropertyIdValue, Integer> propertyCountsReferences = new HashMap<>();
		final HashMap<String, Integer> labelCounts = new HashMap<>();
		final HashMap<String, Integer> descriptionCounts = new HashMap<>();
		final HashMap<String, Integer> aliasCounts = new HashMap<>();

	}

	UsageStatistics itemStatistics = new UsageStatistics();
	UsageStatistics propertyStatistics = new UsageStatistics();
	long countSiteLinks = 0;
	final HashMap<String, Integer> siteLinkStatistics = new HashMap<>();

	/**
	 * Main method. Processes the whole dump using this processor and writes the
	 * results to a file. To change which dump file to use and whether to run in
	 * offline mode, modify the settings in {@link ExampleHelpers}.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		ExampleHelpers.configureLogging();
		EntityStatisticsProcessor.printDocumentation();

		EntityStatisticsProcessor entityStatisticsProcessor = new EntityStatisticsProcessor();
		ExampleHelpers
				.processEntitiesFromWikidataDump(entityStatisticsProcessor);
		entityStatisticsProcessor.writeFinalResults();
	}

	@Override
	public void processItemDocument(ItemDocument itemDocument) {
		// Count items:
		this.itemStatistics.count++;

		countTerms(this.itemStatistics, itemDocument);
		countStatements(this.itemStatistics, itemDocument);

		// Count site links:
		this.countSiteLinks += itemDocument.getSiteLinks().size();
		for (SiteLink siteLink : itemDocument.getSiteLinks().values()) {
			countKey(this.siteLinkStatistics, siteLink.getSiteKey(), 1);
		}

		// Print a report every 10000 items:
		if (this.itemStatistics.count % 10000 == 0) {
			printStatus();
		}
	}

	@Override
	public void processPropertyDocument(PropertyDocument propertyDocument) {
		// Count properties:
		this.propertyStatistics.count++;

		countTerms(this.propertyStatistics, propertyDocument);
		countStatements(this.propertyStatistics, propertyDocument);
	}

	/**
	 * Count the terms (labels, descriptions, aliases) of an item or property
	 * document.
	 *
	 * @param usageStatistics
	 *            statistics object to store counters in
	 * @param termedDocument
	 *            document to count the terms of
	 */
	protected void countTerms(UsageStatistics usageStatistics,
			TermedDocument termedDocument) {
		usageStatistics.countLabels += termedDocument.getLabels().size();
		for (MonolingualTextValue mtv : termedDocument.getLabels().values()) {
			countKey(usageStatistics.labelCounts, mtv.getLanguageCode(), 1);
		}

		usageStatistics.countDescriptions += termedDocument.getDescriptions()
				.size();
		for (MonolingualTextValue mtv : termedDocument.getDescriptions()
				.values()) {
			countKey(usageStatistics.descriptionCounts, mtv.getLanguageCode(),
					1);
		}

		for (String languageKey : termedDocument.getAliases().keySet()) {
			int count = termedDocument.getAliases().get(languageKey).size();
			usageStatistics.countAliases += count;
			countKey(usageStatistics.aliasCounts, languageKey, count);
		}
	}

	/**
	 * Count the statements and property uses of an item or property document.
	 *
	 * @param usageStatistics
	 *            statistics object to store counters in
	 * @param statementDocument
	 *            document to count the statements of
	 */
	protected void countStatements(UsageStatistics usageStatistics,
			StatementDocument statementDocument) {
		// Count Statement data:
		for (StatementGroup sg : statementDocument.getStatementGroups()) {
			// Count Statements:
			usageStatistics.countStatements += sg.size();

			// Count uses of properties in Statements:
			countPropertyMain(usageStatistics, sg.getProperty(), sg.size());
			for (Statement s : sg) {
				for (SnakGroup q : s.getQualifiers()) {
					countPropertyQualifier(usageStatistics, q.getProperty(), q.size());
				}
				for (Reference r : s.getReferences()) {
					usageStatistics.countReferencedStatements++;
					for (SnakGroup snakGroup : r.getSnakGroups()) {
						countPropertyReference(usageStatistics,
								snakGroup.getProperty(), snakGroup.size());
					}
				}
			}
		}
	}

	/**
	 * Prints some basic documentation about this program.
	 */
	public static void printDocumentation() {
		System.out
				.println("********************************************************************");
		System.out.println("*** Wikidata Toolkit: EntityStatisticsProcessor");
		System.out.println("*** ");
		System.out
				.println("*** This program will download and process dumps from Wikidata.");
		System.out
				.println("*** It will print progress information and some simple statistics.");
		System.out
				.println("*** Results about property usage will be stored in a CSV file.");
		System.out.println("*** See source code for further details.");
		System.out
				.println("********************************************************************");
	}

	/**
	 * Prints and stores final result of the processing. This should be called
	 * after finishing the processing of a dump. It will print the statistics
	 * gathered during processing and it will write a CSV file with usage counts
	 * for every property.
	 */
	private void writeFinalResults() {
		// Print a final report:
		printStatus();

		// Store property counts in files:
		writePropertyStatisticsToFile(this.itemStatistics,
				"item-property-counts.csv");
		writePropertyStatisticsToFile(this.propertyStatistics,
				"property-property-counts.csv");

		// Store site link statistics in file:
		try (PrintStream out = new PrintStream(
				ExampleHelpers
						.openExampleFileOuputStream("site-link-counts.csv"))) {

			out.println("Site key,Site links");
			for (Entry<String, Integer> entry : this.siteLinkStatistics
					.entrySet()) {
				out.println(entry.getKey() + "," + entry.getValue());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Store term statistics in file:
		writeTermStatisticsToFile(this.itemStatistics, "item-term-counts.csv");
		writeTermStatisticsToFile(this.propertyStatistics,
				"property-term-counts.csv");
	}

	/**
	 * Stores the gathered usage statistics about property uses to a CSV file.
	 *
	 * @param usageStatistics
	 *            the statistics to store
	 * @param fileName
	 *            the name of the file to use
	 */
	private void writePropertyStatisticsToFile(UsageStatistics usageStatistics,
			String fileName) {
		try (PrintStream out = new PrintStream(
				ExampleHelpers.openExampleFileOuputStream(fileName))) {

			out.println("Property id,in statements,in qualifiers,in references,total");

			for (Entry<PropertyIdValue, Integer> entry : usageStatistics.propertyCountsMain
					.entrySet()) {
				int qCount = usageStatistics.propertyCountsQualifier.get(entry
						.getKey());
				int rCount = usageStatistics.propertyCountsReferences.get(entry
						.getKey());
				int total = entry.getValue() + qCount + rCount;
				out.println(entry.getKey().getId() + "," + entry.getValue()
						+ "," + qCount + "," + rCount + "," + total);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Stores the gathered usage statistics about term uses by language to a CSV
	 * file.
	 *
	 * @param usageStatistics
	 *            the statistics to store
	 * @param fileName
	 *            the name of the file to use
	 */
	private void writeTermStatisticsToFile(UsageStatistics usageStatistics,
			String fileName) {

		// Make sure all keys are present in label count map:
		for (String key : usageStatistics.aliasCounts.keySet()) {
			countKey(usageStatistics.labelCounts, key, 0);
		}
		for (String key : usageStatistics.descriptionCounts.keySet()) {
			countKey(usageStatistics.labelCounts, key, 0);
		}

		try (PrintStream out = new PrintStream(
				ExampleHelpers.openExampleFileOuputStream(fileName))) {

			out.println("Language,Labels,Descriptions,Aliases");
			for (Entry<String, Integer> entry : usageStatistics.labelCounts
					.entrySet()) {
				countKey(usageStatistics.aliasCounts, entry.getKey(), 0);
				int aCount = usageStatistics.aliasCounts.get(entry.getKey());
				countKey(usageStatistics.descriptionCounts, entry.getKey(), 0);
				int dCount = usageStatistics.descriptionCounts.get(entry
						.getKey());
				out.println(entry.getKey() + "," + entry.getValue() + ","
						+ dCount + "," + aCount);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Prints a report about the statistics gathered so far.
	 */
	private void printStatus() {
		System.out.println("---");
		printStatistics(this.itemStatistics, "items");
		System.out.println(" * Site links: " + this.countSiteLinks);

		printStatistics(this.propertyStatistics, "properties");
	}

	/**
	 * Prints a report about the statistics stored in the given data object.
	 *
	 * @param usageStatistics
	 *            the statistics object to print
	 * @param entityLabel
	 *            the label to use to refer to this kind of entities ("items" or
	 *            "properties")
	 */
	private void printStatistics(UsageStatistics usageStatistics,
			String entityLabel) {
		System.out.println("Processed " + usageStatistics.count + " "
				+ entityLabel + ":");
		System.out.println(" * Labels: " + usageStatistics.countLabels
				+ ", descriptions: " + usageStatistics.countDescriptions
				+ ", aliases: " + usageStatistics.countAliases);
		System.out.println(" * Statements: " + usageStatistics.countStatements
				+ ", with references: "
				+ usageStatistics.countReferencedStatements);
	}

	/**
	 * Counts additional occurrences of a property as the main property of
	 * statements.
	 *
	 * @param usageStatistics
	 *            statistics object where count is stored
	 * @param property
	 *            the property to count
	 * @param count
	 *            the number of times to count the property
	 */
	private void countPropertyMain(UsageStatistics usageStatistics,
			PropertyIdValue property, int count) {
		addPropertyCounters(usageStatistics, property);
		usageStatistics.propertyCountsMain.put(property,
				usageStatistics.propertyCountsMain.get(property) + count);
	}

	/**
	 * Counts additional occurrences of a property as qualifier property of
	 * statements.
	 *
	 * @param usageStatistics
	 *            statistics object where count is stored
	 * @param property
	 *            the property to count
	 * @param count
	 *            the number of times to count the property
	 */
	private void countPropertyQualifier(UsageStatistics usageStatistics,
			PropertyIdValue property, int count) {
		addPropertyCounters(usageStatistics, property);
		usageStatistics.propertyCountsQualifier.put(property,
				usageStatistics.propertyCountsQualifier.get(property) + count);
	}

	/**
	 * Counts additional occurrences of a property as property in references.
	 *
	 * @param usageStatistics
	 *            statistics object where count is stored
	 * @param property
	 *            the property to count
	 * @param count
	 *            the number of times to count the property
	 */
	private void countPropertyReference(UsageStatistics usageStatistics,
			PropertyIdValue property, int count) {
		addPropertyCounters(usageStatistics, property);
		usageStatistics.propertyCountsReferences.put(property,
				usageStatistics.propertyCountsReferences.get(property) + count);
	}

	/**
	 * Initializes the counters for a property to zero if not done yet.
	 *
	 * @param usageStatistics
	 *            statistics object to initialize
	 * @param property
	 *            the property to count
	 */
	private void addPropertyCounters(UsageStatistics usageStatistics,
			PropertyIdValue property) {
		if (!usageStatistics.propertyCountsMain.containsKey(property)) {
			usageStatistics.propertyCountsMain.put(property, 0);
			usageStatistics.propertyCountsQualifier.put(property, 0);
			usageStatistics.propertyCountsReferences.put(property, 0);
		}
	}

	/**
	 * Helper method that stores in a hash map how often a certain key occurs.
	 * If the key has not been encountered yet, a new entry is created for it in
	 * the map. Otherwise the existing value for the key is incremented.
	 *
	 * @param map
	 *            the map where the counts are stored
	 * @param key
	 *            the key to be counted
	 * @param count
	 *            value by which the count should be incremented; 1 is the usual
	 *            case
	 */
	private void countKey(Map<String, Integer> map, String key, int count) {
		if (map.containsKey(key)) {
			map.put(key, map.get(key) + count);
		} else {
			map.put(key, count);
		}
	}
}
