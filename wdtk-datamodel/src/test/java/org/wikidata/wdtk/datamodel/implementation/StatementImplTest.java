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

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.DatamodelMapper;
import org.wikidata.wdtk.datamodel.interfaces.*;

public class StatementImplTest {

	private final ObjectMapper mapper = new DatamodelMapper("http://example.com/entity/");

	private final EntityIdValue subjet = new ItemIdValueImpl("Q1", "http://example.com/entity/");
	private final EntityIdValue value = new ItemIdValueImpl("Q42", "http://example.com/entity/");
	private final PropertyIdValue property = new PropertyIdValueImpl("P42", "http://example.com/entity/");
	private final ValueSnak mainSnak = new ValueSnakImpl(property, value);
	private final List<SnakGroup> qualifiers = Collections.singletonList(new SnakGroupImpl(Collections.singletonList(mainSnak)));
	private final List<Reference> references = Collections.singletonList(new ReferenceImpl(qualifiers));
	private final Claim claim = new ClaimImpl(subjet, mainSnak, qualifiers);

	private final Statement s1 = new StatementImpl("MyId", StatementRank.PREFERRED, mainSnak,
			qualifiers, references, subjet);
	private final Statement s2 = new StatementImpl("MyId", StatementRank.PREFERRED, mainSnak,
			qualifiers, references, subjet);
	private final String JSON_STATEMENT = "{\"rank\":\"preferred\",\"references\":[{\"snaks\":{\"P42\":[{\"property\":\"P42\",\"datatype\":\"wikibase-item\",\"datavalue\":{\"value\":{\"id\":\"Q42\",\"numeric-id\":42,\"entity-type\":\"item\"},\"type\":\"wikibase-entityid\"},\"snaktype\":\"value\"}]},\"snaks-order\":[\"P42\"]}],\"id\":\"MyId\",\"mainsnak\":{\"property\":\"P42\",\"datatype\":\"wikibase-item\",\"datavalue\":{\"value\":{\"id\":\"Q42\",\"numeric-id\":42,\"entity-type\":\"item\"},\"type\":\"wikibase-entityid\"},\"snaktype\":\"value\"},\"qualifiers-order\":[\"P42\"],\"type\":\"statement\",\"qualifiers\":{\"P42\":[{\"property\":\"P42\",\"datatype\":\"wikibase-item\",\"datavalue\":{\"value\":{\"id\":\"Q42\",\"numeric-id\":42,\"entity-type\":\"item\"},\"type\":\"wikibase-entityid\"},\"snaktype\":\"value\"}]}}";

	private final Statement smallStatement = new StatementImpl("MyId", StatementRank.PREFERRED, mainSnak,
			Collections.emptyList(), Collections.emptyList(), subjet);
	private final String JSON_SMALL_STATEMENT = "{\"rank\":\"preferred\",\"id\":\"MyId\",\"mainsnak\":{\"property\":\"P42\",\"datatype\":\"wikibase-item\",\"datavalue\":{\"value\":{\"id\":\"Q42\",\"numeric-id\":42,\"entity-type\":\"item\"},\"type\":\"wikibase-entityid\"},\"snaktype\":\"value\"},\"type\":\"statement\"}";

	@Test
	public void gettersWorking() {
		assertEquals(s1.getClaim(), claim);
		assertEquals(s1.getMainSnak(), mainSnak);
		assertEquals(s1.getQualifiers(), qualifiers);
		assertEquals(s1.getReferences(), references);
		assertEquals(s1.getRank(), StatementRank.PREFERRED);
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
	public void withId() {
		Statement statement = new StatementImpl(null, StatementRank.NORMAL, claim.getMainSnak(), claim.getQualifiers(), Collections.emptyList(), claim.getSubject());
		Statement withId = statement.withStatementId("some id");
		assertEquals("some id", withId.getStatementId());
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
		assertNotEquals(s1, sDiffClaim);
		assertNotEquals(s1, sDiffReferences);
		assertNotEquals(s1, sDiffRank);
		assertNotEquals(s1, sDiffId);
		assertNotEquals(s1, null);
		assertNotEquals(s1, this);
	}

	@Test
	public void testStatementToJson() throws JsonProcessingException {
		JsonComparator.compareJsonStrings(JSON_STATEMENT, mapper.writeValueAsString(s1));
	}

	@Test
	public void testStatementToJava() throws IOException {
		assertEquals(s1, mapper.readValue(JSON_STATEMENT, StatementImpl.PreStatement.class).withSubject(subjet));
	}

	@Test
	public void testSmallStatementToJson() throws JsonProcessingException {
		JsonComparator.compareJsonStrings(JSON_SMALL_STATEMENT, mapper.writeValueAsString(smallStatement));
	}

	@Test
	public void testSmallStatementToJava() throws IOException {
		assertEquals(smallStatement, mapper.readValue(JSON_SMALL_STATEMENT, StatementImpl.PreStatement.class).withSubject(subjet));
	}
}
