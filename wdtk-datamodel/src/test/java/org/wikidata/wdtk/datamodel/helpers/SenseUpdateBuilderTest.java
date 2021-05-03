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
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.SenseDocument;
import org.wikidata.wdtk.datamodel.interfaces.SenseIdValue;
import org.wikidata.wdtk.datamodel.interfaces.SenseUpdate;
import org.wikidata.wdtk.datamodel.interfaces.Statement;

public class SenseUpdateBuilderTest {

	private static final SenseIdValue S1 = EntityUpdateBuilderTest.S1;
	private static final SenseDocument SENSE = EntityUpdateBuilderTest.SENSE;
	private static final Statement JOHN_HAS_BROWN_HAIR = StatementUpdateBuilderTest.JOHN_HAS_BROWN_HAIR;
	private static final Statement JOHN_HAS_BLUE_EYES = StatementUpdateBuilderTest.JOHN_HAS_BLUE_EYES;
	private static final MonolingualTextValue EN = TermUpdateBuilderTest.EN;
	private static final MonolingualTextValue SK = TermUpdateBuilderTest.SK;

	@Test
	public void testForEntityId() {
		assertThrows(NullPointerException.class, () -> SenseUpdateBuilder.forEntityId(null));
		assertThrows(IllegalArgumentException.class, () -> SenseUpdateBuilder.forEntityId(SenseIdValue.NULL));
		SenseUpdateBuilder.forEntityId(S1);
	}

	@Test
	public void testForBaseRevision() {
		assertThrows(NullPointerException.class, () -> SenseUpdateBuilder.forBaseRevision(null));
		assertThrows(IllegalArgumentException.class,
				() -> SenseUpdateBuilder.forBaseRevision(SENSE.withEntityId(SenseIdValue.NULL)));
		SenseUpdateBuilder.forBaseRevision(SENSE);
	}

	@Test
	public void testStatementUpdate() {
		SenseUpdate update = SenseUpdateBuilder.forEntityId(S1)
				.updateStatements(StatementUpdateBuilder.create().add(JOHN_HAS_BROWN_HAIR).build())
				.build();
		assertThat(update.getStatements().getAdded(), containsInAnyOrder(JOHN_HAS_BROWN_HAIR));
	}

	@Test
	public void testBlindSenseUpdate() {
		assertThrows(NullPointerException.class, () -> SenseUpdateBuilder.forEntityId(S1).updateGlosses(null));
		SenseUpdate update = SenseUpdateBuilder.forEntityId(S1)
				.updateGlosses(TermUpdateBuilder.create().remove("en").build())
				.updateGlosses(TermUpdateBuilder.create().remove("sk").build())
				.build();
		assertThat(update.getGlosses().getRemoved(), containsInAnyOrder("en", "sk"));
	}

	@Test
	public void testBaseSenseUpdate() {
		SenseUpdate update = SenseUpdateBuilder
				.forBaseRevision(SENSE
						.withGloss(EN)
						.withGloss(SK))
				.updateGlosses(TermUpdateBuilder.create()
						.put(SK) // ignored
						.remove("en") // checked
						.build())
				.build();
		assertThat(update.getGlosses().getModified(), is(anEmptyMap()));
		assertThat(update.getGlosses().getRemoved(), containsInAnyOrder("en"));
	}

	@Test
	public void testMerge() {
		assertThrows(NullPointerException.class, () -> SenseUpdateBuilder.forEntityId(S1).append(null));
		SenseUpdate update = SenseUpdateBuilder.forEntityId(S1)
				.updateStatements(StatementUpdateBuilder.create().add(JOHN_HAS_BROWN_HAIR).build())
				.updateGlosses(TermUpdateBuilder.create().remove("en").build())
				.append(SenseUpdateBuilder.forEntityId(S1)
						.updateStatements(StatementUpdateBuilder.create().add(JOHN_HAS_BLUE_EYES).build())
						.updateGlosses(TermUpdateBuilder.create().remove("sk").build())
						.build())
				.build();
		assertThat(update.getStatements().getAdded(),
				containsInAnyOrder(JOHN_HAS_BROWN_HAIR, JOHN_HAS_BLUE_EYES));
		assertThat(update.getGlosses().getRemoved(), containsInAnyOrder("en", "sk"));
	}

}
