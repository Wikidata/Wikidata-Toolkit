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
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintOneOf;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintOneOfTest;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintTestHelper;
import org.wikidata.wdtk.dumpfiles.constraint.template.Template;
import org.wikidata.wdtk.dumpfiles.constraint.template.TemplateParser;

/**
 * Test class for {@link ConstraintOneOfBuilder}.
 * 
 * @author Julian Mendez
 * 
 */
public class ConstraintOneOfBuilderTest {

	public ConstraintOneOfBuilderTest() {
	}

	@Test
	public void testBuilderItemValues() {
		String propertyName = "P412";
		Template template = (new TemplateParser())
				.parse(ConstraintOneOfTest.TEMPLATE_STR_ITEM_VAL);

		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		ConstraintOneOf expectedConstraint = new ConstraintOneOf(
				constrainedProperty, ConstraintOneOfTest.getItemValues());
		ConstraintOneOfBuilder builder = new ConstraintOneOfBuilder();
		ConstraintOneOf constraint = builder.parse(constrainedProperty,
				template);

		Assert.assertEquals(expectedConstraint, constraint);
	}

	@Test
	public void testBuilderQuantityValues() {
		String propertyName = "P1088";
		Template template = (new TemplateParser())
				.parse(ConstraintOneOfTest.TEMPLATE_STR_QUANTITY_VAL);

		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		ConstraintOneOf expectedConstraint = new ConstraintOneOf(
				constrainedProperty, ConstraintOneOfTest.getQuantityValues(), 0);
		ConstraintOneOfBuilder builder = new ConstraintOneOfBuilder();
		ConstraintOneOf constraint = builder.parse(constrainedProperty,
				template);

		Assert.assertEquals(expectedConstraint, constraint);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBuilderWrongPropertyType() {
		String propertyName = "P10";
		Template template = (new TemplateParser())
				.parse(ConstraintOneOfTest.TEMPLATE_STR_QUANTITY_VAL);

		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		ConstraintOneOfBuilder builder = new ConstraintOneOfBuilder();
		builder.parse(constrainedProperty, template);
	}

}
