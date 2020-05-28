package org.wikidata.wdtk.examples;

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

import org.wikidata.wdtk.datamodel.interfaces.*;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import java.io.IOException;

/**
 * This example shows how to retrieve MediaInfo data from the API.
 *
 * @author Thomas Pellissier Tanon
 *
 */
public class OnlineMediaInfoExample {

	public static void main(String[] args) throws IOException, MediaWikiApiErrorException {
		ExampleHelpers.configureLogging();
		printDocumentation();

		WikibaseDataFetcher commonsDataFetcher = WikibaseDataFetcher.getWikimediaCommonsDataFetcher();
		WikibaseDataFetcher wikidataDataFetcher = WikibaseDataFetcher.getWikidataDataFetcher();

		System.out.println("*** Retrieving a media info document ...");
		MediaInfoDocument mediaInfoDocument = (MediaInfoDocument) commonsDataFetcher.getEntityDocumentByTitle("commonswiki", "File:Black hole - Messier 87 crop max res.jpg");

		// Print the English caption
		System.out.println("Caption: " + mediaInfoDocument.getLabels().get("en").getText());

		// Print the depict with labels from Wikidata:
		for(Statement statement : mediaInfoDocument.findStatementGroup("P180").getStatements()) {
			Value value = statement.getValue();
			if(value instanceof ItemIdValue) {
				ItemDocument depict = (ItemDocument) wikidataDataFetcher.getEntityDocument(((ItemIdValue) value).getId());
				System.out.println("Depict: " + depict.getLabels().get("en").getText() + "(" + depict.getEntityId().getIri() + ")");
			}
		}

		System.out.println("*** Done.");
	}

	/**
	 * Prints some basic documentation about this program.
	 */
	public static void printDocumentation() {
		System.out.println("********************************************************************");
		System.out.println("*** Wikidata Toolkit: MediaInfoDataExample");
		System.out.println("*** ");
		System.out.println("*** It does not download any dump files. See source code for details.");
		System.out.println("********************************************************************");
	}
}
