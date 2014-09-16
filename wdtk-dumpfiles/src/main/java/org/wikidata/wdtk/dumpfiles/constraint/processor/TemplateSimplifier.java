package org.wikidata.wdtk.dumpfiles.constraint.processor;

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

import org.wikidata.wdtk.dumpfiles.constraint.builder.ConstraintBuilderConstant;
import org.wikidata.wdtk.dumpfiles.constraint.template.Template;
import org.wikidata.wdtk.dumpfiles.constraint.template.TemplateParser;

/**
 * This class models an object that simplifies a template.
 *
 * @author Julian Mendez
 *
 */
public class TemplateSimplifier {

	final List<Template> person = new ArrayList<Template>();
	final List<Template> taxon = new ArrayList<Template>();

	public TemplateSimplifier() {
		TemplateParser parser = new TemplateParser();

		this.person.add(parser
				.parse("{{Constraint:Type|class=Q215627|relation=instance}}"));
		this.person.add(parser.parse("{{Constraint:Item|property=P21}}"));
		this.person.add(parser.parse("{{Constraint:Item|property=P19}}"));
		this.person.add(parser.parse("{{Constraint:Item|property=P569}}"));

		this.taxon.add(parser
				.parse("{{Constraint:Type|class=Q16521|relation=instance}}"));
		this.taxon.add(parser.parse("{{Constraint:Item|property=P225}}"));
		this.taxon.add(parser.parse("{{Constraint:Item|property=P171}}"));
		this.taxon.add(parser.parse("{{Constraint:Item|property=P105}}"));
	}

	public List<Template> expandTemplates(Template origTemplate) {
		List<Template> ret = new ArrayList<Template>();
		if (origTemplate.getName().equalsIgnoreCase(
				ConstraintBuilderConstant.C_PERSON)) {
			ret.addAll(this.person);
		} else if (origTemplate.getName().equalsIgnoreCase(
				ConstraintBuilderConstant.C_TAXON)) {
			ret.addAll(this.taxon);
		} else {
			ret.add(origTemplate);
		}
		return ret;
	}

}
