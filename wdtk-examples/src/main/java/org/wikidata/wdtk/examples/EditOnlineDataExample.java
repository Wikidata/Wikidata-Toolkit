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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.ItemDocumentBuilder;
import org.wikidata.wdtk.datamodel.helpers.ReferenceBuilder;
import org.wikidata.wdtk.datamodel.helpers.StatementBuilder;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementDocument;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.util.WebResourceFetcherImpl;
import org.wikidata.wdtk.wikibaseapi.ApiConnection;
import org.wikidata.wdtk.wikibaseapi.LoginFailedException;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataEditor;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.wikibaseapi.apierrors.EditConflictErrorException;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;
import org.wikidata.wdtk.wikibaseapi.apierrors.NoSuchEntityErrorException;

/**
 * This example shows how to create and modify data through the web API of a
 * Wikibase site.
 * <p>
 * IMPORTANT: Running this program will perform edits on test.wikidata.org.
 * These edits are permanent and public. When running this program as is, the
 * edits will be performed without logging in. This means that your current IP
 * address will be recorded in the edit history of the page. If you prefer to
 * use a login, please comment-in the respective line in the source code and
 * modify it to use your credentials.
 * <p>
 * Note that all modification operations can throw an
 * {@link MediaWikiApiErrorException} (if there was an API error) or
 * {@link IOException} (if there was a network error, etc.). We do not handle
 * this here. In real applications, you may want to handle some subclasses of
 * {@link MediaWikiApiErrorException} in special ways, e.g.,
 * {@link EditConflictErrorException} (you tried to edit an entity that has been
 * modified by someone else since) and {@link NoSuchEntityErrorException} (you
 * tried to modify an entity that meanwhile was deleted).
 *
 * @author Markus Kroetzsch
 *
 */
public class EditOnlineDataExample {

	/**
	 * We use this to identify the site test.wikidata.org. This IRI is not
	 * essential for API interactions (the API knows of only one site and will
	 * use local ids only), but it is important to use a fixed IRI in your code
	 * for each site and not to mix IRIs.
	 */
	final static String siteIri = "http://www.test.wikidata.org/entity/";

	static PropertyIdValue stringProperty1;
	static PropertyIdValue stringProperty2;
	static PropertyIdValue stringProperty3;
	static PropertyIdValue stringProperty4;
	static PropertyIdValue stringProperty5;

	public static void main(String[] args) throws IOException, MediaWikiApiErrorException {
		ExampleHelpers.configureLogging();
		printDocumentation();

		// Always set your User-Agent to the name of your application:
		WebResourceFetcherImpl
				.setUserAgent("Wikidata Toolkit EditOnlineDataExample");

		ApiConnection connection = ApiConnection.getTestWikidataApiConnection();
		// Optional login -- required for operations on real wikis:
		// connection.login("my username", "my password");
		WikibaseDataEditor wbde = new WikibaseDataEditor(connection, siteIri);

		// Find some test properties on test.wikidata.org:
		findSomeStringProperties(connection);

		System.out.println("*** Creating a new entity ...");

		ItemIdValue noid = ItemIdValue.NULL; // used when creating new items
		Statement statement1 = StatementBuilder
				.forSubjectAndProperty(noid, stringProperty1)
				.withValue(Datamodel.makeStringValue("String value 1")).build();
		Statement statement2 = StatementBuilder
				.forSubjectAndProperty(noid, stringProperty1)
				.withValue(
						Datamodel
								.makeStringValue("Item created by Wikidata Toolkit example program; see https://github.com/Wikidata/Wikidata-Toolkit/"))
				.build();
		Statement statement3 = StatementBuilder
				.forSubjectAndProperty(noid, stringProperty2)
				.withValue(Datamodel.makeStringValue("String value 3")).build();

		ItemDocument itemDocument = ItemDocumentBuilder.forItemId(noid)
				.withLabel("Wikidata Toolkit test", "en")
				.withStatement(statement1).withStatement(statement2)
				.withStatement(statement3).build();
		// Note: we do not give a description, since label+description must be
		// unique, which would cause problems if this example is run many times.

		ItemDocument newItemDocument = wbde.createItemDocument(itemDocument,
				"Wikidata Toolkit example test item creation", null);

		ItemIdValue newItemId = newItemDocument.getEntityId();
		System.out.println("*** Successfully created a new item "
				+ newItemId.getId()
				+ " (see https://test.wikidata.org/w/index.php?title="
				+ newItemId.getId() + "&oldid="
				+ newItemDocument.getRevisionId() + " for this version)");

		System.out.println("*** Adding more statements to new entity ...");

		// Make a statements with qualifiers for a change:
		Statement statement4 = StatementBuilder
				.forSubjectAndProperty(noid, stringProperty2)
				.withValue(Datamodel.makeStringValue("String value 4"))
				.withQualifierValue(stringProperty1,
						Datamodel.makeStringValue("Qualifier value 1"))
				.withQualifierValue(stringProperty1,
						Datamodel.makeStringValue("Qualifier value 2"))
				.withQualifierValue(stringProperty2,
						Datamodel.makeStringValue("Qualifier value 3")).build();

		// Make a statement with the same claim as statement 1,
		// but with the additional reference;
		// WDTK will merge this automatically into the existing statement
		Reference reference1 = ReferenceBuilder
				.newInstance()
				.withPropertyValue(stringProperty4,
						Datamodel.makeStringValue("Reference property value 1"))
				.withPropertyValue(stringProperty5,
						Datamodel.makeStringValue("Reference property value 2"))
				.build();
		Statement statement1WithRef = StatementBuilder
				.forSubjectAndProperty(noid, stringProperty1)
				.withValue(Datamodel.makeStringValue("String value 1"))
				.withReference(reference1).build();

		// We add three statements:
		// * statement4: new statement; will be added
		// * statement1WithRef: extension of statement1; just add reference to
		// existing statement
		// * statement2: already present, will not be added again
		newItemDocument = wbde.updateStatements(newItemId,
				Arrays.asList(statement4, statement1WithRef, statement2),
				Collections.emptyList(),
				"Wikidata Toolkit example test statement addition", null);

		System.out.println("*** Successfully added statements to "
				+ newItemId.getId()
				+ " (see https://test.wikidata.org/w/index.php?title="
				+ newItemId.getId() + "&oldid="
				+ newItemDocument.getRevisionId() + " for this version)");

		System.out
				.println("*** Deleting and modifying existing statements ...");
		// We first need to find existing statements with their statement id.
		// For this we look at the item Document that we have last retrieved.
		Statement statementToModify = findStatementGroup(stringProperty1,
				newItemDocument).getStatements().get(0);
		// We replace this statement by a new one, with the same reference and
		// property but a different value. The id is essential to make sure that
		// we update the existing statement rather than adding a new one:
		Statement newStatement1 = StatementBuilder
				.forSubjectAndProperty(noid, stringProperty1)
				.withId(statementToModify.getStatementId())
				.withValue(Datamodel.makeStringValue("Updated string value 1"))
				.withReferences(statementToModify.getReferences()).build();

		// We also want to delete a statement:
		Statement statementToDelete = findStatementGroup(stringProperty2,
				newItemDocument).getStatements().get(0);

		newItemDocument = wbde.updateStatements(newItemDocument,
				Collections.singletonList(newStatement1),
				Collections.singletonList(statementToDelete),
				"Wikidata Toolkit example test statement modification", null);

		System.out.println("*** Successfully updated statements of "
				+ newItemId.getId()
				+ " (see https://test.wikidata.org/w/index.php?title="
				+ newItemId.getId() + "&oldid="
				+ newItemDocument.getRevisionId() + " for this version)");
		System.out
				.println("*** The complete history of our edits can be seen at: "
						+ "https://test.wikidata.org/w/index.php?title="
						+ newItemId.getId() + "&action=history");
		System.out.println("*** Done.");
	}

	/**
	 * Finds properties of datatype string on test.wikidata.org. Since the test
	 * site changes all the time, we cannot hardcode a specific property here.
	 * Instead, we just look through all properties starting from P1 to find the
	 * first few properties of type string that have an English label. These
	 * properties are used for testing in this code.
	 *
	 * @param connection
	 * @throws MediaWikiApiErrorException
	 * @throws IOException 
	 */
	public static void findSomeStringProperties(ApiConnection connection)
			throws MediaWikiApiErrorException, IOException {
		WikibaseDataFetcher wbdf = new WikibaseDataFetcher(connection, siteIri);
		wbdf.getFilter().excludeAllProperties();
		wbdf.getFilter().setLanguageFilter(Collections.singleton("en"));

		ArrayList<PropertyIdValue> stringProperties = new ArrayList<>();

		System.out
				.println("*** Trying to find string properties for the example ... ");
		int propertyNumber = 1;
		while (stringProperties.size() < 5) {
			ArrayList<String> fetchProperties = new ArrayList<>();
			for (int i = propertyNumber; i < propertyNumber + 10; i++) {
				fetchProperties.add("P" + i);
			}
			propertyNumber += 10;
			Map<String, EntityDocument> results = wbdf
					.getEntityDocuments(fetchProperties);
			for (EntityDocument ed : results.values()) {
				PropertyDocument pd = (PropertyDocument) ed;
				if (DatatypeIdValue.DT_STRING.equals(pd.getDatatype().getIri())
						&& pd.getLabels().containsKey("en")) {
					stringProperties.add(pd.getEntityId());
					System.out.println("* Found string property "
							+ pd.getEntityId().getId() + " ("
							+ pd.getLabels().get("en") + ")");
				}
			}
		}

		stringProperty1 = stringProperties.get(0);
		stringProperty2 = stringProperties.get(1);
		stringProperty3 = stringProperties.get(2);
		stringProperty4 = stringProperties.get(3);
		stringProperty5 = stringProperties.get(4);

		System.out.println("*** Done.");
	}

	/**
	 * Prints some basic documentation about this program.
	 */
	public static void printDocumentation() {
		System.out
				.println("********************************************************************");
		System.out.println("*** Wikidata Toolkit: EditOnlineDataExample");
		System.out.println("*** ");
		System.out
				.println("*** This program creates and modifies online data at test.wikidata.org.");
		System.out
				.println("*** It does not download any dump files. See source code for details.");
		System.out
				.println("********************************************************************");
	}

	/**
	 * Finds the {@link StatementGroup} for the given property in a document.
	 *
	 * @param pid
	 *            the property to look for
	 * @param document
	 *            the document to search
	 * @return the {@link StatementGroup} with this property, or null if there
	 *         is none
	 */
	protected static StatementGroup findStatementGroup(PropertyIdValue pid,
			StatementDocument document) {
		for (StatementGroup sg : document.getStatementGroups()) {
			if (pid.equals(sg.getProperty())) {
				return sg;
			}
		}
		return null;
	}

}
