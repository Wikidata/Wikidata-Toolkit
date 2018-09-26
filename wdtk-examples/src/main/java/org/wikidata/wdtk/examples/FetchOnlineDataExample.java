package org.wikidata.wdtk.examples;

import java.io.IOException;

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

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.wikibaseapi.ApiConnection;
import org.wikidata.wdtk.wikibaseapi.WbSearchEntitiesResult;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

public class FetchOnlineDataExample {

	public static void main(String[] args) throws MediaWikiApiErrorException, IOException {
		ExampleHelpers.configureLogging();
		printDocumentation();

		WikibaseDataFetcher wbdf = new WikibaseDataFetcher(
				ApiConnection.getTestWikidataApiConnection(),
				Datamodel.SITE_WIKIDATA);

		System.out.println("*** Fetching data for one entity:");
		EntityDocument q42 = wbdf.getEntityDocument("P176");
		System.out.println(q42);

		if (q42 instanceof ItemDocument) {
			System.out.println("The English name for entity Q42 is "
					+ ((ItemDocument) q42).getLabels().get("en").getText());
		}

		System.out.println("*** Fetching data for several entities:");
		Map<String, EntityDocument> results = wbdf.getEntityDocuments("Q80",
				"P31");
		// Keys of this map are Qids, but we only use the values here:
		for (EntityDocument ed : results.values()) {
			System.out.println("Successfully retrieved data for "
					+ ed.getEntityId().getId());
		}

		System.out
				.println("*** Fetching data using filters to reduce data volume:");
		// Only site links from English Wikipedia:
		wbdf.getFilter().setSiteLinkFilter(Collections.singleton("enwiki"));
		// Only labels in French:
		wbdf.getFilter().setLanguageFilter(Collections.singleton("fr"));
		// No statements at all:
		wbdf.getFilter().setPropertyFilter(
				Collections.<PropertyIdValue> emptySet());
		EntityDocument q8 = wbdf.getEntityDocument("Q8");
		if (q8 instanceof ItemDocument) {
			System.out.println("The French label for entity Q8 is "
					+ ((ItemDocument) q8).getLabels().get("fr").getText()
					+ "\nand its English Wikipedia page has the title "
					+ ((ItemDocument) q8).getSiteLinks().get("enwiki")
							.getPageTitle() + ".");
		}

		System.out.println("*** Fetching data based on page title:");
		EntityDocument edPratchett = wbdf.getEntityDocumentByTitle("enwiki",
				"Terry Pratchett");
		System.out.println("The Qid of Terry Pratchett is "
				+ edPratchett.getEntityId().getId());

		System.out.println("*** Fetching data based on several page titles:");
		results = wbdf.getEntityDocumentsByTitle("enwiki", "Wikidata",
				"Wikipedia");
		// In this case, keys are titles rather than Qids
		for (Entry<String, EntityDocument> entry : results.entrySet()) {
			System.out
					.println("Successfully retrieved data for page entitled \""
							+ entry.getKey() + "\": "
							+ entry.getValue().getEntityId().getId());
		}

		System.out.println("** Doing search on Wikidata:");
		for(WbSearchEntitiesResult result : wbdf.searchEntities("Douglas Adams", "fr")) {
			System.out.println("Found " + result.getEntityId() + " with label " + result.getLabel());
		}

		System.out.println("*** Done.");
	}

	/**
	 * Prints some basic documentation about this program.
	 */
	public static void printDocumentation() {
		System.out
				.println("********************************************************************");
		System.out.println("*** Wikidata Toolkit: FetchOnlineDataExample");
		System.out.println("*** ");
		System.out
				.println("*** This program fetches individual data using the wikidata.org API.");
		System.out.println("*** It does not download any dump files.");
		System.out
				.println("********************************************************************");
	}

}
