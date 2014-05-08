package org.wikidata.wdtk.dumpfiles.processor;

import java.util.ArrayList;
import java.util.List;

import org.wikidata.wdtk.dumpfiles.parser.template.Template;
import org.wikidata.wdtk.dumpfiles.parser.template.TemplateParser;
import org.wikidata.wdtk.dumpfiles.parser.template.TemplateScanner;

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
 * This class expands some known complex templates into simpler ones.
 * 
 * @author Julian Mendez
 * 
 */
public class TemplateExpander {

	public static final String CONSTRAINT_PERSON = "Constraint:Person";

	public static final String CONSTRAINT_TAXON = "Constraint:Taxon";

	public static final String TEMPLATE_CONSTRAINT_PERSON = ""
			+ "{{Constraint:Type|class=Q215627|relation=instance}}"
			+ "\n{{Constraint:Item|property=P21}}"
			+ "\n{{Constraint:Item|property=P19}}"
			+ "\n{{Constraint:Item|property=P569}}" + "\n";

	public static final String TEMPLATE_CONSTRAINT_TAXON = ""
			+ "{{Constraint:Type|class=Q16521|relation=instance}}"
			+ "\n{{Constraint:Item|property=P225}}"
			+ "\n{{Constraint:Item|property=P171}}"
			+ "\n{{Constraint:Item|property=P105}}" + "\n";

	final TemplateScanner templateScanner = new TemplateScanner();
	final TemplateParser templateParser = new TemplateParser();

	public TemplateExpander() {
	}

	public List<Template> expand(String page, List<Template> list) {
		List<Template> ret = new ArrayList<Template>();
		for (Template template : list) {
			if (template.getId().equals(CONSTRAINT_PERSON)) {
				ret.addAll(expandTemplate(page, TEMPLATE_CONSTRAINT_PERSON));
			} else if (template.getId().equals(CONSTRAINT_TAXON)) {
				ret.addAll(expandTemplate(page, TEMPLATE_CONSTRAINT_TAXON));
			} else {
				ret.add(template);
			}
		}
		return ret;
	}

	public List<Template> expandTemplate(String page, String expansion) {
		List<Template> ret = new ArrayList<Template>();
		List<String> listOfTemplateStr = this.templateScanner
				.getTemplates(expansion);
		for (String templateStr : listOfTemplateStr) {
			ret.add(this.templateParser.parse(page, templateStr));
		}
		return ret;
	}

}
