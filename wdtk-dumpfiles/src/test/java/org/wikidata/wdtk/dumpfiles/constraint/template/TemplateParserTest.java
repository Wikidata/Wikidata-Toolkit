package org.wikidata.wdtk.dumpfiles.constraint.template;

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

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@link TemplateParser}.
 * 
 * @author Julian Mendez
 * 
 */
public class TemplateParserTest {

	@Test
	public void testParser0() {
		TemplateParser parser = new TemplateParser();
		String str = "{{Constraint:Type|class=Q1048835|relation=instance}}";
		Set<String> parameterNames = new TreeSet<String>();
		parameterNames.add("class");
		parameterNames.add("relation");
		Template template = parser.parse(str);
		Assert.assertEquals("Constraint:Type", template.getName());
		Assert.assertEquals("Q1048835", template.getValue("class"));
		Assert.assertEquals("instance", template.getValue("relation"));
		Assert.assertEquals(2, template.getParameters().size());
		Assert.assertEquals(parameterNames, template.getParameterNames());
		Assert.assertEquals(str, template.toString());
	}

	@Test
	public void testParser1() {
		TemplateParser parser = new TemplateParser();
		String str = "{{Constraint:Value type|class=Q5|relation=instance}}";
		Set<String> parameterNames = new TreeSet<String>();
		parameterNames.add("class");
		parameterNames.add("relation");
		Template template = parser.parse(str);
		Assert.assertEquals("Constraint:Value type", template.getName());
		Assert.assertEquals("Q5", template.getParameters().get("class"));
		Assert.assertEquals("instance", template.getParameters()
				.get("relation"));
		Assert.assertEquals(2, template.getParameters().size());
		Assert.assertEquals(parameterNames, template.getParameterNames());
		Assert.assertEquals(str, template.toString());
	}

	@Test
	public void testParser2() {
		TemplateParser parser = new TemplateParser();
		String str = "{{Constraint:Target required claim|property=P21}}";
		Set<String> parameterNames = new TreeSet<String>();
		parameterNames.add("property");
		Template template = parser.parse(str);
		Assert.assertEquals("Constraint:Target required claim",
				template.getName());
		Assert.assertEquals("P21", template.getParameters().get("property"));
		Assert.assertEquals(1, template.getParameters().size());
		Assert.assertEquals(parameterNames, template.getParameterNames());
		Assert.assertEquals(str, template.toString());
	}

	@Test
	public void testParser3() {
		TemplateParser parser = new TemplateParser();
		String str = "{{Constraint:One of|values={{Q|6581097}}, {{Q|6581072}}, {{Q|1097630}}, {{Q|44148}}, {{Q|43445}}, {{Q|1052281}}, {{Q|2449503}}, {{Q|48270}}, {{Q|1399232}}, {{Q|3277905}}, {{Q|746411}}, {{Q|350374}}, {{Q|660882}}}}";
		Set<String> parameterNames = new TreeSet<String>();
		parameterNames.add("values");
		Template template = parser.parse(str);
		Assert.assertEquals("Constraint:One of", template.getName());
		Assert.assertEquals(
				"{{Q|6581097}}, {{Q|6581072}}, {{Q|1097630}}, {{Q|44148}}, {{Q|43445}}, {{Q|1052281}}, {{Q|2449503}}, {{Q|48270}}, {{Q|1399232}}, {{Q|3277905}}, {{Q|746411}}, {{Q|350374}}, {{Q|660882}}",
				template.getValue("values"));
		Assert.assertEquals(1, template.getParameters().size());
		Assert.assertEquals(parameterNames, template.getParameterNames());
		Assert.assertEquals(str, template.toString());
	}

	@Test
	public void testParser3b() {
		TemplateParser parser = new TemplateParser();
		String str = "{{Constraint:One of|values={{Q|6581097}}, {{Q|6581072}}, {{Q|1097630}}, <!-- more values -->{{Q|44148}}, {{Q|43445}}, {{Q|1052281}}, {{Q|2449503}}, {{Q|48270}}, {{Q|1399232}}, {{Q|3277905}}, {{Q|746411}}, {{Q|350374}}, {{Q|660882}}}}";
		String strNorm = "{{Constraint:One of|values={{Q|6581097}}, {{Q|6581072}}, {{Q|1097630}}, {{Q|44148}}, {{Q|43445}}, {{Q|1052281}}, {{Q|2449503}}, {{Q|48270}}, {{Q|1399232}}, {{Q|3277905}}, {{Q|746411}}, {{Q|350374}}, {{Q|660882}}}}";
		TemplateScanner scanner = new TemplateScanner();
		List<String> list = scanner.getTemplates(str);
		Assert.assertEquals(1, list.size());

		Set<String> parameterNames = new TreeSet<String>();
		parameterNames.add("values");
		Template template = parser.parse(list.get(0));
		Assert.assertEquals("Constraint:One of", template.getName());
		Assert.assertEquals(
				"{{Q|6581097}}, {{Q|6581072}}, {{Q|1097630}}, {{Q|44148}}, {{Q|43445}}, {{Q|1052281}}, {{Q|2449503}}, {{Q|48270}}, {{Q|1399232}}, {{Q|3277905}}, {{Q|746411}}, {{Q|350374}}, {{Q|660882}}",
				template.getValue("values"));
		Assert.assertEquals(1, template.getParameters().size());
		Assert.assertEquals(parameterNames, template.getParameterNames());
		Assert.assertEquals(strNorm, template.toString());
	}

	@Test
	public void testParser4() {
		TemplateParser parser = new TemplateParser();
		String str = "{{Constraint:Item|property=P17||exceptions={{Q|3593529}}}}";
		String strNorm = "{{Constraint:Item|exceptions={{Q|3593529}}|property=P17}}";
		Set<String> parameterNames = new TreeSet<String>();
		parameterNames.add("property");
		parameterNames.add("exceptions");
		Template template = parser.parse(str);
		Assert.assertEquals("Constraint:Item", template.getName());
		Assert.assertEquals("P17", template.getValue("property"));
		Assert.assertEquals("{{Q|3593529}}", template.getValue("exceptions"));
		Assert.assertEquals(2, template.getParameters().size());
		Assert.assertEquals(parameterNames, template.getParameterNames());
		Assert.assertEquals(strNorm, template.toString());
	}

	@Test
	public void testParser5() {
		TemplateParser parser = new TemplateParser();
		String str = "{{Constraint:Target required claim|property=P279|exceptions ={{Q|35120}}}}";
		String strNorm = "{{Constraint:Target required claim|exceptions={{Q|35120}}|property=P279}}";
		Set<String> parameterNames = new TreeSet<String>();
		parameterNames.add("property");
		parameterNames.add("exceptions");
		Template template = parser.parse(str);
		Assert.assertEquals("Constraint:Target required claim",
				template.getName());
		Assert.assertEquals("P279", template.getValue("property"));
		Assert.assertEquals("{{Q|35120}}", template.getValue("exceptions"));
		Assert.assertEquals(2, template.getParameters().size());
		Assert.assertEquals(parameterNames, template.getParameterNames());
		Assert.assertEquals(strNorm, template.toString());
	}

	@Test
	public void testParser6() {
		TemplateParser parser = new TemplateParser();
		String str = "{{Constraint:Target required claim|property=P279|exceptions={{Q|35120}}, {{Q|14897293}}}}";
		String strNorm = "{{Constraint:Target required claim|exceptions={{Q|35120}}, {{Q|14897293}}|property=P279}}";
		Set<String> parameterNames = new TreeSet<String>();
		parameterNames.add("property");
		parameterNames.add("exceptions");
		Template template = parser.parse(str);
		Assert.assertEquals("Constraint:Target required claim",
				template.getName());
		Assert.assertEquals("P279", template.getValue("property"));
		Assert.assertEquals("{{Q|35120}}, {{Q|14897293}}",
				template.getValue("exceptions"));
		Assert.assertEquals(2, template.getParameters().size());
		Assert.assertEquals(parameterNames, template.getParameterNames());
		Assert.assertEquals(strNorm, template.toString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidTemplate0() {
		TemplateParser parser = new TemplateParser();
		parser.parse("{Constraint:Type|class=Q1048835|relation=instance}}");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidTemplate1() {
		TemplateParser parser = new TemplateParser();
		parser.parse("{{Constraint:Type|class=Q1048835|relation=instance}");
	}

	@Test
	public void testLookAhead0() {
		(new TemplateParser() {
			public void testLookAhead() {
				LookAhead lookAhead0 = new LookAhead(-1,
						LookAheadItem.OPENING_BRACES);
				LookAhead lookAhead1 = new LookAhead(-1,
						LookAheadItem.CLOSING_BRACES);
				Assert.assertEquals(0, lookAhead0.compareTo(lookAhead1));

				LookAhead lookAhead2 = new LookAhead(-1,
						LookAheadItem.OPENING_BRACES);
				LookAhead lookAhead3 = new LookAhead(0,
						LookAheadItem.OPENING_BRACES);
				Assert.assertEquals(1, lookAhead2.compareTo(lookAhead3));
				Assert.assertEquals(-1, lookAhead3.compareTo(lookAhead2));

			}
		}).testLookAhead();
	}

	@Test
	public void testLookAhead1() {
		(new TemplateParser() {
			public void testLookAhead() {
				LookAhead lookAhead = new LookAhead(8,
						LookAheadItem.OPENING_BRACES);
				String expected = LookAheadItem.OPENING_BRACES + "@8";
				Assert.assertEquals(expected, lookAhead.toString());
			}
		}).testLookAhead();
	}

}
