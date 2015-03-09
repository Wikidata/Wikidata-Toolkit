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
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.dumpfiles.constraint.model.Constraint;
import org.wikidata.wdtk.dumpfiles.constraint.template.Template;
import org.wikidata.wdtk.dumpfiles.constraint.template.TemplateParser;

/**
 * Test class for {@link ConstraintTemplateString}.
 *
 * @author Julian Mendez
 *
 */
public class ConstraintTemplateStringTest {

	public ConstraintTemplateStringTest() {
	}

	/**
	 * Returns the property id value for the specified property name.
	 *
	 * @param propertyName
	 *            property name
	 * @return the property id value for the specified property name
	 */
	PropertyIdValue getPropertyIdValue(String propertyName) {
		return (new DataObjectFactoryImpl()).getPropertyIdValue(propertyName,
				ConstraintMainBuilder.PREFIX_WIKIDATA);
	}

	@Test
	public void testConstraintOneOf() {
		TemplateParser parser = new TemplateParser();
		ConstraintMainBuilder constraintBuilder = new ConstraintMainBuilder();

		String text0 = "{{Constraint:One of|values={{Q|6581097}}, {{Q|6581072}}, {{Q|1097630}}, {{Q|44148}}, {{Q|43445}}, {{Q|1052281}}, {{Q|2449503}}, {{Q|48270}}, {{Q|1399232}}, {{Q|3277905}}, {{Q|746411}}, {{Q|350374}}, {{Q|660882}}}}";
		Template template0 = parser.parse(text0);
		Constraint c0 = constraintBuilder.parse(getPropertyIdValue("P21"),
				template0);
		Assert.assertEquals(text0, c0.getTemplate());
	}

	@Test
	public void testConstraintTargetRequiredClaim() {
		TemplateParser parser = new TemplateParser();
		ConstraintMainBuilder constraintBuilder = new ConstraintMainBuilder();

		String text0 = "{{Constraint:Target required claim|property=P21}}";
		Template template0 = parser.parse(text0);
		Constraint c0 = constraintBuilder.parse(getPropertyIdValue("P6"),
				template0);
		Assert.assertEquals(text0, c0.getTemplate());

		String text1 = "{{Constraint:Target required claim|property=P279}}";
		Template template1 = parser.parse(text1);
		Constraint c1 = constraintBuilder.parse(getPropertyIdValue("P31"),
				template1);
		Assert.assertEquals(text1, c1.getTemplate());
	}

	@Test
	public void testConstraintItem() {
		TemplateParser parser = new TemplateParser();
		ConstraintMainBuilder constraintBuilder = new ConstraintMainBuilder();

		String text0 = "{{Constraint:Item|property=P17|exceptions={{Q|3593529}}}}";
		Template template0 = parser.parse(text0);
		Constraint c0 = constraintBuilder.parse(getPropertyIdValue("P814"),
				template0);
		Assert.assertEquals(text0, c0.getTemplate());
	}

	@Test
	public void testConstraintType() {
		TemplateParser parser = new TemplateParser();
		ConstraintMainBuilder constraintBuilder = new ConstraintMainBuilder();

		String text0 = "{{Constraint:Type|class=Q1048835|relation=instance}}";
		String expectedText = "{{Constraint:Item|property=P31|item=Q1048835}}";
		Template template0 = parser.parse(text0);
		Constraint c0 = constraintBuilder.parse(getPropertyIdValue("P6"),
				template0);
		Assert.assertEquals(expectedText, c0.getTemplate());
	}

	@Test
	public void testConstraintValueType() {
		TemplateParser parser = new TemplateParser();
		ConstraintMainBuilder constraintBuilder = new ConstraintMainBuilder();

		String text0 = "{{Constraint:Value type|class=Q5|relation=instance}}";
		String expectedText = "{{Constraint:Target required claim|property=P31|item=Q5}}";
		Template template0 = parser.parse(text0);
		Constraint c0 = constraintBuilder.parse(getPropertyIdValue("P6"),
				template0);
		Assert.assertEquals(expectedText, c0.getTemplate());
	}

}
