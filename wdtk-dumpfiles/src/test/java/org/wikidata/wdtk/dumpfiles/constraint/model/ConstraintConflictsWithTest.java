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
 * Test class for {@link ConstraintConflictsWith}
 * 
 * @author Julian Mendez
 * 
 */
public class ConstraintConflictsWithTest {

	List<PropertyValues> getList() {
		List<PropertyValues> ret = new ArrayList<PropertyValues>();
		ret.add(new PropertyValues(ConstraintTestHelper
				.getPropertyIdValue("P225")));
		return ret;
	}

	@Test
	public void testParameters() {
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue("P494");

		ConstraintConflictsWith constraint = new ConstraintConflictsWith(
				constrainedProperty, getList());
		Assert.assertEquals(constrainedProperty,
				constraint.getConstrainedProperty());
		Assert.assertEquals(getList(), constraint.getList());
	}

	@Test
	public void testToStringAndVisit() {
		String propertyName = "P494";
		String template = "{{Constraint:Conflicts with|list={{P|225}}}}";
		String string = propertyName + " " + template;
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		ConstraintConflictsWith constraint = new ConstraintConflictsWith(
				constrainedProperty, getList());
		Assert.assertEquals(template, constraint.getTemplate());
		Assert.assertEquals(string, constraint.toString());

		ConstraintTestHelper.testVisit(constraint);
	}

	@Test
	public void testEquals() {
		PropertyIdValue constrainedProperty0 = ConstraintTestHelper
				.getPropertyIdValue("P494");
		PropertyIdValue constrainedProperty1 = ConstraintTestHelper
				.getPropertyIdValue("P969");

		ConstraintTestHelper.testEquals(new ConstraintConflictsWith(
				constrainedProperty0, getList()), new ConstraintConflictsWith(
				constrainedProperty0, getList()), new ConstraintConflictsWith(
				constrainedProperty1, getList()));
	}

}
