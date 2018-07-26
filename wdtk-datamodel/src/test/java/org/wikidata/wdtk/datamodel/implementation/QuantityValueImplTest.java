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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.Assert.*;

public class QuantityValueImplTest {

	private final ObjectMapper mapper = new ObjectMapper();

	private final BigDecimal nv = new BigDecimal(
			"0.123456789012345678901234567890123456789");
	private final BigDecimal lb = new BigDecimal(
			"0.123456789012345678901234567890123456788");
	private final BigDecimal ub = new BigDecimal(
			"0.123456789012345678901234567890123456790");
	private final String unitMeter = "http://wikidata.org/entity/Q11573";
	private final QuantityValue q1 = new QuantityValueImpl(nv, lb, ub, unitMeter);
	private final QuantityValue q2 = new QuantityValueImpl(nv, lb, ub, unitMeter);
	private final QuantityValue q3 = new QuantityValueImpl(nv, null, null, unitMeter);
	private final QuantityValue q4 = new QuantityValueImpl(nv, lb, ub, "1");
	private final QuantityValue q5 = new QuantityValueImpl(nv, lb, ub, "foobar");
	private static String JSON_QUANTITY_VALUE = "{\"value\":{\"amount\":\"+0.123456789012345678901234567890123456789\",\"lowerBound\":\"+0.123456789012345678901234567890123456788\",\"upperBound\":\"+0.123456789012345678901234567890123456790\",\"unit\":\"http://wikidata.org/entity/Q11573\"},\"type\":\"quantity\"}";
	private static String JSON_UNBOUNDED_QUANTITY_VALUE = "{\"value\":{\"amount\":\"+0.123456789012345678901234567890123456789\",\"unit\":\"http://wikidata.org/entity/Q11573\"},\"type\":\"quantity\"}";

	@Test
	public void gettersWorking() {
		assertEquals(q1.getNumericValue(), nv);
		assertEquals(q1.getLowerBound(), lb);
		assertEquals(q1.getUpperBound(), ub);
	}

	@Test
	public void getUnitItemId() {
		assertEquals(new ItemIdValueImpl("Q11573", "http://wikidata.org/entity/"), q1.getUnitItemId());
	}

	@Test
	public void getUnitItemIdNoUnit() {
		assertNull(q4.getUnitItemId());
	}

	@Test(expected = IllegalArgumentException.class)
	public void getUnitItemIdInvalidIri() {
		q5.getUnitItemId();
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
		QuantityValue q7 = new QuantityValueImpl(nv, lb, ub, "1");

		assertEquals(q1, q1);
		assertEquals(q1, q2);
		assertNotEquals(q1, q3);
		assertNotEquals(q1, q4);
		assertNotEquals(q1, q5);
		assertNotEquals(q1, q6);
		assertNotEquals(q1, q7);
		assertNotEquals(q1, null);
		assertNotEquals(q1, this);
	}
	
	@Test
	public void equalityBasedOnRepresentation() {
		BigDecimal amount1 = new BigDecimal("4.00");
		BigDecimal amount2 = new BigDecimal("4");
		assertNotEquals(amount1, amount2);
		QuantityValue quantity1 = new QuantityValueImpl(amount1, null, null, "1");
		QuantityValue quantity2 = new QuantityValueImpl(amount2, null, null, "1");
		assertNotEquals(quantity1, quantity2);
	}
	
	@Test
	public void faithfulJsonSerialization() {
		BigDecimal amount = new BigDecimal("4.00");
		QuantityValueImpl quantity = new QuantityValueImpl(amount, null, null, "1");
		assertEquals("+4.00", quantity.getValue().getAmountAsString());
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
		new QuantityValueImpl(nv, null, ub, unitMeter);
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
	public void unitNotEmpty() {
		new QuantityValueImpl(nv, lb, ub, "");
	}

	@Test(expected = IllegalArgumentException.class)
	public void lowerBoundNotGreaterNumVal() {
		new QuantityValueImpl(lb, nv, ub, unitMeter);
	}

	@Test(expected = IllegalArgumentException.class)
	public void numValNotGreaterLowerBound() {
		new QuantityValueImpl(ub, lb, nv, unitMeter);
	}

	@Test
	public void testToJson() throws JsonProcessingException {
		JsonComparator.compareJsonStrings(JSON_QUANTITY_VALUE, mapper.writeValueAsString(q1));
	}

	@Test
	public void testToJava() throws IOException {
		assertEquals(q1, mapper.readValue(JSON_QUANTITY_VALUE, ValueImpl.class));
	}

	@Test
	public void testUnboundedToJson() throws JsonProcessingException {
		JsonComparator.compareJsonStrings(JSON_UNBOUNDED_QUANTITY_VALUE, mapper.writeValueAsString(q3));
	}

	@Test
	public void testUnboundedToJava() throws IOException {
		assertEquals(q3, mapper.readValue(JSON_UNBOUNDED_QUANTITY_VALUE, ValueImpl.class));
	}
}
