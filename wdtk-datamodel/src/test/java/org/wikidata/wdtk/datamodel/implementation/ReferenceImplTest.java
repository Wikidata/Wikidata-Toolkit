package org.wikidata.wdtk.datamodel.implementation;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

public class ReferenceImplTest {

	Reference r1;
	Reference r2;
	ValueSnak valueSnak;

	@Before
	public void setUp() throws Exception {
		EntityIdValue subject = new ItemIdValueImpl("Q42",
				"http://wikidata.org/entity/");
		PropertyIdValue property = new PropertyIdValueImpl("P42",
				"http://wikidata.org/entity/");
		valueSnak = new ValueSnakImpl(property, subject);
		r1 = new ReferenceImpl(Collections.<ValueSnak> singletonList(valueSnak));
		r2 = new ReferenceImpl(Collections.<ValueSnak> singletonList(valueSnak));
	}

	@Test
	public void snakListIsCorrect() {
		assertEquals(r1.getSnaks(),
				Collections.<ValueSnak> singletonList(valueSnak));
	}

	@Test
	public void equalityBasedOnContent() {
		Reference r3 = new ReferenceImpl(Collections.<ValueSnak> emptyList());

		assertEquals(r1, r1);
		assertEquals(r1, r2);
		assertThat(r1, not(equalTo(r3)));
		assertThat(r1, not(equalTo(null)));
		assertFalse(r1.equals(this));
	}

	@Test
	public void hashBasedOnContent() {
		assertEquals(r1.hashCode(), r2.hashCode());
	}

	@Test(expected = NullPointerException.class)
	public void SnakListNotNull() {
		new ReferenceImpl(null);
	}

}
