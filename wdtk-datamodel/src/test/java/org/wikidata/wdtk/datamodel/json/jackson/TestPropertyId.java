package org.wikidata.wdtk.datamodel.json.jackson;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.json.jackson.documents.ids.JacksonPropertyId;

public class TestPropertyId extends JsonConversionTest {

	@Test
	public void testEquality(){
		JacksonPropertyId reference = new JacksonPropertyId(propertyId);
		JacksonPropertyId same = new JacksonPropertyId(propertyId);
		JacksonPropertyId different = new JacksonPropertyId("P2");
		
		assertEquals(reference, same);
		assertEquals(reference, (PropertyIdValue)same);
		assertEquals((PropertyIdValue)reference, (PropertyIdValue)same);
		assertFalse(reference.equals(different));
	}
}
