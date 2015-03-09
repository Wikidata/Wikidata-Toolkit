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
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintQualifiers;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintQualifiersTest;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintTestHelper;
import org.wikidata.wdtk.dumpfiles.constraint.template.Template;
import org.wikidata.wdtk.dumpfiles.constraint.template.TemplateParser;

/**
 * Test class for {@link ConstraintQualifiersBuilder}
 * 
 * @author Julian Mendez
 * 
 */
public class ConstraintQualifiersBuilderTest {

	public ConstraintQualifiersBuilderTest() {
	}

	@Test
	public void testBuilderNoProp() {
		TemplateParser parser = new TemplateParser();
		Template template = parser
				.parse(ConstraintQualifiersTest.TEMPLATE_STR_NO_PROP);
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue("P40");
		ConstraintQualifiersBuilder builder = new ConstraintQualifiersBuilder();
		ConstraintQualifiers expectedConstraint = new ConstraintQualifiers(
				constrainedProperty, ConstraintQualifiersTest.getListNoProp());
		ConstraintQualifiers constraint = builder.parse(constrainedProperty,
				template);
		Assert.assertEquals(expectedConstraint, constraint);
	}

	@Test
	public void testBuilderOneProp() {
		TemplateParser parser = new TemplateParser();
		Template template = parser
				.parse(ConstraintQualifiersTest.TEMPLATE_STR_ONE_PROP);
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue("P10");
		ConstraintQualifiersBuilder builder = new ConstraintQualifiersBuilder();
		ConstraintQualifiers expectedConstraint = new ConstraintQualifiers(
				constrainedProperty, ConstraintQualifiersTest.getListOneProp());
		ConstraintQualifiers constraint = builder.parse(constrainedProperty,
				template);
		Assert.assertEquals(expectedConstraint, constraint);
	}

	@Test
	public void testBuilderTwoProp() {
		TemplateParser parser = new TemplateParser();
		Template template = parser
				.parse(ConstraintQualifiersTest.TEMPLATE_STR_TWO_PROP);
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue("P6");
		ConstraintQualifiers expectedConstraint = new ConstraintQualifiers(
				constrainedProperty, ConstraintQualifiersTest.getListTwoProp());
		ConstraintQualifiersBuilder builder = new ConstraintQualifiersBuilder();
		ConstraintQualifiers constraint = builder.parse(constrainedProperty,
				template);
		Assert.assertEquals(expectedConstraint, constraint);
	}

}
