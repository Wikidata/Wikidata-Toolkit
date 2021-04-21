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
package org.wikidata.wdtk.datamodel.helpers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;

public class StatementUpdateBuilderTest {

	private final EntityIdValue john = Datamodel.makeWikidataItemIdValue("Q1");
	private final EntityIdValue rita = Datamodel.makeWikidataItemIdValue("Q2");
	private final PropertyIdValue hair = Datamodel.makeWikidataPropertyIdValue("P1");
	private final PropertyIdValue eyes = Datamodel.makeWikidataPropertyIdValue("P2");
	private final PropertyIdValue shirt = Datamodel.makeWikidataPropertyIdValue("P3");
	private final PropertyIdValue trousers = Datamodel.makeWikidataPropertyIdValue("P4");
	private final StringValue brown = Datamodel.makeStringValue("brown");
	private final StringValue silver = Datamodel.makeStringValue("silver");
	private final StringValue blue = Datamodel.makeStringValue("blue");
	private final Statement nobodyHasBrownHair = StatementBuilder
			.forSubjectAndProperty(ItemIdValue.NULL, hair)
			.withValue(brown)
			.build();
	private final Statement nobodyAlreadyHasBrownHair = nobodyHasBrownHair.withStatementId("ID1");
	private final Statement johnHasBrownHair = StatementBuilder
			.forSubjectAndProperty(john, hair)
			.withValue(brown)
			.build();
	private final Statement johnAlreadyHasBrownHair = johnHasBrownHair.withStatementId("ID2");
	private final Statement ritaHasBrownHair = StatementBuilder
			.forSubjectAndProperty(rita, hair)
			.withValue(brown)
			.build();
	private final Statement ritaAlreadyHasBrownHair = ritaHasBrownHair.withStatementId("ID3");
	private final Statement johnHasBrownEyes = StatementBuilder
			.forSubjectAndProperty(john, eyes)
			.withValue(brown)
			.build();
	private final Statement johnAlreadyHasBrownEyes = johnHasBrownEyes.withStatementId("ID4");
	private final Statement johnHasSilverHair = StatementBuilder
			.forSubjectAndProperty(john, hair)
			.withValue(silver)
			.build();
	private final Statement johnAlreadyHasSilverHair = johnHasSilverHair.withStatementId("ID5");
	private final Statement johnHasBlueShirt = StatementBuilder
			.forSubjectAndProperty(john, shirt)
			.withValue(blue)
			.build();
	private final Statement johnAlreadyHasBlueShirt = johnHasBlueShirt.withStatementId("ID6");
	private final Statement johnHasBrownTrousers = StatementBuilder
			.forSubjectAndProperty(john, trousers)
			.withValue(brown)
			.build();
	private final Statement johnAlreadyHasBrownTrousers = johnHasBrownTrousers.withStatementId("ID7");
	private final Statement johnHasBlueTrousers = StatementBuilder
			.forSubjectAndProperty(john, trousers)
			.withValue(blue)
			.build();

	@Test
	public void testCreate() {
		StatementUpdate update = StatementUpdateBuilder.create().build();
		assertThat(update.getAddedStatements(), is(empty()));
		assertThat(update.getReplacedStatements(), is(anEmptyMap()));
		assertThat(update.getRemovedStatements(), is(empty()));
	}

	@Test
	public void testForStatements() {
		assertThrows(NullPointerException.class, () -> StatementUpdateBuilder.forStatements(null));
		assertThrows(IllegalArgumentException.class,
				() -> StatementUpdateBuilder.forStatements(Arrays.asList(johnAlreadyHasBrownHair, null)));
		// no statement subject
		assertThrows(IllegalArgumentException.class,
				() -> StatementUpdateBuilder.forStatements(Arrays.asList(nobodyAlreadyHasBrownHair)));
		// no statement ID
		assertThrows(IllegalArgumentException.class,
				() -> StatementUpdateBuilder.forStatements(Arrays.asList(johnHasBrownHair)));
		// duplicate statement ID
		assertThrows(IllegalArgumentException.class, () -> StatementUpdateBuilder
				.forStatements(Arrays.asList(johnAlreadyHasBrownHair, johnAlreadyHasBrownHair)));
		// inconsistent statement subject
		assertThrows(IllegalArgumentException.class, () -> StatementUpdateBuilder
				.forStatements(Arrays.asList(johnAlreadyHasBrownHair, ritaAlreadyHasBrownHair)));
		StatementUpdate update = StatementUpdateBuilder
				.forStatements(Arrays.asList(johnAlreadyHasBrownHair, johnAlreadyHasBrownEyes))
				.build();
		assertThat(update.getAddedStatements(), is(empty()));
		assertThat(update.getReplacedStatements(), is(anEmptyMap()));
		assertThat(update.getRemovedStatements(), is(empty()));
	}

	@Test
	public void testForStatementGroups() {
		assertThrows(NullPointerException.class, () -> StatementUpdateBuilder.forStatementGroups(null));
		StatementGroup johnAlreadyHasBrownAndSilverHair = Datamodel.makeStatementGroup(
				Arrays.asList(johnAlreadyHasBrownHair, johnAlreadyHasSilverHair));
		assertThrows(IllegalArgumentException.class,
				() -> StatementUpdateBuilder.forStatementGroups(Arrays.asList(johnAlreadyHasBrownAndSilverHair, null)));
		StatementUpdate update = StatementUpdateBuilder
				.forStatementGroups(Arrays.asList(johnAlreadyHasBrownAndSilverHair))
				.build();
		assertThat(update.getAddedStatements(), is(empty()));
		assertThat(update.getReplacedStatements(), is(anEmptyMap()));
		assertThat(update.getRemovedStatements(), is(empty()));
	}

	@Test
	public void testBlindAddition() {
		StatementUpdateBuilder builder = StatementUpdateBuilder.create();
		assertThrows(NullPointerException.class, () -> builder.addStatement(null));
		builder.addStatement(johnHasBrownHair); // simple case
		builder.addStatement(johnHasBrownHair); // duplicates allowed
		builder.addStatement(johnAlreadyHasBrownEyes); // strip ID
		// inconsistent subject
		assertThrows(IllegalArgumentException.class, () -> builder.addStatement(ritaHasBrownHair));
		StatementUpdate update = builder.build();
		assertEquals(update.getAddedStatements(), Arrays.asList(johnHasBrownHair, johnHasBrownHair, johnHasBrownEyes));
	}

	@Test
	public void testBlindReplacement() {
		StatementUpdateBuilder builder = StatementUpdateBuilder.create();
		assertThrows(NullPointerException.class, () -> builder.replaceStatement(null));
		builder.removeStatement(johnAlreadyHasBrownEyes.getStatementId());
		builder.replaceStatement(johnAlreadyHasBrownHair); // simple case
		builder.replaceStatement(johnAlreadyHasBrownEyes); // previously removed
		builder.replaceStatement(johnAlreadyHasBrownHair); // replace twice
		// inconsistent subject
		assertThrows(IllegalArgumentException.class, () -> builder.replaceStatement(ritaAlreadyHasBrownHair));
		// no statement ID
		assertThrows(IllegalArgumentException.class, () -> builder.replaceStatement(johnHasSilverHair));
		StatementUpdate update = builder.build();
		assertThat(update.getRemovedStatements(), is(empty()));
		assertThat(
				update.getReplacedStatements().values(),
				containsInAnyOrder(johnAlreadyHasBrownHair, johnAlreadyHasBrownEyes));
	}

	@Test
	public void testBlindRemoval() {
		StatementUpdateBuilder builder = StatementUpdateBuilder.create();
		assertThrows(NullPointerException.class, () -> builder.removeStatement(null));
		assertThrows(IllegalArgumentException.class, () -> builder.removeStatement(""));
		builder.replaceStatement(johnAlreadyHasBrownEyes);
		builder.removeStatement(johnAlreadyHasBrownHair.getStatementId()); // simple case
		builder.removeStatement(johnAlreadyHasBrownEyes.getStatementId()); // previously replaced
		StatementUpdate update = builder.build();
		assertThat(update.getReplacedStatements(), is(anEmptyMap()));
		assertThat(
				update.getRemovedStatements(),
				containsInAnyOrder(johnAlreadyHasBrownHair.getStatementId(), johnAlreadyHasBrownEyes.getStatementId()));
	}

	@Test
	public void testBaseAddition() {
		StatementUpdateBuilder builder = StatementUpdateBuilder.forStatements(Arrays.asList(johnAlreadyHasBrownHair));
		// inconsistent subject
		assertThrows(IllegalArgumentException.class, () -> builder.addStatement(ritaHasBrownHair));
		builder.addStatement(johnHasBrownEyes); // simple case
		builder.addStatement(johnAlreadyHasBrownHair); // duplicating existing statements is allowed
		StatementUpdate update = builder.build();
		assertEquals(update.getAddedStatements(), Arrays.asList(johnHasBrownEyes, johnHasBrownHair));
	}

	@Test
	public void testBaseReplacement() {
		StatementUpdateBuilder builder = StatementUpdateBuilder.forStatements(Arrays.asList(
				johnAlreadyHasBrownHair,
				johnAlreadyHasBrownEyes,
				johnAlreadyHasBlueShirt,
				johnAlreadyHasBrownTrousers));
		builder.removeStatement(johnAlreadyHasBrownEyes.getStatementId());
		Statement johnChangesBrownTrousersToBlueTrousers = johnHasBlueTrousers
				.withStatementId(johnAlreadyHasBrownTrousers.getStatementId());
		builder.replaceStatement(johnChangesBrownTrousersToBlueTrousers);
		// inconsistent subject
		assertThrows(IllegalArgumentException.class, () -> builder
				.replaceStatement(ritaAlreadyHasBrownHair.withStatementId(johnAlreadyHasBrownEyes.getStatementId())));
		// unknown ID
		assertThrows(IllegalArgumentException.class,
				() -> builder.replaceStatement(johnAlreadyHasBrownHair.withStatementId("ID999")));
		Statement johnChangesBrownHairToSilverHair = johnHasSilverHair
				.withStatementId(johnAlreadyHasBrownHair.getStatementId());
		builder.replaceStatement(johnChangesBrownHairToSilverHair); // simple case
		builder.replaceStatement(johnAlreadyHasBlueShirt); // no change
		builder.replaceStatement(johnAlreadyHasBrownEyes); // restore deleted
		builder.replaceStatement(johnAlreadyHasBrownTrousers); // restore replaced
		StatementUpdate update = builder.build();
		assertThat(update.getRemovedStatements(), is(empty()));
		assertThat(update.getReplacedStatements().values(), containsInAnyOrder(johnChangesBrownHairToSilverHair));
	}

	@Test
	public void testBaseRemoval() {
		StatementUpdateBuilder builder = StatementUpdateBuilder.forStatements(Arrays.asList(johnAlreadyHasBrownHair));
		assertThrows(IllegalArgumentException.class, () -> builder.removeStatement("ID999")); // unknown ID
		builder.removeStatement(johnAlreadyHasBrownHair.getStatementId()); // simple case
		StatementUpdate update = builder.build();
		assertThat(update.getRemovedStatements(), containsInAnyOrder(johnAlreadyHasBrownHair.getStatementId()));
	}

}
