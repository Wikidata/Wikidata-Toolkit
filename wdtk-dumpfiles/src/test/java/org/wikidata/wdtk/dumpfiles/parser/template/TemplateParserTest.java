package org.wikidata.wdtk.dumpfiles.parser.template;

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

public class TemplateParserTest {

	@Test
	public void testParser() {
		TemplateParser parser = new TemplateParser();

		Template template0 = parser.parse("P31",
				"{{Constraint:Type|class=Q1048835|relation=instance}}");
		Assert.assertEquals("Constraint:Type", template0.getId());
		Assert.assertEquals("Q1048835", template0.get("class"));
		Assert.assertEquals("instance", template0.get("relation"));
		Assert.assertEquals(2, template0.getParameters().size());

		Template template1 = parser.parse("P31",
				"{{Constraint:Value type|class=Q5|relation=instance}}");
		Assert.assertEquals("Constraint:Value type", template1.getId());
		Assert.assertEquals("Q5", template1.getParameters().get("class"));
		Assert.assertEquals("instance",
				template1.getParameters().get("relation"));
		Assert.assertEquals(2, template1.getParameters().size());

		Template template2 = parser.parse("P31",
				"{{Constraint:Target required claim|property=P21}}");
		Assert.assertEquals("Constraint:Target required claim",
				template2.getId());
		Assert.assertEquals("P21", template2.getParameters().get("property"));
		Assert.assertEquals(1, template2.getParameters().size());

		Template template3 = parser
				.parse("P31",
						"{{Constraint:One of|values={{Q|6581097}}, {{Q|6581072}}, {{Q|1097630}}, {{Q|44148}}, {{Q|43445}}, {{Q|1052281}}, {{Q|2449503}}, {{Q|48270}}, {{Q|1399232}}, {{Q|3277905}}, {{Q|746411}}, {{Q|350374}}, {{Q|660882}}}}");
		Assert.assertEquals("Constraint:One of", template3.getId());
		Assert.assertEquals(
				"{{Q|6581097}}, {{Q|6581072}}, {{Q|1097630}}, {{Q|44148}}, {{Q|43445}}, {{Q|1052281}}, {{Q|2449503}}, {{Q|48270}}, {{Q|1399232}}, {{Q|3277905}}, {{Q|746411}}, {{Q|350374}}, {{Q|660882}}",
				template3.get("values"));
		Assert.assertEquals(1, template3.getParameters().size());

	}

}
