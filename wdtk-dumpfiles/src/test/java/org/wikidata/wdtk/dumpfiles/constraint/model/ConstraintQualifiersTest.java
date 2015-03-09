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
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;

/**
 * Test class for {@link ConstraintQualifiers}
 *
 * @author Julian Mendez
 *
 */
public class ConstraintQualifiersTest {

	public static final String TEMPLATE_STR_NO_PROP = "{{Constraint:Qualifiers|list=}}";
	public static final String TEMPLATE_STR_ONE_PROP = "{{Constraint:Qualifiers|list={{P|407}}}}";
	public static final String TEMPLATE_STR_TWO_PROP = "{{Constraint:Qualifiers|list={{P|580}}, {{P|582}}}}";

	public ConstraintQualifiersTest() {
	}

	public static List<PropertyIdValue> getListNoProp() {
		return new ArrayList<PropertyIdValue>();
	}

	public static List<PropertyIdValue> getListOneProp() {
		List<PropertyIdValue> ret = new ArrayList<PropertyIdValue>();
		ret.add(ConstraintTestHelper.getPropertyIdValue("P407"));
		return ret;
	}

	public static List<PropertyIdValue> getListTwoProp() {
		List<PropertyIdValue> ret = new ArrayList<PropertyIdValue>();
		ret.add(ConstraintTestHelper.getPropertyIdValue("P580"));
		ret.add(ConstraintTestHelper.getPropertyIdValue("P582"));
		return ret;
	}

	@Test
	public void testParametersNoProp() {
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue("P40");

		ConstraintQualifiers constraint = new ConstraintQualifiers(
				constrainedProperty, getListNoProp());
		Assert.assertEquals(constrainedProperty,
				constraint.getConstrainedProperty());
		Assert.assertEquals(getListNoProp(), constraint.getList());
	}

	@Test
	public void testParametersOneProp() {
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue("P10");

		ConstraintQualifiers constraint = new ConstraintQualifiers(
				constrainedProperty, getListOneProp());
		Assert.assertEquals(constrainedProperty,
				constraint.getConstrainedProperty());
		Assert.assertEquals(getListOneProp(), constraint.getList());
	}

	@Test
	public void testParametersTwoProp() {
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue("P6");

		ConstraintQualifiers constraint = new ConstraintQualifiers(
				constrainedProperty, getListTwoProp());
		Assert.assertEquals(constrainedProperty,
				constraint.getConstrainedProperty());
		Assert.assertEquals(getListTwoProp(), constraint.getList());
	}

	@Test
	public void testToStringAndVisitNoProp() {
		String propertyName = "P40";
		String templateStr = TEMPLATE_STR_NO_PROP;
		String string = propertyName + " " + templateStr;
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		ConstraintQualifiers constraint = new ConstraintQualifiers(
				constrainedProperty, getListNoProp());
		Assert.assertEquals(templateStr, constraint.getTemplate());
		Assert.assertEquals(string, constraint.toString());

		ConstraintTestHelper.testVisit(constraint);
	}

	@Test
	public void testToStringAndVisitOneProp() {
		String propertyName = "P10";
		String templateStr = TEMPLATE_STR_ONE_PROP;
		String string = propertyName + " " + templateStr;
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		ConstraintQualifiers constraint = new ConstraintQualifiers(
				constrainedProperty, getListOneProp());
		Assert.assertEquals(templateStr, constraint.getTemplate());
		Assert.assertEquals(string, constraint.toString());

		ConstraintTestHelper.testVisit(constraint);
	}

	@Test
	public void testToStringAndVisitTwoProp() {
		String propertyName = "P6";
		String templateStr = TEMPLATE_STR_TWO_PROP;
		String string = propertyName + " " + templateStr;
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		ConstraintQualifiers constraint = new ConstraintQualifiers(
				constrainedProperty, getListTwoProp());
		Assert.assertEquals(templateStr, constraint.getTemplate());
		Assert.assertEquals(string, constraint.toString());

		ConstraintTestHelper.testVisit(constraint);
	}

	@Test
	public void testEquals0() {
		PropertyIdValue constrainedProperty0 = ConstraintTestHelper
				.getPropertyIdValue("P40");
		PropertyIdValue constrainedProperty1 = ConstraintTestHelper
				.getPropertyIdValue("P53");

		ConstraintTestHelper
				.testEquals(new ConstraintQualifiers(constrainedProperty0,
						getListNoProp()), new ConstraintQualifiers(
						constrainedProperty0, getListNoProp()),
						new ConstraintQualifiers(constrainedProperty1,
								getListNoProp()));
	}

	@Test
	public void testEquals1() {
		PropertyIdValue constrainedProperty0 = ConstraintTestHelper
				.getPropertyIdValue("P10");
		PropertyIdValue constrainedProperty1 = ConstraintTestHelper
				.getPropertyIdValue("P856");

		ConstraintTestHelper
				.testEquals(new ConstraintQualifiers(constrainedProperty0,
						getListOneProp()), new ConstraintQualifiers(
						constrainedProperty0, getListOneProp()),
						new ConstraintQualifiers(constrainedProperty1,
								getListOneProp()));
	}

	@Test
	public void testEquals2() {
		PropertyIdValue constrainedProperty1 = ConstraintTestHelper
				.getPropertyIdValue("P6");
		PropertyIdValue constrainedProperty0 = ConstraintTestHelper
				.getPropertyIdValue("P17");

		ConstraintTestHelper
				.testEquals(new ConstraintQualifiers(constrainedProperty0,
						getListTwoProp()), new ConstraintQualifiers(
						constrainedProperty0, getListTwoProp()),
						new ConstraintQualifiers(constrainedProperty1,
								getListTwoProp()));
	}

}
