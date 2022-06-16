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
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.AliasUpdate;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;

public class AliasUpdateBuilderTest {

	static final MonolingualTextValue WALK = Datamodel.makeMonolingualTextValue("walk", "en");
	static final MonolingualTextValue STROLL = Datamodel.makeMonolingualTextValue("stroll", "en");
	static final MonolingualTextValue TRAVEL = Datamodel.makeMonolingualTextValue("travel", "en");
	static final MonolingualTextValue WANDER = Datamodel.makeMonolingualTextValue("wander", "en");
	static final MonolingualTextValue GEHEN = Datamodel.makeMonolingualTextValue("gehen", "de");

	@Test
	public void testCreate() {
		AliasUpdate update = AliasUpdateBuilder.create().build();
		assertFalse(update.getRecreated().isPresent());
		assertThat(update.getAdded(), is(empty()));
		assertThat(update.getRemoved(), is(empty()));
	}

	@Test
	public void testForTerms() {
		assertThrows(NullPointerException.class, () -> AliasUpdateBuilder.forAliases(null));
		assertThrows(NullPointerException.class, () -> AliasUpdateBuilder.forAliases(Arrays.asList(WALK, null)));
		assertThrows(IllegalArgumentException.class, () -> AliasUpdateBuilder.forAliases(Arrays.asList(WALK, WALK)));
		assertThrows(IllegalArgumentException.class, () -> AliasUpdateBuilder.forAliases(Arrays.asList(WALK, GEHEN)));
		AliasUpdate update = AliasUpdateBuilder.forAliases(Arrays.asList(WALK, STROLL)).build();
		assertFalse(update.getRecreated().isPresent());
		assertThat(update.getAdded(), is(empty()));
		assertThat(update.getRemoved(), is(empty()));
	}

	@Test
	public void testBlindAddition() {
		AliasUpdateBuilder builder = AliasUpdateBuilder.create();
		assertThrows(NullPointerException.class, () -> builder.add(null));
		builder.add(WALK); // simple case
		assertThrows(IllegalArgumentException.class, () -> builder.add(GEHEN));
		builder.remove(TRAVEL);
		builder.remove(WANDER);
		builder.add(STROLL);
		builder.add(STROLL); // add twice
		builder.add(TRAVEL); // previously removed
		AliasUpdate update = builder.build();
		assertFalse(update.getRecreated().isPresent());
		assertThat(update.getRemoved(), containsInAnyOrder(WANDER));
		assertThat(update.getAdded(), contains(WALK, STROLL));
	}

	@Test
	public void testRecreatedAddition() {
		AliasUpdateBuilder builder = AliasUpdateBuilder.create();
		builder.recreate(Arrays.asList(WALK, WANDER, TRAVEL));
		assertThrows(IllegalArgumentException.class, () -> builder.add(GEHEN));
		builder.add(STROLL); // simple case
		builder.add(WANDER); // duplicate
		AliasUpdate update = builder.build();
		assertThat(update.getRemoved(), is(empty()));
		assertThat(update.getAdded(), is(empty()));
		assertThat(update.getRecreated().get(), contains(WALK, WANDER, TRAVEL, STROLL));
	}

	@Test
	public void testBaseAddition() {
		AliasUpdateBuilder builder = AliasUpdateBuilder.forAliases(Arrays.asList(WALK, TRAVEL));
		assertThrows(IllegalArgumentException.class, () -> builder.add(GEHEN));
		builder.add(STROLL); // simple case
		builder.add(WALK); // duplicate
		AliasUpdate update = builder.build();
		assertThat(update.getRemoved(), is(empty()));
		assertThat(update.getAdded(), contains(STROLL));
		assertFalse(update.getRecreated().isPresent());
	}

	@Test
	public void testRecreatedBaseAddition() {
		AliasUpdateBuilder builder = AliasUpdateBuilder.forAliases(Arrays.asList(WALK, TRAVEL, STROLL));
		builder.recreate(Arrays.asList(WALK));
		builder.add(TRAVEL); // add to recreated
		assertThat(builder.build().getRecreated().get(), contains(WALK, TRAVEL));
		builder.add(STROLL); // cancel recreation
		assertTrue(builder.build().isEmpty());
		builder.add(WALK); // duplicate
		builder.add(WANDER); // add to base
		AliasUpdate update = builder.build();
		assertThat(update.getRemoved(), is(empty()));
		assertThat(update.getAdded(), contains(WANDER));
		assertFalse(update.getRecreated().isPresent());
	}

	@Test
	public void testBlindRemoval() {
		AliasUpdateBuilder builder = AliasUpdateBuilder.create();
		assertThrows(NullPointerException.class, () -> builder.remove(null));
		builder.remove(WALK); // simple case
		assertThrows(IllegalArgumentException.class, () -> builder.remove(GEHEN));
		builder.add(TRAVEL);
		builder.add(WANDER);
		builder.remove(STROLL);
		builder.remove(STROLL); // remove twice
		builder.remove(TRAVEL); // previously added
		AliasUpdate update = builder.build();
		assertFalse(update.getRecreated().isPresent());
		assertThat(update.getAdded(), contains(WANDER));
		assertThat(update.getRemoved(), containsInAnyOrder(WALK, STROLL));
	}

	@Test
	public void testRecreatedRemoval() {
		AliasUpdateBuilder builder = AliasUpdateBuilder.create();
		builder.recreate(Arrays.asList(WALK, WANDER, TRAVEL));
		assertThrows(IllegalArgumentException.class, () -> builder.remove(GEHEN));
		builder.remove(WANDER); // simple case
		builder.remove(STROLL); // not present
		AliasUpdate update = builder.build();
		assertThat(update.getRemoved(), is(empty()));
		assertThat(update.getAdded(), is(empty()));
		assertThat(update.getRecreated().get(), contains(WALK, TRAVEL));
	}

	@Test
	public void testBaseRemoval() {
		AliasUpdateBuilder builder = AliasUpdateBuilder.forAliases(Arrays.asList(WALK, TRAVEL, WANDER));
		assertThrows(IllegalArgumentException.class, () -> builder.remove(GEHEN));
		builder.remove(TRAVEL); // simple case
		builder.remove(STROLL); // not found
		AliasUpdate update = builder.build();
		assertThat(update.getAdded(), is(empty()));
		assertThat(update.getRemoved(), contains(TRAVEL));
		assertFalse(update.getRecreated().isPresent());
	}

	@Test
	public void testRecreatedBaseRemoval() {
		AliasUpdateBuilder builder = AliasUpdateBuilder.forAliases(Arrays.asList(WALK));
		builder.recreate(Arrays.asList(WALK, TRAVEL, STROLL));
		builder.remove(TRAVEL); // remove from recreated
		assertThat(builder.build().getRecreated().get(), contains(WALK, STROLL));
		builder.remove(STROLL); // cancel recreation
		assertTrue(builder.build().isEmpty());
		builder.remove(WANDER); // not found
		builder.remove(WALK); // remove from base
		AliasUpdate update = builder.build();
		assertThat(update.getAdded(), is(empty()));
		assertThat(update.getRemoved(), contains(WALK));
		assertFalse(update.getRecreated().isPresent());
	}

	@Test
	public void testBlindRecreation() {
		AliasUpdateBuilder builder = AliasUpdateBuilder.create();
		assertThrows(NullPointerException.class, () -> builder.recreate(null));
		assertThrows(NullPointerException.class, () -> builder.recreate(Arrays.asList(WALK, null)));
		assertThrows(IllegalArgumentException.class, () -> builder.recreate(Arrays.asList(WALK, WALK)));
		assertThrows(IllegalArgumentException.class, () -> builder.recreate(Arrays.asList(WALK, GEHEN)));
		builder.add(WANDER);
		builder.remove(WALK);
		builder.recreate(Arrays.asList(WALK, STROLL));
		AliasUpdate update = builder.build();
		assertThat(update.getRecreated().get(), contains(WALK, STROLL));
		assertThat(update.getRemoved(), is(empty()));
		assertThat(update.getAdded(), is(empty()));
	}

	@Test
	public void testBaseRecreation() {
		AliasUpdateBuilder builder = AliasUpdateBuilder.forAliases(Arrays.asList(STROLL, TRAVEL, WALK));
		builder.add(WANDER);
		builder.remove(WALK);
		builder.recreate(Arrays.asList(WALK, STROLL));
		AliasUpdate update = builder.build();
		assertThat(update.getRecreated().get(), contains(WALK, STROLL));
		assertThat(update.getRemoved(), is(empty()));
		assertThat(update.getAdded(), is(empty()));
		builder.recreate(Arrays.asList(STROLL, TRAVEL, WALK));
		assertTrue(builder.build().isEmpty());
	}

	@Test
	public void testMerge() {
		assertThrows(NullPointerException.class, () -> AliasUpdateBuilder.create().append(null));
		AliasUpdate update = AliasUpdateBuilder.create()
				.add(WALK) // prior addition
				.remove(STROLL) // prior removal
				.append(AliasUpdateBuilder.create()
						.add(TRAVEL) // another addition
						.remove(WANDER) // another removal
						.build())
				.build();
		assertFalse(update.getRecreated().isPresent());
		assertThat(update.getAdded(), contains(WALK, TRAVEL));
		assertThat(update.getRemoved(), containsInAnyOrder(STROLL, WANDER));
		update = AliasUpdateBuilder.create()
				.add(WALK) // any prior change
				.append(AliasUpdateBuilder.create()
						.recreate(Arrays.asList(WALK, STROLL))
						.build())
				.build();
		assertThat(update.getRecreated().get(), contains(WALK, STROLL));
		assertThat(update.getRemoved(), is(empty()));
		assertThat(update.getAdded(), is(empty()));
	}

}
