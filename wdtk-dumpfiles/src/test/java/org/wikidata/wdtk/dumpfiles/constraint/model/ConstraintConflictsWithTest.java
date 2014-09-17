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
 * Test class for {@link ConstraintConflictsWith}
 *
 * @author Julian Mendez
 *
 */
public class ConstraintConflictsWithTest {

	public static final String TEMPLATE_STR_ONE_PROP_NO_ITEM = "{{Constraint:Conflicts with|list={{P|225}}}}";
	public static final String TEMPLATE_STR_ONE_PROP_ONE_ITEM = "{{Constraint:Conflicts with|list={{P|31}}: {{Q|5}}}}";
	public static final String TEMPLATE_STR_MANY_PROP_MANY_ITEM = "{{Constraint:Conflicts with|list={{P|527}}; {{P|31}}: {{Q|14756018}}, {{Q|14073567}}, {{Q|4167410}}; {{P|625}}}}";

	public static List<PropertyValues> getListOnePropNoItem() {
		List<PropertyValues> ret = new ArrayList<PropertyValues>();
		ret.add(new PropertyValues(ConstraintTestHelper
				.getPropertyIdValue("P225")));
		return ret;
	}

	public static List<PropertyValues> getListOnePropOneItem() {
		List<PropertyValues> ret = new ArrayList<PropertyValues>();
		List<ItemIdValue> list = new ArrayList<ItemIdValue>();
		list.add(ConstraintTestHelper.getItemIdValue("Q5"));
		ret.add(new PropertyValues(ConstraintTestHelper
				.getPropertyIdValue("P31"), list));
		return ret;
	}

	static PropertyValues getAndTestPropertyValues() {
		List<ItemIdValue> list = new ArrayList<ItemIdValue>();
		list.add(ConstraintTestHelper.getItemIdValue("Q14756018"));
		list.add(ConstraintTestHelper.getItemIdValue("Q14073567"));
		list.add(ConstraintTestHelper.getItemIdValue("Q4167410"));
		PropertyIdValue property = ConstraintTestHelper
				.getPropertyIdValue("P31");
		PropertyValues ret = new PropertyValues(property, list);
		Assert.assertEquals(property, ret.getProperty());
		Assert.assertEquals(list, ret.getItems());
		return ret;
	}

	public static List<PropertyValues> getListManyPropManyItem() {
		List<PropertyValues> ret = new ArrayList<PropertyValues>();
		ret.add(new PropertyValues(ConstraintTestHelper
				.getPropertyIdValue("P527")));
		ret.add(getAndTestPropertyValues());
		ret.add(new PropertyValues(ConstraintTestHelper
				.getPropertyIdValue("P625")));
		return ret;
	}

	@Test
	public void testPropertyValues() {
		PropertyValues ret = getAndTestPropertyValues();
		Assert.assertFalse(ret.hasAllValues());
		Assert.assertEquals(ret, ret);
		Assert.assertEquals(ret, new PropertyValues(ret));
		Assert.assertNotEquals(ret, new PropertyValues(ret.getProperty()));
		Assert.assertNotEquals(ret, null);
		Assert.assertNotEquals(ret, new Object());
	}

	@Test
	public void testParametersOnePropNoItem() {
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue("P494");

		ConstraintConflictsWith constraint = new ConstraintConflictsWith(
				constrainedProperty, getListOnePropNoItem());
		Assert.assertEquals(constrainedProperty,
				constraint.getConstrainedProperty());
		Assert.assertEquals(getListOnePropNoItem(), constraint.getList());
	}

	@Test
	public void testParametersOnePropOneItem() {
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue("P969");

		ConstraintConflictsWith constraint = new ConstraintConflictsWith(
				constrainedProperty, getListOnePropOneItem());
		Assert.assertEquals(constrainedProperty,
				constraint.getConstrainedProperty());
		Assert.assertEquals(getListOnePropOneItem(), constraint.getList());
	}

	@Test
	public void testParametersManyPropManyItem() {
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue("P569");

		ConstraintConflictsWith constraint = new ConstraintConflictsWith(
				constrainedProperty, getListManyPropManyItem());
		Assert.assertEquals(constrainedProperty,
				constraint.getConstrainedProperty());
		Assert.assertEquals(getListManyPropManyItem(), constraint.getList());
	}

	@Test
	public void testToStringAndVisitOnePropNoItem() {
		String propertyName = "P494";
		String templateStr = TEMPLATE_STR_ONE_PROP_NO_ITEM;
		String string = propertyName + " " + templateStr;
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		ConstraintConflictsWith constraint = new ConstraintConflictsWith(
				constrainedProperty, getListOnePropNoItem());
		Assert.assertEquals(templateStr, constraint.getTemplate());
		Assert.assertEquals(string, constraint.toString());

		ConstraintTestHelper.testVisit(constraint);
	}

	@Test
	public void testToStringAndVisitOnePropOneItem() {
		String propertyName = "P969";
		String templateStr = TEMPLATE_STR_ONE_PROP_ONE_ITEM;
		String string = propertyName + " " + templateStr;
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		ConstraintConflictsWith constraint = new ConstraintConflictsWith(
				constrainedProperty, getListOnePropOneItem());
		Assert.assertEquals(templateStr, constraint.getTemplate());
		Assert.assertEquals(string, constraint.toString());

		ConstraintTestHelper.testVisit(constraint);
	}

	@Test
	public void testToStringAndVisitManyPropManyItem() {
		String propertyName = "P569";
		String templateStr = TEMPLATE_STR_MANY_PROP_MANY_ITEM;
		String string = propertyName + " " + templateStr;
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		ConstraintConflictsWith constraint = new ConstraintConflictsWith(
				constrainedProperty, getListManyPropManyItem());
		Assert.assertEquals(templateStr, constraint.getTemplate());
		Assert.assertEquals(string, constraint.toString());

		ConstraintTestHelper.testVisit(constraint);
	}

	@Test
	public void testEquals0() {
		PropertyIdValue constrainedProperty0 = ConstraintTestHelper
				.getPropertyIdValue("P494");
		PropertyIdValue constrainedProperty1 = ConstraintTestHelper
				.getPropertyIdValue("P969");

		ConstraintTestHelper.testEquals(new ConstraintConflictsWith(
				constrainedProperty0, getListOnePropNoItem()),
				new ConstraintConflictsWith(constrainedProperty0,
						getListOnePropNoItem()), new ConstraintConflictsWith(
								constrainedProperty1, getListOnePropOneItem()));
	}

	@Test
	public void testEquals1() {
		PropertyIdValue constrainedProperty1 = ConstraintTestHelper
				.getPropertyIdValue("P569");
		PropertyIdValue constrainedProperty0 = ConstraintTestHelper
				.getPropertyIdValue("P494");

		ConstraintTestHelper.testEquals(new ConstraintConflictsWith(
				constrainedProperty0, getListManyPropManyItem()),
				new ConstraintConflictsWith(constrainedProperty0,
						getListManyPropManyItem()),
						new ConstraintConflictsWith(constrainedProperty1,
								getListOnePropNoItem()));
	}

}
