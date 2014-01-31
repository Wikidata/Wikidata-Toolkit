package org.wikidata.wdtk.datamodel.implementation;

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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;

public class QuantityValueImplTest {

	QuantityValue q1;
	QuantityValue q2;

	@Before
	public void setUp() throws Exception {
		BigDecimal nv = new BigDecimal(
				"0.123456789012345678901234567890123456789");
		q1 = new QuantityValueImpl(nv);
		q2 = new QuantityValueImpl(nv);
	}

	@Test
	public void quantityIsCorrect() {
		BigDecimal nv = new BigDecimal(
				"0.123456789012345678901234567890123456789");
		assertEquals(q1.getNumericValue(), nv);
	}

	@Test
	public void quantitiyValueEqualityBasedOnContent() {
		BigDecimal nv = new BigDecimal("42");
		QuantityValue q3 = new QuantityValueImpl(nv);

		assertEquals(q1, q1);
		assertEquals(q1, q2);
		assertThat(q1, not(equalTo(q3)));
		assertThat(q1, not(equalTo(null)));
		assertFalse(q1.equals(this));
	}

	@Test
	public void quantityValueHashBasedOnContent() {
		assertEquals(q1.hashCode(), q2.hashCode());
	}

	@Test(expected = NullPointerException.class)
	public void quantityValueNumValueNotNull() {
		new QuantityValueImpl(null);
	}

}
