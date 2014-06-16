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
	public void testParser() {
		TemplateParser parser = new TemplateParser();

		String str0 = "{{Constraint:Type|class=Q1048835|relation=instance}}";
		Set<String> parameterNames0 = new TreeSet<String>();
		parameterNames0.add("class");
		parameterNames0.add("relation");
		Template template0 = parser.parse(str0);
		Assert.assertEquals("Constraint:Type", template0.getName());
		Assert.assertEquals("Q1048835", template0.getValue("class"));
		Assert.assertEquals("instance", template0.getValue("relation"));
		Assert.assertEquals(2, template0.getParameters().size());
		Assert.assertEquals(parameterNames0, template0.getParameterNames());
		Assert.assertEquals(str0, template0.toString());

		String str1 = "{{Constraint:Value type|class=Q5|relation=instance}}";
		Set<String> parameterNames1 = new TreeSet<String>();
		parameterNames1.add("class");
		parameterNames1.add("relation");
		Template template1 = parser.parse(str1);
		Assert.assertEquals("Constraint:Value type", template1.getName());
		Assert.assertEquals("Q5", template1.getParameters().get("class"));
		Assert.assertEquals("instance",
				template1.getParameters().get("relation"));
		Assert.assertEquals(2, template1.getParameters().size());
		Assert.assertEquals(parameterNames1, template1.getParameterNames());
		Assert.assertEquals(str1, template1.toString());

		String str2 = "{{Constraint:Target required claim|property=P21}}";
		Set<String> parameterNames2 = new TreeSet<String>();
		parameterNames2.add("property");
		Template template2 = parser.parse(str2);
		Assert.assertEquals("Constraint:Target required claim",
				template2.getName());
		Assert.assertEquals("P21", template2.getParameters().get("property"));
		Assert.assertEquals(1, template2.getParameters().size());
		Assert.assertEquals(parameterNames2, template2.getParameterNames());
		Assert.assertEquals(str2, template2.toString());

		String str3 = "{{Constraint:One of|values={{Q|6581097}}, {{Q|6581072}}, {{Q|1097630}}, {{Q|44148}}, {{Q|43445}}, {{Q|1052281}}, {{Q|2449503}}, {{Q|48270}}, {{Q|1399232}}, {{Q|3277905}}, {{Q|746411}}, {{Q|350374}}, {{Q|660882}}}}";
		Set<String> parameterNames3 = new TreeSet<String>();
		parameterNames3.add("values");
		Template template3 = parser.parse(str3);
		Assert.assertEquals("Constraint:One of", template3.getName());
		Assert.assertEquals(
				"{{Q|6581097}}, {{Q|6581072}}, {{Q|1097630}}, {{Q|44148}}, {{Q|43445}}, {{Q|1052281}}, {{Q|2449503}}, {{Q|48270}}, {{Q|1399232}}, {{Q|3277905}}, {{Q|746411}}, {{Q|350374}}, {{Q|660882}}",
				template3.getValue("values"));
		Assert.assertEquals(1, template3.getParameters().size());
		Assert.assertEquals(parameterNames3, template3.getParameterNames());
		Assert.assertEquals(str3, template3.toString());

		String str4 = "{{Constraint:Item|property=P17||exceptions={{Q|3593529}}}}";
		String str4Norm = "{{Constraint:Item|exceptions={{Q|3593529}}|property=P17}}";
		Set<String> parameterNames4 = new TreeSet<String>();
		parameterNames4.add("property");
		parameterNames4.add("exceptions");
		Template template4 = parser.parse(str4);
		Assert.assertEquals("Constraint:Item", template4.getName());
		Assert.assertEquals("P17", template4.getValue("property"));
		Assert.assertEquals("{{Q|3593529}}", template4.getValue("exceptions"));
		Assert.assertEquals(2, template4.getParameters().size());
		Assert.assertEquals(parameterNames4, template4.getParameterNames());
		Assert.assertEquals(str4Norm, template4.toString());

		String str5 = "{{Constraint:Target required claim|property=P279|exceptions ={{Q|35120}}}}";
		String str5Norm = "{{Constraint:Target required claim|exceptions={{Q|35120}}|property=P279}}";
		Set<String> parameterNames5 = new TreeSet<String>();
		parameterNames5.add("property");
		parameterNames5.add("exceptions");
		Template template5 = parser.parse(str5);
		Assert.assertEquals("Constraint:Target required claim",
				template5.getName());
		Assert.assertEquals("P279", template5.getValue("property"));
		Assert.assertEquals("{{Q|35120}}", template5.getValue("exceptions"));
		Assert.assertEquals(2, template5.getParameters().size());
		Assert.assertEquals(parameterNames5, template5.getParameterNames());
		Assert.assertEquals(str5Norm, template5.toString());

		String str6 = "{{Constraint:Target required claim|property=P279|exceptions={{Q|35120}}, {{Q|14897293}}}}";
		String str6Norm = "{{Constraint:Target required claim|exceptions={{Q|35120}}, {{Q|14897293}}|property=P279}}";
		Set<String> parameterNames6 = new TreeSet<String>();
		parameterNames6.add("property");
		parameterNames6.add("exceptions");
		Template template6 = parser.parse(str6);
		Assert.assertEquals("Constraint:Target required claim",
				template6.getName());
		Assert.assertEquals("P279", template6.getValue("property"));
		Assert.assertEquals("{{Q|35120}}, {{Q|14897293}}",
				template6.getValue("exceptions"));
		Assert.assertEquals(2, template6.getParameters().size());
		Assert.assertEquals(parameterNames6, template6.getParameterNames());
		Assert.assertEquals(str6Norm, template6.toString());

		String str3b = "{{Constraint:One of|values={{Q|6581097}}, {{Q|6581072}}, {{Q|1097630}}, <!-- more values -->{{Q|44148}}, {{Q|43445}}, {{Q|1052281}}, {{Q|2449503}}, {{Q|48270}}, {{Q|1399232}}, {{Q|3277905}}, {{Q|746411}}, {{Q|350374}}, {{Q|660882}}}}";
		String str3bNorm = "{{Constraint:One of|values={{Q|6581097}}, {{Q|6581072}}, {{Q|1097630}}, {{Q|44148}}, {{Q|43445}}, {{Q|1052281}}, {{Q|2449503}}, {{Q|48270}}, {{Q|1399232}}, {{Q|3277905}}, {{Q|746411}}, {{Q|350374}}, {{Q|660882}}}}";
		TemplateScanner scanner = new TemplateScanner();
		List<String> list = scanner.getTemplates(str3b);
		Assert.assertEquals(1, list.size());

		Set<String> parameterNames3b = new TreeSet<String>();
		parameterNames3b.add("values");
		Template template3b = parser.parse(list.get(0));
		Assert.assertEquals("Constraint:One of", template3b.getName());
		Assert.assertEquals(
				"{{Q|6581097}}, {{Q|6581072}}, {{Q|1097630}}, {{Q|44148}}, {{Q|43445}}, {{Q|1052281}}, {{Q|2449503}}, {{Q|48270}}, {{Q|1399232}}, {{Q|3277905}}, {{Q|746411}}, {{Q|350374}}, {{Q|660882}}",
				template3b.getValue("values"));
		Assert.assertEquals(1, template3b.getParameters().size());
		Assert.assertEquals(parameterNames3b, template3b.getParameterNames());
		Assert.assertEquals(str3bNorm, template3b.toString());

	}

}
