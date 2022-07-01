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
import static org.junit.Assert.*;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemUpdate;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Statement;

public class ItemUpdateBuilderTest {

	private static final ItemIdValue Q1 = EntityUpdateBuilderTest.Q1;
	private static final ItemDocument ITEM = EntityUpdateBuilderTest.ITEM;
	private static final Statement JOHN_HAS_BROWN_HAIR = StatementUpdateBuilderTest.JOHN_HAS_BROWN_HAIR;
	private static final SiteLink EN = Datamodel.makeSiteLink("hello", "enwiki");
	private static final SiteLink EN2 = Datamodel.makeSiteLink("hi", "enwiki");
	private static final SiteLink SK = Datamodel.makeSiteLink("ahoj", "skwiki");
	private static final SiteLink CS = Datamodel.makeSiteLink("nazdar", "cswiki");
	private static final SiteLink DE = Datamodel.makeSiteLink("Hallo", "dewiki");
	private static final SiteLink DE2 = Datamodel.makeSiteLink("Guten Tag", "dewiki");
	private static final SiteLink FR = Datamodel.makeSiteLink("Bonjour", "frwiki");

	@Test
	public void testForEntityId() {
		assertThrows(NullPointerException.class, () -> ItemUpdateBuilder.forEntityId(null));
		assertThrows(IllegalArgumentException.class, () -> ItemUpdateBuilder.forEntityId(PropertyIdValue.NULL));
		ItemUpdateBuilder.forEntityId(Q1);
	}

	@Test
	public void testForBaseRevisionId() {
		assertEquals(123, ItemUpdateBuilder.forBaseRevisionId(Q1, 123).getBaseRevisionId());
	}

	@Test
	public void testForBaseRevision() {
		assertThrows(NullPointerException.class, () -> ItemUpdateBuilder.forBaseRevision(null));
		assertThrows(IllegalArgumentException.class,
				() -> ItemUpdateBuilder.forBaseRevision(Datamodel.makeItemDocument(ItemIdValue.NULL)));
		ItemUpdateBuilder.forBaseRevision(ITEM);
	}

	@Test
	public void testStatementUpdate() {
		ItemUpdate update = ItemUpdateBuilder.forEntityId(Q1)
				.updateStatements(StatementUpdateBuilder.create().add(JOHN_HAS_BROWN_HAIR).build())
				.build();
		assertThat(update.getStatements().getAdded(), containsInAnyOrder(JOHN_HAS_BROWN_HAIR));
	}

	@Test
	public void testLabelUpdate() {
		ItemUpdate update = ItemUpdateBuilder.forEntityId(Q1)
				.updateLabels(TermUpdateBuilder.create().remove("en").build())
				.build();
		assertThat(update.getLabels().getRemoved(), containsInAnyOrder("en"));
	}

	@Test
	public void testDescriptionUpdate() {
		ItemUpdate update = ItemUpdateBuilder.forEntityId(Q1)
				.updateDescriptions(TermUpdateBuilder.create().remove("en").build())
				.build();
		assertThat(update.getDescriptions().getRemoved(), containsInAnyOrder("en"));
	}

	@Test
	public void testAliasUpdate() {
		ItemUpdate update = ItemUpdateBuilder.forEntityId(Q1)
				.updateAliases("sk", AliasUpdateBuilder.create().add(TermUpdateBuilderTest.SK).build())
				.build();
		assertThat(update.getAliases().keySet(), containsInAnyOrder("sk"));
	}

	@Test
	public void testBlindSiteLinkAssignment() {
		ItemUpdateBuilder builder = ItemUpdateBuilder.forEntityId(Q1);
		assertThrows(NullPointerException.class, () -> builder.putSiteLink(null));
		builder.removeSiteLink("skwiki");
		builder.removeSiteLink("dewiki");
		builder.putSiteLink(EN); // simple case
		builder.putSiteLink(SK); // previously removed
		ItemUpdate update = builder.build();
		assertThat(update.getRemovedSiteLinks(), containsInAnyOrder("dewiki"));
		assertThat(update.getModifiedSiteLinks().keySet(), containsInAnyOrder("skwiki", "enwiki"));
		assertEquals(EN, update.getModifiedSiteLinks().get("enwiki"));
		assertEquals(SK, update.getModifiedSiteLinks().get("skwiki"));
	}

	@Test
	public void testBlindSiteLinkRemoval() {
		ItemUpdateBuilder builder = ItemUpdateBuilder.forEntityId(Q1);
		assertThrows(NullPointerException.class, () -> builder.removeSiteLink(null));
		assertThrows(IllegalArgumentException.class, () -> builder.removeSiteLink(" "));
		builder.putSiteLink(EN);
		builder.putSiteLink(SK);
		builder.removeSiteLink("dewiki"); // simple case
		builder.removeSiteLink("skwiki"); // previously assigned
		ItemUpdate update = builder.build();
		assertThat(update.getRemovedSiteLinks(), containsInAnyOrder("skwiki", "dewiki"));
		assertThat(update.getModifiedSiteLinks().keySet(), containsInAnyOrder("enwiki"));
	}

	@Test
	public void testBaseSiteLinkAssignment() {
		ItemUpdateBuilder builder = ItemUpdateBuilder.forBaseRevision(ItemDocumentBuilder.fromItemDocument(ITEM)
				.withSiteLink(SK)
				.withSiteLink(EN)
				.withSiteLink(DE)
				.withSiteLink(CS)
				.build());
		builder.removeSiteLink("skwiki");
		builder.removeSiteLink("dewiki");
		builder.putSiteLink(FR); // new language key
		builder.putSiteLink(EN2); // new value
		builder.putSiteLink(CS); // same value
		builder.putSiteLink(SK); // same value for previously removed
		builder.putSiteLink(DE2); // new value for previously removed
		ItemUpdate update = builder.build();
		assertThat(update.getRemovedSiteLinks(), is(empty()));
		assertThat(update.getModifiedSiteLinks().keySet(), containsInAnyOrder("enwiki", "dewiki", "frwiki"));
		assertEquals(FR, update.getModifiedSiteLinks().get("frwiki"));
		assertEquals(EN2, update.getModifiedSiteLinks().get("enwiki"));
		assertEquals(DE2, update.getModifiedSiteLinks().get("dewiki"));
	}

	@Test
	public void testBaseSiteLinkRemoval() {
		ItemUpdateBuilder builder = ItemUpdateBuilder.forBaseRevision(ItemDocumentBuilder.fromItemDocument(ITEM)
				.withSiteLink(EN)
				.withSiteLink(SK)
				.withSiteLink(CS)
				.build());
		builder.putSiteLink(EN2);
		builder.putSiteLink(DE);
		builder.removeSiteLink("skwiki"); // simple case
		builder.removeSiteLink("frwiki"); // not found
		builder.removeSiteLink("enwiki"); // previously modified
		builder.removeSiteLink("dewiki"); // previously added
		ItemUpdate update = builder.build();
		assertThat(update.getModifiedSiteLinks(), anEmptyMap());
		assertThat(update.getRemovedSiteLinks(), containsInAnyOrder("enwiki", "skwiki"));
	}

	@Test
	public void testMerge() {
		ItemUpdate update = ItemUpdateBuilder.forEntityId(Q1)
				.updateDescriptions(TermUpdateBuilder.create().remove("en").build())
				.removeSiteLink("enwiki")
				.append(ItemUpdateBuilder.forEntityId(Q1)
						.updateDescriptions(TermUpdateBuilder.create().remove("sk").build())
						.removeSiteLink("skwiki")
						.build())
				.build();
		assertThat(update.getDescriptions().getRemoved(), containsInAnyOrder("sk", "en"));
		assertThat(update.getRemovedSiteLinks(), containsInAnyOrder("skwiki", "enwiki"));
	}

}
