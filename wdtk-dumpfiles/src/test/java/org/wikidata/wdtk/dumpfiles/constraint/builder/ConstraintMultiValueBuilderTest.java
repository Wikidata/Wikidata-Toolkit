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
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintMultiValue;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintTestHelper;
import org.wikidata.wdtk.dumpfiles.constraint.template.Template;
import org.wikidata.wdtk.dumpfiles.constraint.template.TemplateParser;

/**
 * Test class for {@link ConstraintMultiValueBuilder}.
 * 
 * @author Julian Mendez
 * 
 */
public class ConstraintMultiValueBuilderTest {

	public ConstraintMultiValueBuilderTest() {
	}

	@Test
	public void testBuilder() {

		ConstraintMultiValueBuilder builder = new ConstraintMultiValueBuilder();
		TemplateParser parser = new TemplateParser();
		Template template = parser.parse("{{Constraint:Multi value}}");
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue("P971");
		ConstraintMultiValue expectedConstraint = new ConstraintMultiValue(
				constrainedProperty);
		ConstraintMultiValue constraint = builder.parse(constrainedProperty,
				template);
		Assert.assertEquals(expectedConstraint, constraint);

	}

}
