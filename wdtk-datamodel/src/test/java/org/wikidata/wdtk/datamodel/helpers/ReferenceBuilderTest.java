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
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;

public class ReferenceBuilderTest {

	@Test
	public void testEmptyReference() {
		Reference r1 = Datamodel.makeReference(Collections
				.emptyList());
		Reference r2 = ReferenceBuilder.newInstance().build();

		assertEquals(r1, r2);
	}

	@Test
	public void testComplexReference() {
		ItemIdValue i = ItemIdValue.NULL;
		PropertyIdValue p = PropertyIdValue.NULL;

		Snak q1 = Datamodel.makeSomeValueSnak(p);
		Snak q2 = Datamodel.makeNoValueSnak(p);
		Snak q3 = Datamodel.makeValueSnak(p, i);
		SnakGroup sg = Datamodel.makeSnakGroup(Arrays.asList(q1, q2, q3));

		Reference r1 = Datamodel.makeReference(Collections.singletonList(sg));
		Reference r2 = ReferenceBuilder.newInstance().withSomeValue(p)
				.withNoValue(p).withPropertyValue(p, i).build();

		assertEquals(r1, r2);
	}

}
