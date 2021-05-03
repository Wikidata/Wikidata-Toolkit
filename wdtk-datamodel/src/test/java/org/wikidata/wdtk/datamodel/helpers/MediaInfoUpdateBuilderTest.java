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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.wikidata.wdtk.datamodel.interfaces.MediaInfoDocument;
import org.wikidata.wdtk.datamodel.interfaces.MediaInfoIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MediaInfoUpdate;
import org.wikidata.wdtk.datamodel.interfaces.Statement;

public class MediaInfoUpdateBuilderTest {

	private static final MediaInfoIdValue M1 = EntityUpdateBuilderTest.M1;
	private static final MediaInfoDocument MEDIA = EntityUpdateBuilderTest.MEDIA;
	private static final Statement JOHN_HAS_BROWN_HAIR = StatementUpdateBuilderTest.JOHN_HAS_BROWN_HAIR;

	@Test
	public void testForEntityId() {
		assertThrows(NullPointerException.class, () -> MediaInfoUpdateBuilder.forEntityId(null));
		assertThrows(IllegalArgumentException.class, () -> MediaInfoUpdateBuilder.forEntityId(MediaInfoIdValue.NULL));
		MediaInfoUpdateBuilder.forEntityId(M1);
	}

	@Test
	public void testForBaseRevision() {
		assertThrows(NullPointerException.class, () -> MediaInfoUpdateBuilder.forBaseRevision(null));
		assertThrows(IllegalArgumentException.class,
				() -> MediaInfoUpdateBuilder.forBaseRevision(Datamodel.makeMediaInfoDocument(MediaInfoIdValue.NULL)));
		MediaInfoUpdateBuilder.forBaseRevision(MEDIA);
	}

	@Test
	public void testStatementUpdate() {
		MediaInfoUpdate update = MediaInfoUpdateBuilder.forEntityId(M1)
				.updateStatements(StatementUpdateBuilder.create().add(JOHN_HAS_BROWN_HAIR).build())
				.build();
		assertThat(update.getStatements().getAdded(), containsInAnyOrder(JOHN_HAS_BROWN_HAIR));
	}

	@Test
	public void testLabelUpdate() {
		MediaInfoUpdate update = MediaInfoUpdateBuilder.forEntityId(M1)
				.updateLabels(TermUpdateBuilder.create().remove("en").build())
				.build();
		assertThat(update.getLabels().getRemoved(), containsInAnyOrder("en"));
	}

	@Test
	public void testMerge() {
		MediaInfoUpdate update = MediaInfoUpdateBuilder.forEntityId(M1)
				.updateLabels(TermUpdateBuilder.create().remove("en").build())
				.apply(MediaInfoUpdateBuilder.forEntityId(M1)
						.updateLabels(TermUpdateBuilder.create().remove("sk").build())
						.build())
				.build();
		assertThat(update.getLabels().getRemoved(), containsInAnyOrder("sk", "en"));
	}

}
