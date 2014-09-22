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

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;

/**
 * Test class for {@link ConstraintOneOf}
 *
 * @author Julian Mendez
 *
 */
public class ConstraintOneOfTest {

	public static final String TEMPLATE_STR_ITEM_VAL = "{{Constraint:One of|values={{Q|27914}}, {{Q|30903}}, {{Q|31687}}, {{Q|37137}}, {{Q|186506}}, {{Q|27911}}}}";
	public static final String TEMPLATE_STR_QUANTITY_VAL = "{{Constraint:One of|values=1, 2, 3, 4, 5, 6, 7, 8, 9, 10}}";

	public static List<ItemIdValue> getItemValues() {
		List<ItemIdValue> ret = new ArrayList<ItemIdValue>();
		ret.add(ConstraintTestHelper.getItemIdValue("Q27914"));
		ret.add(ConstraintTestHelper.getItemIdValue("Q30903"));
		ret.add(ConstraintTestHelper.getItemIdValue("Q31687"));
		ret.add(ConstraintTestHelper.getItemIdValue("Q37137"));
		ret.add(ConstraintTestHelper.getItemIdValue("Q186506"));
		ret.add(ConstraintTestHelper.getItemIdValue("Q27911"));
		return ret;
	}

	public static List<Integer> getQuantityValues() {
		List<Integer> ret = new ArrayList<Integer>();
		for (int i = 1; i <= 10; i++) {
			ret.add(i);
		}
		return ret;
	}

	@Test
	public void testParametersItemVal() {
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue("P412");
		ConstraintOneOf constraint = new ConstraintOneOf(constrainedProperty,
				getItemValues());
		Assert.assertEquals(constrainedProperty,
				constraint.getConstrainedProperty());
		Assert.assertEquals(getItemValues(), constraint.getItemValues());
		Assert.assertTrue(constraint.hasItems());
	}

	@Test
	public void testParametersQuantityVal() {
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue("P1088");
		ConstraintOneOf constraint = new ConstraintOneOf(getQuantityValues(),
				constrainedProperty);
		Assert.assertEquals(constrainedProperty,
				constraint.getConstrainedProperty());
		Assert.assertEquals(getQuantityValues(), constraint.getQuantityValues());
		Assert.assertFalse(constraint.hasItems());
	}

	@Test
	public void testToStringAndVisitItemVal() {
		String propertyName = "P412";
		String templateStr = TEMPLATE_STR_ITEM_VAL;
		String string = propertyName + " " + templateStr;
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		ConstraintOneOf constraint = new ConstraintOneOf(constrainedProperty,
				getItemValues());
		Assert.assertEquals(templateStr, constraint.getTemplate());
		Assert.assertEquals(string, constraint.toString());

		ConstraintTestHelper.testVisit(constraint);
	}

	@Test
	public void testToStringAndVisitQuantityVal() {
		String propertyName = "P1088";
		String templateStr = TEMPLATE_STR_QUANTITY_VAL;
		String string = propertyName + " " + templateStr;
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		ConstraintOneOf constraint = new ConstraintOneOf(getQuantityValues(),
				constrainedProperty);
		Assert.assertEquals(templateStr, constraint.getTemplate());
		Assert.assertEquals(string, constraint.toString());

		ConstraintTestHelper.testVisit(constraint);
	}

	@Test
	public void testEqualsItemVal() {
		PropertyIdValue constrainedProperty0 = ConstraintTestHelper
				.getPropertyIdValue("P412");
		PropertyIdValue constrainedProperty1 = ConstraintTestHelper
				.getPropertyIdValue("P413");

		ConstraintTestHelper.testEquals(new ConstraintOneOf(
				constrainedProperty0, getItemValues()), new ConstraintOneOf(
						constrainedProperty0, getItemValues()), new ConstraintOneOf(
								constrainedProperty1, getItemValues()));
	}

	@Test
	public void testEqualsQuantityVal() {
		PropertyIdValue constrainedProperty0 = ConstraintTestHelper
				.getPropertyIdValue("P1088");
		PropertyIdValue constrainedProperty1 = ConstraintTestHelper
				.getPropertyIdValue("P1123");

		ConstraintTestHelper.testEquals(new ConstraintOneOf(
				getQuantityValues(), constrainedProperty0),
				new ConstraintOneOf(getQuantityValues(), constrainedProperty0),
				new ConstraintOneOf(getQuantityValues(), constrainedProperty1));
	}

}
