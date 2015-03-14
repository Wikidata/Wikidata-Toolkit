package org.wikidata.wdtk.dumpfiles.constraint.builder;

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
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintRange;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintTestHelper;
import org.wikidata.wdtk.dumpfiles.constraint.model.DateAndNow;
import org.wikidata.wdtk.dumpfiles.constraint.template.Template;
import org.wikidata.wdtk.dumpfiles.constraint.template.TemplateParser;

/**
 * Test class for {@link ConstraintRangeBuilder}.
 * 
 * @author Julian Mendez
 * 
 */
public class ConstraintRangeBuilderTest {

	public ConstraintRangeBuilderTest() {
	}

	@Test
	public void testParseDate() {
		ConstraintRangeBuilder builder = new ConstraintRangeBuilder();
		Date datePreNow0 = new Date();
		DateAndNow dateNow = builder.parseDate("now");
		Date datePreNow1 = new Date();

		Assert.assertTrue(datePreNow0.getTime() <= dateNow.getDate().getTime());

		// 'now' comes always after any other previously created current date
		Assert.assertTrue(datePreNow1.getTime() <= dateNow.getDate().getTime());

		DateAndNow date0 = builder.parseDate("1");
		DateAndNow date1 = builder.parseDate("100");
		DateAndNow date2 = builder.parseDate("2014");
		DateAndNow date3 = builder.parseDate("2014");
		DateAndNow date4 = builder.parseDate("2014-01");
		DateAndNow date5 = builder.parseDate("2014-01-22");
		DateAndNow date6 = builder.parseDate("2014-01-22 17:14:32");

		Assert.assertEquals(new Date(-62135769600000L), date0.getDate());

		Assert.assertEquals(new Date(-59011632000000L), date1.getDate());

		Assert.assertEquals(new Date(1388534400000L), date2.getDate());
		Assert.assertEquals(date2, date3);
		Assert.assertEquals(date2, date4);
		Assert.assertEquals(date4, date3);

		Assert.assertEquals(new Date(1390348800000L), date5.getDate());
		Assert.assertEquals(new Date(1390410872000L), date6.getDate());

		Assert.assertEquals(null, builder.parseDate("this is not a date"));

		Assert.assertEquals(null, builder.parseDate("-1")); // date not supported
	}

	@Test
	public void testParseDouble() {
		ConstraintRangeBuilder builder = new ConstraintRangeBuilder();
		Assert.assertEquals(new Double(0), builder.parseDouble("0"));
		Assert.assertEquals(new Double(-1.23), builder.parseDouble("-1.23"));
		Assert.assertEquals(new Double(1.23), builder.parseDouble("1.23"));
		Assert.assertNotEquals(new Double(-1.23), builder.parseDouble("1.23"));
		Assert.assertEquals(null, builder.parseDouble("this is not a number"));
	}

	@Test
	public void testBuilderDate() {
		String propertyName = "P620";
		String templateStr = "{{Constraint:Range|min=1957-10-04|max=now}}";
		Template template = (new TemplateParser()).parse(templateStr);

		ConstraintRangeBuilder builder = new ConstraintRangeBuilder();
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		ConstraintRange expectedConstraint = new ConstraintRange(
				constrainedProperty, "1957-10-04", "now", true);
		ConstraintRange constraint = builder.parse(constrainedProperty,
				template);

		Assert.assertEquals(expectedConstraint, constraint);
	}

	@Test
	public void testBuilderQuantity() {
		String propertyName = "P1086";
		String templateStr = "{{Constraint:Range|min=1|max=118}}";
		Template template = (new TemplateParser()).parse(templateStr);

		ConstraintRangeBuilder builder = new ConstraintRangeBuilder();
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		ConstraintRange expectedConstraint = new ConstraintRange(
				constrainedProperty, "1", "118", false);
		ConstraintRange constraint = builder.parse(constrainedProperty,
				template);

		Assert.assertEquals(expectedConstraint, constraint);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBuilderWrongPropertyType() {
		String propertyName = "P31"; // instance of
		String templateStr = "{{Constraint:Range|min=0|max=1}}";
		Template template = (new TemplateParser()).parse(templateStr);

		ConstraintRangeBuilder builder = new ConstraintRangeBuilder();
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		builder.parse(constrainedProperty, template);
	}

}
