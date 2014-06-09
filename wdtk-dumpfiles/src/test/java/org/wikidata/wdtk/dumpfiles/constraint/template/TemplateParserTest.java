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

import org.junit.Assert;
import org.junit.Test;
import org.wikidata.wdtk.dumpfiles.constraint.template.Template;
import org.wikidata.wdtk.dumpfiles.constraint.template.TemplateParser;
import org.wikidata.wdtk.dumpfiles.constraint.template.TemplateScanner;

public class TemplateParserTest {

	@Test
	public void testParser() {
		TemplateParser parser = new TemplateParser();

		Template template0 = parser.parse("P6",
				"{{Constraint:Type|class=Q1048835|relation=instance}}");
		Assert.assertEquals("P6", template0.getPage());
		Assert.assertEquals("Constraint:Type", template0.getId());
		Assert.assertEquals("Q1048835", template0.get("class"));
		Assert.assertEquals("instance", template0.get("relation"));
		Assert.assertEquals(2, template0.getParameters().size());

		Template template1 = parser.parse("P6",
				"{{Constraint:Value type|class=Q5|relation=instance}}");
		Assert.assertEquals("P6", template1.getPage());
		Assert.assertEquals("Constraint:Value type", template1.getId());
		Assert.assertEquals("Q5", template1.getParameters().get("class"));
		Assert.assertEquals("instance",
				template1.getParameters().get("relation"));
		Assert.assertEquals(2, template1.getParameters().size());

		Template template2 = parser.parse("P6",
				"{{Constraint:Target required claim|property=P21}}");
		Assert.assertEquals("P6", template2.getPage());
		Assert.assertEquals("Constraint:Target required claim",
				template2.getId());
		Assert.assertEquals("P21", template2.getParameters().get("property"));
		Assert.assertEquals(1, template2.getParameters().size());

		Template template3 = parser
				.parse("P21",
						"{{Constraint:One of|values={{Q|6581097}}, {{Q|6581072}}, {{Q|1097630}}, {{Q|44148}}, {{Q|43445}}, {{Q|1052281}}, {{Q|2449503}}, {{Q|48270}}, {{Q|1399232}}, {{Q|3277905}}, {{Q|746411}}, {{Q|350374}}, {{Q|660882}}}}");
		Assert.assertEquals("P21", template3.getPage());
		Assert.assertEquals("Constraint:One of", template3.getId());
		Assert.assertEquals(
				"{{Q|6581097}}, {{Q|6581072}}, {{Q|1097630}}, {{Q|44148}}, {{Q|43445}}, {{Q|1052281}}, {{Q|2449503}}, {{Q|48270}}, {{Q|1399232}}, {{Q|3277905}}, {{Q|746411}}, {{Q|350374}}, {{Q|660882}}",
				template3.get("values"));
		Assert.assertEquals(1, template3.getParameters().size());

		Template template4 = parser.parse("P814",
				"{{Constraint:Item|property=P17||exceptions={{Q|3593529}}}}");
		Assert.assertEquals("P814", template4.getPage());
		Assert.assertEquals("Constraint:Item", template4.getId());
		Assert.assertEquals("P17", template4.get("property"));
		Assert.assertEquals("{{Q|3593529}}", template4.get("exceptions"));
		Assert.assertEquals(2, template4.getParameters().size());

		Template template5 = parser
				.parse("P31",
						"{{Constraint:Target required claim|property=P279|exceptions ={{Q|35120}}}}");
		Assert.assertEquals("P31", template5.getPage());
		Assert.assertEquals("Constraint:Target required claim",
				template5.getId());
		Assert.assertEquals("P279", template5.get("property"));
		Assert.assertEquals("{{Q|35120}}", template5.get("exceptions"));
		Assert.assertEquals(2, template5.getParameters().size());

		Template template6 = parser
				.parse("P279",
						"{{Constraint:Target required claim|property=P279|exceptions={{Q|35120}}, {{Q|14897293}}}}");
		Assert.assertEquals("P279", template6.getPage());
		Assert.assertEquals("Constraint:Target required claim",
				template6.getId());
		Assert.assertEquals("P279", template6.get("property"));
		Assert.assertEquals("{{Q|35120}}, {{Q|14897293}}",
				template6.get("exceptions"));
		Assert.assertEquals(2, template6.getParameters().size());

		TemplateScanner scanner = new TemplateScanner();
		List<String> list = scanner
				.getTemplates("{{Constraint:One of|values={{Q|6581097}}, {{Q|6581072}}, {{Q|1097630}}, <!-- more values -->{{Q|44148}}, {{Q|43445}}, {{Q|1052281}}, {{Q|2449503}}, {{Q|48270}}, {{Q|1399232}}, {{Q|3277905}}, {{Q|746411}}, {{Q|350374}}, {{Q|660882}}}}");
		Assert.assertEquals(1, list.size());

		Template template3b = parser.parse("P21", list.get(0));
		Assert.assertEquals("P21", template3b.getPage());
		Assert.assertEquals("Constraint:One of", template3b.getId());
		Assert.assertEquals(
				"{{Q|6581097}}, {{Q|6581072}}, {{Q|1097630}}, {{Q|44148}}, {{Q|43445}}, {{Q|1052281}}, {{Q|2449503}}, {{Q|48270}}, {{Q|1399232}}, {{Q|3277905}}, {{Q|746411}}, {{Q|350374}}, {{Q|660882}}",
				template3b.get("values"));
		Assert.assertEquals(1, template3b.getParameters().size());

	}

}
