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
import static org.junit.Assert.fail;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.json.jackson.documents.ids.DatatypeIdImpl;

public class TestDatatypeId extends JsonConversionTest {

	@Test
	public void testIriForItem(){
		DatatypeIdImpl uut = new DatatypeIdImpl(DatatypeIdImpl.jsonTypeItem);
		assertEquals(uut.getIri(), DatatypeIdValue.DT_ITEM);
	}
	
	@Test
	public void testIriForCoordinate(){
		DatatypeIdImpl uut = new DatatypeIdImpl(DatatypeIdImpl.jsonTypeGlobe);
		assertEquals(uut.getIri(), DatatypeIdValue.DT_GLOBE_COORDINATES);
	}
	
	@Test
	public void testIriForTime(){
		DatatypeIdImpl uut = new DatatypeIdImpl(DatatypeIdImpl.jsonTypeTime);
		assertEquals(uut.getIri(), DatatypeIdValue.DT_TIME);
	}
	
	@Test
	public void testIriForString(){
		DatatypeIdImpl uut = new DatatypeIdImpl(DatatypeIdImpl.jsonTypeString);
		assertEquals(uut.getIri(), DatatypeIdValue.DT_STRING);
	}
	
	@Test
	public void testIriForQuantity(){
		DatatypeIdImpl uut = new DatatypeIdImpl(DatatypeIdImpl.jsonTypeQuantity);
		assertEquals(uut.getIri(), DatatypeIdValue.DT_QUANTITY);
	}
	
	@Test
	public void testIriForCommons(){
		DatatypeIdImpl uut = new DatatypeIdImpl(DatatypeIdImpl.jsonTypeCommonsMedia);
		assertEquals(uut.getIri(), DatatypeIdValue.DT_COMMONS_MEDIA);
	}
	
	@Test
	public void testIriForUrl(){
		DatatypeIdImpl uut = new DatatypeIdImpl(DatatypeIdImpl.jsonTypeUrl);
		assertEquals(uut.getIri(), DatatypeIdValue.DT_URL);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIriForUnknownType(){
		@SuppressWarnings("unused")
		DatatypeIdImpl uut = new DatatypeIdImpl("some wrong type");
		// if we reach this point, the exception did not occur
		fail("Expected test to be aborted with an IllegalArgumentException.");
	}
}
