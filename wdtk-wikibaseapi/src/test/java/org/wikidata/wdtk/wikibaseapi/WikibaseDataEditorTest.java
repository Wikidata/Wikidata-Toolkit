package org.wikidata.wdtk.wikibaseapi;

/*
 * #%L
 * Wikidata Toolkit Wikibase API
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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.ItemDocumentBuilder;
import org.wikidata.wdtk.datamodel.helpers.PropertyDocumentBuilder;
import org.wikidata.wdtk.datamodel.helpers.StatementBuilder;
import org.wikidata.wdtk.datamodel.helpers.JsonSerializer;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.util.CompressionType;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;
import org.wikidata.wdtk.wikibaseapi.apierrors.TokenErrorException;

public class WikibaseDataEditorTest {

	MockBasicApiConnection con;
	ItemIdValue Q5 = Datamodel.makeWikidataItemIdValue("Q5");
	PropertyIdValue P31 = Datamodel.makeWikidataPropertyIdValue("P31");

	@Before
	public void setUp() throws IOException {
		this.con = new MockBasicApiConnection();
		Map<String, String> params = new HashMap<String, String>();
		params.put("action", "query");
		params.put("meta", "tokens");
		params.put("type", "csrf");
		params.put("format", "json");
		this.con.setWebResourceFromPath(params, this.getClass(),
				"/query-csrf-token-loggedin-response.json",
				CompressionType.NONE);
	}

	@Test
	public void testSetMaxLag() throws IOException, MediaWikiApiErrorException {
		WikibaseDataEditor wde = new WikibaseDataEditor(this.con,
				Datamodel.SITE_WIKIDATA);
		wde.setMaxLag(3);
		assertEquals(3, wde.getMaxLag());
	}

	@Test
	public void testSetAverageTimePerEdit() throws IOException,
			MediaWikiApiErrorException {
		WikibaseDataEditor wde = new WikibaseDataEditor(this.con,
				Datamodel.SITE_WIKIDATA);
		wde.setAverageTimePerEdit(5000);
		assertEquals(5000, wde.getAverageTimePerEdit());
	}

	@Test
	public void testSetRemainingEdits() throws IOException,
			MediaWikiApiErrorException {
		WikibaseDataEditor wde = new WikibaseDataEditor(this.con,
				Datamodel.SITE_WIKIDATA);
		wde.setRemainingEdits(1);

		ItemDocument itemDocument = ItemDocumentBuilder.forItemId(
				ItemIdValue.NULL).build();
		ItemDocument expectedResultDocument = ItemDocumentBuilder
				.forItemId(Datamodel.makeWikidataItemIdValue("Q1234"))
				.withRevisionId(1234).build();
		String resultData = JsonSerializer
				.getJsonString(expectedResultDocument);
		String expectedResult = "{\"entity\":" + resultData + ",\"success\":1}";

		Map<String, String> params = new HashMap<String, String>();
		params.put("action", "wbeditentity");
		params.put("summary", "My summary");
		params.put("new", "item");
		params.put("token", "42307b93c79b0cb558d2dfb4c3c92e0955e06041+\\");
		params.put("format", "json");
		params.put("maxlag", "5");
		String data = JsonSerializer.getJsonString(itemDocument);
		params.put("data", data);
		con.setWebResource(params, expectedResult);

		assertEquals(1, wde.getRemainingEdits());
		ItemDocument result = wde
				.createItemDocument(itemDocument, "My summary");
		assertEquals(expectedResultDocument, result);
		assertEquals(0, wde.getRemainingEdits());
		result = wde.createItemDocument(itemDocument, "My summary");
		assertEquals(null, result);
		assertEquals(0, wde.getRemainingEdits());
	}

	@Test
	public void testDisableEditing() throws IOException,
			MediaWikiApiErrorException {
		WikibaseDataEditor wde = new WikibaseDataEditor(this.con,
				Datamodel.SITE_WIKIDATA);
		wde.disableEditing();

		assertEquals(0, wde.getRemainingEdits());

		ItemDocument itemDocument = ItemDocumentBuilder.forItemId(
				ItemIdValue.NULL).build();
		ItemDocument result = wde
				.createItemDocument(itemDocument, "My summary");

		assertEquals(null, result);
		assertEquals(0, wde.getRemainingEdits());
	}

	@Test
	public void testCreateItem() throws IOException, MediaWikiApiErrorException {
		WikibaseDataEditor wde = new WikibaseDataEditor(this.con,
				Datamodel.SITE_WIKIDATA);

		ItemDocument itemDocument = ItemDocumentBuilder.forItemId(
				ItemIdValue.NULL).build();
		ItemDocument expectedResultDocument = ItemDocumentBuilder
				.forItemId(Datamodel.makeWikidataItemIdValue("Q1234"))
				.withRevisionId(1234).build();
		String resultData = JsonSerializer
				.getJsonString(expectedResultDocument);
		String expectedResult = "{\"entity\":" + resultData + ",\"success\":1}";

		Map<String, String> params = new HashMap<String, String>();
		params.put("action", "wbeditentity");
		params.put("summary", "My summary");
		params.put("new", "item");
		params.put("token", "42307b93c79b0cb558d2dfb4c3c92e0955e06041+\\");
		params.put("format", "json");
		params.put("maxlag", "5");
		String data = JsonSerializer.getJsonString(itemDocument);
		params.put("data", data);
		con.setWebResource(params, expectedResult);

		ItemDocument result = wde
				.createItemDocument(itemDocument, "My summary");

		assertEquals(expectedResultDocument, result);
		assertEquals(-1, wde.getRemainingEdits());
	}

	@Test(expected = TokenErrorException.class)
	public void testCreateItemBadToken() throws IOException,
			MediaWikiApiErrorException {
		WikibaseDataEditor wde = new WikibaseDataEditor(this.con,
				Datamodel.SITE_WIKIDATA);

		ItemDocument itemDocument = ItemDocumentBuilder.forItemId(
				ItemIdValue.NULL).build();

		Map<String, String> params = new HashMap<String, String>();
		params.put("action", "wbeditentity");
		params.put("summary", "My summary");
		params.put("new", "item");
		params.put("token", "42307b93c79b0cb558d2dfb4c3c92e0955e06041+\\");
		params.put("format", "json");
		params.put("maxlag", "5");
		String data = JsonSerializer.getJsonString(itemDocument);
		params.put("data", data);
		this.con.setWebResourceFromPath(params, this.getClass(),
				"/error-badtoken.json", CompressionType.NONE);

		wde.createItemDocument(itemDocument, "My summary");
	}

	@Test
	public void testCreateItemCachedToken() throws IOException,
			MediaWikiApiErrorException {
		WikibaseDataEditor wde = new WikibaseDataEditor(this.con,
				Datamodel.SITE_WIKIDATA);

		ItemDocument itemDocument = ItemDocumentBuilder.forItemId(
				ItemIdValue.NULL).build();
		ItemDocument expectedResultDocument = ItemDocumentBuilder
				.forItemId(Datamodel.makeWikidataItemIdValue("Q1234"))
				.withRevisionId(1234).build();
		String resultData = JsonSerializer
				.getJsonString(expectedResultDocument);
		String expectedResult = "{\"entity\":" + resultData + ",\"success\":1}";

		Map<String, String> params = new HashMap<String, String>();
		params.put("action", "wbeditentity");
		params.put("summary", "My summary");
		params.put("new", "item");
		params.put("token", "42307b93c79b0cb558d2dfb4c3c92e0955e06041+\\");
		params.put("format", "json");
		params.put("maxlag", "5");
		String data = JsonSerializer.getJsonString(itemDocument);
		params.put("data", data);
		con.setWebResource(params, expectedResult);

		// Create item twice
		wde.createItemDocument(itemDocument, "My summary");
		ItemDocument result = wde
				.createItemDocument(itemDocument, "My summary");

		assertEquals(expectedResultDocument, result);
	}

	@Test
	public void testCreateItemWikibaseJsonBug() throws IOException,
			MediaWikiApiErrorException {
		// Test what happens if the API returns JSON without an actual entity
		// document and without any respective key
		WikibaseDataEditor wde = new WikibaseDataEditor(this.con,
				Datamodel.SITE_WIKIDATA);

		ItemDocument itemDocument = ItemDocumentBuilder.forItemId(
				ItemIdValue.NULL).build();
		ItemDocument expectedResultDocument = ItemDocumentBuilder
				.forItemId(Datamodel.makeWikidataItemIdValue("Q1234"))
				.withRevisionId(1234).build();
		String expectedResult = "{\"entity\":"
				+ "{\"type\":\"item\",\"aliases\":[],\"labels\":[],\"descriptions\":[],\"lastrevid\":1234,\"sitelinks\":[],\"id\":\"Q1234\",\"claims\":[]}"
				+ ",\"success\":1}";

		Map<String, String> params = new HashMap<String, String>();
		params.put("action", "wbeditentity");
		params.put("summary", "My summary");
		params.put("new", "item");
		params.put("token", "42307b93c79b0cb558d2dfb4c3c92e0955e06041+\\");
		params.put("format", "json");
		params.put("maxlag", "5");
		String data = JsonSerializer.getJsonString(itemDocument);
		params.put("data", data);
		con.setWebResource(params, expectedResult);

		ItemDocument result = wde
				.createItemDocument(itemDocument, "My summary");

		assertEquals(expectedResultDocument, result);
	}

	@Test(expected = IOException.class)
	public void testCreateItemBadEntityDocumentJson() throws IOException,
			MediaWikiApiErrorException {
		// Test what happens if the API returns JSON without an actual entity
		// document, but with a respective key pointing to an empty object
		WikibaseDataEditor wde = new WikibaseDataEditor(this.con,
				Datamodel.SITE_WIKIDATA);

		ItemDocument itemDocument = ItemDocumentBuilder.forItemId(
				ItemIdValue.NULL).build();
		String expectedResult = "{\"entity\":" + "{}" + ",\"success\":1}";

		Map<String, String> params = new HashMap<String, String>();
		params.put("action", "wbeditentity");
		params.put("summary", "My summary");
		params.put("new", "item");
		params.put("token", "42307b93c79b0cb558d2dfb4c3c92e0955e06041+\\");
		params.put("format", "json");
		params.put("maxlag", "5");
		String data = JsonSerializer.getJsonString(itemDocument);
		params.put("data", data);
		con.setWebResource(params, expectedResult);

		wde.createItemDocument(itemDocument, "My summary");
	}

	@Test(expected = IOException.class)
	public void testCreateItemMissingEntityDocumentJson() throws IOException,
			MediaWikiApiErrorException {
		WikibaseDataEditor wde = new WikibaseDataEditor(this.con,
				Datamodel.SITE_WIKIDATA);

		ItemDocument itemDocument = ItemDocumentBuilder.forItemId(
				ItemIdValue.NULL).build();
		String expectedResult = "{\"success\":1}";

		Map<String, String> params = new HashMap<String, String>();
		params.put("action", "wbeditentity");
		params.put("summary", "My summary");
		params.put("new", "item");
		params.put("token", "42307b93c79b0cb558d2dfb4c3c92e0955e06041+\\");
		params.put("format", "json");
		params.put("maxlag", "5");
		String data = JsonSerializer.getJsonString(itemDocument);
		params.put("data", data);
		con.setWebResource(params, expectedResult);

		wde.createItemDocument(itemDocument, "My summary");
	}

	@Test
	public void testCreatePropertyBot() throws IOException,
			MediaWikiApiErrorException {
		WikibaseDataEditor wde = new WikibaseDataEditor(this.con,
				Datamodel.SITE_WIKIDATA);
		wde.setEditAsBot(true);

		PropertyDocument propertyDocument = PropertyDocumentBuilder
				.forPropertyIdAndDatatype(PropertyIdValue.NULL,
						DatatypeIdValue.DT_ITEM).build();
		PropertyDocument expectedResultDocument = PropertyDocumentBuilder
				.forPropertyIdAndDatatype(
						Datamodel.makeWikidataPropertyIdValue("P1234"),
						DatatypeIdValue.DT_ITEM).withRevisionId(1234).build();
		String resultData = JsonSerializer
				.getJsonString(expectedResultDocument);
		String expectedResult = "{\"entity\":" + resultData + ",\"success\":1}";

		Map<String, String> params = new HashMap<String, String>();
		params.put("action", "wbeditentity");
		params.put("new", "property");
		params.put("bot", "");
		params.put("token", "42307b93c79b0cb558d2dfb4c3c92e0955e06041+\\");
		params.put("format", "json");
		params.put("maxlag", "5");
		String data = JsonSerializer.getJsonString(propertyDocument);
		params.put("data", data);
		con.setWebResource(params, expectedResult);

		PropertyDocument result = wde.createPropertyDocument(propertyDocument,
				null);

		assertTrue(wde.editAsBot());
		assertEquals(expectedResultDocument, result);
	}

	@Test
	public void testEditItem() throws IOException, MediaWikiApiErrorException {
		WikibaseDataEditor wde = new WikibaseDataEditor(this.con,
				Datamodel.SITE_WIKIDATA);

		ItemIdValue id = Datamodel.makeWikidataItemIdValue("Q1234");

		ItemDocument itemDocument = ItemDocumentBuilder.forItemId(id)
				.withRevisionId(1234).build();
		ItemDocument expectedResultDocument = ItemDocumentBuilder.forItemId(id)
				.withRevisionId(1235).build();
		String resultData = JsonSerializer
				.getJsonString(expectedResultDocument);
		String expectedResult = "{\"entity\":" + resultData + ",\"success\":1}";

		Map<String, String> params = new HashMap<String, String>();
		params.put("action", "wbeditentity");
		params.put("id", "Q1234");
		params.put("summary", "My summary");
		params.put("token", "42307b93c79b0cb558d2dfb4c3c92e0955e06041+\\");
		params.put("format", "json");
		params.put("baserevid", "1234");
		params.put("maxlag", "5");
		String data = JsonSerializer.getJsonString(itemDocument);
		params.put("data", data);
		con.setWebResource(params, expectedResult);

		ItemDocument result = wde.editItemDocument(itemDocument, false,
				"My summary");

		assertEquals(expectedResultDocument, result);
	}
	
	@Test
	public void testStatementUpdateWithoutChanges() throws MediaWikiApiErrorException, IOException {
		WikibaseDataEditor wde = new WikibaseDataEditor(this.con,
				Datamodel.SITE_WIKIDATA);

		ItemIdValue id = Datamodel.makeWikidataItemIdValue("Q1234");
		ItemIdValue Q5 = Datamodel.makeWikidataItemIdValue("Q5");
		PropertyIdValue P31 = Datamodel.makeWikidataPropertyIdValue("P31");

		Statement s1 = StatementBuilder.forSubjectAndProperty(id, P31)
				.withValue(Q5).withId("ID-s1").build();
		Statement s1dup = StatementBuilder.forSubjectAndProperty(id, P31)
				.withValue(Q5).build();
		Statement s2 = StatementBuilder.forSubjectAndProperty(id, P31)
				.withValue(id).build();
		ItemDocument itemDocument = ItemDocumentBuilder.forItemId(id)
				.withStatement(s1)
				.withRevisionId(1234).build();
		
		wde.setRemainingEdits(10);
		
		ItemDocument editedItemDocument = wde.updateStatements(
				itemDocument,
				Arrays.asList(s1dup),
				Arrays.asList(s2),
				"Doing spurious changes");
		
		// no edit was made at all
		assertEquals(itemDocument, editedItemDocument);
		assertEquals(10, wde.getRemainingEdits());
	}
	
	@Test
	public void testTermStatementUpdateWithoutChanges() throws MediaWikiApiErrorException, IOException {
		WikibaseDataEditor wde = new WikibaseDataEditor(this.con,
				Datamodel.SITE_WIKIDATA);

		ItemIdValue id = Datamodel.makeWikidataItemIdValue("Q1234");
		MonolingualTextValue label = Datamodel.makeMonolingualTextValue("My label", "en");
		MonolingualTextValue description = Datamodel.makeMonolingualTextValue("Meine Beschreibung", "de");
		MonolingualTextValue alias = Datamodel.makeMonolingualTextValue("Mon alias", "fr");

		Statement s1 = StatementBuilder.forSubjectAndProperty(id, P31)
				.withValue(Q5).withId("ID-s1").build();
		Statement s1dup = StatementBuilder.forSubjectAndProperty(id, P31)
				.withValue(Q5).build();
		Statement s2 = StatementBuilder.forSubjectAndProperty(id, P31)
				.withValue(id).build();
		ItemDocument itemDocument = ItemDocumentBuilder.forItemId(id)
				.withLabel(label)
				.withDescription(description)
				.withStatement(s1)
				.withRevisionId(1234).build();
		
		wde.setRemainingEdits(10);
		
		ItemDocument editedItemDocument = wde.updateTermsStatements(
				itemDocument,
				Arrays.asList(label),
				Arrays.asList(description), 
				Collections.<MonolingualTextValue>emptyList(),
				Arrays.asList(alias),
				Arrays.asList(s1dup),
				Arrays.asList(s2),
				"Doing spurious changes");
		
		// no edit was made at all
		assertEquals(itemDocument, editedItemDocument);
		assertEquals(10, wde.getRemainingEdits());
	}
	
	
	@Test
	public void testNullEdit() throws IOException, MediaWikiApiErrorException {
		WikibaseDataEditor wde = new WikibaseDataEditor(this.con,
				Datamodel.SITE_WIKIDATA);
		wde.setRemainingEdits(10);
		
		ItemIdValue id = Datamodel.makeWikidataItemIdValue("Q1234");
		
		Statement s1 = StatementBuilder.forSubjectAndProperty(id, P31)
				.withValue(Q5).withId("ID-s1").build();
		ItemDocument itemDocument = ItemDocumentBuilder.forItemId(id)
				.withStatement(s1)
				.withRevisionId(1234).build();
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("action", "wbeditentity");
		params.put("id", "Q1234");
		params.put("token", "42307b93c79b0cb558d2dfb4c3c92e0955e06041+\\");
		params.put("format", "json");
		params.put("baserevid", "1234");
		params.put("maxlag", "5");
		params.put("data", "{}");
		String data = JsonSerializer.getJsonString(itemDocument);
		String expectedResult = "{\"entity\":"+data+",\"success\":1}";
		con.setWebResource(params, expectedResult);
		
		ItemDocument nullEditedItemDocument = wde.nullEdit(itemDocument);
		
		assertEquals(itemDocument, nullEditedItemDocument);
		assertEquals(9, wde.getRemainingEdits());
	}
	
	@Test
	public void testLabelEdit() throws MediaWikiApiErrorException, IOException {
		WikibaseDataEditor wde = new WikibaseDataEditor(this.con,
				Datamodel.SITE_WIKIDATA);
		ItemIdValue id = Datamodel.makeWikidataItemIdValue("Q1234");
		Statement s1 = StatementBuilder.forSubjectAndProperty(id, P31)
				.withValue(Q5).withId("ID-s1").build();
		MonolingualTextValue label = Datamodel.makeMonolingualTextValue("My label", "en");
		ItemDocument itemDocument = ItemDocumentBuilder.forItemId(id)
				.withStatement(s1)
				.withRevisionId(1234)
				.build();
		ItemDocument expectedDocument = ItemDocumentBuilder.forItemId(id)
				.withLabel(label)
				.withStatement(s1)
				.withRevisionId(1235)
				.build();
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("action", "wbsetlabel");
		params.put("id", "Q1234");
		params.put("summary", "Adding a label");
		params.put("token", "42307b93c79b0cb558d2dfb4c3c92e0955e06041+\\");
		params.put("format", "json");
		params.put("baserevid", "1234");
		params.put("maxlag", "5");
		params.put("language", "en");
		params.put("value", "My label");
		String expectedResult = "{\"entity\":{\"labels\":{\"en\":{\"language\":\"en\",\"value\":\"My label\"}},"+
				"\"id\":\"Q1234\",\"type\":\"item\",\"lastrevid\":1235},\"success\":1}";
		con.setWebResource(params, expectedResult);
		
		ItemDocument editedDocument = wde.updateTermsStatements(itemDocument, Arrays.asList(label),
				Collections.<MonolingualTextValue>emptyList(), Collections.<MonolingualTextValue>emptyList(),
				Collections.<MonolingualTextValue>emptyList(), Collections.<Statement>emptyList(),
				Collections.<Statement>emptyList(), "Adding a label");
		
		assertEquals(expectedDocument, editedDocument);
	}
	
	@Test
	public void testDescriptionEdit() throws MediaWikiApiErrorException, IOException {
		WikibaseDataEditor wde = new WikibaseDataEditor(this.con,
				Datamodel.SITE_WIKIDATA);
		ItemIdValue id = Datamodel.makeWikidataItemIdValue("Q1234");
		Statement s1 = StatementBuilder.forSubjectAndProperty(id, P31)
				.withValue(Q5).withId("ID-s1").build();
		MonolingualTextValue description = Datamodel.makeMonolingualTextValue("My description", "en");
		ItemDocument itemDocument = ItemDocumentBuilder.forItemId(id)
				.withStatement(s1)
				.withRevisionId(1234)
				.build();
		ItemDocument expectedDocument = ItemDocumentBuilder.forItemId(id)
				.withDescription(description)
				.withStatement(s1)
				.withRevisionId(1235L)
				.build();
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("action", "wbsetdescription");
		params.put("id", "Q1234");
		params.put("summary", "Adding a description");
		params.put("token", "42307b93c79b0cb558d2dfb4c3c92e0955e06041+\\");
		params.put("format", "json");
		params.put("baserevid", "1234");
		params.put("maxlag", "5");
		params.put("language", "en");
		params.put("value", "My description");
		String expectedResult = "{\"entity\":{\"descriptions\":{\"en\":{\"language\":\"en\",\"value\":\"My description\"}},"+
				"\"id\":\"Q1234\",\"type\":\"item\",\"lastrevid\":1235},\"success\":1}";
		con.setWebResource(params, expectedResult);
		
		ItemDocument editedDocument = wde.updateTermsStatements(itemDocument, Collections.<MonolingualTextValue>emptyList(), 
				Arrays.asList(description),	Collections.<MonolingualTextValue>emptyList(),
				Collections.<MonolingualTextValue>emptyList(), Collections.<Statement>emptyList(),
				Collections.<Statement>emptyList(), "Adding a description");
		
		assertEquals(expectedDocument, editedDocument);
	}
	
	@Test
	public void testAliasEdit() throws MediaWikiApiErrorException, IOException {
		WikibaseDataEditor wde = new WikibaseDataEditor(this.con,
				Datamodel.SITE_WIKIDATA);
		ItemIdValue id = Datamodel.makeWikidataItemIdValue("Q1234");
		Statement s1 = StatementBuilder.forSubjectAndProperty(id, P31)
				.withValue(Q5).withId("ID-s1").build();
		MonolingualTextValue label = Datamodel.makeMonolingualTextValue("My label", "en");
		MonolingualTextValue addedAlias = Datamodel.makeMonolingualTextValue("My added alias", "en");
		MonolingualTextValue removedAlias = Datamodel.makeMonolingualTextValue("My removed alias", "en");
		ItemDocument itemDocument = ItemDocumentBuilder.forItemId(id)
				.withStatement(s1)
				.withLabel(label)
				.withAlias(removedAlias)
				.withRevisionId(1234)
				.build();
		ItemDocument expectedDocument = ItemDocumentBuilder.forItemId(id)
				.withStatement(s1)
				.withLabel(label)
				.withAlias(addedAlias)
				.withRevisionId(1235)
				.build();
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("action", "wbsetaliases");
		params.put("id", "Q1234");
		params.put("summary", "Changing aliases");
		params.put("token", "42307b93c79b0cb558d2dfb4c3c92e0955e06041+\\");
		params.put("format", "json");
		params.put("baserevid", "1234");
		params.put("maxlag", "5");
		params.put("language", "en");
		params.put("add", "My added alias");
		params.put("remove", "My removed alias");
		String expectedResult = "{\"entity\":{\"aliases\":{\"en\":[{\"language\":\"en\",\"value\":\"My added alias\"}]},"+
				"\"id\":\"Q1234\",\"type\":\"item\",\"lastrevid\":1235},\"success\":1}";
		con.setWebResource(params, expectedResult);
		
		ItemDocument editedDocument = wde.updateTermsStatements(itemDocument, Collections.<MonolingualTextValue>emptyList(), 
				Collections.<MonolingualTextValue>emptyList(), Arrays.asList(addedAlias),
				Arrays.asList(removedAlias), Collections.<Statement>emptyList(),
				Collections.<Statement>emptyList(), "Changing aliases");
		
		assertEquals(expectedDocument, editedDocument);
	}
	
	@Test
	public void testNewSingleStatement() throws MediaWikiApiErrorException, IOException {
		String guid = "8372EF7A-B72C-7DE2-98D0-DFB4-8EC8392AC28E";
		WikibaseDataEditor wde = new WikibaseDataEditor(this.con,
				Datamodel.SITE_WIKIDATA, new MockGuidGenerator(guid));
		ItemIdValue id = Datamodel.makeWikidataItemIdValue("Q1234");
		Statement s1 = StatementBuilder.forSubjectAndProperty(id, P31)
				.withValue(Q5).build();
		Statement s2 = StatementBuilder.forSubjectAndProperty(id, P31)
				.withValue(Q5).withId("Q1234$"+guid).build();
		ItemDocument itemDocument = ItemDocumentBuilder.forItemId(id)
				.withRevisionId(1234)
				.build();
		ItemDocument expectedDocument = ItemDocumentBuilder.forItemId(id)
				.withStatement(s2)
				.withRevisionId(1235)
				.build();
		
		String statementJson = JsonSerializer.getJsonString(s2);
		Map<String, String> params = new HashMap<String, String>();
		params.put("action", "wbsetclaim");
		params.put("summary", "Adding a claim");
		params.put("token", "42307b93c79b0cb558d2dfb4c3c92e0955e06041+\\");
		params.put("format", "json");
		params.put("baserevid", "1234");
		params.put("maxlag", "5");
		params.put("claim", statementJson);
		String expectedResult = "{\"pageinfo\":{\"lastrevid\":1235},\"success\":1,\"claim\":"+statementJson+"}";
		con.setWebResource(params, expectedResult);
		
		ItemDocument editedDocument = wde.updateTermsStatements(itemDocument, Collections.<MonolingualTextValue>emptyList(), 
				Collections.<MonolingualTextValue>emptyList(), Collections.<MonolingualTextValue>emptyList(),
				Collections.<MonolingualTextValue>emptyList(), Arrays.asList(s1),
				Collections.<Statement>emptyList(), "Adding a claim");
		
		assertEquals(expectedDocument, editedDocument);
	}
	
	@Test
	public void testDeleteStatements() throws MediaWikiApiErrorException, IOException {
		String guid1 = "8372EF7A-B72C-7DE2-98D0-DFB4-8EC8392AC28E";
		String guid2 = "4311895D-9091-4BC9-9B34-DFB4-1B00EE8CFA62";
		WikibaseDataEditor wde = new WikibaseDataEditor(this.con, Datamodel.SITE_WIKIDATA);
		ItemIdValue id = Datamodel.makeWikidataItemIdValue("Q1234");
		Statement s1 = StatementBuilder.forSubjectAndProperty(id, P31)
				.withValue(Q5).withId("Q1234$"+guid1).build();
		Statement s2 = StatementBuilder.forSubjectAndProperty(id, P31)
				.withValue(Q5).withId("Q1234$"+guid2).build();
		ItemDocument itemDocument = ItemDocumentBuilder.forItemId(id)
				.withRevisionId(1234)
				.withStatement(s1)
				.withStatement(s2)
				.build();
		ItemDocument expectedDocument = ItemDocumentBuilder.forItemId(id)
				.withRevisionId(1235)
				.build();
		
		List<String> statementIds = Arrays.asList("Q1234$"+guid1, "Q1234$"+guid2);
		
		String statementsList = String.join("|", statementIds);
		Map<String, String> params = new HashMap<String, String>();
		params.put("action", "wbremoveclaims");
		params.put("summary", "Removing claims");
		params.put("token", "42307b93c79b0cb558d2dfb4c3c92e0955e06041+\\");
		params.put("format", "json");
		params.put("baserevid", "1234");
		params.put("maxlag", "5");
		params.put("claim", statementsList);
		String expectedResult = "{\"pageinfo\":{\"lastrevid\":1235},\"success\":1,\"claims\":[\""+statementIds.get(0)+"\",\""+statementIds.get(1)+"\"]}";
		con.setWebResource(params, expectedResult);
		
		ItemDocument editedDocument = wde.updateTermsStatements(itemDocument, Collections.<MonolingualTextValue>emptyList(), 
				Collections.<MonolingualTextValue>emptyList(), Collections.<MonolingualTextValue>emptyList(),
				Collections.<MonolingualTextValue>emptyList(), Collections.<Statement>emptyList(),
				Arrays.asList(s1,s2), "Removing claims");
		
		assertEquals(expectedDocument, editedDocument);
	}

	@Test
	public void testEditProperty() throws IOException,
			MediaWikiApiErrorException {
		WikibaseDataEditor wde = new WikibaseDataEditor(this.con,
				Datamodel.SITE_WIKIDATA);

		PropertyIdValue id = Datamodel.makeWikidataPropertyIdValue("P1234");

		PropertyDocument itemDocument = PropertyDocumentBuilder
				.forPropertyIdAndDatatype(id, DatatypeIdValue.DT_ITEM)
				.withRevisionId(1234).build();
		PropertyDocument expectedResultDocument = PropertyDocumentBuilder
				.forPropertyIdAndDatatype(id, DatatypeIdValue.DT_ITEM)
				.withRevisionId(1235).build();
		String resultData = JsonSerializer
				.getJsonString(expectedResultDocument);
		String expectedResult = "{\"entity\":" + resultData + ",\"success\":1}";

		Map<String, String> params = new HashMap<String, String>();
		params.put("action", "wbeditentity");
		params.put("id", "P1234");
		params.put("summary", "My summary");
		params.put("token", "42307b93c79b0cb558d2dfb4c3c92e0955e06041+\\");
		params.put("format", "json");
		params.put("clear", "");
		params.put("baserevid", "1234");
		params.put("maxlag", "5");
		String data = JsonSerializer.getJsonString(itemDocument);
		params.put("data", data);
		con.setWebResource(params, expectedResult);

		PropertyDocument result = wde.editPropertyDocument(itemDocument, true,
				"My summary");

		assertEquals(expectedResultDocument, result);
	}

}
