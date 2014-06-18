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
 * Test class for {@link ConstraintItem}
 * 
 * @author Julian Mendez
 * 
 */
public class ConstraintItemTest {

	List<ItemIdValue> getItems() {
		List<ItemIdValue> ret = new ArrayList<ItemIdValue>();
		ret.add(ConstraintTestHelper.getItemIdValue("Q7432"));
		ret.add(ConstraintTestHelper.getItemIdValue("Q767728"));
		ret.add(ConstraintTestHelper.getItemIdValue("Q68947"));
		return ret;
	}

	@Test
	public void testParameters0() {
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue("P141");
		PropertyIdValue property = ConstraintTestHelper
				.getPropertyIdValue("P225");
		ConstraintItem constraint = new ConstraintItem(constrainedProperty,
				property, null, null, null, null, null);
		Assert.assertEquals(constrainedProperty,
				constraint.getConstrainedProperty());
		Assert.assertEquals(property, constraint.getProperty());
	}

	@Test
	public void testParameters1() {
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue("P141");
		PropertyIdValue property = ConstraintTestHelper
				.getPropertyIdValue("P105");
		ConstraintItem constraint = new ConstraintItem(constrainedProperty,
				property, null, null, null, getItems(), null);
		Assert.assertEquals(constrainedProperty,
				constraint.getConstrainedProperty());
		Assert.assertEquals(property, constraint.getProperty());
		Assert.assertEquals(getItems(), constraint.getItems());
	}

	@Test
	public void testToStringAndVisit0() {
		String propertyName = "P141";
		String template = "{{Constraint:Item|property=P225}}";
		String string = propertyName + " " + template;
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		PropertyIdValue property = ConstraintTestHelper
				.getPropertyIdValue("P225");
		ConstraintItem constraint = new ConstraintItem(constrainedProperty,
				property, null, null, null, null, null);
		Assert.assertEquals(template, constraint.getTemplate());
		Assert.assertEquals(string, constraint.toString());

		ConstraintTestHelper.testVisit(constraint);
	}

	@Test
	public void testToStringAndVisit1() {
		String propertyName = "P141";
		String template = "{{Constraint:Item|property=P105|items={{Q|7432}}, {{Q|767728}}, {{Q|68947}}}}";
		String string = propertyName + " " + template;
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		PropertyIdValue property = ConstraintTestHelper
				.getPropertyIdValue("P105");
		ConstraintItem constraint = new ConstraintItem(constrainedProperty,
				property, null, null, null, getItems(), null);
		Assert.assertEquals(template, constraint.getTemplate());
		Assert.assertEquals(string, constraint.toString());

		ConstraintTestHelper.testVisit(constraint);
	}

	@Test
	public void testEquals() {
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue("P141");
		PropertyIdValue property0 = ConstraintTestHelper
				.getPropertyIdValue("P225");
		PropertyIdValue property1 = ConstraintTestHelper
				.getPropertyIdValue("P105");
		ConstraintTestHelper.testEquals(new ConstraintItem(constrainedProperty,
				property0, null, null, null, null, null), new ConstraintItem(
				constrainedProperty, property0, null, null, null, null, null),
				new ConstraintItem(constrainedProperty, property1, null, null,
						null, null, null));
	}

}
