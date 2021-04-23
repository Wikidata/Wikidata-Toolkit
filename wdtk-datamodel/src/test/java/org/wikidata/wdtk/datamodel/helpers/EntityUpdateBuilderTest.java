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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.FormDocument;
import org.wikidata.wdtk.datamodel.interfaces.FormIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.LexemeDocument;
import org.wikidata.wdtk.datamodel.interfaces.LexemeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MediaInfoDocument;
import org.wikidata.wdtk.datamodel.interfaces.MediaInfoIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.SenseDocument;
import org.wikidata.wdtk.datamodel.interfaces.SenseIdValue;

public class EntityUpdateBuilderTest {

	private final ItemIdValue q1 = Datamodel.makeWikidataItemIdValue("Q1");
	private final PropertyIdValue p1 = Datamodel.makeWikidataPropertyIdValue("P1");
	private final MediaInfoIdValue m1 = Datamodel.makeWikimediaCommonsMediaInfoIdValue("M1");
	private final LexemeIdValue l1 = Datamodel.makeWikidataLexemeIdValue("L1");
	private final FormIdValue f1 = Datamodel.makeWikidataFormIdValue("L1-F1");
	private final SenseIdValue s1 = Datamodel.makeWikidataSenseIdValue("L1-S1");
	private final ItemDocument item = Datamodel.makeItemDocument(q1);
	private final PropertyDocument property = Datamodel.makePropertyDocument(
			p1, Datamodel.makeDatatypeIdValue(DatatypeIdValue.DT_ITEM));
	private final MediaInfoDocument media = Datamodel.makeMediaInfoDocument(m1);
	private final LexemeDocument lexeme = Datamodel.makeLexemeDocument(l1, q1, q1,
			Arrays.asList(Datamodel.makeMonolingualTextValue("hello", "en")));
	private final FormDocument form = Datamodel.makeFormDocument(
			f1,
			Arrays.asList(Datamodel.makeMonolingualTextValue("hello", "en")),
			Collections.emptyList(),
			Collections.emptyList());
	private final SenseDocument sense = Datamodel.makeSenseDocument(
			s1, Arrays.asList(Datamodel.makeMonolingualTextValue("something", "en")), Collections.emptyList());

	@Test
	public void testForEntityId() {
		assertThrows(NullPointerException.class, () -> EntityUpdateBuilder.forEntityId(null));
		assertThrows(IllegalArgumentException.class, () -> EntityUpdateBuilder.forEntityId(ItemIdValue.NULL));
		assertThat(EntityUpdateBuilder.forEntityId(q1), is(instanceOf(ItemUpdateBuilder.class)));
		assertThat(EntityUpdateBuilder.forEntityId(p1), is(instanceOf(PropertyUpdateBuilder.class)));
		assertThat(EntityUpdateBuilder.forEntityId(m1), is(instanceOf(MediaInfoUpdateBuilder.class)));
		assertThat(EntityUpdateBuilder.forEntityId(l1), is(instanceOf(LexemeUpdateBuilder.class)));
		assertThat(EntityUpdateBuilder.forEntityId(f1), is(instanceOf(FormUpdateBuilder.class)));
		assertThat(EntityUpdateBuilder.forEntityId(s1), is(instanceOf(SenseUpdateBuilder.class)));
		EntityUpdateBuilder builder = EntityUpdateBuilder.forEntityId(q1);
		assertEquals(q1, builder.getEntityId());
		assertNull(builder.getBaseRevision());
	}

	@Test
	public void testForBaseRevision() {
		assertThrows(NullPointerException.class, () -> EntityUpdateBuilder.forBaseRevision(null));
		assertThrows(IllegalArgumentException.class,
				() -> EntityUpdateBuilder.forBaseRevision(Datamodel.makeItemDocument(ItemIdValue.NULL)));
		assertThat(EntityUpdateBuilder.forBaseRevision(item), is(instanceOf(ItemUpdateBuilder.class)));
		assertThat(EntityUpdateBuilder.forBaseRevision(property), is(instanceOf(PropertyUpdateBuilder.class)));
		assertThat(EntityUpdateBuilder.forBaseRevision(media), is(instanceOf(MediaInfoUpdateBuilder.class)));
		assertThat(EntityUpdateBuilder.forBaseRevision(lexeme), is(instanceOf(LexemeUpdateBuilder.class)));
		assertThat(EntityUpdateBuilder.forBaseRevision(form), is(instanceOf(FormUpdateBuilder.class)));
		assertThat(EntityUpdateBuilder.forBaseRevision(sense), is(instanceOf(SenseUpdateBuilder.class)));
		EntityUpdateBuilder builder = EntityUpdateBuilder.forBaseRevision(item);
		assertEquals(q1, builder.getEntityId());
		assertSame(item, builder.getBaseRevision());
	}

}
