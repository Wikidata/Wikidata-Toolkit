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
import java.util.Collections;
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

	List<ItemIdValue> getExceptions() {
		List<ItemIdValue> ret = new ArrayList<ItemIdValue>();
		ret.add(ConstraintTestHelper.getItemIdValue("Q695"));
		ret.add(ConstraintTestHelper.getItemIdValue("Q702"));
		ret.add(ConstraintTestHelper.getItemIdValue("Q709"));
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

		Assert.assertTrue(constraint.equals(null, null));
		Assert.assertFalse(constraint.equals(null, new Object()));
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
		Assert.assertEquals(null, constraint.getItem());
		Assert.assertEquals(Collections.<ItemIdValue> emptyList(),
				constraint.getExceptions());
	}

	@Test
	public void testParameters2() {
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue("P1031");
		PropertyIdValue property = ConstraintTestHelper
				.getPropertyIdValue("P1001");
		PropertyIdValue property2 = ConstraintTestHelper
				.getPropertyIdValue("P953");
		ConstraintItem constraint = new ConstraintItem(constrainedProperty,
				property, null, property2, null, null, null);
		Assert.assertEquals(constrainedProperty,
				constraint.getConstrainedProperty());
		Assert.assertEquals(property, constraint.getProperty());
		Assert.assertEquals(property2, constraint.getProperty2());
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
	public void testToString1() {
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
	}

	@Test
	public void testToString2() {
		String propertyName = "P1031";
		String template = "{{Constraint:Item|property=P1001|property2=P953}}";
		String string = propertyName + " " + template;
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		PropertyIdValue property = ConstraintTestHelper
				.getPropertyIdValue("P1001");
		PropertyIdValue property2 = ConstraintTestHelper
				.getPropertyIdValue("P953");
		ConstraintItem constraint = new ConstraintItem(constrainedProperty,
				property, null, property2, null, null, null);
		Assert.assertEquals(template, constraint.getTemplate());
		Assert.assertEquals(string, constraint.toString());
	}

	@Test
	public void testToString3() {
		String propertyName = "P240";
		String template = "{{Constraint:Item|property=P17|item=Q30}}";
		String string = propertyName + " " + template;
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		PropertyIdValue property = ConstraintTestHelper
				.getPropertyIdValue("P17");
		ItemIdValue item = ConstraintTestHelper.getItemIdValue("Q30");
		ConstraintItem constraint = new ConstraintItem(constrainedProperty,
				property, item, null, null, null, null);
		Assert.assertEquals(template, constraint.getTemplate());
		Assert.assertEquals(string, constraint.toString());
	}

	@Test
	public void testToString4() {
		String propertyName = "P345";
		String template = "{{Constraint:Item|property=P107|item=Q386724|item2=Q215627}}";
		String string = propertyName + " " + template;
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		PropertyIdValue property = ConstraintTestHelper
				.getPropertyIdValue("P107");
		ItemIdValue item = ConstraintTestHelper.getItemIdValue("Q386724");
		ItemIdValue item2 = ConstraintTestHelper.getItemIdValue("Q215627");
		ConstraintItem constraint = new ConstraintItem(constrainedProperty,
				property, item, null, item2, null, null);
		Assert.assertEquals(template, constraint.getTemplate());
		Assert.assertEquals(string, constraint.toString());
	}

	@Test
	public void testToString5() {
		String propertyName = "P883";
		String template = "{{Constraint:Item|property=P17|item=Q30|exceptions={{Q|695}}, {{Q|702}}, {{Q|709}}}}";
		String string = propertyName + " " + template;
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		PropertyIdValue property = ConstraintTestHelper
				.getPropertyIdValue("P17");
		ItemIdValue item = ConstraintTestHelper.getItemIdValue("Q30");
		ConstraintItem constraint = new ConstraintItem(constrainedProperty,
				property, item, null, null, null, getExceptions());
		Assert.assertEquals(template, constraint.getTemplate());
		Assert.assertEquals(string, constraint.toString());
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
