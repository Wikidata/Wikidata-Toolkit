package org.wikidata.wdtk.datamodel.json.jackson;

import static org.junit.Assert.assertEquals;

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
	
	@Test
	public void testIriForUnknownType(){
		DatatypeIdImpl uut = new DatatypeIdImpl("some wrong type");
		assertEquals(uut.getIri(), null);
	}
}
