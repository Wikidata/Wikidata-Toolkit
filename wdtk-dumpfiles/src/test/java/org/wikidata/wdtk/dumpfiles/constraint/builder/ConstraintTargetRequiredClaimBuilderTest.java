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
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintTargetRequiredClaim;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintTestHelper;
import org.wikidata.wdtk.dumpfiles.constraint.template.Template;
import org.wikidata.wdtk.dumpfiles.constraint.template.TemplateParser;

/**
 * Test class for {@link ConstraintTargetRequiredClaimBuilder}.
 * 
 * @author Julian Mendez
 * 
 */
public class ConstraintTargetRequiredClaimBuilderTest {

	public ConstraintTargetRequiredClaimBuilderTest() {
	}

	@Test
	public void testBuilder() {
		String propertyName = "P9";
		String templateStr = "{{Constraint:Target required claim|property=P21|item=Q6581072}}";
		Template template = (new TemplateParser()).parse(templateStr);

		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		PropertyIdValue property = ConstraintTestHelper
				.getPropertyIdValue("P21");
		ItemIdValue item = ConstraintTestHelper.getItemIdValue("Q6581072");
		ConstraintTargetRequiredClaim expectedConstraint = new ConstraintTargetRequiredClaim(
				constrainedProperty, property, item);
		ConstraintTargetRequiredClaimBuilder builder = new ConstraintTargetRequiredClaimBuilder();
		ConstraintTargetRequiredClaim constraint = builder.parse(
				constrainedProperty, template);

		Assert.assertEquals(expectedConstraint, constraint);
	}

}
