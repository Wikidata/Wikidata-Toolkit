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

import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.StatementBuilder;
import org.wikidata.wdtk.datamodel.interfaces.*;
import org.wikidata.wdtk.wikibaseapi.ApiConnection;
import org.wikidata.wdtk.wikibaseapi.BasicApiConnection;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataEditor;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import java.io.IOException;
import java.util.Collections;

/**
 * This example shows how to create and modify media info data.
 * <p>
 * IMPORTANT: Running this program will perform edits on commons.wikimedia.beta.wmflabs.org.
 * These edits are permanent and public. When running this program as is, the
 * edits will be performed without logging in. This means that your current IP
 * address will be recorded in the edit history of the page. If you prefer to
 * use a login, please comment-in the respective line in the source code and
 * modify it to use your credentials.
 * <p>
 *
 * @author Thomas Pellissier Tanon
 *
 */
public class EditOnlineMediaInfoExample {

	/**
	 * We use this to identify the site commons.wikimedia.beta.wmflabs.org. This IRI is not
	 * essential for API interactions (the API knows of only one site and will
	 * use local ids only), but it is important to use a fixed IRI in your code
	 * for each site and not to mix IRIs.
	 *
	 * For wikimedia Commons, use {@link Datamodel.SITE_WIKIMEDIA_COMMONS}.
	 */
	private final static String siteIri = "https://commons.wikimedia.beta.wmflabs.org/entity/";

	public static void main(String[] args) throws IOException, MediaWikiApiErrorException {
		ExampleHelpers.configureLogging();
		printDocumentation();

		ApiConnection connection = new BasicApiConnection("https://commons.wikimedia.beta.wmflabs.org/w/api.php");
		// Optional login -- required for operations on real wikis:
		// connection.login("my username", "my password");
		WikibaseDataFetcher wbdf = new WikibaseDataFetcher(connection, siteIri);
		WikibaseDataEditor wbde = new WikibaseDataEditor(connection, siteIri);

		System.out.println("*** Fetching the current media info or retrieving a dummy value...");
		// If the entity does not exists, it's going to be returned anyway
		MediaInfoDocument mediaInfoDocument = (MediaInfoDocument) wbdf.getEntityDocumentByTitle("commonswiki", "File:RandomImage 4658098723742867.jpg");

		System.out.println("*** Editing a media info document ...");
		mediaInfoDocument = mediaInfoDocument
				.withLabel(Datamodel.makeMonolingualTextValue("random image", "en"))
				.withStatement(StatementBuilder
						.forSubjectAndProperty(mediaInfoDocument.getEntityId(), Datamodel.makeWikidataPropertyIdValue("P245962"))
						.withValue(Datamodel.makeWikidataItemIdValue("Q81566")).build());
		wbde.editMediaInfoDocument(mediaInfoDocument, false, "Wikidata Toolkit example media info edit", Collections.emptyList());
	}

	/**
	 * Prints some basic documentation about this program.
	 */
	public static void printDocumentation() {
		System.out.println("********************************************************************");
		System.out.println("*** Wikidata Toolkit: MediaInfoDataExample");
		System.out.println("*** ");
		System.out.println("*** This program creates and modifies online data at commons.wikimedia.beta.wmflabs.org.");
		System.out.println("*** It does not download any dump files. See source code for details.");
		System.out.println("********************************************************************");
	}
}
