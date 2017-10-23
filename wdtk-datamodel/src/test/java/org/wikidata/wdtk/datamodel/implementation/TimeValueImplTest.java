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

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;

public class TimeValueImplTest {

	TimeValue t1;
	TimeValue t2;

	@Before
	public void setUp() throws Exception {
		t1 = new TimeValueImpl(2007, (byte) 5, (byte) 12, (byte) 10, (byte) 45,
				(byte) 0, TimeValue.PREC_SECOND, 0, 1, 60,
				TimeValue.CM_GREGORIAN_PRO);
		t2 = new TimeValueImpl(2007, (byte) 5, (byte) 12, (byte) 10, (byte) 45,
				(byte) 0, TimeValue.PREC_SECOND, 0, 1, 60,
				TimeValue.CM_GREGORIAN_PRO);
	}

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
		assertThat(t1, not(equalTo(tdYear)));
		assertThat(t1, not(equalTo(tdMonth)));
		assertThat(t1, not(equalTo(tdDay)));
		assertThat(t1, not(equalTo(tdHour)));
		assertThat(t1, not(equalTo(tdMinute)));
		assertThat(t1, not(equalTo(tdSecond)));
		assertThat(t1, not(equalTo(tdTimezone)));
		assertThat(t1, not(equalTo(tdBefore)));
		assertThat(t1, not(equalTo(tdAfter)));
		assertThat(t1, not(equalTo(tdPrecision)));
		assertThat(t1, not(equalTo(tdCalendar)));
		assertThat(t1, not(equalTo(null)));
		assertFalse(t1.equals(this));
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

}
