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

public class SenseDocumentImplTest {

	private final ObjectMapper mapper = new DatamodelMapper("http://example.com/entity/");

	private final SenseIdValue sid = new SenseIdValueImpl("L42-S1", "http://example.com/entity/");
	private final Statement s = new StatementImpl("Q5721$b763ede3-42b3-5ecb-ec0e-4bb85d4d348d", StatementRank.NORMAL,
			new SomeValueSnakImpl(new PropertyIdValueImpl("P42", "http://example.com/entity/")),
			Collections.emptyList(), Collections.emptyList(), sid);
	private final List<StatementGroup> statementGroups = Collections.singletonList(
			new StatementGroupImpl(Collections.singletonList(s))
	);
	private final MonolingualTextValue rep = new TermImpl("en", "rep");
	private final List<MonolingualTextValue> repList = Collections.singletonList(rep);

	private final SenseDocument sd1 = new SenseDocumentImpl(sid, repList, statementGroups, 1234);
	private final SenseDocument sd2 = new SenseDocumentImpl(sid, repList, statementGroups, 1234);

	private final String JSON_SENSE = "{\"type\":\"sense\",\"id\":\"L42-S1\",\"glosses\":{\"en\":{\"language\":\"en\",\"value\":\"rep\"}},\"claims\":{\"P42\":[{\"rank\":\"normal\",\"id\":\"Q5721$b763ede3-42b3-5ecb-ec0e-4bb85d4d348d\",\"mainsnak\":{\"property\":\"P42\",\"snaktype\":\"somevalue\"},\"type\":\"statement\"}]},\"lastrevid\":1234}";

	@Test
	public void fieldsAreCorrect() {
		assertEquals(sd1.getEntityId(), sid);
		assertEquals(sd1.getGlosses(), Collections.singletonMap(rep.getLanguageCode(), rep));
		assertEquals(sd1.getStatementGroups(), statementGroups);
	}

	@Test
	public void equalityBasedOnContent() {
		SenseDocument irDiffGlosses = new SenseDocumentImpl(sid, Collections.singletonList(new MonolingualTextValueImpl("fr", "bar")), statementGroups, 1234);
		SenseDocument irDiffStatementGroups = new SenseDocumentImpl(sid, repList, Collections.emptyList(), 1234);
		SenseDocument irDiffRevisions = new SenseDocumentImpl(sid, repList, statementGroups, 1235);
		PropertyDocument pr = new PropertyDocumentImpl(
				new PropertyIdValueImpl("P42", "foo"),
				repList, Collections.emptyList(), Collections.emptyList(),
				Collections.emptyList(),
				new DatatypeIdImpl(DatatypeIdValue.DT_STRING), 1234);
		SenseDocument irDiffSenseIdValue = new SenseDocumentImpl(
				new SenseIdValueImpl("L42-S2", "http://example.com/entity/"),
				repList, Collections.emptyList(), 1235);

		assertEquals(sd1, sd1);
		assertEquals(sd1, sd2);
		assertNotEquals(sd1, irDiffGlosses);
		assertNotEquals(sd1, irDiffStatementGroups);
		assertNotEquals(sd1, irDiffRevisions);
		assertNotEquals(irDiffStatementGroups, irDiffSenseIdValue);
		assertNotEquals(sd1, pr);
		assertNotEquals(sd1, null);
		assertNotEquals(sd1, this);
	}

	@Test
	public void hashBasedOnContent() {
		assertEquals(sd1.hashCode(), sd2.hashCode());
	}

	@Test(expected = NullPointerException.class)
	public void idNotNull() {
		new SenseDocumentImpl(null, repList, statementGroups, 1234);
	}

	@Test(expected = NullPointerException.class)
	public void glossesNotNull() {
		new SenseDocumentImpl(sid,  null, statementGroups, 1234);
	}

	@Test(expected = IllegalArgumentException.class)
	public void glossesNotEmpty() {
		new SenseDocumentImpl(sid, Collections.emptyList(), statementGroups, 1234);
	}

	@Test
	public void statementGroupsCanBeNull() {
		SenseDocument doc = new SenseDocumentImpl(sid, repList, null, 1234);
		assertTrue(doc.getStatementGroups().isEmpty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void statementGroupsUseSameSubject() {
		SenseIdValue iid2 = new SenseIdValueImpl("Q23", "http://example.org/");
		Statement s2 = new StatementImpl("Q5721$b763ede3-42b3-5ecb-ec0e-4bb85d4d348d", StatementRank.NORMAL,
				new SomeValueSnakImpl(new PropertyIdValueImpl("P42", "http://wikibase.org/entity/")),
				Collections.emptyList(),  Collections.emptyList(), iid2);
		StatementGroup sg2 = new StatementGroupImpl(Collections.singletonList(s2));

		List<StatementGroup> statementGroups2 = new ArrayList<>();
		statementGroups2.add(statementGroups.get(0));
		statementGroups2.add(sg2);

		new SenseDocumentImpl(sid, repList, statementGroups2, 1234);
	}

	@Test
	public void iterateOverAllStatements() {
		Iterator<Statement> statements = sd1.getAllStatements();

		assertTrue(statements.hasNext());
		assertEquals(s, statements.next());
		assertFalse(statements.hasNext());
	}

	@Test
	public void testWithRevisionId() {
		assertEquals(1235L, sd1.withRevisionId(1235L).getRevisionId());
		assertEquals(sd1, sd1.withRevisionId(1325L).withRevisionId(sd1.getRevisionId()));
	}

	@Test
	public void testWithGlossInNewLanguage() {
		MonolingualTextValue newGloss = new MonolingualTextValueImpl("Foo", "fr");
		SenseDocument withGloss = sd1.withGloss(newGloss);
		assertEquals(newGloss, withGloss.getGlosses().get("fr"));
	}

	@Test
	public void testAddStatement() {
		Statement fresh = new StatementImpl("Q5721$b763ede3-42b3-5ecb-ec0e-4bb85d4d348d", StatementRank.NORMAL,
				new SomeValueSnakImpl(new PropertyIdValueImpl("P29", "http://example.com/entity/")),
				Collections.emptyList(), Collections.emptyList(), sid);
		Claim claim = fresh.getClaim();
		assertFalse(sd1.hasStatementValue(
				claim.getMainSnak().getPropertyId(),
				claim.getValue()));
		SenseDocument withStatement = sd1.withStatement(fresh);
		assertTrue(withStatement.hasStatementValue(
				claim.getMainSnak().getPropertyId(),
				claim.getValue()));
	}

	@Test
	public void testDeleteStatements() {
		Statement toRemove = statementGroups.get(0).getStatements().get(0);
		SenseDocument withoutStatement = sd1.withoutStatementIds(Collections.singleton(toRemove.getStatementId()));
		assertNotEquals(withoutStatement, sd1);
	}


	@Test
	public void testSenseToJson() throws JsonProcessingException {
		JsonComparator.compareJsonStrings(JSON_SENSE, mapper.writeValueAsString(sd1));
	}

	@Test
	public void testSenseToJava() throws IOException {
		assertEquals(sd1, mapper.readValue(JSON_SENSE, SenseDocumentImpl.class));
	}

}
