package org.wikidata.wdtk.dumpfiles.constraint.model;

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

/**
 * Test class for {@link ConstraintRange}.
 *
 * @author Julian Mendez
 *
 */
public class ConstraintRangeTest {

	public static final String TEMPLATE_STR_DATE = "{{Constraint:Range|min=1957-10-04|max=now}}";
	public static final String TEMPLATE_STR_QUANTITY = "{{Constraint:Range|min=1|max=118}}";

	public ConstraintRangeTest() {
	}

	@Test
	public void testParametersDate() {
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue("P620");
		ConstraintRange constraint = new ConstraintRange(constrainedProperty,
				"1957-10-04", "now", true);
		Assert.assertEquals(constrainedProperty,
				constraint.getConstrainedProperty());
		Assert.assertEquals("1957-10-04", constraint.getMin());
		Assert.assertEquals("now", constraint.getMax());
		Assert.assertTrue(constraint.isTime());
		Assert.assertFalse(constraint.isQuantity());
	}

	@Test
	public void testParametersQuantity() {
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue("P1086");
		ConstraintRange constraint = new ConstraintRange(constrainedProperty,
				"1", "118", false);
		Assert.assertEquals(constrainedProperty,
				constraint.getConstrainedProperty());
		Assert.assertEquals("1", constraint.getMin());
		Assert.assertEquals("118", constraint.getMax());
		Assert.assertTrue(constraint.isQuantity());
		Assert.assertFalse(constraint.isTime());
	}

	@Test
	public void testToStringAndVisitDate() {
		String propertyName = "P620";
		String string = propertyName + " " + TEMPLATE_STR_DATE;
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		ConstraintRange constraint = new ConstraintRange(constrainedProperty,
				"1957-10-04", "now", true);
		Assert.assertEquals(TEMPLATE_STR_DATE, constraint.getTemplate());
		Assert.assertEquals(string, constraint.toString());

		ConstraintTestHelper.testVisit(constraint);
	}

	@Test
	public void testToStringAndVisitQuantity() {
		String propertyName = "P1086";
		String string = propertyName + " " + TEMPLATE_STR_QUANTITY;
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		ConstraintRange constraint = new ConstraintRange(constrainedProperty,
				"1", "118", false);
		Assert.assertEquals(TEMPLATE_STR_QUANTITY, constraint.getTemplate());
		Assert.assertEquals(string, constraint.toString());

		ConstraintTestHelper.testVisit(constraint);
	}

	@Test
	public void testEqualsDate() {
		PropertyIdValue constrainedProperty0 = ConstraintTestHelper
				.getPropertyIdValue("P620");
		PropertyIdValue constrainedProperty1 = ConstraintTestHelper
				.getPropertyIdValue("P619");

		ConstraintTestHelper.testEquals(new ConstraintRange(
				constrainedProperty0, "1957-10-04", "now", true),
				new ConstraintRange(constrainedProperty0, "1957-10-04", "now",
						true), new ConstraintRange(constrainedProperty1,
						"1957-10-04", "now", true));
	}

	@Test
	public void testEqualsQuantity() {
		PropertyIdValue constrainedProperty0 = ConstraintTestHelper
				.getPropertyIdValue("P1086");
		PropertyIdValue constrainedProperty1 = ConstraintTestHelper
				.getPropertyIdValue("P1081");

		ConstraintTestHelper.testEquals(new ConstraintRange(
				constrainedProperty0, "1", "118", false), new ConstraintRange(
				constrainedProperty0, "1", "118", false), new ConstraintRange(
				constrainedProperty1, "0", "1", false));
	}

	@Test
	public void testDateAndNow() {
		DateAndNow date0 = new DateAndNow();
		Assert.assertTrue(date0.isNow());
		Assert.assertEquals("now", date0.toString());
		DateAndNow date1 = new DateAndNow(new Date(0)); // 1970-01-01
		Assert.assertFalse(date1.isNow());

		Assert.assertEquals(new Date(0), date1.getDate());
		Assert.assertEquals("1970-01-01", date1.toString());
		Assert.assertEquals(date0, date0);
		Assert.assertEquals(date0.hashCode(), (new DateAndNow()).hashCode());
		Assert.assertEquals(date1.hashCode(),
				(new DateAndNow(new Date(0))).hashCode());
		Assert.assertEquals(date1, new DateAndNow(new Date(0)));

		Assert.assertNotEquals(date0, new DateAndNow(new Date(0)));
		Assert.assertNotEquals(date0, null);
		Assert.assertNotEquals(date0, date1);
		Assert.assertNotEquals(date0, new Object());
	}

}
