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

package org.wikidata.wdtk.datamodel.helpers;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.implementation.EntityDocumentImpl;
import org.wikidata.wdtk.datamodel.implementation.JsonComparator;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

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
	public void testItemDocumentToJson() throws JsonProcessingException {
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
	public void testPropertyDocumentToJson() throws JsonProcessingException {
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
	public void testStatementToJson() throws JsonProcessingException {
		Statement s = Datamodel.makeStatement(ItemIdValue.NULL,
				Datamodel.makeNoValueSnak(Datamodel.makeWikidataPropertyIdValue("P1")),
				Collections.emptyList(), Collections.emptyList(),
				StatementRank.NORMAL, "MyId");
		String json = "{\"rank\":\"normal\",\"id\":\"MyId\",\"mainsnak\":{\"property\":\"P1\",\"snaktype\":\"novalue\"},\"type\":\"statement\"}";
		JsonComparator.compareJsonStrings(json, JsonSerializer.getJsonString(s));
	}

	@Test
	public void testJacksonObjectToJsonError() {
		ItemDocument obj = new ItemDocument() {

			@Override
			public List<StatementGroup> getStatementGroups() {
				return null;
			}

			@Override
			public long getRevisionId() {
				return 0;
			}

			@Override
			public Map<String, MonolingualTextValue> getLabels() {
				return null;
			}

			@Override
			public Map<String, MonolingualTextValue> getDescriptions() {
				return null;
			}

			@Override
			public Map<String, List<MonolingualTextValue>> getAliases() {
				return null;
			}

			@Override
			public ItemDocument withEntityId(ItemIdValue newEntityId) {
				return null;
			}

			@Override
			public ItemDocument withoutStatementIds(Set<String> statementIds) {
				return null;
			}

			@Override
			public ItemDocument withStatement(Statement statement) {
				return null;
			}

			@Override
			public ItemDocument withRevisionId(long newRevisionId) {
				return null;
			}

			@Override
			public ItemDocument withLabel(MonolingualTextValue newLabel) {
				return null;
			}

			@Override
			public ItemDocument withDescription(MonolingualTextValue newDescription) {
				return null;
			}

			@Override
			public ItemDocument withAliases(String language, List<MonolingualTextValue> aliases) {
				return null;
			}

			@Override
			public Map<String, SiteLink> getSiteLinks() {
				return null;
			}

			@Override
			public ItemIdValue getEntityId() {
				return null;
			}
		};

		assertThrows(JsonProcessingException.class, () -> JsonSerializer.getJsonString(obj));
	}
}
