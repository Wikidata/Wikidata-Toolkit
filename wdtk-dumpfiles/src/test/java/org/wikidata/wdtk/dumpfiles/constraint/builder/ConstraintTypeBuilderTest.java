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
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintTestHelper;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintType;
import org.wikidata.wdtk.dumpfiles.constraint.model.RelationType;
import org.wikidata.wdtk.dumpfiles.constraint.template.Template;
import org.wikidata.wdtk.dumpfiles.constraint.template.TemplateParser;

/**
 * Test class for {@link ConstraintTypeBuilder}
 * 
 * @author Julian Mendez
 * 
 */
public class ConstraintTypeBuilderTest {

	@Test
	public void testBuilder() {
		String propertyName = "P30";
		String templateStr = "{{Constraint:Type|class=Q2221906|relation=instance}}";
		Template template = (new TemplateParser()).parse(templateStr);

		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		ItemIdValue item = ConstraintTestHelper.getItemIdValue("Q2221906");
		ConstraintType expectedConstraint = new ConstraintType(
				constrainedProperty, item, RelationType.INSTANCE);

		ConstraintTypeBuilder builder = new ConstraintTypeBuilder();
		ConstraintType constraint = builder
				.parse(constrainedProperty, template);

		Assert.assertEquals(expectedConstraint, constraint);
	}

}
