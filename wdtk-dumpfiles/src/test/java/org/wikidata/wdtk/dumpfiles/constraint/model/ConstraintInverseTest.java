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
 * Test class for {@link ConstraintInverse}
 * 
 * @author Julian Mendez
 * 
 */
public class ConstraintInverseTest {

	@Test
	public void testParameters() {
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue("P155");
		PropertyIdValue property = ConstraintTestHelper
				.getPropertyIdValue("P156");
		ConstraintInverse constraint = new ConstraintInverse(
				constrainedProperty, property);
		Assert.assertEquals(constrainedProperty,
				constraint.getConstrainedProperty());
		Assert.assertEquals(property, constraint.getProperty());
	}

	@Test
	public void testToStringAndVisit() {
		String propertyName = "P155";
		String template = "{{Constraint:Inverse|property=P156}}";
		String string = propertyName + " " + template;
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		PropertyIdValue property = ConstraintTestHelper
				.getPropertyIdValue("P156");
		ConstraintInverse constraint = new ConstraintInverse(
				constrainedProperty, property);
		Assert.assertEquals(template, constraint.getTemplate());
		Assert.assertEquals(string, constraint.toString());

		ConstraintTestHelper.testVisit(constraint);
	}

	@Test
	public void testEquals() {
		PropertyIdValue constrainedProperty0 = ConstraintTestHelper
				.getPropertyIdValue("P155");
		PropertyIdValue property0 = ConstraintTestHelper
				.getPropertyIdValue("P156");
		PropertyIdValue constrainedProperty1 = ConstraintTestHelper
				.getPropertyIdValue("P184");
		PropertyIdValue property1 = ConstraintTestHelper
				.getPropertyIdValue("P185");

		ConstraintTestHelper.testEquals(new ConstraintInverse(
				constrainedProperty0, property0), new ConstraintInverse(
				constrainedProperty0, property0), new ConstraintInverse(
				constrainedProperty1, property1));
	}

}
