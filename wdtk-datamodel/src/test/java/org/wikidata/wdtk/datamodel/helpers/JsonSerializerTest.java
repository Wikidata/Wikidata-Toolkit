package org.wikidata.wdtk.datamodel.helpers;

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

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.implementation.EntityDocumentImpl;
import org.wikidata.wdtk.datamodel.implementation.JsonComparator;
import org.wikidata.wdtk.datamodel.interfaces.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class JsonSerializerTest {

	@Test
	public void testSerializer() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		JsonSerializer serializer = new JsonSerializer(out);

		ItemIdValue qid1 = Datamodel.makeWikidataItemIdValue("Q1");
		ItemDocument id1 = Datamodel.makeItemDocument(
				qid1,
				Collections.singletonList(Datamodel.makeMonolingualTextValue("Label1", "lang1")),
				Collections.emptyList(), Collections.emptyList(),
				Collections.singletonList(Datamodel.makeStatementGroup(Collections.singletonList(
						Datamodel.makeStatement(qid1,
								Datamodel.makeNoValueSnak(Datamodel.makeWikidataPropertyIdValue("P42")),
								Collections.emptyList(), Collections.emptyList(),
								StatementRank.NORMAL, "MyId"
				)))), Collections.emptyMap(), 1234);
		ItemDocument id2 = Datamodel.makeItemDocument(
				Datamodel.makeWikidataItemIdValue("Q2"),
				Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
				Collections.emptyList(), Collections.emptyMap(), 12);
		PropertyDocument pd1 = Datamodel.makePropertyDocument(
				Datamodel.makeWikidataPropertyIdValue("P1"),
				Collections.emptyList(), Collections.emptyList(),
				Collections.singletonList(Datamodel.makeMonolingualTextValue("Alias1", "lang1")),
				Collections.emptyList(), Datamodel.makeDatatypeIdValue(DatatypeIdValue.DT_COMMONS_MEDIA),
				3456);

		serializer.open();
		serializer.processItemDocument(id1);
		serializer.processItemDocument(id2);
		serializer.processPropertyDocument(pd1);
		serializer.close();

		List<EntityDocument> inputDocuments = Arrays.asList(id1, id2, pd1);

		List<EntityDocument> outputDocuments = new ArrayList<>();

		ObjectMapper mapper = new DatamodelMapper("http://www.wikidata.org/entity/");
		ObjectReader documentReader = mapper.readerFor(EntityDocumentImpl.class);

		MappingIterator<EntityDocument> documentIterator = documentReader.readValues(out.toString());
		while (documentIterator.hasNextValue()) {
			outputDocuments.add(documentIterator.nextValue());
		}
		documentIterator.close();

		assertEquals(inputDocuments, outputDocuments);
	}

	@Test
	public void testItemDocumentToJson() {
		ItemDocument id = Datamodel.makeItemDocument(
				Datamodel.makeWikidataItemIdValue("Q42"),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyMap());
		String json = "{\"type\":\"item\",\"id\":\"Q42\",\"labels\":{},\"descriptions\":{},\"aliases\":{},\"claims\":{},\"sitelinks\":{}}";
		JsonComparator.compareJsonStrings(json, JsonSerializer.getJsonString(id));
	}

	@Test
	public void testPropertyDocumentToJson() {
		PropertyDocument pd = Datamodel.makePropertyDocument(
				Datamodel.makeWikidataPropertyIdValue("P1"),
				Collections. emptyList(),
				Collections. emptyList(),
				Collections. emptyList(),
				Collections. emptyList(),
				Datamodel.makeDatatypeIdValue(DatatypeIdValue.DT_ITEM));
		String json = "{\"id\":\"P1\",\"aliases\":{},\"labels\":{},\"descriptions\":{},\"claims\":{},\"type\":\"property\", \"datatype\":\"wikibase-item\"}";
		JsonComparator.compareJsonStrings(json, JsonSerializer.getJsonString(pd));
	}

	@Test
	public void testStatementToJson() {
		Statement s = Datamodel.makeStatement(ItemIdValue.NULL,
				Datamodel.makeNoValueSnak(Datamodel.makeWikidataPropertyIdValue("P1")),
				Collections.emptyList(), Collections.emptyList(),
				StatementRank.NORMAL, "MyId");
		String json = "{\"rank\":\"normal\",\"id\":\"MyId\",\"mainsnak\":{\"property\":\"P1\",\"snaktype\":\"novalue\"},\"type\":\"statement\"}";
		JsonComparator.compareJsonStrings(json, JsonSerializer.getJsonString(s));
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
		assertNull(result);
	}
}
