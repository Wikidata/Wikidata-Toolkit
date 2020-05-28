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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.DatamodelMapper;
import org.wikidata.wdtk.datamodel.interfaces.*;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;

public class FormDocumentImplTest {

	private final ObjectMapper mapper = new DatamodelMapper("http://example.com/entity/");

	private final FormIdValue fid = new FormIdValueImpl("L42-F1", "http://example.com/entity/");
	private final List<ItemIdValue> gramFeatures = Arrays.asList(
			new ItemIdValueImpl("Q2", "http://example.com/entity/"),
			new ItemIdValueImpl("Q1", "http://example.com/entity/")
	);
	private final Statement s = new StatementImpl("MyId", StatementRank.NORMAL,
			new SomeValueSnakImpl(new PropertyIdValueImpl("P42", "http://example.com/entity/")),
			Collections.emptyList(), Collections.emptyList(), fid);
	private final List<StatementGroup> statementGroups = Collections.singletonList(
			new StatementGroupImpl(Collections.singletonList(s))
	);
	private final MonolingualTextValue rep = new TermImpl("en", "rep");
	private final List<MonolingualTextValue> repList = Collections.singletonList(rep);

	private final FormDocument fd1 = new FormDocumentImpl(fid, repList, gramFeatures, statementGroups, 1234);
	private final FormDocument fd2 = new FormDocumentImpl(fid, repList, gramFeatures, statementGroups, 1234);

	private final String JSON_FORM = "{\"type\":\"form\",\"id\":\"L42-F1\",\"grammaticalFeatures\":[\"Q1\",\"Q2\"],\"representations\":{\"en\":{\"language\":\"en\",\"value\":\"rep\"}},\"claims\":{\"P42\":[{\"rank\":\"normal\",\"id\":\"MyId\",\"mainsnak\":{\"property\":\"P42\",\"snaktype\":\"somevalue\"},\"type\":\"statement\"}]},\"lastrevid\":1234}";

	@Test
	public void fieldsAreCorrect() {
		assertEquals(fd1.getEntityId(), fid);
		assertEquals(fd1.getRepresentations(), Collections.singletonMap(rep.getLanguageCode(), rep));
		assertEquals(fd1.getGrammaticalFeatures(), gramFeatures);
		assertEquals(fd1.getStatementGroups(), statementGroups);
	}

	@Test
	public void equalityBasedOnContent() {
		FormDocument irDiffRepresentations = new FormDocumentImpl(fid, Collections.singletonList(new MonolingualTextValueImpl("fr", "bar")), gramFeatures, statementGroups, 1234);
		FormDocument irDiffGramFeatures = new FormDocumentImpl(fid, repList, Collections.emptyList(), statementGroups, 1234);
		FormDocument irDiffStatementGroups = new FormDocumentImpl(fid, repList, gramFeatures, Collections.emptyList(), 1234);
		FormDocument irDiffRevisions = new FormDocumentImpl(fid, repList, gramFeatures, statementGroups, 1235);
		PropertyDocument pr = new PropertyDocumentImpl(
				new PropertyIdValueImpl("P42", "foo"),
				repList, Collections.emptyList(), Collections.emptyList(),
				Collections.emptyList(),
				new DatatypeIdImpl(DatatypeIdValue.DT_STRING), 1234);
		FormDocument irDiffFormIdValue = new FormDocumentImpl(
				new FormIdValueImpl("L42-F2", "http://example.com/entity/"),
				repList, gramFeatures, Collections.emptyList(), 1235);

		assertEquals(fd1, fd1);
		assertEquals(fd1, fd2);
		assertNotEquals(fd1, irDiffRepresentations);
		assertNotEquals(fd1, irDiffGramFeatures);
		assertNotEquals(fd1, irDiffStatementGroups);
		assertNotEquals(fd1, irDiffRevisions);
		assertNotEquals(irDiffStatementGroups, irDiffFormIdValue);
		assertNotEquals(fd1, pr);
		assertNotEquals(fd1, null);
		assertNotEquals(fd1, this);
	}

	@Test
	public void hashBasedOnContent() {
		assertEquals(fd1.hashCode(), fd2.hashCode());
	}

	@Test(expected = NullPointerException.class)
	public void idNotNull() {
		new FormDocumentImpl(null, repList, gramFeatures, statementGroups, 1234);
	}

	@Test(expected = NullPointerException.class)
	public void representationsNotNull() {
		new FormDocumentImpl(fid,  null, gramFeatures, statementGroups, 1234);
	}

	@Test(expected = IllegalArgumentException.class)
	public void representationsNotEmpty() {
		new FormDocumentImpl(fid, Collections.emptyList(), gramFeatures, statementGroups, 1234);
	}

	@Test
	public void grammaticalFeaturesCanBeNull() {
		FormDocument doc = new FormDocumentImpl(fid, repList, null, statementGroups, 1234);
		assertTrue(doc.getGrammaticalFeatures().isEmpty());
	}

	@Test
	public void statementGroupsCanBeNull() {
		FormDocument doc = new FormDocumentImpl(fid, repList, gramFeatures, null, 1234);
		assertTrue(doc.getStatementGroups().isEmpty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void statementGroupsUseSameSubject() {
		FormIdValue iid2 = new FormIdValueImpl("Q23", "http://example.org/");
		Statement s2 = new StatementImpl("MyId", StatementRank.NORMAL,
				new SomeValueSnakImpl(new PropertyIdValueImpl("P42", "http://wikibase.org/entity/")),
				Collections.emptyList(),  Collections.emptyList(), iid2);
		StatementGroup sg2 = new StatementGroupImpl(Collections.singletonList(s2));

		List<StatementGroup> statementGroups2 = new ArrayList<>();
		statementGroups2.add(statementGroups.get(0));
		statementGroups2.add(sg2);

		new FormDocumentImpl(fid, repList, gramFeatures, statementGroups2, 1234);
	}

	@Test
	public void iterateOverAllStatements() {
		Iterator<Statement> statements = fd1.getAllStatements();

		assertTrue(statements.hasNext());
		assertEquals(s, statements.next());
		assertFalse(statements.hasNext());
	}

	@Test
	public void testWithRevisionId() {
		assertEquals(1235L, fd1.withRevisionId(1235L).getRevisionId());
		assertEquals(fd1, fd1.withRevisionId(1325L).withRevisionId(fd1.getRevisionId()));
	}

	@Test
	public void testWithRepresentationInNewLanguage() {
		MonolingualTextValue newRepresentation = new MonolingualTextValueImpl("Foo", "fr");
		FormDocument withRepresentation = fd1.withRepresentation(newRepresentation);
		assertEquals(newRepresentation, withRepresentation.getRepresentations().get("fr"));
	}

	@Test
	public void testWithNewGrammaticalFeatures() {
		ItemIdValue newGrammaticalFeature = new ItemIdValueImpl("Q3", "http://example.com/entity/");
		FormDocument withGrammaticalFeature = fd1.withGrammaticalFeature(newGrammaticalFeature);
		assertTrue(withGrammaticalFeature.getGrammaticalFeatures().containsAll(gramFeatures));
		assertTrue(withGrammaticalFeature.getGrammaticalFeatures().contains(newGrammaticalFeature));
	}

	@Test
	public void testWithExistingGrammaticalFeatures() {
		ItemIdValue newGrammaticalFeature = new ItemIdValueImpl("Q2", "http://example.com/entity/");
		FormDocument withGrammaticalFeature = fd1.withGrammaticalFeature(newGrammaticalFeature);
		assertEquals(fd1, withGrammaticalFeature);
	}

	@Test
	public void testAddStatement() {
		Statement fresh = new StatementImpl("MyFreshId", StatementRank.NORMAL,
				new SomeValueSnakImpl(new PropertyIdValueImpl("P29", "http://example.com/entity/")),
				Collections.emptyList(), Collections.emptyList(), fid);
		Claim claim = fresh.getClaim();
		assertFalse(fd1.hasStatementValue(
				claim.getMainSnak().getPropertyId(),
				claim.getValue()));
		FormDocument withStatement = fd1.withStatement(fresh);
		assertTrue(withStatement.hasStatementValue(
				claim.getMainSnak().getPropertyId(),
				claim.getValue()));
	}

	@Test
	public void testDeleteStatements() {
		Statement toRemove = statementGroups.get(0).getStatements().get(0);
		FormDocument withoutStatement = fd1.withoutStatementIds(Collections.singleton(toRemove.getStatementId()));
		assertNotEquals(withoutStatement, fd1);
	}

	@Test
	public void testFormToJson() throws JsonProcessingException {
		JsonComparator.compareJsonStrings(JSON_FORM, mapper.writeValueAsString(fd1));
	}

	@Test
	public void testFormToJava() throws IOException {
		assertEquals(fd1, mapper.readValue(JSON_FORM, FormDocumentImpl.class));
	}

}
