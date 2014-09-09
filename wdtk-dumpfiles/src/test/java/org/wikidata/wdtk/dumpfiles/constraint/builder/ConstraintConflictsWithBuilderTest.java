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
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintConflictsWith;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintConflictsWithTest;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintTestHelper;
import org.wikidata.wdtk.dumpfiles.constraint.template.Template;
import org.wikidata.wdtk.dumpfiles.constraint.template.TemplateParser;

/**
 * Test class for {@link ConstraintConflictsWithBuilder}
 * 
 * @author Julian Mendez
 * 
 */
public class ConstraintConflictsWithBuilderTest {

	@Test
	public void testBuilderOnePropNoItem() {
		String propertyName = "P494";
		TemplateParser parser = new TemplateParser();
		Template template = parser
				.parse(ConstraintConflictsWithTest.templateStrOnePropNoItem);
		ConstraintConflictsWithBuilder builder = new ConstraintConflictsWithBuilder();
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		ConstraintConflictsWith expectedConstraint = new ConstraintConflictsWith(
				constrainedProperty, ConstraintConflictsWithTest.getList0());
		ConstraintConflictsWith constraint = builder.parse(constrainedProperty,
				template);
		Assert.assertEquals(expectedConstraint, constraint);
	}

	@Test
	public void testBuilderStrOnePropOneItem() {
		String propertyName = "P969";
		TemplateParser parser = new TemplateParser();
		Template template = parser
				.parse(ConstraintConflictsWithTest.templateStrOnePropOneItem);
		ConstraintConflictsWithBuilder builder = new ConstraintConflictsWithBuilder();
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		ConstraintConflictsWith expectedConstraint = new ConstraintConflictsWith(
				constrainedProperty, ConstraintConflictsWithTest.getList1());
		ConstraintConflictsWith constraint = builder.parse(constrainedProperty,
				template);
		Assert.assertEquals(expectedConstraint, constraint);
	}

	@Test
	public void testBuilderManyPropManyItem() {
		String propertyName = "P569";
		TemplateParser parser = new TemplateParser();
		Template template = parser
				.parse(ConstraintConflictsWithTest.templateStrManyPropManyItem);
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		ConstraintConflictsWith expectedConstraint = new ConstraintConflictsWith(
				constrainedProperty, ConstraintConflictsWithTest.getList2());
		ConstraintConflictsWithBuilder builder = new ConstraintConflictsWithBuilder();
		ConstraintConflictsWith constraint = builder.parse(constrainedProperty,
				template);
		Assert.assertEquals(expectedConstraint, constraint);
	}

}
