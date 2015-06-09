package org.wikidata.wdtk.rdf;

/*
 * #%L
 * Wikidata Toolkit RDF
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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.DocumentDataFilter;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;

public class PropertyRegisterTest {

	PropertyRegister wikidataPropertyRegister;

	final String siteIri = "http://www.wikidata.org/";

	final TestObjectFactory objectFactory = new TestObjectFactory();
	final DataObjectFactory dataObjectFactory = new DataObjectFactoryImpl();

	@Before
	public void setUp() throws Exception {
		List<String> propertyIds = new ArrayList<String>();
		propertyIds.add("P434");
		for (int i = 1; i < 50; i++) {
			propertyIds.add("P" + i);
		}
		Map<String, EntityDocument> mockResult = new HashMap<String, EntityDocument>();
		List<StatementGroup> mockStatementGroups = new ArrayList<StatementGroup>();
		List<Statement> mockStatements = new ArrayList<Statement>();
		mockStatements
				.add(dataObjectFactory.getStatement(
						dataObjectFactory.getClaim(
								dataObjectFactory.getPropertyIdValue("P434",
										this.siteIri),
								dataObjectFactory.getValueSnak(
										dataObjectFactory.getPropertyIdValue(
												"P1921", this.siteIri),
										dataObjectFactory
												.getStringValue("http://musicbrainz.org/$1/artist")),
								Collections.<SnakGroup> emptyList()),
						Collections.<Reference> emptyList(),
						StatementRank.NORMAL, "000"));
		mockStatementGroups.add(dataObjectFactory
				.getStatementGroup(mockStatements));
		mockResult
				.put("P434", dataObjectFactory.getPropertyDocument(
						dataObjectFactory.getPropertyIdValue("P434",
								this.siteIri), Collections
								.<MonolingualTextValue> emptyList(),
						Collections.<MonolingualTextValue> emptyList(),
						Collections.<MonolingualTextValue> emptyList(),
						mockStatementGroups, dataObjectFactory
								.getDatatypeIdValue(DatatypeIdValue.DT_STRING)));
		this.wikidataPropertyRegister = PropertyRegister
				.getWikidataPropertyRegister();
		WikibaseDataFetcher dataFetcher = Mockito
				.mock(WikibaseDataFetcher.class);
		Mockito.when(dataFetcher.getEntityDocuments(propertyIds)).thenReturn(
				mockResult);
		Mockito.when(dataFetcher.getFilter()).thenReturn(
				new DocumentDataFilter());
		dataFetcher.getEntityDocuments(propertyIds);
		this.wikidataPropertyRegister.dataFetcher = dataFetcher;
	}

	@Test
	public void testGetWikidataPropertyRegister() {
		assertEquals("P1921",
				this.wikidataPropertyRegister.uriPatternPropertyId);
	}

	@Test
	public void testFetchPropertyInformation() {
		this.wikidataPropertyRegister
				.fetchPropertyInformation(this.dataObjectFactory
						.getPropertyIdValue("P434", this.siteIri));
		assertEquals(50, this.wikidataPropertyRegister.lowestPropertyIdNumber);
		assertTrue(this.wikidataPropertyRegister.datatypes.keySet().contains(
				"P434"));
		assertEquals("http://musicbrainz.org/$1/artist",
				this.wikidataPropertyRegister.uriPatterns.get("P434"));

	}

	@Test
	public void testGetPropertyType() {
		assertEquals(DatatypeIdValue.DT_STRING,
				this.wikidataPropertyRegister.getPropertyType(dataObjectFactory
						.getPropertyIdValue("P434", this.siteIri)));
	}

	@Test
	public void testSetPropertyTypeFromEntityIdValue() {
		assertEquals(
				this.wikidataPropertyRegister.setPropertyTypeFromEntityIdValue(
						this.dataObjectFactory.getPropertyIdValue("P1001",
								"http://www.wikidata.org/property"),
						this.dataObjectFactory.getItemIdValue("Q20",
								"http://www.wikidata.org/entity/")),
				DatatypeIdValue.DT_ITEM);
	}
	
	@Test
	public void testSetPropertyTypeFromStringValue() {
		assertEquals(this.wikidataPropertyRegister.setPropertyTypeFromStringValue(
				dataObjectFactory.getPropertyIdValue("P434",
						"http://www.wikidata.org/property"), dataObjectFactory
						.getStringValue("http://musicbrainz.org/$1/artist")),
				"http://www.wikidata.org/ontology#propertyTypeString");
	}

}
