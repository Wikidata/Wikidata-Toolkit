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

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@link TemplateScanner}.
 * 
 * @author Julian Mendez
 * 
 */
public class TemplateScannerTest {

	static final String text0 = "\n<!-- ignored -->{{Constraint:Type|class=Q1048835\n|relation=instance<!-- ignored -->}}"
			+ "\n{{Constraint:Value <!-- ignored \n \n -->type|class=Q5|relation<!---->=instance}}<!-- nothing here -->"
			+ "\n{<!--ignored --->{Constraint:Target required claim|property=P21}}"
			+ "\n{{Constraint:One of|values=<!-- ignored-->{{Q|6581097}}, {{Q|6581072}}, {{Q|1097630}}, {{Q|44148}}, {{Q|43445}}, {{Q|1052281}}, {{Q|2449503}}, {{Q|48270}}, {{Q|1399232}}, {{Q|3277905}}, {{Q|746411}}, {{Q|350374}}, {{Q|660882}}}}"
			+ "\n";
	static final String text1 = "== Constraint item ==\n"
			+ "<code><nowiki>{{</nowiki><nowiki>Constraint:Item|property=P107|item=Q386724|item2=Q215627|item3=Q43229}}</nowiki></code> (yes, organization is already there) ";
	static final String text2 = "== Constraint item ==\n"
			+ "<code><nowiki>{{Constraint:Item|property=P107|item=Q386724|item2=Q215627|item3=Q43229}}</nowiki></code> (yes, organization is already there) ";
	static final String text3 = "== Constraint item ==\n"
			+ "<code>&lt;nowiki>{{Constraint:Item|property=P107|item=Q386724|item2=Q215627|item3=Q43229}}</nowiki></code> (yes, organization is already there) ";

	@Test
	public void testScannerMultipleTemplates() {
		List<String> expected = new ArrayList<String>();
		expected.add("{{Constraint:Type|class=Q1048835\n|relation=instance}}");
		expected.add("{{Constraint:Value type|class=Q5|relation=instance}}");
		expected.add("{{Constraint:Target required claim|property=P21}}");
		expected.add("{{Constraint:One of|values={{Q|6581097}}, {{Q|6581072}}, {{Q|1097630}}, {{Q|44148}}, {{Q|43445}}, {{Q|1052281}}, {{Q|2449503}}, {{Q|48270}}, {{Q|1399232}}, {{Q|3277905}}, {{Q|746411}}, {{Q|350374}}, {{Q|660882}}}}");

		TemplateScanner scanner = new TemplateScanner();
		Assert.assertEquals(expected, scanner.getTemplates(text0));
	}

	@Test
	public void testScannerWithNowiki() {
		List<String> expected = new ArrayList<String>();

		TemplateScanner scanner = new TemplateScanner();
		Assert.assertEquals(expected, scanner.getTemplates(text1));
		Assert.assertEquals(expected, scanner.getTemplates(text2));

		expected.add("{{Constraint:Item|property=P107|item=Q386724|item2=Q215627|item3=Q43229}}");
		Assert.assertEquals(expected, scanner.getTemplates(text3));
	}

	@Test
	public void testScannerMultipleTemplatesMultipleLines() {
		String str = ""
				+ "{{Property documentation\n"
				+ "|description=FIPS code for US states (numeric or alpha) per former FIPS 5-2 standard, see {{Q|917824}}. See also: <br>\n"
				+ "**{{P|774}}\n"
				+ "**{{P|882}}\n"
				+ "**{{P|901}}\n"
				+ "|infobox parameter=\n"
				+ "|datatype=string\n"
				+ "|domain=places: US states and certain other associated areas\n"
				+ "|allowed values=\\d\\d or \\D\\D: see [[:en:Federal Information Processing Standard state code]]\n"
				+ "|suggested values=\n"
				+ "|source=[[en:Federal_Information_Processing_Standard_state_code]], http://www.census.gov/geo/reference/ansi_statetables.html\n"
				+ "|example= {{Q|173}} => \"AL\" and \"01\"\n"
				+ "|filter=\n"
				+ "|robot and gadget jobs=\n"
				+ "|proposed by=13\n"
				+ "}}\n"
				+ "\n"
				+ "\n"
				+ "{{Constraint:Format|pattern=<nowiki>\\d\\d|\\D\\D</nowiki>}}\n"
				+ "{{Constraint:Unique value}}\n"
				+ "{{Constraint:Item|property=P17|item=Q30|exceptions= {{Q|695}}, {{Q|702}}, {{Q|709}} }}\n"
				+ "{{Constraint:Item|property=P132|exceptions= {{Q|695}}, {{Q|702}}, {{Q|709}}, {{Q|16645}} }}\n";

		List<String> expected = new ArrayList<String>();
		expected.add("{{Property documentation\n"
				+ "|description=FIPS code for US states (numeric or alpha) per former FIPS 5-2 standard, see {{Q|917824}}. See also: <br>\n"
				+ "**{{P|774}}\n"
				+ "**{{P|882}}\n"
				+ "**{{P|901}}\n"
				+ "|infobox parameter=\n"
				+ "|datatype=string\n"
				+ "|domain=places: US states and certain other associated areas\n"
				+ "|allowed values=\\d\\d or \\D\\D: see [[:en:Federal Information Processing Standard state code]]\n"
				+ "|suggested values=\n"
				+ "|source=[[en:Federal_Information_Processing_Standard_state_code]], http://www.census.gov/geo/reference/ansi_statetables.html\n"
				+ "|example= {{Q|173}} => \"AL\" and \"01\"\n" + "|filter=\n"
				+ "|robot and gadget jobs=\n" + "|proposed by=13\n" + "}}");
		expected.add("{{Constraint:Format|pattern=<nowiki>\\d\\d|\\D\\D</nowiki>}}");
		expected.add("{{Constraint:Unique value}}");
		expected.add("{{Constraint:Item|property=P17|item=Q30|exceptions= {{Q|695}}, {{Q|702}}, {{Q|709}} }}");
		expected.add("{{Constraint:Item|property=P132|exceptions= {{Q|695}}, {{Q|702}}, {{Q|709}}, {{Q|16645}} }}");

		TemplateScanner scanner = new TemplateScanner();
		Assert.assertEquals(expected, scanner.getTemplates(str));
	}

}
