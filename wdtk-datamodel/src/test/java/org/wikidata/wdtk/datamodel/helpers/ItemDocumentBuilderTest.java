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

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;

public class ItemDocumentBuilderTest {
	
	ItemIdValue i;
	StatementGroup sg;
	Statement s2;
	Statement s1;
	
	@Before
	public void setUp() {
		i = ItemIdValue.NULL;
		s1 = StatementBuilder.forSubjectAndProperty(i,
				Datamodel.makeWikidataPropertyIdValue("P1")).build();
		s2 = StatementBuilder
				.forSubjectAndProperty(i,
						Datamodel.makeWikidataPropertyIdValue("P1"))
				.withValue(i).build();
		sg = Datamodel.makeStatementGroup(Arrays.asList(s1, s2));
	}

	@Test
	public void testEmptyItemDocumentBuild() {
		ItemDocument id1 = Datamodel.makeItemDocument(ItemIdValue.NULL,
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyMap(), 0);

		ItemDocument id2 = ItemDocumentBuilder.forItemId(ItemIdValue.NULL)
				.build();

		assertEquals(id1, id2);
	}

	@Test
	public void testComplexItemDocumentBuild() {


		MonolingualTextValue mtv = Datamodel.makeMonolingualTextValue("Test",
				"de");
		SiteLink sl = Datamodel.makeSiteLink("Test", "frwiki",
				Collections.singletonList(Datamodel.makeWikidataItemIdValue("Q42")));

		ItemDocument id1 = Datamodel.makeItemDocument(i,
				Collections.singletonList(mtv), Collections.singletonList(mtv),
				Collections.singletonList(mtv), Collections.singletonList(sg),
				Collections.singletonMap("frwiki", sl), 1234);

		ItemDocument id2 = ItemDocumentBuilder.forItemId(i)
				.withLabel("Test", "de").withDescription("Test", "de")
				.withAlias("Test", "de")
				.withSiteLink("Test", "frwiki", Datamodel.makeWikidataItemIdValue("Q42")).withStatement(s1)
				.withStatement(s2).withRevisionId(1234).build();

		assertEquals(id1, id2);
	}
	
	@Test
	public void testModifyingBuild() {
		MonolingualTextValue label = Datamodel.makeMonolingualTextValue("canneberge",
				"fr");
		MonolingualTextValue alias1 = Datamodel.makeMonolingualTextValue("grande airelle rouge d’Amérique du Nord",
				"fr");
		MonolingualTextValue alias2 = Datamodel.makeMonolingualTextValue("atoca", "fr");
		SiteLink sl = Datamodel.makeSiteLink("Canneberge", "frwiki",
				Collections.singletonList(Datamodel.makeWikidataItemIdValue("Q42")));
		
		ItemDocument initial = Datamodel.makeItemDocument(i,
				Collections.singletonList(label),
				Collections.emptyList(),
		        Arrays.asList(alias1, alias2),
		        Collections.singletonList(sg),
		        Collections.singletonMap("frwiki", sl), 1234);
		
		ItemDocument copy = ItemDocumentBuilder.fromItemDocument(initial).build();
		
		assertEquals(copy, initial);
		
		MonolingualTextValue alias3 = Datamodel.makeMonolingualTextValue("cranberry",
				"fr");
		
		ItemDocument withAlias = ItemDocumentBuilder.fromItemDocument(initial).withAlias(alias3).build();
		assertEquals(withAlias.getAliases().get("fr"), Arrays.asList(alias1, alias2, alias3));
	}
	
	@Test
	public void testChangeOfSubjectId() {
		MonolingualTextValue label = Datamodel.makeMonolingualTextValue("pleutre",
				"fr");
		ItemDocument initial = Datamodel.makeItemDocument(i,
				Collections.singletonList(label), Collections.emptyList(), Collections.emptyList(),
				Collections.singletonList(sg),
				Collections.emptyMap(), 4567);
		
		ItemDocument copy = ItemDocumentBuilder.fromItemDocument(initial).withEntityId(ItemIdValue.NULL).build();
		
		assertEquals(ItemIdValue.NULL, copy.getEntityId());
		assertEquals("pleutre", copy.findLabel("fr"));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testInvalidChangeOfSubjectId() {
		ItemDocumentBuilder.forItemId(ItemIdValue.NULL).withRevisionId(1234).withEntityId(PropertyIdValue.NULL);
	}

	@Test(expected = IllegalStateException.class)
	public void testDoubleBuild() {
		ItemDocumentBuilder b = ItemDocumentBuilder.forItemId(ItemIdValue.NULL);
		b.build();
		b.build();
	}
}
