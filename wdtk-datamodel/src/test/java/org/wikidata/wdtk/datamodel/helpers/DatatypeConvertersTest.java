package org.wikidata.wdtk.datamodel.helpers;

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

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;

public class DatatypeConvertersTest {

	@Test
	public void testFormatTimeISO8601() {
		TimeValue time = Datamodel.makeTimeValue(306, (byte) 11, (byte) 3,
				(byte) 13, (byte) 7, (byte) 6, TimeValue.PREC_SECOND, 0, 0,
				0, TimeValue.CM_GREGORIAN_PRO);
		assertEquals(DataFormatter.formatTimeISO8601(time),
				"+00000000306-11-03T13:07:06Z");

	}

	@Test
	public void testBigDecimals() {
		BigDecimal test = new BigDecimal(3638);
		assertEquals(DataFormatter.formatBigDecimal(test), "+3638");
	}

}
