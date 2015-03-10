package org.wikidata.wdtk.dumpfiles.constraint.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintTestHelper;
import org.wikidata.wdtk.dumpfiles.constraint.template.Template;
import org.wikidata.wdtk.dumpfiles.constraint.template.TemplateParser;

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

/**
 *
 * @author Julian Mendez
 *
 */
public class PropertyTalkTemplateMwRevisionProcessorTest {

	final String SITE_NAME = "Wikidata";
	final String BASE_URL = "http://www.wikidata.org/wiki/Wikidata:Main_Page";
	final Map<Integer, String> NAMESPACES = new TreeMap<Integer, String>();
	final String TEMPLATE_31_0 = "{{Constraint:Target required claim|property=P279|exceptions ={{Q|35120}}}}";
	final String TEMPLATE_31_1 = "{{Constraint:Conflicts with|list={{P|31}}: {{Q|8441}}, {{Q|467}}, {{Q|6581097}}, {{Q|6581072}}|mandatory=true}}";

	public PropertyTalkTemplateMwRevisionProcessorTest() {
		NAMESPACES.put(-2, "Media");
		NAMESPACES.put(-1, "Special");
		NAMESPACES.put(0, "");
		NAMESPACES.put(1, "Talk");
		NAMESPACES.put(2, "User");
		NAMESPACES.put(3, "User talk");
		NAMESPACES.put(4, "Wikidata");
		NAMESPACES.put(5, "Wikidata talk");
		NAMESPACES.put(6, "File");
		NAMESPACES.put(7, "File talk");
		NAMESPACES.put(8, "MediaWiki");
		NAMESPACES.put(9, "MediaWiki talk");
		NAMESPACES.put(10, "Template");
		NAMESPACES.put(11, "Template talk");
		NAMESPACES.put(12, "Help");
		NAMESPACES.put(13, "Help talk");
		NAMESPACES.put(14, "Category");
		NAMESPACES.put(15, "Category talk");
		NAMESPACES.put(120, "Property");
		NAMESPACES.put(121, "Property talk");
		NAMESPACES.put(122, "Query");
		NAMESPACES.put(123, "Query talk");
		NAMESPACES.put(828, "Module");
		NAMESPACES.put(829, "Module talk");
		NAMESPACES.put(1198, "Translations");
		NAMESPACES.put(1199, "Translations talk");
	}

	@Test
	public void testProcessRevision() {
		PropertyTalkTemplateMwRevisionProcessor processor = new PropertyTalkTemplateMwRevisionProcessor();
		processor.startRevisionProcessing(SITE_NAME, BASE_URL, NAMESPACES);
		MockMwRevision mwRevision = new MockMwRevision();
		processor.processRevision(mwRevision);
		Map<PropertyIdValue, List<Template>> map = processor.getMap();
		Assert.assertEquals(1, map.keySet().size());

		PropertyIdValue expectedProperty = ConstraintTestHelper
				.getPropertyIdValue("P31");
		PropertyIdValue actualProperty = map.keySet().iterator().next();
		Assert.assertEquals(expectedProperty, actualProperty);

		List<Template> expectedList = new ArrayList<Template>();
		TemplateParser parser = new TemplateParser();
		expectedList.add(parser.parse(TEMPLATE_31_0));
		expectedList.add(parser.parse(TEMPLATE_31_1));
		List<Template> actualList = map.get(actualProperty);
		Assert.assertEquals(expectedList, actualList);
	}

}
