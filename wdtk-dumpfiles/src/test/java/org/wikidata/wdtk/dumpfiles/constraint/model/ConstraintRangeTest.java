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

import org.junit.Assert;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;

/**
 * Test class for {@link ConstraintRange}
 * 
 * @author Julian Mendez
 * 
 */
public class ConstraintRangeTest {

	@Test
	public void testParameters0() {
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
	public void testParameters1() {
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
	public void testToStringAndVisit0() {
		String propertyName = "P620";
		String template = "{{Constraint:Range|min=1957-10-04|max=now}}";
		String string = propertyName + " " + template;
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		ConstraintRange constraint = new ConstraintRange(constrainedProperty,
				"1957-10-04", "now", true);
		Assert.assertEquals(template, constraint.getTemplate());
		Assert.assertEquals(string, constraint.toString());

		ConstraintTestHelper.testVisit(constraint);
	}

	@Test
	public void testToStringAndVisit1() {
		String propertyName = "P1086";
		String template = "{{Constraint:Range|min=1|max=118}}";
		String string = propertyName + " " + template;
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		ConstraintRange constraint = new ConstraintRange(constrainedProperty,
				"1", "118", false);
		Assert.assertEquals(template, constraint.getTemplate());
		Assert.assertEquals(string, constraint.toString());

		ConstraintTestHelper.testVisit(constraint);
	}

	@Test
	public void testEquals0() {
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
	public void testEquals1() {
		PropertyIdValue constrainedProperty0 = ConstraintTestHelper
				.getPropertyIdValue("P1086");
		PropertyIdValue constrainedProperty1 = ConstraintTestHelper
				.getPropertyIdValue("P1081");

		ConstraintTestHelper.testEquals(new ConstraintRange(
				constrainedProperty0, "1", "118", false), new ConstraintRange(
				constrainedProperty0, "1", "118", false), new ConstraintRange(
				constrainedProperty1, "0", "1", false));
	}

}
