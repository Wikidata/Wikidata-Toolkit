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

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyUpdate;
import org.wikidata.wdtk.datamodel.interfaces.Statement;

public class PropertyUpdateBuilderTest {

	private static final PropertyIdValue P1 = EntityUpdateBuilderTest.P1;
	private static final PropertyDocument PROPERTY = EntityUpdateBuilderTest.PROPERTY;
	private static final Statement JOHN_HAS_BROWN_HAIR = StatementUpdateBuilderTest.JOHN_HAS_BROWN_HAIR;

	@Test
	public void testForEntityId() {
		assertThrows(NullPointerException.class, () -> PropertyUpdateBuilder.forEntityId(null));
		assertThrows(IllegalArgumentException.class, () -> PropertyUpdateBuilder.forEntityId(PropertyIdValue.NULL));
		PropertyUpdateBuilder.forEntityId(P1);
	}

	@Test
	public void testForBaseRevision() {
		assertThrows(NullPointerException.class, () -> PropertyUpdateBuilder.forBaseRevision(null));
		assertThrows(IllegalArgumentException.class, () -> PropertyUpdateBuilder.forBaseRevision(
				Datamodel.makePropertyDocument(
						PropertyIdValue.NULL,
						Datamodel.makeDatatypeIdValue(DatatypeIdValue.DT_ITEM))));
		PropertyUpdateBuilder.forBaseRevision(PROPERTY);
	}

	@Test
	public void testStatementUpdate() {
		PropertyUpdate update = PropertyUpdateBuilder.forEntityId(P1)
				.updateStatements(StatementUpdateBuilder.create().addStatement(JOHN_HAS_BROWN_HAIR).build())
				.build();
		assertThat(update.getStatements().getAddedStatements(), containsInAnyOrder(JOHN_HAS_BROWN_HAIR));
	}

	@Test
	public void testLabelUpdate() {
		PropertyUpdate update = PropertyUpdateBuilder.forEntityId(P1)
				.updateLabels(TermUpdateBuilder.create().removeTerm("en").build())
				.build();
		assertThat(update.getLabels().getRemovedTerms(), containsInAnyOrder("en"));
	}

	@Test
	public void testDescriptionUpdate() {
		PropertyUpdate update = PropertyUpdateBuilder.forEntityId(P1)
				.updateDescriptions(TermUpdateBuilder.create().removeTerm("en").build())
				.build();
		assertThat(update.getDescriptions().getRemovedTerms(), containsInAnyOrder("en"));
	}

	@Test
	public void testAliasUpdate() {
		PropertyUpdate update = PropertyUpdateBuilder.forEntityId(P1)
				.setAliases("en", Arrays.asList("hello"))
				.build();
		assertThat(update.getAliases().keySet(), containsInAnyOrder("en"));
	}

	@Test
	public void testMerge() {
		PropertyUpdate update = PropertyUpdateBuilder.forEntityId(P1)
				.updateDescriptions(TermUpdateBuilder.create().removeTerm("en").build())
				.apply(PropertyUpdateBuilder.forEntityId(P1)
						.updateDescriptions(TermUpdateBuilder.create().removeTerm("sk").build())
						.build())
				.build();
		assertThat(update.getDescriptions().getRemovedTerms(), containsInAnyOrder("sk", "en"));
	}

}
