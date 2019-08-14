package org.wikidata.wdtk.datamodel.implementation;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.DatamodelMapper;
import org.wikidata.wdtk.datamodel.interfaces.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class MediaInfoDocumentImplTest {

	private final ObjectMapper mapper = new DatamodelMapper("http://example.com/entity/");

	private final MediaInfoIdValue mid = new MediaInfoIdValueImpl("M42", "http://example.com/entity/");
	private final Statement s = new StatementImpl("MyId", StatementRank.NORMAL,
			new SomeValueSnakImpl(new PropertyIdValueImpl("P42", "http://example.com/entity/")),
			Collections.emptyList(), Collections.emptyList(), mid);
	private final List<StatementGroup> statementGroups = Collections.singletonList(
			new StatementGroupImpl(Collections.singletonList(s))
	);
	private final MonolingualTextValue label = new TermImpl("en", "label");
	private final List<MonolingualTextValue> labelList = Collections.singletonList(label);

	private final MediaInfoDocument mi1 = new MediaInfoDocumentImpl(mid, labelList, statementGroups,1234);
	private final MediaInfoDocument mi2 = new MediaInfoDocumentImpl(mid, labelList, statementGroups,  1234);

	private final String JSON_MEDIA_INFO_LABEL = "{\"type\":\"mediainfo\",\"id\":\"M42\",\"labels\":{\"en\":{\"language\":\"en\",\"value\":\"label\"}},\"statements\":{}}";
	private final String JSON_MEDIA_INFO_DESCRIPTION = "{\"type\":\"mediainfo\",\"id\":\"M42\",\"labels\":{},\"descriptions\":{},\"statements\":{}}";
	private final String JSON_MEDIA_INFO_STATEMENTS = "{\"type\":\"mediainfo\",\"id\":\"M42\",\"labels\":{},\"statements\":{\"P42\":[{\"rank\":\"normal\",\"id\":\"MyId\",\"mainsnak\":{\"property\":\"P42\",\"snaktype\":\"somevalue\"},\"type\":\"statement\"}]}}";
	private final String JSON_MEDIA_INFO_CLAIMS = "{\"type\":\"mediainfo\",\"id\":\"M42\",\"labels\":{},\"claims\":{\"P42\":[{\"rank\":\"normal\",\"id\":\"MyId\",\"mainsnak\":{\"property\":\"P42\",\"snaktype\":\"somevalue\"},\"type\":\"statement\"}]}}";
	private final String JSON_MEDIA_INFO_EMPTY_ARRAYS = "{\"type\":\"mediainfo\",\"id\":\"M42\",\"labels\":[],\"descriptions\":[],\"statements\":[],\"sitelinks\":[]}";

	@Test
	public void fieldsAreCorrect() {
		assertEquals(mi1.getEntityId(), mid);
		assertEquals(mi1.getLabels(), Collections.singletonMap(label.getLanguageCode(), label));
		assertEquals(mi1.getStatementGroups(), statementGroups);
	}

	@Test
	public void findLabels() {
		assertEquals("label", mi1.findLabel("en"));
		assertNull( mi1.findLabel("ja"));
	}

	@Test
	public void equalityBasedOnContent() {
		MediaInfoDocument irDiffLabel = new MediaInfoDocumentImpl(mid, Collections.emptyList(), statementGroups, 1234);
		MediaInfoDocument irDiffStatementGroups = new MediaInfoDocumentImpl(mid,
				labelList, 
				Collections.emptyList(), 1234);
		MediaInfoDocument irDiffRevisions = new MediaInfoDocumentImpl(mid,
				labelList, 
				statementGroups, 1235);

		PropertyDocument pr = new PropertyDocumentImpl(
				new PropertyIdValueImpl("P42", "foo"),
				labelList, Collections.emptyList(),
				Collections.emptyList(), Collections.emptyList(),
				new DatatypeIdImpl(DatatypeIdValue.DT_STRING), 1234);

		// we need to use empty lists of Statement groups to test inequality
		// based on different item ids with all other data being equal
		MediaInfoDocument irDiffMediaInfoIdValue = new MediaInfoDocumentImpl(
				new MediaInfoIdValueImpl("M23", "http://example.org/"),
				labelList, 
				Collections.emptyList(), 1234);

		assertEquals(mi1, mi1);
		assertEquals(mi1, mi2);
		assertNotEquals(mi1, irDiffLabel);
		assertNotEquals(mi1, irDiffStatementGroups);
		assertNotEquals(mi1, irDiffRevisions);
		assertNotEquals(irDiffStatementGroups, irDiffMediaInfoIdValue);
		assertNotEquals(mi1, pr);
		assertNotEquals(mi1, null);
		assertNotEquals(mi1, this);
	}

	@Test
	public void hashBasedOnContent() {
		assertEquals(mi1.hashCode(), mi2.hashCode());
	}

	@Test(expected = NullPointerException.class)
	public void idNotNull() {
		new MediaInfoDocumentImpl(null, Collections.emptyList(), statementGroups, 1234);
	}

	@Test
	public void labelsCanBeNull() {
		MediaInfoDocument doc = new MediaInfoDocumentImpl(mid, null, statementGroups, 1234);
		assertTrue(doc.getLabels().isEmpty());
	}

	@Test
	public void statementGroupsCanBeNull() {
		MediaInfoDocument doc = new MediaInfoDocumentImpl(mid, Collections.emptyList(), null, 1234);
		assertTrue(doc.getStatementGroups().isEmpty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void statementGroupsUseSameSubject() {
		MediaInfoIdValue mid2 = new MediaInfoIdValueImpl("Q23", "http://example.org/");
		Statement s2 = new StatementImpl("MyId", StatementRank.NORMAL,
				new SomeValueSnakImpl(new PropertyIdValueImpl("P42", "http://wikibase.org/entity/")),
				Collections.emptyList(),  Collections.emptyList(), mid2);
		StatementGroup sg2 = new StatementGroupImpl(Collections.singletonList(s2));

		List<StatementGroup> statementGroups2 = new ArrayList<>();
		statementGroups2.add(statementGroups.get(0));
		statementGroups2.add(sg2);

		new MediaInfoDocumentImpl(mid, Collections.emptyList(), statementGroups2, 1234);
	}

	@Test
	public void iterateOverAllStatements() {
		Iterator<Statement> statements = mi1.getAllStatements();

		assertTrue(statements.hasNext());
		assertEquals(s, statements.next());
		assertFalse(statements.hasNext());
	}
	
	@Test
	public void testWithRevisionId() {
		assertEquals(1235L, mi1.withRevisionId(1235L).getRevisionId());
		assertEquals(mi1, mi1.withRevisionId(1325L).withRevisionId(mi1.getRevisionId()));
	}
	
	@Test
	public void testWithLabelInNewLanguage() {
		MonolingualTextValue newLabel = new MonolingualTextValueImpl(
				"MediaInfo M42", "fr");
		MediaInfoDocument withLabel = mi1.withLabel(newLabel);
		assertEquals("MediaInfo M42", withLabel.findLabel("fr"));
	}
	
	@Test
	public void testAddStatement() {
		Statement fresh = new StatementImpl("MyFreshId", StatementRank.NORMAL,
				new SomeValueSnakImpl(new PropertyIdValueImpl("P29", "http://example.com/entity/")),
				Collections.emptyList(), Collections.emptyList(), mid);
		Claim claim = fresh.getClaim();
		assertFalse(mi1.hasStatementValue(
				claim.getMainSnak().getPropertyId(),
				claim.getValue()));
		MediaInfoDocument withStatement = mi1.withStatement(fresh);
		assertTrue(withStatement.hasStatementValue(
				claim.getMainSnak().getPropertyId(),
				claim.getValue()));
	}
	
	@Test
	public void testDeleteStatements() {
		Statement toRemove = statementGroups.get(0).getStatements().get(0);
		MediaInfoDocument withoutStatement = mi1.withoutStatementIds(Collections.singleton(toRemove.getStatementId()));
		assertNotEquals(withoutStatement, mi1);
	}

	@Test
	public void testLabelsToJson() throws JsonProcessingException {
		MediaInfoDocumentImpl document = new MediaInfoDocumentImpl(mid, labelList, Collections.emptyList(), 0);
		JsonComparator.compareJsonStrings(JSON_MEDIA_INFO_LABEL, mapper.writeValueAsString(document));
	}

	@Test
	public void testLabelToJava() throws IOException {
		MediaInfoDocumentImpl document = new MediaInfoDocumentImpl(mid,
				labelList, Collections.emptyList(), 0);
		assertEquals(document, mapper.readValue(JSON_MEDIA_INFO_LABEL, EntityDocumentImpl.class));
	}

	@Test
	public void testDescriptionsToJava() throws IOException {
		MediaInfoDocumentImpl document = new MediaInfoDocumentImpl(mid,
				Collections.emptyList(), Collections.emptyList(), 0);
		assertEquals(document, mapper.readValue(JSON_MEDIA_INFO_DESCRIPTION, EntityDocumentImpl.class));
	}

	@Test
	public void testStatementsToJson() throws JsonProcessingException {
		MediaInfoDocumentImpl document = new MediaInfoDocumentImpl(mid, Collections.emptyList(), statementGroups,  0);
		JsonComparator.compareJsonStrings(JSON_MEDIA_INFO_STATEMENTS, mapper.writeValueAsString(document));
	}

	@Test
	public void testStatementsToJava() throws IOException {
		MediaInfoDocumentImpl document = new MediaInfoDocumentImpl(mid, Collections.emptyList(), statementGroups, 0);
		assertEquals(document, mapper.readValue(JSON_MEDIA_INFO_STATEMENTS, MediaInfoDocumentImpl.class));
	}

	@Test
	public void testStatementsNamedClaimsToJava() throws IOException {
		MediaInfoDocumentImpl document = new MediaInfoDocumentImpl(mid, Collections.emptyList(), statementGroups, 0);
		assertEquals(document, mapper.readValue(JSON_MEDIA_INFO_CLAIMS, MediaInfoDocumentImpl.class));
	}

	/**
	 * Checks support of wrong serialization of empty object as empty array
	 */
	@Test
	public void testEmptyArraysForTerms() throws IOException {
		MediaInfoDocumentImpl document = new MediaInfoDocumentImpl(mid, Collections.emptyList(), Collections.emptyList(), 0);

		assertEquals(document, mapper.readerFor(MediaInfoDocumentImpl.class)
				.with(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
				.readValue(JSON_MEDIA_INFO_EMPTY_ARRAYS)
		);
	}

}
