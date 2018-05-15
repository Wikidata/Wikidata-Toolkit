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

import static org.junit.Assert.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;

import java.io.IOException;

public class TimeValueImplTest {

	private final ObjectMapper mapper = new ObjectMapper();

	private final TimeValue t1 = new TimeValueImpl(2007, (byte) 5, (byte) 12, (byte) 10, (byte) 45,
			(byte) 0, TimeValue.PREC_SECOND, 0, 1, 60,
			TimeValue.CM_GREGORIAN_PRO);
	private final TimeValue t2 = new TimeValueImpl(2007, (byte) 5, (byte) 12, (byte) 10, (byte) 45,
			(byte) 0, TimeValue.PREC_SECOND, 0, 1, 60,
			TimeValue.CM_GREGORIAN_PRO);
	private final TimeValue t3 = new TimeValueImpl(2007, (byte) 5, (byte) 12, (byte) 10, (byte) 45,
			(byte) 0, TimeValue.PREC_SECOND, 0, 1, 60, "foo");
	private final String JSON_TIME_VALUE = "{\"value\":{\"time\":\"+2007-05-12T10:45:00Z\",\"timezone\":60,\"before\":0,\"after\":1,\"precision\":14,\"calendarmodel\":\"http://www.wikidata.org/entity/Q1985727\"},\"type\":\"time\"}";

	@Test
	public void storedValuesCorrect() {
		assertEquals(t1.getYear(), 2007);
		assertEquals(t1.getMonth(), 5);
		assertEquals(t1.getDay(), 12);
		assertEquals(t1.getHour(), 10);
		assertEquals(t1.getMinute(), 45);
		assertEquals(t1.getSecond(), 0);
		assertEquals(t1.getPrecision(), TimeValue.PREC_SECOND);
		assertEquals(t1.getBeforeTolerance(), 0);
		assertEquals(t1.getAfterTolerance(), 1);
		assertEquals(t1.getTimezoneOffset(), 60);
		assertEquals(t1.getPreferredCalendarModel(), TimeValue.CM_GREGORIAN_PRO);
	}

	@Test
	public void getPreferredCalendarModelItemId() {
		assertEquals(new ItemIdValueImpl("Q1985727", "http://www.wikidata.org/entity/"), t1.getPreferredCalendarModelItemId());
	}

	@Test(expected = IllegalArgumentException.class)
	public void getPreferredCalendarModelItemIdInvalidIri() {
		t3.getPreferredCalendarModelItemId();
	}

	@Test
	public void equalityBasedOnContent() {
		TimeValue tdYear = new TimeValueImpl(2013, (byte) 5, (byte) 12,
				(byte) 10, (byte) 45, (byte) 0, TimeValue.PREC_SECOND, 0, 1,
				60, TimeValue.CM_GREGORIAN_PRO);
		TimeValue tdMonth = new TimeValueImpl(2007, (byte) 6, (byte) 12,
				(byte) 10, (byte) 45, (byte) 0, TimeValue.PREC_SECOND, 0, 1,
				60, TimeValue.CM_GREGORIAN_PRO);
		TimeValue tdDay = new TimeValueImpl(2007, (byte) 5, (byte) 13,
				(byte) 10, (byte) 45, (byte) 0, TimeValue.PREC_SECOND, 0, 1,
				60, TimeValue.CM_GREGORIAN_PRO);
		TimeValue tdHour = new TimeValueImpl(2007, (byte) 5, (byte) 12,
				(byte) 11, (byte) 45, (byte) 0, TimeValue.PREC_SECOND, 0, 1,
				60, TimeValue.CM_GREGORIAN_PRO);
		TimeValue tdMinute = new TimeValueImpl(2007, (byte) 5, (byte) 12,
				(byte) 10, (byte) 47, (byte) 0, TimeValue.PREC_SECOND, 0, 1,
				60, TimeValue.CM_GREGORIAN_PRO);
		TimeValue tdSecond = new TimeValueImpl(2007, (byte) 5, (byte) 12,
				(byte) 10, (byte) 45, (byte) 1, TimeValue.PREC_SECOND, 0, 1,
				60, TimeValue.CM_GREGORIAN_PRO);
		TimeValue tdTimezone = new TimeValueImpl(2007, (byte) 5, (byte) 12,
				(byte) 10, (byte) 45, (byte) 0, TimeValue.PREC_SECOND, 0, 1,
				120, TimeValue.CM_GREGORIAN_PRO);
		TimeValue tdBefore = new TimeValueImpl(2007, (byte) 5, (byte) 12,
				(byte) 10, (byte) 45, (byte) 0, TimeValue.PREC_SECOND, 1, 1,
				60, TimeValue.CM_GREGORIAN_PRO);
		TimeValue tdAfter = new TimeValueImpl(2007, (byte) 5, (byte) 12,
				(byte) 10, (byte) 45, (byte) 0, TimeValue.PREC_SECOND, 0, 2,
				60, TimeValue.CM_GREGORIAN_PRO);
		TimeValue tdPrecision = new TimeValueImpl(2007, (byte) 5, (byte) 12,
				(byte) 10, (byte) 45, (byte) 0, TimeValue.PREC_DAY, 0, 1, 60,
				TimeValue.CM_GREGORIAN_PRO);
		TimeValue tdCalendar = new TimeValueImpl(2007, (byte) 5, (byte) 12,
				(byte) 10, (byte) 45, (byte) 0, TimeValue.PREC_SECOND, 0, 1,
				60, TimeValue.CM_JULIAN_PRO);

		assertEquals(t1, t1);
		assertEquals(t1, t2);
		assertNotEquals(t1, tdYear);
		assertNotEquals(t1, tdMonth);
		assertNotEquals(t1, tdDay);
		assertNotEquals(t1, tdHour);
		assertNotEquals(t1, tdMinute);
		assertNotEquals(t1, tdSecond);
		assertNotEquals(t1, tdTimezone);
		assertNotEquals(t1, tdBefore);
		assertNotEquals(t1, tdAfter);
		assertNotEquals(t1, tdPrecision);
		assertNotEquals(t1, tdCalendar);
		assertNotEquals(t1, null);
		assertNotEquals(t1,  this);
	}

	@Test
	public void hashBasedOnContent() {
		assertEquals(t1.hashCode(), t2.hashCode());
	}

	@Test(expected = NullPointerException.class)
	public void calendarModelNotNull() {
		new TimeValueImpl(2007, (byte) 5, (byte) 12, (byte) 10, (byte) 45,
				(byte) 0, TimeValue.PREC_SECOND, 0, 1, 60, null);
	}

	@Test
	public void largeYearValues() {
		// May 12 in the first year after the beginning of the universe:
		TimeValue t = new TimeValueImpl(-13800000000L, (byte) 5, (byte) 12,
				(byte) 10, (byte) 45, (byte) 0, TimeValue.PREC_SECOND, 0, 1,
				60, TimeValue.CM_GREGORIAN_PRO);
		assertEquals(t.getYear(), -13800000000L);
	}

	@Test
	public void testToJson() throws JsonProcessingException {
		JsonComparator.compareJsonStrings(JSON_TIME_VALUE, mapper.writeValueAsString(t1));
	}

	@Test
	public void testToJava() throws IOException {
		assertEquals(t1, mapper.readValue(JSON_TIME_VALUE, ValueImpl.class));
	}

}
