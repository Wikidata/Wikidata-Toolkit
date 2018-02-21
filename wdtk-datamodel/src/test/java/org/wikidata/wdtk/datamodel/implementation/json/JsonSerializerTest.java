package org.wikidata.wdtk.datamodel.implementation.json;

/*
 * #%L
 * Wikidata Toolkit Data Model
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

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.DatamodelConverter;
import org.wikidata.wdtk.datamodel.helpers.DatamodelMapper;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImplTest;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.implementation.TermedStatementDocumentImpl;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

public class JsonSerializerTest {

	static final DatamodelConverter datamodelConverter = new DatamodelConverter(
			new DataObjectFactoryImpl());

	@Test
	public void testSerializer() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		JsonSerializer serializer = new JsonSerializer(out);

		ItemDocument id1 = Datamodel.makeItemDocument(DataObjectFactoryImplTest
				.getTestItemIdValue(1), Collections
				.singletonList(Datamodel
						.makeMonolingualTextValue("Label1", "lang1")),
				Collections.<MonolingualTextValue> emptyList(), Collections
						.<MonolingualTextValue> emptyList(),
				DataObjectFactoryImplTest.getTestStatementGroups(1, 24, 1,
						EntityIdValue.ET_ITEM), Collections
						.<String, SiteLink> emptyMap(), 1234);
		ItemDocument id2 = Datamodel.makeItemDocument(DataObjectFactoryImplTest
				.getTestItemIdValue(2), Collections
				.<MonolingualTextValue> emptyList(), Collections
				.<MonolingualTextValue> emptyList(), Collections
				.<MonolingualTextValue> emptyList(), DataObjectFactoryImplTest
				.getTestStatementGroups(2, 23, 1, EntityIdValue.ET_ITEM),
				Collections.singletonMap(
						"enwiki",
						Datamodel.makeSiteLink("Title2", "enwiki",
								Collections.<String>emptyList())), 0);
		PropertyDocument pd1 = Datamodel.makePropertyDocument(
				DataObjectFactoryImplTest.getTestPropertyIdValue(1),
				Collections.<MonolingualTextValue> emptyList(), Collections
						.<MonolingualTextValue> emptyList(), Collections
						.singletonList(Datamodel
								.makeMonolingualTextValue("Alias1", "lang1")),
				Collections.<StatementGroup> emptyList(), Datamodel
						.makeDatatypeIdValue(DatatypeIdValue.DT_COMMONS_MEDIA),
				3456);

		serializer.open();
		serializer.processItemDocument(id1);
		serializer.processItemDocument(id2);
		serializer.processPropertyDocument(pd1);
		serializer.close();

		ArrayList<EntityDocument> inputDocuments = new ArrayList<>();
		inputDocuments.add(id1);
		inputDocuments.add(id2);
		inputDocuments.add(pd1);

		ArrayList<EntityDocument> outputDocuments = new ArrayList<>();

		ObjectMapper mapper = new DatamodelMapper("foo:");
		ObjectReader documentReader = mapper
				.reader(TermedStatementDocumentImpl.class);

		MappingIterator<TermedStatementDocumentImpl> documentIterator = documentReader
				.readValues(out.toString());
		while (documentIterator.hasNextValue()) {
			TermedStatementDocumentImpl document = documentIterator
					.nextValue();
			outputDocuments.add(document);
		}
		documentIterator.close();

		for (int i = 0; i < outputDocuments.size(); i++) {
			assertEquals(inputDocuments.get(i), outputDocuments.get(i));
		}
		assertEquals(serializer.getEntityDocumentCount(), 3);
	}

	@Test
	public void testItemDocumentToJson() {
		ItemDocument id = Datamodel.makeItemDocument(
				Datamodel.makeWikidataItemIdValue(JsonTestData.TEST_ITEM_ID),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<StatementGroup> emptyList(),
				Collections.<String, SiteLink> emptyMap());

		String result1 = JsonSerializer.getJsonString(id);
		String result2 = JsonSerializer.getJsonString(datamodelConverter
				.copy(id));

		JsonComparator.compareJsonStrings(JsonTestData.JSON_WRAPPED_ITEMID,
				result1);
		JsonComparator.compareJsonStrings(JsonTestData.JSON_WRAPPED_ITEMID,
				result2);
	}

	@Test
	public void testPropertyDocumentToJson() {
		PropertyDocument pd = Datamodel.makePropertyDocument(
				Datamodel.makeWikidataPropertyIdValue("P1"),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<StatementGroup> emptyList(),
				Datamodel.makeDatatypeIdValue(DatatypeIdValue.DT_ITEM));

		String result1 = JsonSerializer.getJsonString(pd);
		String result2 = JsonSerializer.getJsonString(datamodelConverter
				.copy(pd));

		String json = "{\"id\":\"P1\",\"aliases\":{},\"labels\":{},\"descriptions\":{},\"claims\":{},\"type\":\"property\", \"datatype\":\"wikibase-item\"}";

		JsonComparator.compareJsonStrings(json, result1);
		JsonComparator.compareJsonStrings(json, result2);
	}

	@Test
	public void testStatementToJson() {
		Statement s = Datamodel
				.makeStatement(
						Datamodel.makeClaim(
								ItemIdValue.NULL,
								Datamodel.makeNoValueSnak(Datamodel
										.makeWikidataPropertyIdValue(JsonTestData.TEST_PROPERTY_ID)),
								Collections.<SnakGroup> emptyList()),
						Collections.<Reference> emptyList(),
						StatementRank.NORMAL, JsonTestData.TEST_STATEMENT_ID);

		String result1 = JsonSerializer.getJsonString(s);
		String result2 = JsonSerializer.getJsonString(datamodelConverter
				.copy(s));

		JsonComparator.compareJsonStrings(JsonTestData.JSON_NOVALUE_STATEMENT,
				result1);
		JsonComparator.compareJsonStrings(JsonTestData.JSON_NOVALUE_STATEMENT,
				result2);
	}

	@Test
	public void testJacksonObjectToJsonError() {
		Object obj = new Object() {
			@SuppressWarnings("unused")
			public String getData() throws JsonGenerationException {
				throw new JsonGenerationException("Test exception");
			}
		};

		String result = JsonSerializer.jacksonObjectToString(obj);
		assertEquals(null, result);
	}
}
