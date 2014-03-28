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
import static org.junit.Assert.fail;

import java.io.IOException;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
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
	private static final String BASE_IRI = "test";
	private static final TestObjectFactory testObjectFactory = new TestObjectFactory();

	private JsonConverter uut; // unit under test

	@Before
	public void setUp() {
		this.uut = new JsonConverter(BASE_IRI,
				new DataObjectFactoryImpl());
	}

	@Test
	public void testEmptyProperty() throws JSONException, IOException {
		PropertyDocument propertyDocument = jsonFetcher
				.getPropertyDocumentFromResource("EmptyProperty.json", "P1",
						this.uut);

		PropertyDocument emptyPropertyDocument = testObjectFactory
				.createEmptyPropertyDocument(BASE_IRI);

		assertEquals(propertyDocument, emptyPropertyDocument);
	}

	@Test
	public void testEmptyItem() throws JSONException, IOException {
		ItemDocument itemDocument = jsonFetcher.getItemDocumentFromResource(
				"EmptyItem.json", "Q1", this.uut);

		ItemDocument emptyItemDocument = testObjectFactory
				.createEmptyItemDocument(BASE_IRI);

		assertEquals(itemDocument, emptyItemDocument);
	}

	@Test
	public void testLabels() throws JSONException, IOException{
		// NOTE: empty labels are tested in the empty documents
		// NOTE: only one label notation is known so far
		// which is {"key":"value", … } 
		ItemDocument itemDocument = jsonFetcher.getItemDocumentFromResource(
				"Item_Labels.json", "Q1", this.uut);
		
		assertEquals(itemDocument.getLabels(), testObjectFactory.createTestLabels());
	}
	
	@Test
	public void testDescriptions(){
		// TODO complete
		fail("Empty test");
	}
	
	@Test
	public void testAliases(){
		// TODO complete
		fail("Empty test");
	}
	
	@Test
	public void testLinks(){
		// TODO complete
		fail("Empty test");
	}
	
	// TODO rework
	@Test
	public void testClaims() throws JSONException, IOException {
		jsonFetcher.getItemDocumentFromResource("GlobalCoordinates.json", "Q1",
				this.uut);
		jsonFetcher.getItemDocumentFromResource("StatementRanks.json", "Q1", this.uut);
		jsonFetcher.getItemDocumentFromResource("SnakTypes.json", "Q1", this.uut);
		// FIXME this does not test anything (copied from earlier test file)
	}
	
	@Test
	public void testDatatype(){
		// TODO complete
		fail("Empty test");
	}

	// TODO improve
	@Test(expected = JSONException.class)
	public void testPropertyDocumentLacksDatatype() throws JSONException,
			IOException {
		jsonFetcher.getPropertyDocumentFromResource("NoEntityDocument.json", "P1", this.uut);
	}

	// TODO improve
	@Test(expected = JSONException.class)
	public void testItemDocumentWithErrors() throws JSONException, IOException {
		jsonFetcher.getItemDocumentFromResource("MiscErrors.json", "Q1", this.uut);
	}

	// TODO:
	// * Both
	// 		* label notations
	// 			* labels as strings
	// 			* labels as objects
	// 		* description notations
	// 		* aliases
	// 		* (entity)
	// * Items only
	// 		* links
	// 		* claims
	//			* different ranks
	// 			* different snak types
	// * Properties only
	// 		* datatype

}
