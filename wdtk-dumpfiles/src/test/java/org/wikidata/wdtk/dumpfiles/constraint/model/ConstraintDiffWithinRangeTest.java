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
 * Test class for {@link ConstraintDiffWithinRange}.
 *
 * @author Julian Mendez
 *
 */
public class ConstraintDiffWithinRangeTest {

	public static final String TEMPLATE_STR_TIME_0 = "{{Constraint:Diff within range|base_property=P569|min=0|max=150}}";
	public static final String TEMPLATE_STR_TIME_1 = "{{Constraint:Diff within range|base_property=P571|min=0|max=2000}}";

	public ConstraintDiffWithinRangeTest() {
	}

	@Test
	public void testParametersTime0() {
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue("P570");
		PropertyIdValue baseProperty = ConstraintTestHelper
				.getPropertyIdValue("P569");
		ConstraintDiffWithinRange constraint = new ConstraintDiffWithinRange(
				constrainedProperty, baseProperty, "0", "150", true);
		Assert.assertEquals(constrainedProperty,
				constraint.getConstrainedProperty());
		Assert.assertEquals(baseProperty, constraint.getBaseProperty());
		Assert.assertEquals("0", constraint.getMin());
		Assert.assertEquals("150", constraint.getMax());
		Assert.assertTrue(constraint.isTime());
		Assert.assertFalse(constraint.isQuantity());
	}

	@Test
	public void testParametersTime1() {
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue("P576");
		PropertyIdValue baseProperty = ConstraintTestHelper
				.getPropertyIdValue("P571");
		ConstraintDiffWithinRange constraint = new ConstraintDiffWithinRange(
				constrainedProperty, baseProperty, "0", "2000", true);
		Assert.assertEquals(constrainedProperty,
				constraint.getConstrainedProperty());
		Assert.assertEquals(baseProperty, constraint.getBaseProperty());
		Assert.assertEquals("0", constraint.getMin());
		Assert.assertEquals("2000", constraint.getMax());
		Assert.assertTrue(constraint.isTime());
		Assert.assertFalse(constraint.isQuantity());
	}

	@Test
	public void testToStringAndVisitDate() {
		String propertyName = "P570";
		String string = propertyName + " " + TEMPLATE_STR_TIME_0;
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		PropertyIdValue baseProperty = ConstraintTestHelper
				.getPropertyIdValue("P569");
		ConstraintDiffWithinRange constraint = new ConstraintDiffWithinRange(
				constrainedProperty, baseProperty, "0", "150", true);
		Assert.assertEquals(TEMPLATE_STR_TIME_0, constraint.getTemplate());
		Assert.assertEquals(string, constraint.toString());

		ConstraintTestHelper.testVisit(constraint);
	}

	@Test
	public void testToStringAndVisitQuantity() {
		String propertyName = "P576";
		String string = propertyName + " " + TEMPLATE_STR_TIME_1;
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		PropertyIdValue baseProperty = ConstraintTestHelper
				.getPropertyIdValue("P571");
		ConstraintDiffWithinRange constraint = new ConstraintDiffWithinRange(
				constrainedProperty, baseProperty, "0", "2000", false);
		Assert.assertEquals(TEMPLATE_STR_TIME_1, constraint.getTemplate());
		Assert.assertEquals(string, constraint.toString());

		ConstraintTestHelper.testVisit(constraint);
	}

	@Test
	public void testEqualsDate() {
		PropertyIdValue constrainedProperty0 = ConstraintTestHelper
				.getPropertyIdValue("P570");
		PropertyIdValue baseProperty0 = ConstraintTestHelper
				.getPropertyIdValue("P569");

		PropertyIdValue constrainedProperty1 = ConstraintTestHelper
				.getPropertyIdValue("P576");
		PropertyIdValue baseProperty1 = ConstraintTestHelper
				.getPropertyIdValue("P571");

		ConstraintTestHelper.testEquals(new ConstraintDiffWithinRange(
				constrainedProperty0, baseProperty0, "0", "150", true),
				new ConstraintDiffWithinRange(constrainedProperty0,
						baseProperty0, "0", "150", true),
				new ConstraintDiffWithinRange(constrainedProperty1,
						baseProperty1, "0", "2000", true));
	}

}
