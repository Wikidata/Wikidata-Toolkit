package org.wikidata.wdtk.datamodel.json.jackson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.EntityId;

/**
 * This class tests the inner objects lying behind the …ValueImpl-classes.
 * 
 * @author Fredo Erxleben
 *
 */
public class TestInnerValueObjects {
	
	private static String itemType = "item";
	private static String wrongType = "wrongType";
	
	private EntityId testEntityId;

	@Before
	public void setupTestEntityIds() {
		this.testEntityId = new EntityId(itemType, 1);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testEntityIdConstructor(){
		@SuppressWarnings("unused")
		EntityId unknownType = new EntityId(wrongType, 1);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testEntityIdSetter(){
		EntityId emptyId = new EntityId();
		emptyId.setNumericId(1);
		emptyId.setEntityType(itemType); // should work
		emptyId.setEntityType(wrongType); // should fail
	}

	@Test
	public void testEntityIdMethods() {
		assertEquals("Q1", this.testEntityId.getStringId());
		assertEquals(EntityIdValue.ET_ITEM, this.testEntityId.getDatamodelEntityType());
		assertEquals(this.testEntityId.toString(), this.testEntityId.getStringId());
		assertEquals(this.testEntityId.getNumericId(), 1);
		
		// test equals
		assertEquals(this.testEntityId, new EntityId("item", 1));
		assertEquals(this.testEntityId, this.testEntityId);
		assertFalse(this.testEntityId.equals(new Object()));
		assertFalse(this.testEntityId.equals(new EntityId("item", 2)));
		
	}
}
