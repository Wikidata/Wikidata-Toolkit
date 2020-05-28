package org.wikidata.wdtk.datamodel.implementation;

/*
 * #%L
 * Wikidata Toolkit Data Model
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

import org.junit.Test;
import org.mockito.internal.util.collections.Sets;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.ItemDocumentBuilder;
import org.wikidata.wdtk.datamodel.helpers.StatementBuilder;
import org.wikidata.wdtk.datamodel.interfaces.*;

import static org.junit.Assert.*;

/**
 * Test general statement access methods as implemented in
 * {@link StatementDocument}.
 *
 * @author Markus Kroetzsch
 *
 */
public class StatementDocumentAccessTest {

	private final static ItemIdValue q1 = Datamodel.makeWikidataItemIdValue("Q1");
	private final static ItemIdValue q2 = Datamodel.makeWikidataItemIdValue("Q2");
	private final static PropertyIdValue p1 = Datamodel
			.makeWikidataPropertyIdValue("P1");
	private final static PropertyIdValue p2 = Datamodel
			.makeWikidataPropertyIdValue("P2");
	private final static PropertyIdValue p3 = Datamodel
			.makeWikidataPropertyIdValue("P3");

	@Test
	public void testFindUniqueStatements() {
		Statement s1 = StatementBuilder.forSubjectAndProperty(q1, p1)
				.withValue(q1).build();
		Statement s2 = StatementBuilder.forSubjectAndProperty(q1, p2)
				.withValue(q1).build();
		Statement s3 = StatementBuilder.forSubjectAndProperty(q1, p2)
				.withValue(q2).build();

		ItemDocument id = ItemDocumentBuilder.forItemId(q1).withStatement(s1)
				.withStatement(s2).withStatement(s3).build();

		assertTrue(id.hasStatement(p1));
		assertTrue(id.hasStatement("P1"));
		assertTrue(id.hasStatement(p2));
		assertTrue(id.hasStatement("P2"));
		assertFalse(id.hasStatement(p3));
		assertFalse(id.hasStatement("P3"));

		assertEquals(s1, id.findStatement(p1));
		assertEquals(s1, id.findStatement("P1"));
		assertNull(id.findStatement(p2));
		assertNull(id.findStatement("P2"));
		assertNull(id.findStatement(p3));
		assertNull(id.findStatement("P3"));

		assertEquals(q1, id.findStatementValue(p1));
		assertNull(id.findStatementValue(p2));
		assertNull(id.findStatementValue(p3));
	}

	@Test
	public void testHasStatementValue() {
		Statement s1 = StatementBuilder.forSubjectAndProperty(q1, p1)
				.withValue(q1).build();
		Statement s2 = StatementBuilder.forSubjectAndProperty(q1, p1)
				.withValue(q2).build();
		Statement s3 = StatementBuilder.forSubjectAndProperty(q1, p1)
				.withSomeValue().build();

		ItemDocument id = ItemDocumentBuilder.forItemId(q1).withStatement(s1)
				.withStatement(s2).withStatement(s3).build();

		assertTrue(id.hasStatementValue(p1, q2));
		assertTrue(id.hasStatementValue("P1", q2));
		assertTrue(id.hasStatementValue(p1, Sets.newSet(q1, p3)));
		assertFalse(id.hasStatementValue(p1, p3));
		assertFalse(id.hasStatementValue("P2", q2));
	}

	@Test
	public void testFindValueSnaks() {
		Statement s1 = StatementBuilder.forSubjectAndProperty(q1, p1)
				.withValue(q1).build();
		Statement s2 = StatementBuilder.forSubjectAndProperty(q1, p2)
				.withSomeValue().build();
		Statement s3 = StatementBuilder.forSubjectAndProperty(q1, p3)
				.withNoValue().build();

		ItemDocument id = ItemDocumentBuilder.forItemId(q1).withStatement(s1)
				.withStatement(s2).withStatement(s3).build();

		assertEquals(s1, id.findStatement(p1));
		assertEquals(s1, id.findStatement("P1"));
		assertEquals(s2, id.findStatement(p2));
		assertEquals(s2, id.findStatement("P2"));
		assertEquals(s3, id.findStatement(p3));
		assertEquals(s3, id.findStatement("P3"));

		assertEquals(q1, id.findStatementValue(p1));
		assertNull(id.findStatementValue(p2));
		assertNull(id.findStatementValue(p3));
	}

	@Test
	public void testFindStatementItemIdValue() {
		Statement s = StatementBuilder.forSubjectAndProperty(q1, p1)
				.withValue(q1).build();

		ItemDocument id = ItemDocumentBuilder.forItemId(q1).withStatement(s)
				.build();

		assertEquals(q1, id.findStatementValue(p1));
		assertEquals(q1, id.findStatementValue("P1"));
		assertEquals(q1, id.findStatementEntityIdValue(p1));
		assertEquals(q1, id.findStatementEntityIdValue("P1"));
		assertEquals(q1, id.findStatementItemIdValue(p1));
		assertEquals(q1, id.findStatementItemIdValue("P1"));
		assertNull(id.findStatementPropertyIdValue(p1));
		assertNull(id.findStatementPropertyIdValue("P1"));
	}

	@Test
	public void testFindStatementPropertyIdValue() {
		Statement s = StatementBuilder.forSubjectAndProperty(q1, p1)
				.withValue(p2).build();

		ItemDocument id = ItemDocumentBuilder.forItemId(q1).withStatement(s)
				.build();

		assertEquals(p2, id.findStatementValue(p1));
		assertEquals(p2, id.findStatementValue("P1"));
		assertEquals(p2, id.findStatementEntityIdValue(p1));
		assertEquals(p2, id.findStatementEntityIdValue("P1"));
		assertEquals(p2, id.findStatementPropertyIdValue(p1));
		assertEquals(p2, id.findStatementPropertyIdValue("P1"));
		assertNull(id.findStatementItemIdValue(p1));
		assertNull(id.findStatementItemIdValue("P1"));
	}

	@Test
	public void testFindStatementTimeValue() {
		TimeValue v = Datamodel.makeTimeValue((byte) 2015, (byte) 10,
				(byte) 16, (byte) 16, (byte) 51, (byte) 23,
				TimeValue.PREC_SECOND, 0, 0, 0, TimeValue.CM_GREGORIAN_PRO);

		Statement s = StatementBuilder.forSubjectAndProperty(q1, p1)
				.withValue(v).build();

		ItemDocument id = ItemDocumentBuilder.forItemId(q1).withStatement(s)
				.build();

		assertEquals(v, id.findStatementValue(p1));
		assertEquals(v, id.findStatementValue("P1"));
		assertEquals(v, id.findStatementTimeValue(p1));
		assertEquals(v, id.findStatementTimeValue("P1"));
	}

	@Test
	public void testFindStatementGlobeCoordinatesValue() {
		GlobeCoordinatesValue v = Datamodel.makeGlobeCoordinatesValue(1.2, 2.3,
				1, GlobeCoordinatesValue.GLOBE_MOON);

		Statement s = StatementBuilder.forSubjectAndProperty(q1, p1)
				.withValue(v).build();

		ItemDocument id = ItemDocumentBuilder.forItemId(q1).withStatement(s)
				.build();

		assertEquals(v, id.findStatementValue(p1));
		assertEquals(v, id.findStatementValue("P1"));
		assertEquals(v, id.findStatementGlobeCoordinatesValue(p1));
		assertEquals(v, id.findStatementGlobeCoordinatesValue("P1"));
	}

	@Test
	public void testFindStatementQuantityValue() {
		QuantityValue v = Datamodel.makeQuantityValue(1234, 1233, 1235);

		Statement s = StatementBuilder.forSubjectAndProperty(q1, p1)
				.withValue(v).build();

		ItemDocument id = ItemDocumentBuilder.forItemId(q1).withStatement(s)
				.build();

		assertEquals(v, id.findStatementValue(p1));
		assertEquals(v, id.findStatementValue("P1"));
		assertEquals(v, id.findStatementQuantityValue(p1));
		assertEquals(v, id.findStatementQuantityValue("P1"));
	}

	@Test
	public void testFindStatementMonolingualTextValue() {
		MonolingualTextValue v = Datamodel.makeMonolingualTextValue("Test",
				"en");

		Statement s = StatementBuilder.forSubjectAndProperty(q1, p1)
				.withValue(v).build();

		ItemDocument id = ItemDocumentBuilder.forItemId(q1).withStatement(s)
				.build();

		assertEquals(v, id.findStatementValue(p1));
		assertEquals(v, id.findStatementValue("P1"));
		assertEquals(v, id.findStatementMonolingualTextValue(p1));
		assertEquals(v, id.findStatementMonolingualTextValue("P1"));
	}

	@Test
	public void testFindStatementStringValue() {
		StringValue v = Datamodel.makeStringValue("Test");

		Statement s = StatementBuilder.forSubjectAndProperty(q1, p1)
				.withValue(v).build();

		ItemDocument id = ItemDocumentBuilder.forItemId(q1).withStatement(s)
				.build();

		assertEquals(v, id.findStatementValue(p1));
		assertEquals(v, id.findStatementValue("P1"));
		assertEquals(v, id.findStatementStringValue(p1));
		assertEquals(v, id.findStatementStringValue("P1"));
	}

}
