package org.wikidata.wdtk.storage.wdtkbindings;

/*
 * #%L
 * Wikidata Toolkit Storage
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

import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.storage.wdtktodb.ItemDocumentAsEdgeContainer;
import org.wikidata.wdtk.storage.wdtktodb.PropertyDocumentAsEdgeContainer;
import org.wikidata.wdtk.storage.wdtktodb.WdtkAdaptorHelper;

/**
 * EntityDocumentProcessor that writes entity data to a database. If the
 * database already contains data for a processed entity, this data will be
 * overwritten. This will, however, not clean up dependent data objects that may
 * no longer be needed. Therefore, it is currently advisable to recreate the
 * database from scratch once in a while.
 *
 * @author Markus Kroetzsch
 */
public class DumpImportEntityDocumentProcessor implements
		EntityDocumentProcessor {

	long countProperties = 0;
	long countItems = 0;
	long countLabels = 0;
	long countDescriptions = 0;
	long countAliases = 0;
	long countStatements = 0;
	long countSiteLinks = 0;

	final WdtkDatabaseManager wdtkDatabaseManager;
	final WdtkAdaptorHelper wdtkAdaptorHelper;

	/**
	 * Creates a new object for the given database.
	 *
	 * @param wdtkDatabaseManager
	 *            the database to write to
	 */
	public DumpImportEntityDocumentProcessor(
			WdtkDatabaseManager wdtkDatabaseManager) {
		this.wdtkDatabaseManager = wdtkDatabaseManager;
		this.wdtkAdaptorHelper = new WdtkAdaptorHelper(
				wdtkDatabaseManager.getSortSchema());
	}

	@Override
	public void processItemDocument(ItemDocument itemDocument) {
		this.countItems++;

		ItemDocumentAsEdgeContainer edgeContainer = new ItemDocumentAsEdgeContainer(
				itemDocument, this.wdtkAdaptorHelper);
		this.wdtkDatabaseManager.updateEdges(edgeContainer);

		// Gather some statistics while you are at it:
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

		// Print a report every 10000 items:
		if (this.countItems % 10000 == 0) {
			printReport();
			// Commit data every 50000 items:
			if (this.countItems % 50000 == 0) {
				this.wdtkDatabaseManager.commit();
			}
		}
	}

	@Override
	public void processPropertyDocument(PropertyDocument propertyDocument) {
		this.countProperties++;

		PropertyDocumentAsEdgeContainer edgeContainer = new PropertyDocumentAsEdgeContainer(
				propertyDocument, this.wdtkAdaptorHelper);
		this.wdtkDatabaseManager.updateEdges(edgeContainer);
	}

	/**
	 * Closes the database connection. This must be called eventually to ensure
	 * a healthy database file. You could also commit and close the database
	 * yourself, of course.
	 */
	public void close() {
		printReport(); // print a final report
		wdtkDatabaseManager.commit();
		wdtkDatabaseManager.close();
	}

	/**
	 * Prints a report about the statistics gathered so far.
	 */
	private void printReport() {
		System.out.println("Processed " + this.countItems + " items:");
		System.out.println(" * Labels: " + this.countLabels);
		System.out.println(" * Descriptions: " + this.countDescriptions);
		System.out.println(" * Aliases: " + this.countAliases);
		System.out.println(" * Statements: " + this.countStatements);
		System.out.println(" * Site links: " + this.countSiteLinks);

		System.out
				.println("Processed " + this.countProperties + " properties.");
	}

}
