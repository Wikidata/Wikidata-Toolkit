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

import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;

public class QuantityValueImplTest {

	final BigDecimal nv = new BigDecimal(
			"0.123456789012345678901234567890123456789");
	final BigDecimal lb = new BigDecimal(
			"0.123456789012345678901234567890123456788");
	final BigDecimal ub = new BigDecimal(
			"0.123456789012345678901234567890123456790");
	final String unitMeter = "http://wikidata.org/entity/Q11573";
	final QuantityValue q1 = new QuantityValueImpl(nv, lb, ub, unitMeter);
	final QuantityValue q2 = new QuantityValueImpl(nv, lb, ub, unitMeter);
	final QuantityValue q3 = new QuantityValueImpl(nv, null, null, unitMeter);

	@Test
	public void gettersWorking() {
		assertEquals(q1.getNumericValue(), nv);
		assertEquals(q1.getLowerBound(), lb);
		assertEquals(q1.getUpperBound(), ub);
	}

	@Test
	public void equalityBasedOnContent() {
		BigDecimal nvplus = new BigDecimal(
				"0.1234567890123456789012345678901234567895");
		BigDecimal nvminus = new BigDecimal(
				"0.1234567890123456789012345678901234567885");
		QuantityValue q4 = new QuantityValueImpl(nvplus, lb, ub, unitMeter);
		QuantityValue q5 = new QuantityValueImpl(nv, nvminus, ub, unitMeter);
		QuantityValue q6 = new QuantityValueImpl(nv, lb, nvplus, unitMeter);
		QuantityValue q7 = new QuantityValueImpl(nv, lb, ub, "");

		assertEquals(q1, q1);
		assertEquals(q1, q2);
		assertThat(q1, not(equalTo(q3)));
		assertThat(q1, not(equalTo(q4)));
		assertThat(q1, not(equalTo(q5)));
		assertThat(q1, not(equalTo(q6)));
		assertThat(q1, not(equalTo(q7)));
		assertThat(q1, not(equalTo(null)));
		assertFalse(q1.equals(this));
	}

	@Test
	public void hashBasedOnContent() {
		assertEquals(q1.hashCode(), q2.hashCode());
	}

	@Test(expected = NullPointerException.class)
	public void numValueNotNull() {
		new QuantityValueImpl(null, lb, ub, unitMeter);
	}

	@Test(expected = NullPointerException.class)
	public void lowerBoundNotNull() {
		new QuantityValueImpl(nv, null, lb, unitMeter);
	}

	@Test(expected = NullPointerException.class)
	public void upperBoundNotNull() {
		new QuantityValueImpl(nv, lb, null, unitMeter);
	}

	@Test(expected = NullPointerException.class)
	public void unitNotNull() {
		new QuantityValueImpl(nv, lb, ub, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void lowerBoundNotGreaterNumVal() {
		new QuantityValueImpl(lb, nv, ub, unitMeter);
	}

	@Test(expected = IllegalArgumentException.class)
	public void numValNotGreaterLowerBound() {
		new QuantityValueImpl(ub, lb, nv, unitMeter);
	}

}
