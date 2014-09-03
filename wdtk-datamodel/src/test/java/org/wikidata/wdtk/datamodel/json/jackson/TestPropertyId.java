package org.wikidata.wdtk.datamodel.json.jackson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.json.jackson.documents.ids.PropertyIdImpl;

public class TestPropertyId extends JsonConversionTest {

	@Test
	public void testEquality(){
		PropertyIdImpl reference = new PropertyIdImpl(propertyId);
		PropertyIdImpl same = new PropertyIdImpl(propertyId);
		PropertyIdImpl different = new PropertyIdImpl("P2");
		
		assertEquals(reference, same);
		assertEquals(reference, (PropertyIdValue)same);
		assertEquals((PropertyIdValue)reference, (PropertyIdValue)same);
		assertFalse(reference.equals(different));
	}
}
