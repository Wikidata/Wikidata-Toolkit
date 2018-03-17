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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.Collections;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.DatamodelMapper;
import org.wikidata.wdtk.datamodel.implementation.json.JacksonPreStatement;
import org.wikidata.wdtk.datamodel.implementation.json.JsonComparator;
import org.wikidata.wdtk.datamodel.implementation.json.JsonTestData;
import org.wikidata.wdtk.datamodel.interfaces.*;

public class StatementImplTest {

	private final ObjectMapper mapper = new DatamodelMapper(Datamodel.SITE_WIKIDATA);

	private final EntityIdValue subjet = new ItemIdValueImpl("Q1", "http://wikidata.org/entity/");
	private final EntityIdValue value = new ItemIdValueImpl("Q42", "http://wikidata.org/entity/");
	private final PropertyIdValue property = new PropertyIdValueImpl("P42", "http://wikidata.org/entity/");
	private final ValueSnak mainSnak = new ValueSnakImpl(property, value);
	private final Claim claim = new ClaimImpl(subjet, mainSnak, Collections.emptyList());

	private final Statement s1 = new StatementImpl("MyId", StatementRank.NORMAL, mainSnak,
			Collections.emptyList(), Collections.emptyList(), subjet);
	private final Statement s2 = new StatementImpl("MyId", StatementRank.NORMAL, mainSnak,
			Collections.emptyList(), Collections.emptyList(), subjet);

	@Test
	public void gettersWorking() {
		assertEquals(s1.getClaim(), claim);
		assertEquals(s1.getMainSnak(), mainSnak);
		assertEquals(s1.getQualifiers(), Collections.emptyList());
		assertEquals(s1.getReferences(), Collections. emptyList());
		assertEquals(s1.getRank(), StatementRank.NORMAL);
		assertEquals(s1.getStatementId(), "MyId");
		assertEquals(s1.getValue(), value);
		assertEquals(s1.getSubject(), subjet);
	}

	@Test(expected = NullPointerException.class)
	public void mainSnakNotNull() {
		new StatementImpl("MyId", StatementRank.NORMAL, null,
				Collections.emptyList(), Collections.emptyList(), value);
	}

	@Test
	public void referencesCanBeNull() {
		Statement statement = new StatementImpl("MyId", StatementRank.NORMAL, mainSnak,  Collections.emptyList(), null, value);
		assertTrue(statement.getReferences().isEmpty());
	}

	@Test(expected = NullPointerException.class)
	public void rankNotNull() {
		new StatementImpl("MyId", null, mainSnak,
				Collections.emptyList(), Collections.emptyList(), value);
	}

	@Test
	public void idCanBeNull() {
		Statement statement = new StatementImpl(null, StatementRank.NORMAL, mainSnak,
				Collections.emptyList(), Collections.emptyList(), value);
		assertEquals(statement.getStatementId(), "");
	}

	@Test
	public void hashBasedOnContent() {
		assertEquals(s1.hashCode(), s2.hashCode());
	}

	@Test
	public void equalityBasedOnContent() {
		Statement sDiffClaim = new StatementImpl("MyId", StatementRank.NORMAL, mainSnak,
				Collections.emptyList(), Collections.emptyList(),
				new ItemIdValueImpl("Q43", "http://wikidata.org/entity/"));
		Statement sDiffReferences = new StatementImpl("MyId", StatementRank.NORMAL, mainSnak,
				Collections.emptyList(), Collections.singletonList(new ReferenceImpl(
						Collections.singletonList(new SnakGroupImpl(Collections.singletonList(mainSnak)))
				)), value);
		Statement sDiffRank = new StatementImpl("MyId", StatementRank.PREFERRED, mainSnak,
				Collections.emptyList(), Collections.emptyList(), value);
		Statement sDiffId = new StatementImpl("MyOtherId", StatementRank.NORMAL, mainSnak,
				Collections.emptyList(), Collections.emptyList(), value);

		assertEquals(s1, s1);
		assertEquals(s1, s2);
		assertThat(s1, not(equalTo(sDiffClaim)));
		assertThat(s1, not(equalTo(sDiffReferences)));
		assertThat(s1, not(equalTo(sDiffRank)));
		assertThat(s1, not(equalTo(sDiffId)));
		assertThat(s1, not(equalTo(null)));
		assertFalse(s1.equals(this));
	}

	@Test
	public void testEmptyStatementToJson() throws JsonProcessingException {
		StatementImpl statement = JsonTestData.getTestNoValueStatement();

		String result = mapper.writeValueAsString(statement);
		JsonComparator.compareJsonStrings(JsonTestData.JSON_NOVALUE_STATEMENT,
				result);
	}

	@Test
	public void testFullStatementToJson() throws IOException {
		Statement statement = DataObjectFactoryImplTest.getTestStatement(2, 3, 4, EntityIdValue.ET_ITEM);
		ObjectMapper mapper = new DatamodelMapper(statement.getSubject().getSiteIri());
		String json = mapper.writeValueAsString(statement);
		JacksonPreStatement deserialized = mapper.readValue(json, JacksonPreStatement.class);
		assertEquals(statement, deserialized.withSubject(statement.getSubject()));
	}

	@Test
	public void testEmptyStatementNoIdToJson() throws JsonProcessingException {
		StatementImpl statement = JsonTestData.getTestNoValueNoIdStatement();

		String result = mapper.writeValueAsString(statement);
		JsonComparator.compareJsonStrings(
				JsonTestData.JSON_NOVALUE_NOID_STATEMENT, result);
	}

	@Test
	public void testEmptyStatementToJava() throws
			IOException {
		StatementImpl result = mapper.readValue(
				JsonTestData.JSON_NOVALUE_STATEMENT, JacksonPreStatement.class).withSubject(
				JsonTestData.getEmptyTestItemDocument().getEntityId());

		assertNotNull(result);
		assertNull(result.getValue());
		assertNull(result.getClaim().getValue());
		assertEquals(JsonTestData.getTestNoValueStatement(), result);
	}

	@Test
	public void testEquality() {
		StatementImpl correctStatement = new StatementImpl(
				JsonTestData.TEST_STATEMENT_ID, JsonTestData.TEST_NOVALUE_SNAK,
				JsonTestData.getEmptyTestItemDocument()
						.getEntityId());
		StatementImpl wrongId = new StatementImpl("another id",
				JsonTestData.TEST_NOVALUE_SNAK,
				JsonTestData.getEmptyTestItemDocument()
						.getEntityId());

		assertEquals(JsonTestData.getTestNoValueStatement(), JsonTestData.getTestNoValueStatement());
		assertEquals(JsonTestData.getTestNoValueStatement(), correctStatement);
		assertFalse(JsonTestData.getTestNoValueStatement().equals(wrongId));
	}
}
