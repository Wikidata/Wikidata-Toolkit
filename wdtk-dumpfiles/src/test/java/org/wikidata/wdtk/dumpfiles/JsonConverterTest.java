package org.wikidata.wdtk.dumpfiles;

/*
 * #%L
 * Wikidata Toolkit Dump File Handling
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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.dumpfiles.TestHelpers.JsonFetcher;
import org.wikidata.wdtk.dumpfiles.TestHelpers.TestObjectFactory;

/**
 * The test setup uses several files containing JSON. These files are read by
 * the org.json-parser into sample objects to be converted.
 * 
 * @author Fredo Erxleben
 * 
 */
public class JsonConverterTest {

	private static final JsonFetcher jsonFetcher = new JsonFetcher();
	private static final String BASE_IRI = "";
	private static final TestObjectFactory testObjectFactory = new TestObjectFactory();

	private JsonConverter uut; // unit under test

	@Before
	public void setUp() {
		this.uut = new JsonConverter(BASE_IRI, new DataObjectFactoryImpl());
	}

	@Test
	public void testEmptyProperty() throws JSONException, IOException {
		// since there can be no property without data type,
		// the datatype is tested here implicitly

		PropertyDocument propertyDocument = jsonFetcher
				.getPropertyDocumentFromResource("Property_Empty.json", "P1",
						this.uut);

		PropertyDocument emptyPropertyDocument = testObjectFactory
				.createEmptyPropertyDocument(BASE_IRI);

		assertEquals(propertyDocument, emptyPropertyDocument);
	}
	
	@Test
	public void testPropertyWithStringEntity() throws JSONException, IOException {
		// this tests an alternative description equivalent to an empty property
		
		PropertyDocument propertyDocument = jsonFetcher
				.getPropertyDocumentFromResource("Property_StringEntity.json", "P1",
						this.uut);

		PropertyDocument emptyPropertyDocument = testObjectFactory
				.createEmptyPropertyDocument(BASE_IRI);

		assertEquals(propertyDocument, emptyPropertyDocument);
	}

	@Test
	public void testEmptyItem() throws JSONException, IOException {
		ItemDocument itemDocument = jsonFetcher.getItemDocumentFromResource(
				"Item_Empty.json", "Q1", this.uut);

		ItemDocument emptyItemDocument = testObjectFactory
				.createEmptyItemDocument(BASE_IRI);

		assertEquals(itemDocument, emptyItemDocument);
	}

	@Test
	public void testLabels() throws JSONException, IOException {
		// NOTE: empty labels are tested in the empty documents
		// NOTE: only one label notation is tested so far
		// which is {"key":"value", … }
		ItemDocument itemDocument = jsonFetcher.getItemDocumentFromResource(
				"Item_Labels.json", "Q1", this.uut);

		assertEquals(itemDocument.getLabels(),
				testObjectFactory.createTestLabels());
	}

	@Test
	public void testDescriptions() throws JSONException, IOException {
		// NOTE: empty descriptions are tested in the empty documents
		// NOTE: only one description notation is tested so far
		// which is {"key":"value", … }
		ItemDocument itemDocument = jsonFetcher.getItemDocumentFromResource(
				"Item_Descriptions.json", "Q1", this.uut);

		assertEquals(itemDocument.getDescriptions(),
				testObjectFactory.createTestDescriptions());
	}

	@Test
	public void testAliases() throws JSONException, IOException {
		// NOTE: empty aliases are tested in the empty documents
		// NOTE: following alias notations are tested so far:
		// {"key":"value", … }
		// {"key":["value1", "value2"], … }
		ItemDocument itemDocument = jsonFetcher.getItemDocumentFromResource(
				"Item_Aliases.json", "Q1", this.uut);

		assertEquals(itemDocument.getAliases(),
				testObjectFactory.createTestAliases());
	}

	@Test
	public void testLinks() throws JSONException, IOException {
		// NOTE: empty links are tested in the empty documents
		// NOTE: following link notations are tested so far:
		// {"key":"value", … }
		// {"key":{"name":"value","badges":["value", … ]}, … }
		ItemDocument itemDocument = jsonFetcher.getItemDocumentFromResource(
				"Item_SiteLinks.json", "Q1", this.uut);

		assertEquals(itemDocument.getSiteLinks(),
				testObjectFactory.createTestLinks());
	}

	// TODO expand test case
	@Test
	public void testClaims() throws JSONException, IOException {
		// NOTE: this combines multipe sub-tests
		// These sub-tests are grouped by the propertyId of the main snak
		// and this way also test the proper working of the statement groups
		// * novalue snaks, somevalue snaks
		// * statement ranks

		ItemDocument itemDocument = jsonFetcher.getItemDocumentFromResource(
				"Item_Claims.json", "Q1", this.uut);

		List<StatementGroup> statementGroup = testObjectFactory.createTestStatementGroups();
		assertEquals(itemDocument.getStatementGroups(),
				statementGroup);

	}

	// TODO improve
	@Test(expected = JSONException.class)
	public void testPropertyDocumentLacksDatatype() throws JSONException,
			IOException {
		jsonFetcher.getPropertyDocumentFromResource("NoEntityDocument.json",
				"P1", this.uut);
	}

	// TODO improve
	@Test
	public void testItemDocumentWithErrors() throws JSONException, IOException {
		jsonFetcher.getItemDocumentFromResource("MiscErrors.json", "Q1",
				this.uut);
		// FIXME this does not test anything (copied from earlier test file)
	}

	// TODO:
	// * (entity)
	// * Items only
	// * claims
	// * different snak types

}
