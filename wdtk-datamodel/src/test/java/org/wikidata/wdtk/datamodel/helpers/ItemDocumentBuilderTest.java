package org.wikidata.wdtk.datamodel.helpers;

/*
 * #%L
 * Wikidata Toolkit Data Model
 * %%
 * Copyright (C) 2014 - 2015 Wikidata Toolkit Developers
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

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;

public class ItemDocumentBuilderTest {

	@Test
	public void testEmptyItemDocumentBuild() {
		ItemDocument id1 = Datamodel.makeItemDocument(ItemIdValue.NULL,
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<StatementGroup> emptyList(),
				Collections.<String, SiteLink> emptyMap(), 0);

		ItemDocument id2 = ItemDocumentBuilder.forItemId(ItemIdValue.NULL)
				.build();

		assertEquals(id1, id2);
	}

	@Test
	public void testComplexItemDocumentBuild() {
		ItemIdValue i = ItemIdValue.NULL;

		MonolingualTextValue mtv = Datamodel.makeMonolingualTextValue("Test",
				"de");
		SiteLink sl = Datamodel.makeSiteLink("Test", "frwiki",
				Collections.singletonList("Badge"));

		Statement s1 = StatementBuilder.forSubjectAndProperty(i,
				Datamodel.makeWikidataPropertyIdValue("P1")).build();
		Statement s2 = StatementBuilder
				.forSubjectAndProperty(i,
						Datamodel.makeWikidataPropertyIdValue("P1"))
				.withValue(i).build();
		StatementGroup sg = Datamodel.makeStatementGroup(Arrays.asList(s1, s2));

		ItemDocument id1 = Datamodel.makeItemDocument(i,
				Collections.singletonList(mtv), Collections.singletonList(mtv),
				Collections.singletonList(mtv), Collections.singletonList(sg),
				Collections.singletonMap("frwiki", sl), 1234);

		ItemDocument id2 = ItemDocumentBuilder.forItemId(i)
				.withLabel("Test", "de").withDescription("Test", "de")
				.withAlias("Test", "de")
				.withSiteLink("Test", "frwiki", "Badge").withStatement(s1)
				.withStatement(s2).withRevisionId(1234).build();

		assertEquals(id1, id2);
	}

	@Test(expected = IllegalStateException.class)
	public void testDoubleBuild() {
		ItemDocumentBuilder b = ItemDocumentBuilder.forItemId(ItemIdValue.NULL);
		b.build();
		b.build();
	}
}
