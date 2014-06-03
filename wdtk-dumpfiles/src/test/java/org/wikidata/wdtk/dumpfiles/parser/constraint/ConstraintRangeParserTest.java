package org.wikidata.wdtk.dumpfiles.parser.constraint;

/*
 * #%L
 * Wikidata Toolkit Dump File Handling
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

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.wikidata.wdtk.dumpfiles.constraint.DateAndNow;

/**
 * 
 * @author Julian Mendez
 * 
 */
public class ConstraintRangeParserTest {

	@Test
	public void testParseDate() {
		ConstraintRangeParser parser = new ConstraintRangeParser();
		Date datePreNow = new Date();
		DateAndNow dateNow = parser.parseDate("now");
		Date datePostNow = new Date();

		Assert.assertTrue(datePreNow.getTime() <= dateNow.getDate().getTime());
		Assert.assertTrue(dateNow.getDate().getTime() <= datePostNow.getTime());

		DateAndNow date0 = parser.parseDate("2014");
		DateAndNow date1 = parser.parseDate("2014");
		DateAndNow date2 = parser.parseDate("2014-01");
		DateAndNow date3 = parser.parseDate("2014-01-22");
		DateAndNow date4 = parser.parseDate("2014-01-22 17:14:32");

		Assert.assertEquals(new Date(1388534400000L), date0.getDate());
		Assert.assertEquals(date0, date1);
		Assert.assertEquals(date0, date2);
		Assert.assertEquals(date2, date1);

		Assert.assertEquals(new Date(1390348800000L), date3.getDate());
		Assert.assertEquals(new Date(1390410872000L), date4.getDate());
	}

}
