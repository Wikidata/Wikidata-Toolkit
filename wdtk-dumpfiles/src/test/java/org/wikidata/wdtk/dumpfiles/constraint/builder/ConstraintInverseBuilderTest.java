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
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintInverse;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintInverseTest;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintTestHelper;
import org.wikidata.wdtk.dumpfiles.constraint.template.Template;
import org.wikidata.wdtk.dumpfiles.constraint.template.TemplateParser;

/**
 * Test class for {@link ConstraintInverseBuilder}.
 *
 * @author Julian Mendez
 *
 */
public class ConstraintInverseBuilderTest {

	public ConstraintInverseBuilderTest() {
	}

	@Test
	public void testBuilder() {
		ConstraintInverseBuilder builder = new ConstraintInverseBuilder();

		TemplateParser parser = new TemplateParser();
		Template template = parser.parse(ConstraintInverseTest.TEMPLATE_STR);

		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue("P155");
		PropertyIdValue property = ConstraintTestHelper
				.getPropertyIdValue("P156");
		ConstraintInverse expectedConstraint = new ConstraintInverse(
				constrainedProperty, property);

		ConstraintInverse constraint = builder.parse(constrainedProperty,
				template);

		Assert.assertEquals(expectedConstraint, constraint);
	}

}
