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
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;

public class PropertyDocumentBuilderTest {

	@Test
	public void testSimplePropertyDocumentBuild() {
		MonolingualTextValue mtv = Datamodel.makeMonolingualTextValue("Test",
				"de");

		PropertyDocument pd1 = Datamodel.makePropertyDocument(
				PropertyIdValue.NULL, Collections.singletonList(mtv),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				Datamodel.makeDatatypeIdValue(DatatypeIdValue.DT_ITEM));

		PropertyDocument pd2 = PropertyDocumentBuilder
				.forPropertyIdAndDatatype(PropertyIdValue.NULL,
						DatatypeIdValue.DT_ITEM).withLabel(mtv).build();

		assertEquals(pd1, pd2);
	}
	
	@Test
	public void testModifyingBuild() {
		MonolingualTextValue label = Datamodel.makeMonolingualTextValue("color",
				"en");
		
		PropertyDocument initial = Datamodel.makePropertyDocument(PropertyIdValue.NULL,
				Collections.singletonList(label),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections. emptyList(),
				Datamodel.makeDatatypeIdValue(DatatypeIdValue.DT_QUANTITY),
		        1234);
		
		PropertyDocument copy = PropertyDocumentBuilder.fromPropertyDocument(initial).build();
		assertEquals(copy, initial);
		
		MonolingualTextValue alias = Datamodel.makeMonolingualTextValue("tone",
				"en");
		
		PropertyDocument withAlias = PropertyDocumentBuilder.fromPropertyDocument(initial).withAlias(alias).build();
		assertEquals(withAlias.getAliases().get("en"), Collections.singletonList(alias));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testInvalidSubjectId() {
		PropertyDocumentBuilder.forPropertyIdAndDatatype(PropertyIdValue.NULL, DatatypeIdValue.DT_EXTERNAL_ID).withEntityId(ItemIdValue.NULL);
	}
}
