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
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintDiffWithinRange;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintTestHelper;
import org.wikidata.wdtk.dumpfiles.constraint.template.Template;
import org.wikidata.wdtk.dumpfiles.constraint.template.TemplateParser;

/**
 * Test class for {@link ConstraintDiffWithinRangeBuilder}
 * 
 * @author Julian Mendez
 * 
 */
public class ConstraintDiffWithinRangeBuilderTest {

	@Test
	public void testParseDouble() {
		ConstraintDiffWithinRangeBuilder builder = new ConstraintDiffWithinRangeBuilder();
		Assert.assertEquals(new Double(0), builder.parseDouble("0"));
		Assert.assertEquals(new Double(-1.23), builder.parseDouble("-1.23"));
		Assert.assertEquals(new Double(1.23), builder.parseDouble("1.23"));
		Assert.assertNotEquals(new Double(-1.23), builder.parseDouble("1.23"));
		Assert.assertEquals(null, builder.parseDouble("this is not a number"));
	}

	@Test
	public void testBuilderTime0() {
		String templateStr = "{{Constraint:Diff within range|base_property=P569|min=0|max=150}}";
		Template template = (new TemplateParser()).parse(templateStr);
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue("P570");
		PropertyIdValue baseProperty = ConstraintTestHelper
				.getPropertyIdValue("P569");

		ConstraintDiffWithinRangeBuilder builder = new ConstraintDiffWithinRangeBuilder();
		ConstraintDiffWithinRange expectedConstraint = new ConstraintDiffWithinRange(
				constrainedProperty, baseProperty, "0", "150", true);
		ConstraintDiffWithinRange constraint = builder.parse(
				constrainedProperty, template);

		Assert.assertEquals(expectedConstraint, constraint);
	}

	@Test
	public void testBuilderTime1() {
		String templateStr = "{{Constraint:Diff within range|base_property=P571|min=0|max=2000}}";
		Template template = (new TemplateParser()).parse(templateStr);
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue("P576");
		PropertyIdValue baseProperty = ConstraintTestHelper
				.getPropertyIdValue("P571");

		ConstraintDiffWithinRangeBuilder builder = new ConstraintDiffWithinRangeBuilder();
		ConstraintDiffWithinRange expectedConstraint = new ConstraintDiffWithinRange(
				constrainedProperty, baseProperty, "0", "2000", true);
		ConstraintDiffWithinRange constraint = builder.parse(
				constrainedProperty, template);

		Assert.assertEquals(expectedConstraint, constraint);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBuilderWrongPropertyType() {
		String propertyName = "P31"; // instance of
		String templateStr = "{{Constraint:Diff within range|base_property=P569|min=0|max=150}}";
		Template template = (new TemplateParser()).parse(templateStr);

		ConstraintDiffWithinRangeBuilder builder = new ConstraintDiffWithinRangeBuilder();
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		builder.parse(constrainedProperty, template);
	}

}
