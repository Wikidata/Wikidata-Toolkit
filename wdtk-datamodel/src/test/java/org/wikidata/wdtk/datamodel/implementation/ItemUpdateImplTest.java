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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.wikidata.wdtk.datamodel.implementation.JsonTestUtils.producesJson;
import static org.wikidata.wdtk.datamodel.implementation.JsonTestUtils.toJson;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.ItemUpdateBuilder;
import org.wikidata.wdtk.datamodel.interfaces.AliasUpdate;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemUpdate;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;
import org.wikidata.wdtk.datamodel.interfaces.TermUpdate;

public class ItemUpdateImplTest {

	private static final ItemIdValue Q1 = LabeledDocumentUpdateImplTest.JOHN;
	private static final StatementUpdate STATEMENTS = StatementDocumentUpdateImplTest.STATEMENTS;
	private static final TermUpdate LABELS = LabeledDocumentUpdateImplTest.LABELS;
	private static final TermUpdate DESCRIPTIONS = TermedDocumentUpdateImplTest.DESCRIPTIONS;
	private static final AliasUpdate ALIAS = TermedDocumentUpdateImplTest.ALIAS;
	private static final Map<String, AliasUpdate> ALIASES = TermedDocumentUpdateImplTest.ALIASES;
	private static final SiteLink SITELINK1 = Datamodel.makeSiteLink("Something", "enwiki");
	private static final List<SiteLink> SITELINKS = Arrays.asList(SITELINK1);
	private static final List<String> REMOVED_SITELINKS = Arrays.asList("skwiki");

	@Test
	public void testFields() {
		ItemUpdate update = new ItemUpdateImpl(
				Q1, 123, LABELS, DESCRIPTIONS, ALIASES, STATEMENTS, SITELINKS, REMOVED_SITELINKS);
		assertEquals(Q1, update.getEntityId());
		assertEquals(123, update.getBaseRevisionId());
		assertSame(LABELS, update.getLabels());
		assertSame(DESCRIPTIONS, update.getDescriptions());
		assertEquals(ALIASES, update.getAliases());
		assertSame(STATEMENTS, update.getStatements());
		assertThat(update.getModifiedSiteLinks().keySet(), containsInAnyOrder("enwiki"));
		assertEquals(SITELINK1, update.getModifiedSiteLinks().get("enwiki"));
		assertThat(update.getRemovedSiteLinks(), containsInAnyOrder("skwiki"));
	}

	@Test
	public void testValidation() {
		assertThrows(NullPointerException.class, () -> new ItemUpdateImpl(
				Q1, 123, LABELS, DESCRIPTIONS, ALIASES, STATEMENTS, null, REMOVED_SITELINKS));
		assertThrows(NullPointerException.class, () -> new ItemUpdateImpl(
				Q1, 123, LABELS, DESCRIPTIONS, ALIASES, STATEMENTS, SITELINKS, null));
		assertThrows(NullPointerException.class, () -> new ItemUpdateImpl(
				Q1, 123, LABELS, DESCRIPTIONS, ALIASES, STATEMENTS,
				Arrays.asList(SITELINK1, null), REMOVED_SITELINKS));
		assertThrows(NullPointerException.class, () -> new ItemUpdateImpl(
				Q1, 123, LABELS, DESCRIPTIONS, ALIASES, STATEMENTS, SITELINKS, Arrays.asList("skwiki", null)));
		assertThrows(IllegalArgumentException.class, () -> new ItemUpdateImpl(
				Q1, 123, LABELS, DESCRIPTIONS, ALIASES, STATEMENTS, SITELINKS, Arrays.asList("skwiki", " ")));
		assertThrows(IllegalArgumentException.class, () -> new ItemUpdateImpl(
				Q1, 123, LABELS, DESCRIPTIONS, ALIASES, STATEMENTS,
				Arrays.asList(SITELINK1, SITELINK1), REMOVED_SITELINKS));
		assertThrows(IllegalArgumentException.class, () -> new ItemUpdateImpl(
				Q1, 123, LABELS, DESCRIPTIONS, ALIASES, STATEMENTS, SITELINKS, Arrays.asList("skwiki", "skwiki")));
		assertThrows(IllegalArgumentException.class, () -> new ItemUpdateImpl(
				Q1, 123, LABELS, DESCRIPTIONS, ALIASES, STATEMENTS, SITELINKS, Arrays.asList("enwiki")));
	}

	@Test
	public void testEmpty() {
		ItemUpdate empty = new ItemUpdateImpl(Q1, 123, TermUpdate.EMPTY, TermUpdate.EMPTY,
				Collections.emptyMap(), StatementUpdate.EMPTY, Collections.emptyList(), Collections.emptyList());
		assertTrue(empty.isEmpty());
		ItemUpdate nonempty1 = new ItemUpdateImpl(Q1, 123, TermUpdate.EMPTY, DESCRIPTIONS,
				Collections.emptyMap(), StatementUpdate.EMPTY, Collections.emptyList(), Collections.emptyList());
		ItemUpdate nonempty2 = new ItemUpdateImpl(Q1, 123, TermUpdate.EMPTY, TermUpdate.EMPTY,
				Collections.emptyMap(), StatementUpdate.EMPTY, SITELINKS, Collections.emptyList());
		ItemUpdate nonempty3 = new ItemUpdateImpl(Q1, 123, TermUpdate.EMPTY, TermUpdate.EMPTY,
				Collections.emptyMap(), StatementUpdate.EMPTY, Collections.emptyList(), REMOVED_SITELINKS);
		assertFalse(nonempty1.isEmpty());
		assertFalse(nonempty2.isEmpty());
		assertFalse(nonempty3.isEmpty());
	}

	@Test
	@SuppressWarnings("unlikely-arg-type")
	public void testEquality() {
		ItemUpdate update = new ItemUpdateImpl(
				Q1, 123, LABELS, DESCRIPTIONS, ALIASES, STATEMENTS, SITELINKS, REMOVED_SITELINKS);
		assertFalse(update.equals(null));
		assertFalse(update.equals(this));
		assertTrue(update.equals(update));
		assertTrue(update.equals(new ItemUpdateImpl(
				Q1, 123, LABELS, DESCRIPTIONS, ALIASES, STATEMENTS, SITELINKS, REMOVED_SITELINKS)));
		assertFalse(update.equals(new ItemUpdateImpl(
				Q1, 123, LABELS, TermUpdate.EMPTY, ALIASES, STATEMENTS, SITELINKS, REMOVED_SITELINKS)));
		assertFalse(update.equals(new ItemUpdateImpl(
				Q1, 123, LABELS, DESCRIPTIONS, ALIASES, STATEMENTS, Collections.emptyList(), REMOVED_SITELINKS)));
		assertFalse(update.equals(new ItemUpdateImpl(
				Q1, 123, LABELS, DESCRIPTIONS, ALIASES, STATEMENTS, SITELINKS, Collections.emptyList())));
	}

	@Test
	public void testHashCode() {
		ItemUpdate update1 = new ItemUpdateImpl(
				Q1, 123, LABELS, DESCRIPTIONS, ALIASES, STATEMENTS, SITELINKS, REMOVED_SITELINKS);
		ItemUpdate update2 = new ItemUpdateImpl(
				Q1, 123, LABELS, DESCRIPTIONS, ALIASES, STATEMENTS, SITELINKS, REMOVED_SITELINKS);
		assertEquals(update1.hashCode(), update2.hashCode());
	}

	@Test
	public void testJson() {
		assertThat(
				new ItemUpdateImpl(Q1, 123, TermUpdate.EMPTY, TermUpdate.EMPTY, Collections.emptyMap(),
						StatementUpdate.EMPTY, Collections.emptyList(), Collections.emptyList()),
				producesJson("{}"));
		assertThat(ItemUpdateBuilder.forEntityId(Q1).updateLabels(LABELS).build(),
				producesJson("{'labels':" + toJson(LABELS) + "}"));
		assertThat(ItemUpdateBuilder.forEntityId(Q1).updateDescriptions(DESCRIPTIONS).build(),
				producesJson("{'descriptions':" + toJson(LABELS) + "}"));
		assertThat(ItemUpdateBuilder.forEntityId(Q1).updateAliases("en", ALIAS).build(),
				producesJson("{'aliases':{'en':" + toJson(ALIAS) + "}}"));
		assertThat(ItemUpdateBuilder.forEntityId(Q1).updateStatements(STATEMENTS).build(),
				producesJson("{'claims':" + toJson(STATEMENTS) + "}"));
		assertThat(ItemUpdateBuilder.forEntityId(Q1).putSiteLink(SITELINK1).build(),
				producesJson("{'sitelinks':{'enwiki':" + toJson(SITELINK1) + "}}"));
		assertThat(ItemUpdateBuilder.forEntityId(Q1).removeSiteLink("enwiki").build(),
				producesJson("{'sitelinks':{'enwiki':{'remove':'','site':'enwiki'}}}"));
	}

}
