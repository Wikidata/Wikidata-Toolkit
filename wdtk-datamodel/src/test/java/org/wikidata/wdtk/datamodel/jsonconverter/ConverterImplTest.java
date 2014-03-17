package org.wikidata.wdtk.datamodel.jsonconverter;

/*
 * #%L
 * Wikidata Toolkit Data Model
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

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.Claim;
import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.NoValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

public class ConverterImplTest {

	final DataObjectFactory factory = new DataObjectFactoryImpl();;
	final TestObjectFactory objectFactory = new TestObjectFactory();
	final ConverterImpl converter = new ConverterImpl();

	public void compareJSONObjects(JSONObject obj1, JSONObject obj2) {
		for (Object key : obj1.keySet()) {
			if (obj1.get((String) key) instanceof JSONObject) {
				compareJSONObjects(obj1.getJSONObject((String) key),
						obj2.getJSONObject((String) key));
			} else if (obj1.get((String) key) instanceof JSONArray) {
				JSONArray arrayObj1 = obj1.getJSONArray((String) key);
				JSONArray arrayObj2 = obj2.getJSONArray((String) key);
				compareJSONArrays(arrayObj1, arrayObj2);
			} else {
				assertTrue(comparableValue(obj1.get((String) key)).equals(
						comparableValue(obj2.get((String) key))));
			}
		}
	}

	public void compareJSONArrays(JSONArray array1, JSONArray array2) {
		for (int index = 0; index < array1.length(); index++) {
			if (array1.get(index) instanceof JSONObject) {
				compareJSONObjects(array1.getJSONObject(index),
						array2.getJSONObject(index));
			} else if (array1.get(index) instanceof JSONArray) {
				JSONArray arrayElem1 = array1.getJSONArray(index);
				JSONArray arrayElem2 = array2.getJSONArray(index);
				compareJSONArrays(arrayElem1, arrayElem2);
			} else {
				assertTrue(array1.get(index).equals(array2.get(index)));
			}
		}
	}

	public Object comparableValue(Object val) {
		long result;
		if (val instanceof BigDecimal) {
			result = ((BigDecimal) val).longValue();
			return result;
		} else if (val instanceof Integer) {
			result = ((Integer) val).longValue();
			return result;
		} else if (val instanceof Byte) {
			result = ((Byte) val).longValue();
			return result;
		}

		return val;
	}

	@Test
	public void testCompareJSONObjects() {
		JSONObject obj1 = new JSONObject(
				"{\"numeric-id\":\"Q200\", \"array\":[\"a\", \"b\"], \"entity-type\":\"item\"}");
		JSONObject obj2 = new JSONObject(
				"{\"array\":[\"a\", \"b\"], \"entity-type\":\"item\", \"numeric-id\":\"Q200\"}");
		compareJSONObjects(obj1, obj2);
	}

	@Test
	public void testOrder() {
		JSONArray array = new JSONArray();
		array.put("one");
		array.put("two");
		array.put("three");
		assertEquals(array.toString(), "[\"one\",\"two\",\"three\"]");
	}

	@Test
	public void testConvertItemDocumentToJson() throws JSONException {

		List<Statement> statements = new ArrayList<Statement>();
		List<StatementGroup> statementGroups = new ArrayList<StatementGroup>();
		Claim claim = factory.getClaim(factory.getItemIdValue("Q10", "base/"),
				factory.getNoValueSnak(factory.getPropertyIdValue("P11",
						"base/")), Collections.<Snak> emptyList());
		statements.add(factory.getStatement(claim,
				Collections.<Reference> emptyList(), StatementRank.NORMAL,
				"none"));

		statementGroups.add(factory.getStatementGroup(statements));

		statements = new ArrayList<Statement>();

		Claim claim2 = factory.getClaim(factory.getItemIdValue("Q10", "base/"),
				objectFactory.createValueSnakTime(17, "P1040"),
				objectFactory.createQualifiers());
		Claim claim3 = factory.getClaim(factory.getItemIdValue("Q10", "base/"),
				objectFactory.createValueSnakTime(3, "P1040"),
				Collections.<Snak> emptyList());

		statements
				.add(factory.getStatement(claim2,
						objectFactory.createReferences(), StatementRank.NORMAL,
						"none2"));
		statements.add(factory.getStatement(claim3,
				Collections.<Reference> emptyList(), StatementRank.NORMAL,
				"none"));
		statementGroups.add(factory.getStatementGroup(statements));
		compareJSONObjects(converter.visit(factory
				.getItemDocument(factory.getItemIdValue("Q10", "base/"),
						objectFactory.createLabels(),
						objectFactory.createDescriptions(),
						objectFactory.createAliases(), statementGroups,
						objectFactory.createSiteLinks())), new JSONObject(
				TestRessources.ITEM_DOCUMENT_REPRES));
	}

	@Test
	public void testConvertPropertyDocumentToJson() throws JSONException {
		PropertyDocument document = factory.getPropertyDocument(
				factory.getPropertyIdValue("P42", "base/"),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				factory.getDatatypeIdValue("string"));
		compareJSONObjects(converter.visit(document),
				new JSONObject(TestRessources.EMPTY_PROPERTY_DOCUMENT_REPRES));
		compareJSONObjects(
				converter.visit(objectFactory
						.createEmptyPropertyDocument()),
				new JSONObject(
						"{\"id\":\"P1\",\"title\":\"P1\",\"type\":\"property\"}"));

	}

	@Test
	public void testConvertValueSnakToJson() {
		// ItemIdValue:
		ValueSnak snak = objectFactory.createValueSnakItemIdValue("P132",
				"Q233");
		compareJSONObjects(converter.convertSnakToJson(snak), new JSONObject(
				TestRessources.VALUE_SNAK_ITEM_ID_VALUE_REPRES));
		// String
		snak = objectFactory.createValueSnakStringValue("P132");
		compareJSONObjects(converter.convertSnakToJson(snak), new JSONObject(
				TestRessources.VALUE_SNAK_STRING_VALUE_REPRES));
		// Globe-Coordinates
		snak = objectFactory.createValueSnakCoordinatesValue("P132");
		compareJSONObjects(converter.convertSnakToJson(snak), new JSONObject(
				TestRessources.VALUE_SNAK_GLOBE_COORDINATES_VALUE_REPRES));
		// Quantity
		snak = objectFactory.createValueSnakQuantityValue("P231");
		compareJSONObjects(converter.convertSnakToJson(snak), new JSONObject(
				TestRessources.VALUE_SNAK_QUANTITY_VALUE_REPRES));
	}

	@Test
	public void testConvertSomeValueSnakToJson() {
		SomeValueSnak snak = objectFactory.createSomeValueSnak("P1231");
		compareJSONObjects(converter.convertSnakToJson(snak), new JSONObject(
				TestRessources.SOME_VALUE_SNAK_REPRES));

	}

	@Test
	public void testConvertNoValueSnakToJson() {
		NoValueSnak snak = factory.getNoValueSnak(factory.getPropertyIdValue(
				"P10", "test/"));
		compareJSONObjects(converter.convertSnakToJson(snak),
				converter.visit(snak));
		compareJSONObjects(converter.convertSnakToJson(snak), new JSONObject(
				TestRessources.NO_VALUE_SNAK_REPRES));
	}

	@Test
	public void testConvertClaimToJson() {
		ValueSnak snak = objectFactory.createValueSnakTime(42, "P129");
		Claim claim = objectFactory.createClaim("Q31", snak);
		assertEquals(converter.visit(claim).toString(),
				TestRessources.CLAIM_REPRES);

	}

	@Test
	public void testConvertReferenceToJson() {
		Reference ref = objectFactory.createReference();
		compareJSONObjects(converter.visit(ref),
				new JSONObject(TestRessources.REFERENCE_REPRES));
	}

	@Test
	public void testConvertStatementToJson() {
		Statement statement = objectFactory.createStatement("Q100", "P131");
		compareJSONObjects(converter.visit(statement),
				new JSONObject(TestRessources.STATEMENT_REPRES));

	}

	@Test
	public void testConvertStatementGroupToJson() {
		StatementGroup group = objectFactory.createStatementGroup();
		compareJSONArrays(converter.convertStatementGroupToJson(group),
				new JSONArray(TestRessources.STATEMENT_GROUP_REPRES));

	}

	@Test
	public void testConvertPropertyIdValueToJson() {
		PropertyIdValue value = objectFactory.createPropertyIdValue("P200");
		assertEquals(converter.visit(value).toString(),
				TestRessources.PROPERTY_ID_VALUE_REPRES);
		assertEquals(converter.convertEntityIdValueToJson((EntityIdValue)value).toString(),
				TestRessources.ENTITY_ID_VALUE_REPRES_2);

	}

	@Test
	public void testConvertItemIdValueToJson() {
		ItemIdValue value = objectFactory.createItemIdValue("Q200");
		assertEquals(converter.visit(value).toString(),
				TestRessources.ITEM_ID_VALUE_REPRES);

		assertEquals(converter.convertEntityIdValueToJson(value).toString(),
				TestRessources.ENTITY_ID_VALUE_REPRES);
	}

	@Test
	public void testConvertMonolingualTextValue() {
		MonolingualTextValue value = factory.getMonolingualTextValue(
				"some text in a language lc", "lc");
		compareJSONObjects(converter.visit(value),
				new JSONObject(TestRessources.MONOLINGUAL_TEXT_VALUE_REPRES));

	}

	@Test
	public void testConvertSiteLinks() {
		SiteLink siteLink = factory.getSiteLink("title", "siteKey", "baseIri",
				Collections.<String> emptyList());
		compareJSONObjects(converter.visit(siteLink),
				new JSONObject(TestRessources.SITE_LINK_REPRES));

	}

}
