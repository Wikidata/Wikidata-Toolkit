package org.wikidata.wdtk.datamodel.json;

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

import java.io.IOException;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.Claim;
import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;
import org.wikidata.wdtk.testing.MockStringContentFactory;

public class JsonConverterTest {

	final static String FILE_NAME_ITEM_DOCUMENT = "ItemDocument.json";
	final static String FILE_NAME_STATEMENT = "Statement.json";
	final static String FILE_NAME_STATEMENT_GROUP = "StatementGroup.json";
	final static String FILE_NAME_CLAIM = "Claim.json";
	final static String FILE_NAME_REFERENCE = "Reference.json";

	final static String JSON_EMPTY_PROPERTY_DOCUMENT = "{\"id\":\"P42\",\"title\":\"P42\",\"type\":\"property\"}";
	final static String JSON_SITE_LINK = "{\"site\":\"enwiki\",\"badges\":[],\"title\":\"title\"}";

	final DataObjectFactory dataObjectFactory = new DataObjectFactoryImpl();
	final TestObjectFactory testObjectFactory = new TestObjectFactory();
	final JsonConverter jsonConverter = new JsonConverter();

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
	public void testItemDocument() throws JSONException, IOException {
		JsonResultComparer.compareJSONObjects(
				jsonConverter.getJsonForItemDocument(testObjectFactory
						.createItemDocument()),
				getResourceFromFile(FILE_NAME_ITEM_DOCUMENT));
	}

	@Test
	public void testPropertyDocument() throws JSONException {
		PropertyDocument document = dataObjectFactory.getPropertyDocument(
				dataObjectFactory.getPropertyIdValue("P42", "base/"),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				dataObjectFactory.getDatatypeIdValue("string"));
		JsonResultComparer.compareJSONObjects(
				jsonConverter.getJsonForPropertyDocument(document),
				new JSONObject(JSON_EMPTY_PROPERTY_DOCUMENT));
		JsonResultComparer.compareJSONObjects(jsonConverter
				.getJsonForPropertyDocument(testObjectFactory
						.createEmptyPropertyDocument()), new JSONObject(
				"{\"id\":\"P1\",\"title\":\"P1\",\"type\":\"property\"}"));

	}

	@Test
	public void testClaim() throws JSONException, IOException {
		ValueSnak snak = testObjectFactory.createValueSnakTimeValue("P129");
		Claim claim = testObjectFactory.createClaim("Q31", snak);
		JsonResultComparer.compareJSONObjects(
				jsonConverter.getJsonForClaim(claim),
				getResourceFromFile(FILE_NAME_CLAIM));

	}

	@Test
	public void testReference() throws JSONException, IOException {
		Reference ref = testObjectFactory.createReference();
		JsonResultComparer.compareJSONObjects(
				jsonConverter.getJsonForReference(ref),
				getResourceFromFile(FILE_NAME_REFERENCE));
	}

	@Test
	public void testStatement() throws JSONException, IOException {
		Statement statement = testObjectFactory.createStatement("Q100", "P131");
		JsonResultComparer.compareJSONObjects(
				jsonConverter.getJsonForStatement(statement),
				getResourceFromFile(FILE_NAME_STATEMENT));

	}

	@Test
	public void testStatementGroup() throws JSONException, IOException {
		StatementGroup group = testObjectFactory.createStatementGroup();
		JsonResultComparer.compareJSONArrays(
				jsonConverter.convertStatementGroupToJson(group),
				getArrayResourceFromFile(FILE_NAME_STATEMENT_GROUP));

	}

	@Test
	public void testSiteLinks() {
		SiteLink siteLink = dataObjectFactory.getSiteLink("title", "enwiki",
				Collections.<String> emptyList());
		JsonResultComparer.compareJSONObjects(jsonConverter
				.getJsonForSiteLink(siteLink), new JSONObject(JSON_SITE_LINK));
	}

}
