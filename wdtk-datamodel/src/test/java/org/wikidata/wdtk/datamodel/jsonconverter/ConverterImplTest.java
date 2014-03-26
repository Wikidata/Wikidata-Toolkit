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

import java.io.IOException;
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
import org.wikidata.wdtk.testing.MockStringContentFactory;

public class ConverterImplTest {

	final static String ITEM_DOCUMENT_REPRES = "ItemDocument.json";
	final static String STATEMENT_REPRES = "Statement.json";
	final static String STATEMENT_GROUP_REPRES = "StatementGroup.json";
	final static String CLAIM_REPRES = "Claim.json";
	final static String REFERENCE_REPRES = "Reference.json";

	final static String EMPTY_PROPERTY_DOCUMENT_REPRES = "{\"id\":\"P42\",\"title\":\"P42\",\"type\":\"property\"}";
	final static String ITEM_ID_VALUE_REPRES = "{\"entity-type\":\"item\",\"numeric-id\":\"Q200\"}";
	final static String PROPERTY_ID_VALUE_REPRES = "{\"entity-type\":\"property\",\"numeric-id\":\"P200\"}";
	final static String ENTITY_ID_VALUE_REPRES = "{\"value\":{\"entity-type\":\"item\",\"numeric-id\":\"Q200\"},\"type\":\"wikibase-entityid\"}";
	final static String ENTITY_ID_VALUE_REPRES_2 = "{\"value\":{\"entity-type\":\"property\",\"numeric-id\":\"P200\"},\"type\":\"wikibase-entityid\"}";
	final static String VALUE_SNAK_ITEM_ID_VALUE_REPRES = "{\"property\":\"P132\",\"snaktype\":\"value\",\"datavalue\":{\"value\":{\"entity-type\":\"item\",\"numeric-id\":\"Q233\"},\"type\":\"wikibase-entityid\"}}";
	final static String VALUE_SNAK_STRING_VALUE_REPRES = "{\"property\":\"P132\",\"snaktype\":\"value\",\"datavalue\":{\"value\":\"TestString\",\"type\":\"string\"}}";
	final static String VALUE_SNAK_GLOBE_COORDINATES_VALUE_REPRES = "{\"property\":\"P132\",\"snaktype\":\"value\",\"datavalue\":{\"value\":{\"precision\":0.016666667,\"longitude\":2.1314E-5,\"latitude\":2.13124E-4,\"globe\":\"http://www.wikidata.org/entity/Q2\"},\"type\":\"globecoordinate\"}}";
	final static String VALUE_SNAK_QUANTITY_VALUE_REPRES = "{\"property\":\"P231\",\"snaktype\":\"value\",\"datavalue\":{\"value\":{\"amount\":\"+3\",\"unit\":\"1\",\"lowerBound\":\"+3\",\"upperBound\":\"+3\"},\"type\":\"quantity\"}}";
	final static String SOME_VALUE_SNAK_REPRES = "{\"property\":\"P1231\",\"snaktype\":\"somevalue\"}";
	final static String NO_VALUE_SNAK_REPRES = "{\"property\":\"P10\",\"snaktype\":\"novalue\"}";
	final static String MONOLINGUAL_TEXT_VALUE_REPRES = "{\"value\":\"some text in a language lc\",\"language\":\"lc\"}";
	final static String SITE_LINK_REPRES = "{\"site\":\"siteKey\",\"badges\":[],\"title\":\"title\"}";

	final DataObjectFactory factory = new DataObjectFactoryImpl();;
	final TestObjectFactory objectFactory = new TestObjectFactory();
	final ConverterImpl converter = new ConverterImpl();

	/**
	 * Compares obj1 with obj2. If the content is equal (same keys, same values)
	 * nothing happens. Otherwise the function will cause a Fail.
	 * 
	 * @param obj1
	 * @param obj2
	 */
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
				assertTrue(comparableObject(obj1.get((String) key)).equals(
						comparableObject(obj2.get((String) key))));
			}
		}
	}

	/**
	 * Compares array1 with array2. If the content is equal (same values in the
	 * same order) nothing happens. Otherwise the function will cause a Fail.
	 * 
	 * @param array1
	 * @param array2
	 */
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
				assertTrue(comparableObject(array1.get(index)).equals(
						comparableObject(array2.get(index))));
			}
		}
	}

	/**
	 * Sometimes values in the key-value-pairs of a json file are not clearly
	 * assignable to a data type. This function convert these values in a
	 * comparable type.
	 * 
	 * @param val
	 * 
	 * @return comparable object
	 */
	public Object comparableObject(Object val) {
		if (val instanceof Integer) {
			return ((Integer) val).longValue();
		}

		return val;
	}

	/**
	 * Loads the resource file with fileName and returns the content as a
	 * JSONObject.
	 * 
	 * @param fileName
	 * @return JSONObject of fileName
	 * @throws JSONException
	 * @throws IOException
	 */
	public JSONObject getResourceFromFile(String fileName)
			throws JSONException, IOException {
		return new JSONObject(MockStringContentFactory.getStringFromUrl(this
				.getClass().getResource("/" + fileName)));
	}

	/**
	 * Loads the resource file with fileName and returns the content as a
	 * JSONArray.
	 * 
	 * @param fileName
	 * @return JSONArray of fileName
	 * @throws JSONException
	 * @throws IOException
	 */
	public JSONArray getArrayResourceFromFile(String fileName)
			throws JSONException, IOException {
		return new JSONArray(MockStringContentFactory.getStringFromUrl(this
				.getClass().getResource("/" + fileName)));
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
	public void testVisitItemDocument() throws JSONException, IOException {
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
				objectFactory.createValueSnakTimeValue("P1040"),
				objectFactory.createQualifiers());
		Claim claim3 = factory.getClaim(factory.getItemIdValue("Q10", "base/"),
				objectFactory.createValueSnakStringValue("P1040"),
				Collections.<Snak> emptyList());

		statements
				.add(factory.getStatement(claim2,
						objectFactory.createReferences(), StatementRank.NORMAL,
						"none2"));
		statements.add(factory.getStatement(claim3,
				Collections.<Reference> emptyList(), StatementRank.NORMAL,
				"none"));
		statementGroups.add(factory.getStatementGroup(statements));

		compareJSONObjects(converter.visit(factory.getItemDocument(
				factory.getItemIdValue("Q10", "base/"),
				objectFactory.createLabels(),
				objectFactory.createDescriptions(),
				objectFactory.createAliases(), statementGroups,
				objectFactory.createSiteLinks())),
				getResourceFromFile(ITEM_DOCUMENT_REPRES));
	}

	@Test
	public void testvisitPropertyDocument() throws JSONException {
		PropertyDocument document = factory.getPropertyDocument(
				factory.getPropertyIdValue("P42", "base/"),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				factory.getDatatypeIdValue("string"));
		compareJSONObjects(converter.visit(document), new JSONObject(
				EMPTY_PROPERTY_DOCUMENT_REPRES));
		compareJSONObjects(converter.visit(objectFactory
				.createEmptyPropertyDocument("test")), new JSONObject(
				"{\"id\":\"P1\",\"title\":\"P1\",\"type\":\"property\"}"));

	}

	@Test
	public void testVisitValueSnakItemIdValue() {
		ValueSnak snak = objectFactory.createValueSnakItemIdValue("P132",
				"Q233");
		compareJSONObjects(converter.convertSnakToJson(snak), new JSONObject(
				VALUE_SNAK_ITEM_ID_VALUE_REPRES));
	}

	@Test
	public void testVisitValueSnakStringValue() {
		ValueSnak snak = objectFactory.createValueSnakStringValue("P132");
		compareJSONObjects(converter.convertSnakToJson(snak), new JSONObject(
				VALUE_SNAK_STRING_VALUE_REPRES));
	}

	@Test
	public void testVisitValueSnakGlobeCoordinatesValue() {
		ValueSnak snak = objectFactory.createValueSnakGlobeCoordinatesValue("P132");
		compareJSONObjects(converter.convertSnakToJson(snak), new JSONObject(
				VALUE_SNAK_GLOBE_COORDINATES_VALUE_REPRES));
	}

	@Test
	public void testVisitValueSnakQuantityValue() {
		ValueSnak snak = objectFactory.createValueSnakQuantityValue("P231");
		compareJSONObjects(converter.convertSnakToJson(snak), new JSONObject(
				VALUE_SNAK_QUANTITY_VALUE_REPRES));
	}

	@Test
	public void testVisitConvertSomeValueSnak() {
		SomeValueSnak snak = objectFactory.createSomeValueSnak("P1231");
		compareJSONObjects(converter.convertSnakToJson(snak), new JSONObject(
				SOME_VALUE_SNAK_REPRES));

	}

	@Test
	public void testVisitNoValueSnak() {
		NoValueSnak snak = factory.getNoValueSnak(factory.getPropertyIdValue(
				"P10", "test/"));
		compareJSONObjects(converter.convertSnakToJson(snak),
				converter.visit(snak));
		compareJSONObjects(converter.convertSnakToJson(snak), new JSONObject(
				NO_VALUE_SNAK_REPRES));
	}

	@Test
	public void testVisitClaim() throws JSONException, IOException {
		ValueSnak snak = objectFactory.createValueSnakTimeValue("P129");
		Claim claim = objectFactory.createClaim("Q31", snak);
		compareJSONObjects(converter.visit(claim),
				getResourceFromFile(CLAIM_REPRES));

	}

	@Test
	public void testVisitReference() throws JSONException, IOException {
		Reference ref = objectFactory.createReference();
		System.out.println(converter.visit(ref));
		compareJSONObjects(converter.visit(ref),
				getResourceFromFile(REFERENCE_REPRES));
	}

	@Test
	public void testVisitStatement() throws JSONException, IOException {
		Statement statement = objectFactory.createStatement("Q100", "P131");
		compareJSONObjects(converter.visit(statement),
				getResourceFromFile(STATEMENT_REPRES));

	}

	@Test
	public void testConvertStatementGroupToJson() throws JSONException,
			IOException {
		StatementGroup group = objectFactory.createStatementGroup();
		compareJSONArrays(converter.convertStatementGroupToJson(group),
				getArrayResourceFromFile(STATEMENT_GROUP_REPRES));

	}

	@Test
	public void testVisitPropertyIdValue() {
		PropertyIdValue value = objectFactory.createPropertyIdValue("P200");
		assertEquals(converter.visit(value).toString(),
				PROPERTY_ID_VALUE_REPRES);
		assertEquals(converter
				.convertEntityIdValueToJson((EntityIdValue) value).toString(),
				ENTITY_ID_VALUE_REPRES_2);

	}

	@Test
	public void testVisitItemIdValue() {
		ItemIdValue value = objectFactory.createItemIdValue("Q200");
		assertEquals(converter.visit(value).toString(), ITEM_ID_VALUE_REPRES);

		assertEquals(converter.convertEntityIdValueToJson(value).toString(),
				ENTITY_ID_VALUE_REPRES);
	}

	@Test
	public void testVisitMonolingualTextValue() {
		MonolingualTextValue value = factory.getMonolingualTextValue(
				"some text in a language lc", "lc");
		compareJSONObjects(converter.visit(value), new JSONObject(
				MONOLINGUAL_TEXT_VALUE_REPRES));

	}

	@Test
	public void testVisitSiteLinks() {
		SiteLink siteLink = factory.getSiteLink("title", "siteKey", "baseIri",
				Collections.<String> emptyList());
		compareJSONObjects(converter.visit(siteLink), new JSONObject(
				SITE_LINK_REPRES));

	}

}
