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
<<<<<<< HEAD
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
=======
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
>>>>>>> 082a2c39ba534b6046431b9a01782b93e5eb9e7a
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
 * Test class for {@link ConstraintQualifier}.
 *
 * @author Julian Mendez
 *
 */
public class ConstraintQualifierTest {

	public static final String TEMPLATE_STR = "{{Constraint:Qualifier}}";

	public ConstraintQualifierTest() {
	}

	@Test
	public void testParameters() {
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue("P1011");
		ConstraintQualifier constraint = new ConstraintQualifier(
				constrainedProperty);
		Assert.assertEquals(constrainedProperty,
				constraint.getConstrainedProperty());
	}

	@Test
	public void testToStringAndVisit() {
		String propertyName = "P1011";
		String string = propertyName + " " + TEMPLATE_STR;
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		ConstraintQualifier constraint = new ConstraintQualifier(
				constrainedProperty);
		Assert.assertEquals(TEMPLATE_STR, constraint.getTemplate());
		Assert.assertEquals(string, constraint.toString());

		ConstraintTestHelper.testVisit(constraint);
	}

	@Test
	public void testEquals() {
		PropertyIdValue constrainedProperty0 = ConstraintTestHelper
				.getPropertyIdValue("P1011");
		PropertyIdValue constrainedProperty1 = ConstraintTestHelper
				.getPropertyIdValue("P1012");

		ConstraintTestHelper.testEquals(new ConstraintQualifier(
				constrainedProperty0), new ConstraintQualifier(
				constrainedProperty0), new ConstraintQualifier(
				constrainedProperty1));
	}

}
