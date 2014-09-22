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
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;

/**
 * Test class for {@link ConstraintTargetRequiredClaim}
 *
 * @author Julian Mendez
 *
 */
public class ConstraintTargetRequiredClaimTest {

	public static final String TEMPLATE_STR = "{{Constraint:Target required claim|property=P21|item=Q6581072}}";

	@Test
	public void testParameters() {
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue("P9");
		PropertyIdValue property = ConstraintTestHelper
				.getPropertyIdValue("P21");
		ItemIdValue item = ConstraintTestHelper.getItemIdValue("Q6581072");
		ConstraintTargetRequiredClaim constraint = new ConstraintTargetRequiredClaim(
				constrainedProperty, property, item);
		Assert.assertEquals(constrainedProperty,
				constraint.getConstrainedProperty());
		Assert.assertEquals(property, constraint.getProperty());
		Assert.assertEquals(item, constraint.getItem());

		Assert.assertTrue(constraint.equals(null, null));
		Assert.assertFalse(constraint.equals(null, new Object()));
	}

	@Test
	public void testToStringAndVisit() {
		String propertyName = "P9";
		String string = propertyName + " " + TEMPLATE_STR;
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		PropertyIdValue property = ConstraintTestHelper
				.getPropertyIdValue("P21");
		ItemIdValue item = ConstraintTestHelper.getItemIdValue("Q6581072");
		ConstraintTargetRequiredClaim constraint = new ConstraintTargetRequiredClaim(
				constrainedProperty, property, item);
		Assert.assertEquals(TEMPLATE_STR, constraint.getTemplate());
		Assert.assertEquals(string, constraint.toString());

		ConstraintTestHelper.testVisit(constraint);
	}

	@Test
	public void testEquals() {
		PropertyIdValue constrainedProperty0 = ConstraintTestHelper
				.getPropertyIdValue("P9");
		PropertyIdValue constrainedProperty1 = ConstraintTestHelper
				.getPropertyIdValue("P7");
		PropertyIdValue property = ConstraintTestHelper
				.getPropertyIdValue("P21");
		ItemIdValue item0 = ConstraintTestHelper.getItemIdValue("Q6581072");
		ItemIdValue item1 = ConstraintTestHelper.getItemIdValue("Q6581097");

		ConstraintTestHelper.testEquals(new ConstraintTargetRequiredClaim(
				constrainedProperty0, property, item0),
				new ConstraintTargetRequiredClaim(constrainedProperty0,
						property, item0), new ConstraintTargetRequiredClaim(
								constrainedProperty1, property, item1));

	}

}
