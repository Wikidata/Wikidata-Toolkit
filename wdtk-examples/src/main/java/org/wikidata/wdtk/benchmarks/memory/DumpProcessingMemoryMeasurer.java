package org.wikidata.wdtk.benchmarks.memory;

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
import java.util.Date;

import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.dumpfiles.DumpProcessingController;
import org.wikidata.wdtk.dumpfiles.MwRevision;
import org.wikidata.wdtk.dumpfiles.MwRevisionProcessor;
import org.wikidata.wdtk.dumpfiles.StatisticsMwRevisionProcessor;
import org.wikidata.wdtk.examples.ExampleHelpers;

/**
 * This class demonstrates how to write an application that downloads and
 * processes dumpfiles from Wikidata.org, that also measures the used memory.
 *
 * @author Markus Kroetzsch
 * @author Julian Mendez
 *
 */
public class DumpProcessingMemoryMeasurer {

	/** Number of entities processed between reports. */
	public static final long REPORT_FREQUENCY = 100000;

	/** The string "wikidatawiki" identifies Wikidata.org */
	public static final String WIKIDATAWIKI = "wikidatawiki";

	public static final String REV_PROC_STAT = "revision processing statistics";

	/** The following can also be set to another directory */
	public static final String USER_DIR = "user.dir";

	public static void main(String[] args) throws IOException {
		ExampleHelpers.configureLogging();
		System.out.println(getDocumentation());
		DumpProcessingController dumpProcessingController = new DumpProcessingController(
				WIKIDATAWIKI);
		EntityDocumentProcessor edpItemStats = new EntityStatisticsProcessor();
		dumpProcessingController.registerEntityDocumentProcessor(edpItemStats,
				MwRevision.MODEL_WIKIBASE_ITEM, true);
		dumpProcessingController.registerEntityDocumentProcessor(edpItemStats,
				MwRevision.MODEL_WIKIBASE_PROPERTY, true);
		MwRevisionProcessor rpRevisionStats = new StatisticsMwRevisionProcessor(
				REV_PROC_STAT, (int) REPORT_FREQUENCY);
		dumpProcessingController.registerMwRevisionProcessor(rpRevisionStats,
				null, true);
		dumpProcessingController.processAllRecentRevisionDumps();
	}

	/**
	 * Returns basic documentation about this program.
	 */
	private static String getDocumentation() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n********************************************************************");
		sb.append("\n*** Wikidata Toolkit: Dump Processing Memory Measurer");
		sb.append("\n*** ");
		sb.append("\n*** This program will download and process dumps from Wikidata.");
		sb.append("\n*** It will print progress information and some simple statistics.");
		sb.append("\n*** These statistics include memory usage by items and properties.");
		sb.append("\n*** Downloading may take some time initially. After that, files");
		sb.append("\n*** are stored on disk and are used until newer dumps are available.");
		sb.append("\n*** You can delete files manually when no longer needed (see ");
		sb.append("\n*** message below for the directory where files are found).");
		sb.append("\n********************************************************************");
		return sb.toString();
	}

	/**
	 * A simple example class that processes EntityDocuments to compute basic
	 * statistics that are printed to the standard output. This could be
	 * replaced with any other class that processes entity documents in some
	 * way.
	 *
	 * @author Markus Kroetzsch
	 * @author Julian Mendez
	 *
	 */
	static class EntityStatisticsProcessor implements EntityDocumentProcessor {

		Date start = new Date();

		long countEntities = 0;
		long countProperties = 0;
		long countItems = 0;
		long countLabels = 0;
		long countDescriptions = 0;
		long countAliases = 0;
		long countStatements = 0;
		long countSiteLinks = 0;

		SizeRecord sizeOfUsedMemory = new SizeRecord();
		SizeRecord sizeOfUsedMemoryByItems = new SizeRecord();
		SizeRecord sizeOfUsedMemoryByProperties = new SizeRecord();

		@Override
		public void processItemDocument(ItemDocument itemDocument) {
			SizeRecord sizeRecord = new SizeRecord(itemDocument);
			this.sizeOfUsedMemory.add(sizeRecord);
			this.sizeOfUsedMemoryByItems.add(sizeRecord);

			this.countLabels += itemDocument.getLabels().size();
			this.countDescriptions += itemDocument.getDescriptions().size();
			for (String languageKey : itemDocument.getAliases().keySet()) {
				this.countAliases += itemDocument.getAliases().get(languageKey)
						.size();
			}
			for (StatementGroup sg : itemDocument.getStatementGroups()) {
				this.countStatements += sg.getStatements().size();
			}
			this.countSiteLinks += itemDocument.getSiteLinks().size();

			this.countItems++;
			this.countEntities++;

			if ((this.countEntities % REPORT_FREQUENCY) == 0) {
				System.out.println("(counting item)" + getReport());
			}
		}

		@Override
		public void processPropertyDocument(PropertyDocument propertyDocument) {
			SizeRecord sizeRecord = new SizeRecord(propertyDocument);
			this.sizeOfUsedMemory.add(sizeRecord);
			this.sizeOfUsedMemoryByProperties.add(sizeRecord);

			this.countLabels += propertyDocument.getLabels().size();
			this.countDescriptions += propertyDocument.getDescriptions().size();
			for (String languageKey : propertyDocument.getAliases().keySet()) {
				this.countAliases += propertyDocument.getAliases()
						.get(languageKey).size();
			}

			this.countProperties++;
			this.countEntities++;

			if ((this.countEntities % REPORT_FREQUENCY) == 0) {
				System.out.println("(counting property)" + getReport());
			}
		}

		/**
		 * Returns a report about the statistics gathered so far.
		 */
		private String getReport() {
			StringBuilder sb = new StringBuilder();
			sb.append("\n * Entities: ");
			sb.append(this.countEntities);
			sb.append("\n * Items: ");
			sb.append(this.countItems);
			sb.append("\n * Properties: ");
			sb.append(this.countProperties);
			sb.append("\n * Labels: ");
			sb.append(this.countLabels);
			sb.append("\n * Descriptions: ");
			sb.append(this.countDescriptions);
			sb.append("\n * Aliases: ");
			sb.append(this.countAliases);
			sb.append("\n * Statements: ");
			sb.append(this.countStatements);
			sb.append("\n * Site links: ");
			sb.append(this.countSiteLinks);
			sb.append("\n * Memory usage (in bytes): ");
			sb.append(this.sizeOfUsedMemory.getSizeOfEntity());
			sb.append("\n * Memory usage of chars (in bytes): ");
			sb.append(this.sizeOfUsedMemory.getSizeOfChars());
			sb.append("\n * Details of memory usage: ");
			sb.append("\n    - by entities: ");
			sb.append(this.sizeOfUsedMemory.toString());
			sb.append("\n    - by items: ");
			sb.append(this.sizeOfUsedMemoryByItems.toString());
			sb.append("\n    - by properties: ");
			sb.append(this.sizeOfUsedMemoryByProperties.toString());
			sb.append("\n * Elapsed time: ");
			sb.append(((new Date()).getTime() - this.start.getTime()));
			sb.append(" ms\n\n");
			return sb.toString();
		}
	}

}
