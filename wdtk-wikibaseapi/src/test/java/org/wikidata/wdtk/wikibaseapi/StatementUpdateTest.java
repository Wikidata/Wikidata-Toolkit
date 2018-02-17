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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.ItemDocumentBuilder;
import org.wikidata.wdtk.datamodel.helpers.ReferenceBuilder;
import org.wikidata.wdtk.datamodel.helpers.StatementBuilder;
import org.wikidata.wdtk.datamodel.helpers.JsonSerializer;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StatementUpdateTest {

	final static ItemIdValue Q1 = Datamodel.makeWikidataItemIdValue("Q1");
	final static ItemIdValue Q2 = Datamodel.makeWikidataItemIdValue("Q2");
	final static ItemIdValue Q3 = Datamodel.makeWikidataItemIdValue("Q3");
	final static PropertyIdValue P1 = Datamodel
			.makeWikidataPropertyIdValue("P1");
	final static PropertyIdValue P2 = Datamodel
			.makeWikidataPropertyIdValue("P2");
	final static PropertyIdValue P3 = Datamodel
			.makeWikidataPropertyIdValue("P3");

	@Test
	public void testMergeReferences() {
		Reference r1 = ReferenceBuilder.newInstance().withPropertyValue(P1, Q1)
				.withPropertyValue(P2, Q2).build();
		Reference r2 = ReferenceBuilder.newInstance().withPropertyValue(P1, Q1)
				.build();
		Reference r3 = ReferenceBuilder.newInstance().withPropertyValue(P2, Q2)
				.build();
		Reference r4 = ReferenceBuilder.newInstance().withPropertyValue(P2, Q2)
				.withPropertyValue(P1, Q1).build();

		Statement s1 = StatementBuilder.forSubjectAndProperty(Q1, P1)
				.withValue(Q1).withReference(r1).withReference(r2)
				.withId("ID-s1").build();
		Statement s2 = StatementBuilder.forSubjectAndProperty(Q1, P1)
				.withValue(Q1).withReference(r3).withReference(r4).build();

		ItemDocument currentDocument = ItemDocumentBuilder.forItemId(Q1)
				.withStatement(s1).build();

		StatementUpdate su = new StatementUpdate(currentDocument,
				Arrays.asList(s2), Collections.<Statement> emptyList());

		Statement s1merged = StatementBuilder.forSubjectAndProperty(Q1, P1)
				.withValue(Q1).withReference(r1).withReference(r2)
				.withReference(r3).withId("ID-s1").build();

		assertEquals(0, su.toDelete.size());
		assertEquals(1, su.toKeep.size());
		assertTrue(su.toKeep.containsKey(P1));
		assertEquals(1, su.toKeep.get(P1).size());
		assertEquals(s1merged, su.toKeep.get(P1).get(0).statement);
		assertTrue(su.toKeep.get(P1).get(0).write);
	}

	@Test
	public void testMergeRanks() {
		Reference r1 = ReferenceBuilder.newInstance().withPropertyValue(P1, Q1)
				.withPropertyValue(P2, Q2).build();

		Statement s1 = StatementBuilder.forSubjectAndProperty(Q1, P1)
				.withRank(StatementRank.PREFERRED).withValue(Q1)
				.withId("ID-s1").build();
		Statement s2 = StatementBuilder.forSubjectAndProperty(Q1, P1)
				.withValue(Q1).withReference(r1).build();

		ItemDocument currentDocument = ItemDocumentBuilder.forItemId(Q1)
				.withStatement(s1).build();

		StatementUpdate su = new StatementUpdate(currentDocument,
				Arrays.asList(s2), Collections.<Statement> emptyList());

		Statement s1merged = StatementBuilder.forSubjectAndProperty(Q1, P1)
				.withValue(Q1).withRank(StatementRank.PREFERRED)
				.withReference(r1).withId("ID-s1").build();

		assertEquals(0, su.toDelete.size());
		assertEquals(1, su.toKeep.size());
		assertTrue(su.toKeep.containsKey(P1));
		assertEquals(1, su.toKeep.get(P1).size());
		assertEquals(s1merged, su.toKeep.get(P1).get(0).statement);
		assertTrue(su.toKeep.get(P1).get(0).write);
	}

	@Test
	public void testNoMergeDiffMainSnak() {
		Statement s1 = StatementBuilder.forSubjectAndProperty(Q1, P1)
				.withValue(Q1).withId("ID-s1").build();
		Statement s2 = StatementBuilder.forSubjectAndProperty(Q1, P1)
				.withValue(Q2).build();

		ItemDocument currentDocument = ItemDocumentBuilder.forItemId(Q1)
				.withStatement(s1).build();

		StatementUpdate su = new StatementUpdate(currentDocument,
				Arrays.asList(s2), Collections.<Statement> emptyList());

		assertEquals(0, su.toDelete.size());
		assertEquals(1, su.toKeep.size());
		assertTrue(su.toKeep.containsKey(P1));
		assertEquals(2, su.toKeep.get(P1).size());
		assertEquals(s2, su.toKeep.get(P1).get(0).statement);
		assertTrue(su.toKeep.get(P1).get(0).write);
		assertEquals(s1, su.toKeep.get(P1).get(1).statement);
		assertFalse(su.toKeep.get(P1).get(1).write);
	}

	@Test
	public void testNoMergeDiffQualifier() {
		Statement s1 = StatementBuilder.forSubjectAndProperty(Q1, P1)
				.withValue(Q1).withQualifierValue(P3, Q2).withId("ID-s1")
				.build();
		Statement s2 = StatementBuilder.forSubjectAndProperty(Q1, P1)
				.withValue(Q1).withQualifierValue(P3, Q3).build();

		ItemDocument currentDocument = ItemDocumentBuilder.forItemId(Q1)
				.withStatement(s1).build();

		StatementUpdate su = new StatementUpdate(currentDocument,
				Arrays.asList(s2), Collections.<Statement> emptyList());

		assertEquals(0, su.toDelete.size());
		assertEquals(1, su.toKeep.size());
		assertTrue(su.toKeep.containsKey(P1));
		assertEquals(2, su.toKeep.get(P1).size());
		assertEquals(s2, su.toKeep.get(P1).get(0).statement);
		assertTrue(su.toKeep.get(P1).get(0).write);
		assertEquals(s1, su.toKeep.get(P1).get(1).statement);
		assertFalse(su.toKeep.get(P1).get(1).write);
	}

	@Test
	public void testNoMergeRankConflict() {
		Statement s1 = StatementBuilder.forSubjectAndProperty(Q1, P1)
				.withValue(Q1).withRank(StatementRank.PREFERRED)
				.withId("ID-s1").build();
		Statement s2 = StatementBuilder.forSubjectAndProperty(Q1, P1)
				.withValue(Q1).withRank(StatementRank.DEPRECATED).build();

		ItemDocument currentDocument = ItemDocumentBuilder.forItemId(Q1)
				.withStatement(s1).build();

		StatementUpdate su = new StatementUpdate(currentDocument,
				Arrays.asList(s2), Collections.<Statement> emptyList());

		assertEquals(0, su.toDelete.size());
		assertEquals(1, su.toKeep.size());
		assertTrue(su.toKeep.containsKey(P1));
		assertEquals(2, su.toKeep.get(P1).size());
		assertEquals(s2, su.toKeep.get(P1).get(0).statement);
		assertTrue(su.toKeep.get(P1).get(0).write);
		assertEquals(s1, su.toKeep.get(P1).get(1).statement);
		assertFalse(su.toKeep.get(P1).get(1).write);
	}

	@Test
	public void testUpdateStatement() {

		Statement s1 = StatementBuilder.forSubjectAndProperty(Q1, P1)
				.withValue(Q1).withId("ID-s1").build();
		Statement s2 = StatementBuilder.forSubjectAndProperty(Q1, P1)
				.withValue(Q2).withId("ID-s1").build();

		ItemDocument currentDocument = ItemDocumentBuilder.forItemId(Q1)
				.withStatement(s1).build();

		StatementUpdate su = new StatementUpdate(currentDocument,
				Arrays.asList(s2), Collections.<Statement> emptyList());

		assertEquals(0, su.toDelete.size());
		assertEquals(1, su.toKeep.size());
		assertTrue(su.toKeep.containsKey(P1));
		assertEquals(1, su.toKeep.get(P1).size());
		assertEquals(s2, su.toKeep.get(P1).get(0).statement);
		assertTrue(su.toKeep.get(P1).get(0).write);
	}

	@Test
	public void testAddStatements() {
		// Inserting new P2 statements won't touch existing P1 statement
		Statement s1 = StatementBuilder.forSubjectAndProperty(Q1, P1)
				.withValue(Q1).withId("ID-s1").build();
		Statement s2 = StatementBuilder.forSubjectAndProperty(Q1, P2)
				.withValue(Q1).withId("ID-s2").build();
		Statement s3 = StatementBuilder.forSubjectAndProperty(Q1, P2)
				.withValue(Q2).build();
		Statement s4 = StatementBuilder.forSubjectAndProperty(Q1, P2)
				.withValue(Q3).build();

		ItemDocument currentDocument = ItemDocumentBuilder.forItemId(Q1)
				.withStatement(s1).withStatement(s2).build();

		StatementUpdate su = new StatementUpdate(currentDocument,
				Arrays.asList(s3, s4), Collections.<Statement> emptyList());

		assertEquals(0, su.toDelete.size());
		assertEquals(1, su.toKeep.size());
		assertTrue(su.toKeep.containsKey(P2));
		assertEquals(3, su.toKeep.get(P2).size());
		assertEquals(s3, su.toKeep.get(P2).get(0).statement);
		assertTrue(su.toKeep.get(P2).get(0).write);
		assertEquals(s4, su.toKeep.get(P2).get(1).statement);
		assertTrue(su.toKeep.get(P2).get(1).write);
		assertEquals(s2, su.toKeep.get(P2).get(2).statement);
		assertFalse(su.toKeep.get(P2).get(2).write);

		assertEquals("{\"claims\":[" + JsonSerializer.getJsonString(s3) + ","
				+ JsonSerializer.getJsonString(s4) + "]}",
				su.getJsonUpdateString());
	}

	@Test
	public void testDeleteAndAdd() {
		// Explicitly deleted statement won't merge
		Reference r1 = ReferenceBuilder.newInstance().withPropertyValue(P1, Q1)
				.build();
		Reference r2 = ReferenceBuilder.newInstance().withPropertyValue(P2, Q2)
				.build();

		Statement s3 = StatementBuilder.forSubjectAndProperty(Q1, P2)
				.withReference(r1).withValue(Q1).withId("ID-s3").build();
		Statement s4 = StatementBuilder.forSubjectAndProperty(Q1, P2)
				.withReference(r2).withValue(Q1).withId("ID-s4").build();

		ItemDocument currentDocument = ItemDocumentBuilder.forItemId(Q1)
				.withStatement(s4).build();

		List<Statement> addStatements = Arrays.asList(s3);
		List<Statement> deleteStatements = Arrays.asList(s4);

		StatementUpdate su = new StatementUpdate(currentDocument,
				addStatements, deleteStatements);

		assertTrue(su.toDelete.contains("ID-s4"));
		assertTrue(su.toKeep.containsKey(P2));
		assertEquals(1, su.toKeep.get(P2).size());
		assertEquals(s3, su.toKeep.get(P2).get(0).statement);
		assertFalse(su.isEmptyEdit());
	}

	@Test
	public void testMergeExisting() {
		// Existing duplicates are removed in passing, when modifying statements
		// of a property
		Statement s1 = StatementBuilder.forSubjectAndProperty(Q1, P1)
				.withValue(Q1).withId("ID-s1").build();
		Statement s2 = StatementBuilder.forSubjectAndProperty(Q1, P1)
				.withValue(Q1).withId("ID-s2").build();
		Statement s3 = StatementBuilder.forSubjectAndProperty(Q1, P1)
				.withValue(Q2).build();

		ItemDocument currentDocument = ItemDocumentBuilder.forItemId(Q1)
				.withStatement(s1).withStatement(s2).build();

		StatementUpdate su = new StatementUpdate(currentDocument,
				Arrays.asList(s3), Collections.<Statement> emptyList());

		assertEquals(su.toDelete, Arrays.asList("ID-s1"));
		assertEquals(1, su.toKeep.size());
		assertTrue(su.toKeep.containsKey(P1));
		assertEquals(2, su.toKeep.get(P1).size());
		assertEquals(s3, su.toKeep.get(P1).get(0).statement);
		assertTrue(su.toKeep.get(P1).get(0).write);
		assertEquals(s2, su.toKeep.get(P1).get(1).statement);
		assertFalse(su.toKeep.get(P1).get(1).write);
		assertFalse(su.isEmptyEdit());
	}
	
	@Test
	public void testNullEdit() {
		Statement s1 = StatementBuilder.forSubjectAndProperty(Q1, P1)
				.withValue(Q1).withId("ID-s1").build();
		Statement s1dup = StatementBuilder.forSubjectAndProperty(Q1, P1)
				.withValue(Q1).build();
		Statement s2 = StatementBuilder.forSubjectAndProperty(Q1, P1)
				.withValue(Q1).withId("ID-s2").build();
		
		ItemDocument currentDocument = ItemDocumentBuilder.forItemId(Q1)
				.withStatement(s1).build();
		
		StatementUpdate su = new StatementUpdate(currentDocument,
				Arrays.asList(s1dup), Arrays.asList(s2));
		assertTrue(su.isEmptyEdit());
		
	}

	@Test
	public void testMergeNew() {
		// Additions of duplicates are merged
		Statement s1 = StatementBuilder.forSubjectAndProperty(Q1, P3)
				.withValue(Q1).build();
		Statement s2 = StatementBuilder.forSubjectAndProperty(Q1, P3)
				.withValue(Q1).build();

		ItemDocument currentDocument = ItemDocumentBuilder.forItemId(Q1)
				.build();

		StatementUpdate su = new StatementUpdate(currentDocument,
				Arrays.asList(s1, s2), Collections.<Statement> emptyList());

		assertEquals(0, su.toDelete.size());
		assertEquals(1, su.toKeep.size());
		assertTrue(su.toKeep.containsKey(P3));
		assertEquals(1, su.toKeep.get(P3).size());
		assertEquals(s1, su.toKeep.get(P3).get(0).statement);
		assertTrue(su.toKeep.get(P3).get(0).write);
		assertFalse(su.isEmptyEdit());
	}

	@Test
	public void testDelete() throws JsonProcessingException, IOException {
		Statement s1 = StatementBuilder.forSubjectAndProperty(Q1, P1)
				.withValue(Q1).withId("ID-s1").build();
		Statement s2 = StatementBuilder.forSubjectAndProperty(Q1, P1)
				.withValue(Q2).withId("ID-s2").build();
		Statement s3 = StatementBuilder.forSubjectAndProperty(Q1, P1)
				.withValue(Q3).withId("ID-s3").build();
		Statement s4 = StatementBuilder.forSubjectAndProperty(Q1, P2)
				.withValue(Q1).withId("ID-s4").build();
		Statement s4changed = StatementBuilder.forSubjectAndProperty(Q1, P2)
				.withValue(Q2).withId("ID-s4").build();
		Statement s5 = StatementBuilder.forSubjectAndProperty(Q1, P2)
				.withValue(Q3).withId("ID-s5").build();
		Statement s6 = StatementBuilder.forSubjectAndProperty(Q1, P3)
				.withValue(Q1).withId("ID-s6").build();

		ItemDocument currentDocument = ItemDocumentBuilder.forItemId(Q1)
				.withStatement(s1).withStatement(s2).withStatement(s4changed)
				.withStatement(s5).withStatement(s6).build();

		StatementUpdate su = new StatementUpdate(currentDocument,
				Collections.<Statement> emptyList(), Arrays.asList(s2, s3, s4,
						s5));
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode expectedJson = mapper.readTree("{\"claims\":[{\"id\":\"ID-s2\",\"remove\":\"\"},{\"id\":\"ID-s5\",\"remove\":\"\"}]}");
		JsonNode actualJson = mapper.readTree(su.getJsonUpdateString());

		assertEquals(Arrays.asList("ID-s2", "ID-s5"), su.toDelete);
		assertEquals(0, su.toKeep.size());
		assertEquals(expectedJson, actualJson);
		assertFalse(su.isEmptyEdit());
	}

}
