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
package org.wikidata.wdtk.datamodel.implementation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.wikidata.wdtk.datamodel.implementation.JsonTestUtils.producesJson;
import static org.wikidata.wdtk.datamodel.implementation.JsonTestUtils.toJson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.StatementBuilder;
import org.wikidata.wdtk.datamodel.helpers.StatementUpdateBuilder;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;

public class StatementUpdateImplTest {

	private static final Collection<Statement> NO_STATEMENTS = Collections.emptyList();
	private static final Collection<String> NO_IDS = Collections.emptyList();
	static final ItemIdValue JOHN = Datamodel.makeWikidataItemIdValue("Q1");
	private static final EntityIdValue RITA = Datamodel.makeWikidataItemIdValue("Q2");
	private static final PropertyIdValue HAIR = Datamodel.makeWikidataPropertyIdValue("P1");
	private static final PropertyIdValue EYES = Datamodel.makeWikidataPropertyIdValue("P2");
	private static final StringValue BROWN = Datamodel.makeStringValue("brown");
	private static final StringValue SILVER = Datamodel.makeStringValue("silver");
	private static final StringValue BLUE = Datamodel.makeStringValue("blue");
	private static final Statement NOBODY_HAS_BROWN_HAIR = StatementBuilder
			.forSubjectAndProperty(ItemIdValue.NULL, HAIR)
			.withValue(BROWN)
			.build();
	private static final Statement NOBODY_ALREADY_HAS_BROWN_HAIR = NOBODY_HAS_BROWN_HAIR.withStatementId("ID1");
	static final Statement JOHN_HAS_BROWN_HAIR = StatementBuilder
			.forSubjectAndProperty(JOHN, HAIR)
			.withValue(BROWN)
			.build();
	static final Statement RITA_HAS_BROWN_HAIR = StatementBuilder
			.forSubjectAndProperty(RITA, HAIR)
			.withValue(BROWN)
			.build();
	private static final Statement JOHN_HAS_SILVER_HAIR = StatementBuilder
			.forSubjectAndProperty(JOHN, HAIR)
			.withValue(SILVER)
			.build();
	private static final Statement JOHN_ALREADY_HAS_SILVER_HAIR = JOHN_HAS_SILVER_HAIR.withStatementId("ID5");
	private static final Statement JOHN_HAS_BLUE_EYES = StatementBuilder
			.forSubjectAndProperty(JOHN, EYES)
			.withValue(BLUE)
			.build();
	private static final Statement JOHN_ALREADY_HAS_BLUE_EYES = JOHN_HAS_BLUE_EYES.withStatementId("ID8");

	@Test
	public void testFields() {
		StatementUpdate update = new StatementUpdateImpl(
				Arrays.asList(JOHN_HAS_BROWN_HAIR),
				Arrays.asList(JOHN_ALREADY_HAS_BLUE_EYES),
				Arrays.asList(JOHN_ALREADY_HAS_SILVER_HAIR.getStatementId()));
		assertEquals(Arrays.asList(JOHN_HAS_BROWN_HAIR), update.getAdded());
		assertThat(update.getReplaced().keySet(), containsInAnyOrder(JOHN_ALREADY_HAS_BLUE_EYES.getStatementId()));
		assertEquals(JOHN_ALREADY_HAS_BLUE_EYES, update.getReplaced().get(JOHN_ALREADY_HAS_BLUE_EYES.getStatementId()));
		assertThat(update.getRemoved(), containsInAnyOrder(JOHN_ALREADY_HAS_SILVER_HAIR.getStatementId()));
	}

	@Test
	public void testValidation() {
		new StatementUpdateImpl(NO_STATEMENTS, NO_STATEMENTS, NO_IDS);
		assertThrows(NullPointerException.class, () -> new StatementUpdateImpl(null, NO_STATEMENTS, NO_IDS));
		assertThrows(NullPointerException.class, () -> new StatementUpdateImpl(NO_STATEMENTS, null, NO_IDS));
		assertThrows(NullPointerException.class, () -> new StatementUpdateImpl(NO_STATEMENTS, NO_STATEMENTS, null));
		assertThrows(NullPointerException.class, () -> new StatementUpdateImpl(
				Arrays.asList(JOHN_HAS_BROWN_HAIR, null), NO_STATEMENTS, NO_IDS));
		assertThrows(NullPointerException.class, () -> new StatementUpdateImpl(
				NO_STATEMENTS, Arrays.asList(JOHN_ALREADY_HAS_BLUE_EYES, null), NO_IDS));
		assertThrows(NullPointerException.class, () -> new StatementUpdateImpl(
				NO_STATEMENTS, NO_STATEMENTS, Arrays.asList(JOHN_ALREADY_HAS_SILVER_HAIR.getStatementId(), null)));
		assertThrows(IllegalArgumentException.class, () -> new StatementUpdateImpl(
				Arrays.asList(JOHN_ALREADY_HAS_BLUE_EYES), NO_STATEMENTS, NO_IDS));
		assertThrows(IllegalArgumentException.class, () -> new StatementUpdateImpl(
				NO_STATEMENTS, Arrays.asList(JOHN_HAS_BLUE_EYES), NO_IDS));
		assertThrows(IllegalArgumentException.class, () -> new StatementUpdateImpl(
				NO_STATEMENTS, NO_STATEMENTS, Arrays.asList(" ")));
		assertThrows(IllegalArgumentException.class, () -> new StatementUpdateImpl(
				NO_STATEMENTS, Arrays.asList(JOHN_ALREADY_HAS_BLUE_EYES, JOHN_ALREADY_HAS_BLUE_EYES), NO_IDS));
		assertThrows(IllegalArgumentException.class,
				() -> new StatementUpdateImpl(NO_STATEMENTS, NO_STATEMENTS, Arrays.asList(
						JOHN_ALREADY_HAS_BLUE_EYES.getStatementId(), JOHN_ALREADY_HAS_BLUE_EYES.getStatementId())));
		assertThrows(IllegalArgumentException.class, () -> new StatementUpdateImpl(NO_STATEMENTS,
				Arrays.asList(JOHN_ALREADY_HAS_BLUE_EYES), Arrays.asList(JOHN_ALREADY_HAS_BLUE_EYES.getStatementId())));
		assertThrows(IllegalArgumentException.class, () -> new StatementUpdateImpl(
				Arrays.asList(RITA_HAS_BROWN_HAIR), Arrays.asList(JOHN_ALREADY_HAS_BLUE_EYES), NO_IDS));
		assertThrows(IllegalArgumentException.class, () -> new StatementUpdateImpl(
				Arrays.asList(NOBODY_HAS_BROWN_HAIR), NO_STATEMENTS, NO_IDS));
		assertThrows(IllegalArgumentException.class, () -> new StatementUpdateImpl(
				NO_STATEMENTS, Arrays.asList(NOBODY_ALREADY_HAS_BROWN_HAIR), NO_IDS));
	}

	@Test
	public void testImmutability() {
		List<Statement> added = new ArrayList<>();
		List<Statement> replaced = new ArrayList<>();
		List<String> removed = new ArrayList<>();
		added.add(JOHN_HAS_BROWN_HAIR);
		replaced.add(JOHN_ALREADY_HAS_BLUE_EYES);
		removed.add(JOHN_ALREADY_HAS_SILVER_HAIR.getStatementId());
		StatementUpdate update = new StatementUpdateImpl(added, replaced, removed);
		assertThrows(UnsupportedOperationException.class, () -> update.getAdded().add(JOHN_HAS_SILVER_HAIR));
		assertThrows(UnsupportedOperationException.class, () -> update.getReplaced()
				.put(JOHN_ALREADY_HAS_SILVER_HAIR.getStatementId(), JOHN_ALREADY_HAS_SILVER_HAIR));
		assertThrows(UnsupportedOperationException.class,
				() -> update.getRemoved().add(JOHN_ALREADY_HAS_BLUE_EYES.getStatementId()));
		added.add(JOHN_HAS_SILVER_HAIR);
		replaced.add(JOHN_ALREADY_HAS_SILVER_HAIR);
		removed.add(JOHN_ALREADY_HAS_BLUE_EYES.getStatementId());
		assertEquals(1, update.getAdded().size());
		assertEquals(1, update.getReplaced().size());
		assertEquals(1, update.getRemoved().size());
	}

	@Test
	public void testEmpty() {
		List<Statement> added = Arrays.asList(JOHN_HAS_BROWN_HAIR);
		List<Statement> replaced = Arrays.asList(JOHN_ALREADY_HAS_BLUE_EYES);
		List<String> removed = Arrays.asList(JOHN_ALREADY_HAS_SILVER_HAIR.getStatementId());
		assertTrue(new StatementUpdateImpl(NO_STATEMENTS, NO_STATEMENTS, NO_IDS).isEmpty());
		assertFalse(new StatementUpdateImpl(added, NO_STATEMENTS, NO_IDS).isEmpty());
		assertFalse(new StatementUpdateImpl(NO_STATEMENTS, replaced, NO_IDS).isEmpty());
		assertFalse(new StatementUpdateImpl(NO_STATEMENTS, NO_STATEMENTS, removed).isEmpty());
	}

	@Test
	@SuppressWarnings("unlikely-arg-type")
	public void testEquality() {
		List<Statement> added = Arrays.asList(JOHN_HAS_BROWN_HAIR);
		List<Statement> replaced = Arrays.asList(JOHN_ALREADY_HAS_BLUE_EYES);
		List<String> removed = Arrays.asList(JOHN_ALREADY_HAS_SILVER_HAIR.getStatementId());
		StatementUpdate update = new StatementUpdateImpl(added, replaced, removed);
		assertFalse(update.equals(null));
		assertFalse(update.equals(this));
		assertTrue(update.equals(update));
		assertTrue(update.equals(new StatementUpdateImpl(added, replaced, removed)));
		assertFalse(update.equals(new StatementUpdateImpl(NO_STATEMENTS, replaced, removed)));
		assertFalse(update.equals(new StatementUpdateImpl(added, NO_STATEMENTS, removed)));
		assertFalse(update.equals(new StatementUpdateImpl(added, replaced, NO_IDS)));
	}

	@Test
	public void testHashCode() {
		StatementUpdate update1 = new StatementUpdateImpl(
				Arrays.asList(JOHN_HAS_BROWN_HAIR),
				Arrays.asList(JOHN_ALREADY_HAS_BLUE_EYES),
				Arrays.asList(JOHN_ALREADY_HAS_SILVER_HAIR.getStatementId()));
		StatementUpdate update2 = new StatementUpdateImpl(
				Arrays.asList(JOHN_HAS_BROWN_HAIR),
				Arrays.asList(JOHN_ALREADY_HAS_BLUE_EYES),
				Arrays.asList(JOHN_ALREADY_HAS_SILVER_HAIR.getStatementId()));
		assertEquals(update1.hashCode(), update2.hashCode());
	}

	@Test
	public void testJson() {
		assertThat(StatementUpdateBuilder.create().build(), producesJson("[]"));
		assertThat(StatementUpdateBuilder.create().add(JOHN_HAS_BROWN_HAIR).build(),
				producesJson("[" + toJson(JOHN_HAS_BROWN_HAIR) + "]"));
		assertThat(StatementUpdateBuilder.create().replace(JOHN_ALREADY_HAS_BLUE_EYES).build(),
				producesJson("[" + toJson(JOHN_ALREADY_HAS_BLUE_EYES) + "]"));
		assertThat(StatementUpdateBuilder.create().remove("ID123").build(),
				producesJson("[{'id':'ID123','remove':''}]"));
	}

}
