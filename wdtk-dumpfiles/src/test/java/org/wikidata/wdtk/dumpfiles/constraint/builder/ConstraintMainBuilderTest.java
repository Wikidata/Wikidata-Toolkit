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

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.dumpfiles.constraint.model.Constraint;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintTestHelper;
import org.wikidata.wdtk.dumpfiles.constraint.model.PropertyValues;
import org.wikidata.wdtk.dumpfiles.constraint.template.Template;
import org.wikidata.wdtk.dumpfiles.constraint.template.TemplateParser;

/**
 * Test class for {@link ConstraintMainBuilder}.
 * 
 * @author Julian Mendez
 * 
 */
public class ConstraintMainBuilderTest {

	public ConstraintMainBuilderTest() {
	}

	@Test
	public void testRemoveBrackets() {
		String input0 = "{{Q|16}}";
		String expected0 = "Q16";
		Assert.assertEquals(expected0,
				ConstraintMainBuilder.removeBrackets(input0));
		String input1 = "[[Q16]]";
		String expected1 = "Q16";
		Assert.assertEquals(expected1,
				ConstraintMainBuilder.removeBrackets(input1));
		String input2 = "{{P|31}}";
		String expected2 = "P31";
		Assert.assertEquals(expected2,
				ConstraintMainBuilder.removeBrackets(input2));
	}

	private PropertyIdValue getPropertyIdValue(int itemId) {
		return (new DataObjectFactoryImpl()).getPropertyIdValue("P" + itemId,
				ConstraintMainBuilder.PREFIX_WIKIDATA);
	}

	private ItemIdValue getItemIdValue(int itemId) {
		return (new DataObjectFactoryImpl()).getItemIdValue("Q" + itemId,
				ConstraintMainBuilder.PREFIX_WIKIDATA);
	}

	@Test
	public void testParseListOfProperties() {
		String input = "{{P|580}}, {{P|582}}, {{P|805}}, {{P|1480}}";
		List<PropertyIdValue> expected = new ArrayList<PropertyIdValue>();
		expected.add(getPropertyIdValue(580));
		expected.add(getPropertyIdValue(582));
		expected.add(getPropertyIdValue(805));
		expected.add(getPropertyIdValue(1480));
		Assert.assertEquals(expected,
				ConstraintMainBuilder.parseListOfProperties(input));
	}

	@Test
	public void testParseListOfItems() {
		String input = "{{Q|46}}, {{Q|48}}, {{Q|15}}, {{Q|49}}, {{Q|18}}, {{Q|51}}, {{Q|3960}}, {{Q|5401}}, {{Q|538}}, {{Q|27611}}, {{Q|828}}, {{Q|664609}}";
		List<ItemIdValue> expected = new ArrayList<ItemIdValue>();
		expected.add(getItemIdValue(46));
		expected.add(getItemIdValue(48));
		expected.add(getItemIdValue(15));
		expected.add(getItemIdValue(49));
		expected.add(getItemIdValue(18));
		expected.add(getItemIdValue(51));
		expected.add(getItemIdValue(3960));
		expected.add(getItemIdValue(5401));
		expected.add(getItemIdValue(538));
		expected.add(getItemIdValue(27611));
		expected.add(getItemIdValue(828));
		expected.add(getItemIdValue(664609));
		Assert.assertEquals(expected,
				ConstraintMainBuilder.parseListOfItems(input));
	}

	@Test
	public void testParseListOfItemsWrongItem() {
		String input = "{{Q|46}}, {{P|48}}, {{Q|15}}, {{Q|49}},";
		List<ItemIdValue> expected = new ArrayList<ItemIdValue>();
		expected.add(getItemIdValue(46));
		expected.add(getItemIdValue(15));
		expected.add(getItemIdValue(49));
		Assert.assertEquals(expected,
				ConstraintMainBuilder.parseListOfItems(input));
	}

	@Test
	public void testParseListOfPropertyValuesTwoItems() {
		String input = "{{P|31}}: {{Q|5}}, {{Q|4167410}}";
		List<PropertyValues> expected = new ArrayList<PropertyValues>();
		List<ItemIdValue> listOfItems = new ArrayList<ItemIdValue>();
		listOfItems.add(getItemIdValue(5));
		listOfItems.add(getItemIdValue(4167410));
		expected.add(new PropertyValues(getPropertyIdValue(31), listOfItems));
		Assert.assertEquals(expected,
				ConstraintMainBuilder.parseListOfPropertyValues(input));
	}

	@Test
	public void testParseListOfPropertyValuesTwoProperties() {
		String input = "{{P|31}}: {{Q|4167410}}; {{P|625}}";
		List<PropertyValues> expected = new ArrayList<PropertyValues>();
		List<ItemIdValue> listOfItems = new ArrayList<ItemIdValue>();
		listOfItems.add(getItemIdValue(4167410));
		expected.add(new PropertyValues(getPropertyIdValue(31), listOfItems));
		expected.add(new PropertyValues(getPropertyIdValue(625)));
		Assert.assertEquals(expected,
				ConstraintMainBuilder.parseListOfPropertyValues(input));
	}

	@Test
	public void testParseListOfPropertyValuesMultiple() {
		String input = "{{P|527}}; {{P|31}}: {{Q|14756018}}, {{Q|14073567}}, {{Q|4167410}}; {{P|625}}";
		List<PropertyValues> expected = new ArrayList<PropertyValues>();
		List<ItemIdValue> listOfItems = new ArrayList<ItemIdValue>();
		listOfItems.add(getItemIdValue(14756018));
		listOfItems.add(getItemIdValue(14073567));
		listOfItems.add(getItemIdValue(4167410));
		expected.add(new PropertyValues(getPropertyIdValue(527)));
		expected.add(new PropertyValues(getPropertyIdValue(31), listOfItems));
		expected.add(new PropertyValues(getPropertyIdValue(625)));
		Assert.assertEquals(expected,
				ConstraintMainBuilder.parseListOfPropertyValues(input));
	}

	@Test
	public void testNormalize() {
		ConstraintMainBuilder builder = new ConstraintMainBuilder();
		Assert.assertEquals("Lisp", builder.normalize("LISP"));
		Assert.assertEquals("Text", builder.normalize("text"));
		Assert.assertEquals("Big data", builder.normalize("Big_Data"));
		Assert.assertEquals("2-aminoethanol",
				builder.normalize("2-Aminoethanol"));
	}

	@Test
	public void testFirstLetterToUpperCase() {
		Assert.assertEquals("LISP",
				ConstraintMainBuilder.firstLetterToUpperCase("LISP"));
		Assert.assertEquals("Text",
				ConstraintMainBuilder.firstLetterToUpperCase("text"));
		Assert.assertEquals("Big_Data",
				ConstraintMainBuilder.firstLetterToUpperCase("Big_Data"));
		Assert.assertEquals("2-Aminoethanol",
				ConstraintMainBuilder.firstLetterToUpperCase("2-Aminoethanol"));
		Assert.assertEquals("P31",
				ConstraintMainBuilder.firstLetterToUpperCase("p31"));
		Assert.assertEquals(null,
				ConstraintMainBuilder.firstLetterToUpperCase(null));
		Assert.assertEquals("",
				ConstraintMainBuilder.firstLetterToUpperCase(""));
	}

	@Test
	public void testParseListOfQuantities() {
		String input = "1, 2, 3, 4, 5, 6, 7, 8, 9, 10";
		List<Integer> expected = new ArrayList<Integer>();
		for (int i = 1; i <= 10; i++) {
			expected.add(i);
		}
		Assert.assertEquals(expected,
				ConstraintMainBuilder.parseListOfQuantities(input));
	}

	@Test
	public void testInvalidConstraintException() {
		String propertyName = "P31"; // instance of
		String templateStr = "{{Constraint:Range|min=0|max=1}}";
		Template template = (new TemplateParser()).parse(templateStr);

		ConstraintMainBuilder builder = new ConstraintMainBuilder();
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		Constraint constraint = builder.parse(constrainedProperty, template);
		Assert.assertEquals(null, constraint);
	}

}
