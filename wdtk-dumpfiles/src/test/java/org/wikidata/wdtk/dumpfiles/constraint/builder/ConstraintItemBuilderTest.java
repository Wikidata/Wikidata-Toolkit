package org.wikidata.wdtk.dumpfiles.constraint.builder;

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
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintItem;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintItemTest;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintTestHelper;
import org.wikidata.wdtk.dumpfiles.constraint.template.Template;
import org.wikidata.wdtk.dumpfiles.constraint.template.TemplateParser;

/**
 * Test class for {@link ConstraintItemBuilder}
 * 
 * @author Julian Mendez
 * 
 */
public class ConstraintItemBuilderTest {

	public ConstraintItemBuilderTest() {
	}

	@Test
	public void testBuilderOneProp() {
		String propertyName = "P141";
		Template template = (new TemplateParser())
				.parse(ConstraintItemTest.TEMPLATE_STR_ONE_PROP);
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		PropertyIdValue property = ConstraintTestHelper
				.getPropertyIdValue("P225");
		ConstraintItem expectedConstraint = new ConstraintItem(
				constrainedProperty, property, null, null, null, null, null);
		ConstraintItemBuilder builder = new ConstraintItemBuilder();
		ConstraintItem constraint = builder
				.parse(constrainedProperty, template);

		Assert.assertEquals(expectedConstraint, constraint);
	}

	@Test
	public void testBuilderOnePropManyItem() {
		String propertyName = "P141";
		Template template = (new TemplateParser())
				.parse(ConstraintItemTest.TEMPLATE_STR_ONE_PROP_MANY_ITEM);

		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		PropertyIdValue property = ConstraintTestHelper
				.getPropertyIdValue("P105");
		ConstraintItem expectedConstraint = new ConstraintItem(
				constrainedProperty, property, null, null, null,
				ConstraintItemTest.getItems(), null);
		ConstraintItemBuilder builder = new ConstraintItemBuilder();
		ConstraintItem constraint = builder
				.parse(constrainedProperty, template);

		Assert.assertEquals(expectedConstraint, constraint);
	}

	@Test
	public void testBuilderTwoProp() {
		String propertyName = "P1031";
		Template template = (new TemplateParser())
				.parse(ConstraintItemTest.TEMPLATE_STR_TWO_PROP);

		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		PropertyIdValue property = ConstraintTestHelper
				.getPropertyIdValue("P1001");
		PropertyIdValue property2 = ConstraintTestHelper
				.getPropertyIdValue("P953");
		ConstraintItem expectedConstraint = new ConstraintItem(
				constrainedProperty, property, null, property2, null, null,
				null);
		ConstraintItemBuilder builder = new ConstraintItemBuilder();
		ConstraintItem constraint = builder
				.parse(constrainedProperty, template);

		Assert.assertEquals(expectedConstraint, constraint);
	}

	@Test
	public void testBuilderOnePropOneItem() {
		String propertyName = "P240";
		Template template = (new TemplateParser())
				.parse(ConstraintItemTest.TEMPLATE_STR_ONE_PROP_ONE_ITEM);

		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		PropertyIdValue property = ConstraintTestHelper
				.getPropertyIdValue("P17");
		ItemIdValue item = ConstraintTestHelper.getItemIdValue("Q30");
		ConstraintItem expectedConstraint = new ConstraintItem(
				constrainedProperty, property, item, null, null, null, null);
		ConstraintItemBuilder builder = new ConstraintItemBuilder();
		ConstraintItem constraint = builder
				.parse(constrainedProperty, template);

		Assert.assertEquals(expectedConstraint, constraint);
	}

	@Test
	public void testBuilderOnePropTwoItem() {
		String propertyName = "P345";
		Template template = (new TemplateParser())
				.parse(ConstraintItemTest.TEMPLATE_STR_ONE_PROP_TWO_ITEM);

		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		PropertyIdValue property = ConstraintTestHelper
				.getPropertyIdValue("P107");
		ItemIdValue item = ConstraintTestHelper.getItemIdValue("Q386724");
		ItemIdValue item2 = ConstraintTestHelper.getItemIdValue("Q215627");
		ConstraintItem expectedConstraint = new ConstraintItem(
				constrainedProperty, property, item, null, item2, null, null);
		ConstraintItemBuilder builder = new ConstraintItemBuilder();
		ConstraintItem constraint = builder
				.parse(constrainedProperty, template);

		Assert.assertEquals(expectedConstraint, constraint);
	}

	@Test
	public void testBuilderOnePropOneItemExcep() {
		String propertyName = "P883";
		Template template = (new TemplateParser())
				.parse(ConstraintItemTest.TEMPLATE_STR_ONE_PROP_ONE_ITEM_EXCEP);

		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		PropertyIdValue property = ConstraintTestHelper
				.getPropertyIdValue("P17");
		ItemIdValue item = ConstraintTestHelper.getItemIdValue("Q30");
		ConstraintItem expectedConstraint = new ConstraintItem(
				constrainedProperty, property, item, null, null, null,
				ConstraintItemTest.getExceptions());
		ConstraintItemBuilder builder = new ConstraintItemBuilder();
		ConstraintItem constraint = builder
				.parse(constrainedProperty, template);

		Assert.assertEquals(expectedConstraint, constraint);
	}

}
