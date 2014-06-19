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
 * Test class for {@link ConstraintType}
 * 
 * @author Julian Mendez
 * 
 */
public class ConstraintTypeTest {

	@Test
	public void testParameters() {
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue("P30");
		ItemIdValue item = ConstraintTestHelper.getItemIdValue("Q2221906");
		ConstraintType constraint = new ConstraintType(constrainedProperty,
				item, RelationType.INSTANCE);
		Assert.assertEquals(constrainedProperty,
				constraint.getConstrainedProperty());
		Assert.assertEquals(item, constraint.getClassId());
		Assert.assertEquals(RelationType.INSTANCE, constraint.getRelation());
		Assert.assertNotEquals(RelationType.SUBCLASS, constraint.getRelation());
	}

	@Test
	public void testToStringAndVisit() {
		String propertyName = "P30";
		String template = "{{Constraint:Type|class=Q2221906|relation=instance}}";
		String string = propertyName + " " + template;
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		ItemIdValue item = ConstraintTestHelper.getItemIdValue("Q2221906");
		ConstraintType constraint = new ConstraintType(constrainedProperty,
				item, RelationType.INSTANCE);
		Assert.assertEquals(template, constraint.getTemplate());
		Assert.assertEquals(string, constraint.toString());

		ConstraintTestHelper.testVisit(constraint);
	}

	@Test
	public void testEquals() {
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue("P30");
		ItemIdValue item = ConstraintTestHelper.getItemIdValue("Q2221906");

		ConstraintTestHelper.testEquals(new ConstraintType(constrainedProperty,
				item, RelationType.INSTANCE), new ConstraintType(
				constrainedProperty, item, RelationType.INSTANCE),
				new ConstraintType(constrainedProperty, item,
						RelationType.SUBCLASS));
	}

}
