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
import static org.junit.Assert.*;

import java.util.Collections;

import org.junit.Test;
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

	static final ItemIdValue Q1 = Datamodel.makeWikidataItemIdValue("Q1");
	static final PropertyIdValue P1 = Datamodel.makeWikidataPropertyIdValue("P1");
	static final MediaInfoIdValue M1 = Datamodel.makeWikimediaCommonsMediaInfoIdValue("M1");
	static final LexemeIdValue L1 = Datamodel.makeWikidataLexemeIdValue("L1");
	static final FormIdValue F1 = Datamodel.makeWikidataFormIdValue("L1-F1");
	static final SenseIdValue S1 = Datamodel.makeWikidataSenseIdValue("L1-S1");
	static final ItemDocument ITEM = Datamodel.makeItemDocument(Q1).withRevisionId(123);
	static final PropertyDocument PROPERTY = Datamodel.makePropertyDocument(
			P1, Datamodel.makeDatatypeIdValue(DatatypeIdValue.DT_ITEM));
	static final MediaInfoDocument MEDIA = Datamodel.makeMediaInfoDocument(M1);
	static final LexemeDocument LEXEME = Datamodel.makeLexemeDocument(L1, Q1, Q1, Collections.emptyList());
	static final FormDocument FORM = Datamodel.makeFormDocument(F1,
			Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
	static final SenseDocument SENSE = Datamodel.makeSenseDocument(S1,
			Collections.emptyList(), Collections.emptyList());

	@Test
	public void testForEntityId() {
		assertThrows(NullPointerException.class, () -> EntityUpdateBuilder.forEntityId(null));
		assertThrows(IllegalArgumentException.class, () -> EntityUpdateBuilder.forEntityId(ItemIdValue.NULL));
		assertThat(EntityUpdateBuilder.forEntityId(Q1), is(instanceOf(ItemUpdateBuilder.class)));
		assertThat(EntityUpdateBuilder.forEntityId(P1), is(instanceOf(PropertyUpdateBuilder.class)));
		assertThat(EntityUpdateBuilder.forEntityId(M1), is(instanceOf(MediaInfoUpdateBuilder.class)));
		assertThat(EntityUpdateBuilder.forEntityId(L1), is(instanceOf(LexemeUpdateBuilder.class)));
		assertThat(EntityUpdateBuilder.forEntityId(F1), is(instanceOf(FormUpdateBuilder.class)));
		assertThat(EntityUpdateBuilder.forEntityId(S1), is(instanceOf(SenseUpdateBuilder.class)));
		EntityUpdateBuilder builder = EntityUpdateBuilder.forEntityId(Q1);
		assertEquals(Q1, builder.getEntityId());
		assertNull(builder.getBaseRevision());
		assertEquals(0, builder.getBaseRevisionId());
	}

    @Test
	public void testForBaseRevisionId() {
		EntityUpdateBuilder builder = EntityUpdateBuilder.forBaseRevisionId(Q1, 123);
		assertEquals(Q1, builder.getEntityId());
		assertNull(builder.getBaseRevision());
		assertEquals(123, builder.getBaseRevisionId());
		assertEquals(123, EntityUpdateBuilder.forBaseRevisionId(Q1, 123).getBaseRevisionId());
		assertEquals(123, EntityUpdateBuilder.forBaseRevisionId(P1, 123).getBaseRevisionId());
		assertEquals(123, EntityUpdateBuilder.forBaseRevisionId(M1, 123).getBaseRevisionId());
		assertEquals(123, EntityUpdateBuilder.forBaseRevisionId(L1, 123).getBaseRevisionId());
		assertEquals(123, EntityUpdateBuilder.forBaseRevisionId(F1, 123).getBaseRevisionId());
		assertEquals(123, EntityUpdateBuilder.forBaseRevisionId(S1, 123).getBaseRevisionId());
	}

	@Test
	public void testForBaseRevision() {
		assertThrows(NullPointerException.class, () -> EntityUpdateBuilder.forBaseRevision(null));
		assertThrows(IllegalArgumentException.class,
				() -> EntityUpdateBuilder.forBaseRevision(Datamodel.makeItemDocument(ItemIdValue.NULL)));
		assertThat(EntityUpdateBuilder.forBaseRevision(ITEM), is(instanceOf(ItemUpdateBuilder.class)));
		assertThat(EntityUpdateBuilder.forBaseRevision(PROPERTY), is(instanceOf(PropertyUpdateBuilder.class)));
		assertThat(EntityUpdateBuilder.forBaseRevision(MEDIA), is(instanceOf(MediaInfoUpdateBuilder.class)));
		assertThat(EntityUpdateBuilder.forBaseRevision(LEXEME), is(instanceOf(LexemeUpdateBuilder.class)));
		assertThat(EntityUpdateBuilder.forBaseRevision(FORM), is(instanceOf(FormUpdateBuilder.class)));
		assertThat(EntityUpdateBuilder.forBaseRevision(SENSE), is(instanceOf(SenseUpdateBuilder.class)));
		EntityUpdateBuilder builder = EntityUpdateBuilder.forBaseRevision(ITEM);
		assertEquals(Q1, builder.getEntityId());
		assertSame(ITEM, builder.getBaseRevision());
		assertEquals(123, builder.getBaseRevisionId());
	}

}
