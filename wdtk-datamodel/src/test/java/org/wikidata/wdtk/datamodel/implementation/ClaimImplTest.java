package org.wikidata.wdtk.datamodel.implementation;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.Claim;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

public class ClaimImplTest {

	EntityIdValue subject;
	ValueSnak mainSnak;

	Claim c1;
	Claim c2;

	@Before
	public void setUp() throws Exception {
		subject = new ItemIdValueImpl("Q42", "http://wikidata.org/entity/");
		PropertyIdValue property = new PropertyIdValueImpl("P42",
				"http://wikidata.org/entity/");
		mainSnak = new ValueSnakImpl(property, subject);

		c1 = new ClaimImpl(subject, mainSnak, Collections.<Snak> emptyList());
		c2 = new ClaimImpl(subject, mainSnak, Collections.<Snak> emptyList());
	}

	@Test
	public void gettersWorking() {
		assertEquals(c1.getSubject(), subject);
		assertEquals(c1.getMainSnak(), mainSnak);
		assertEquals(c1.getQualifiers(), Collections.<Snak> emptyList());
	}

	@Test(expected = NullPointerException.class)
	public void subjectNotNull() {
		new ClaimImpl(null, mainSnak, Collections.<Snak> emptyList());
	}

	@Test(expected = NullPointerException.class)
	public void mainSnakNotNull() {
		new ClaimImpl(subject, null, Collections.<Snak> emptyList());
	}

	@Test(expected = NullPointerException.class)
	public void qualifiersNotNull() {
		new ClaimImpl(subject, mainSnak, null);
	}

	@Test
	public void hashBasedOnContent() {
		assertEquals(c1.hashCode(), c2.hashCode());
	}

	@Test
	public void statementEqualityBasedOnContent() {
		Claim c3, c4, c5;
		EntityIdValue subject2 = new ItemIdValueImpl("Q43",
				"http://wikidata.org/entity/");
		PropertyIdValue property = new PropertyIdValueImpl("P43",
				"http://wikidata.org/entity/");
		ValueSnak mainSnak2 = new ValueSnakImpl(property, subject2);

		c3 = new ClaimImpl(subject2, mainSnak, Collections.<Snak> emptyList());
		c4 = new ClaimImpl(subject, mainSnak2, Collections.<Snak> emptyList());
		c5 = new ClaimImpl(subject, mainSnak,
				Collections.<Snak> singletonList(mainSnak));

		assertEquals(c1, c1);
		assertEquals(c1, c2);
		assertThat(c1, not(equalTo(c3)));
		assertThat(c1, not(equalTo(c4)));
		assertThat(c1, not(equalTo(c5)));
		assertThat(c1, not(equalTo(null)));
		assertFalse(c1.equals(this));
	}
}
