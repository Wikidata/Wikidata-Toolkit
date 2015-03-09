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
 * Test class for {@link ConstraintFormat}
 *
 * @author Julian Mendez
 *
 */
public class ConstraintFormatTest {

	public static final String TEMPLATE_STR = "{{Constraint:Format|pattern=<nowiki>[a-z]{2}</nowiki>}}";

	public ConstraintFormatTest() {
	}

	@Test
	public void testParameters() {
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue("P218");
		String pattern = "[a-z]{2}";

		ConstraintFormat constraint = new ConstraintFormat(constrainedProperty,
				pattern);
		Assert.assertEquals(constrainedProperty,
				constraint.getConstrainedProperty());
		Assert.assertEquals(pattern, constraint.getPattern());
	}

	@Test
	public void testToStringAndVisit() {
		String propertyName = "P218";
		String pattern = "[a-z]{2}";
		String string = propertyName + " " + TEMPLATE_STR;
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		ConstraintFormat constraint = new ConstraintFormat(constrainedProperty,
				pattern);
		Assert.assertEquals(TEMPLATE_STR, constraint.getTemplate());
		Assert.assertEquals(string, constraint.toString());

		ConstraintTestHelper.testVisit(constraint);
	}

	@Test
	public void testEquals() {
		PropertyIdValue constrainedProperty0 = ConstraintTestHelper
				.getPropertyIdValue("P218");
		String pattern0 = "[a-z]{2}";
		PropertyIdValue constrainedProperty1 = ConstraintTestHelper
				.getPropertyIdValue("P219");
		String pattern1 = "[a-z]{3}";

		ConstraintTestHelper.testEquals(new ConstraintFormat(
				constrainedProperty0, pattern0), new ConstraintFormat(
				constrainedProperty0, pattern0), new ConstraintFormat(
				constrainedProperty1, pattern1));
	}

}
