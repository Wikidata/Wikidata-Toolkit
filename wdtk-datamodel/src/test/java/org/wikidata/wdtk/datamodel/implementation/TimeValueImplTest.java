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
		t1 = new TimeValueImpl(2012, (byte) 5, (byte) 12, TimeValue.PREC_DAY,
				TimeValue.CM_GREGORIAN_PRO);
		t2 = new TimeValueImpl(2012, (byte) 5, (byte) 12, TimeValue.PREC_DAY,
				TimeValue.CM_GREGORIAN_PRO);
	}

	@Test
	public void storedValuesCorrect() {
		assertEquals(t1.getYear(), 2012);
		assertEquals(t1.getMonth(), 5);
		assertEquals(t1.getDay(), 12);
		assertEquals(t1.getPrecision(), TimeValue.PREC_DAY);
		assertEquals(t1.getPreferredCalendarModel(), TimeValue.CM_GREGORIAN_PRO);
	}

	@Test
	public void timeValueEqualityBasedOnContent() {
		TimeValue t3 = new TimeValueImpl(2013, (byte) 5, (byte) 12,
				TimeValue.PREC_DAY, TimeValue.CM_GREGORIAN_PRO);
		TimeValue t4 = new TimeValueImpl(2012, (byte) 6, (byte) 12,
				TimeValue.PREC_DAY, TimeValue.CM_GREGORIAN_PRO);
		TimeValue t5 = new TimeValueImpl(2012, (byte) 5, (byte) 13,
				TimeValue.PREC_DAY, TimeValue.CM_GREGORIAN_PRO);
		TimeValue t6 = new TimeValueImpl(2012, (byte) 5, (byte) 12,
				TimeValue.PREC_YEAR, TimeValue.CM_GREGORIAN_PRO);
		TimeValue t7 = new TimeValueImpl(2012, (byte) 5, (byte) 12,
				TimeValue.PREC_DAY, TimeValue.CM_JULIAN_PRO);

		assertEquals(t1, t1);
		assertEquals(t1, t2);
		assertThat(t1, not(equalTo(t3)));
		assertThat(t1, not(equalTo(t4)));
		assertThat(t1, not(equalTo(t5)));
		assertThat(t1, not(equalTo(t6)));
		assertThat(t1, not(equalTo(t7)));
		assertThat(t1, not(equalTo(null)));
		assertFalse(t1.equals(this));
	}

	@Test
	public void timeValueHashBasedOnContent() {
		assertEquals(t1.hashCode(), t2.hashCode());
	}

	@Test(expected = NullPointerException.class)
	public void timeValueCalendarModelNotNull() {
		new TimeValueImpl(2012, (byte) 5, (byte) 12, TimeValue.PREC_DAY, null);
	}

}
