package org.wikidata.wdtk.dumpfiles.constraint.processor;

/*
 * #%L
 * Wikidata Toolkit Dump File Handling
 * %%
 * Copyright (C) 2014 - 2015 Wikidata Toolkit Developers
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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.dumpfiles.constraint.builder.ConstraintMainBuilder;
import org.wikidata.wdtk.dumpfiles.constraint.format.Owl2FunctionalRendererFormat;
import org.wikidata.wdtk.dumpfiles.constraint.format.RendererFormat;
import org.wikidata.wdtk.dumpfiles.constraint.model.Constraint;
import org.wikidata.wdtk.dumpfiles.constraint.renderer.ConstraintMainRenderer;
import org.wikidata.wdtk.dumpfiles.constraint.template.Template;
import org.wikidata.wdtk.dumpfiles.constraint.template.TemplateParser;

/**
 * An object of this class has a list of translation prototypes. They are simple
 * examples of how each constraint is translated.
 * 
 * @author Julian Mendez
 *
 */
public class TranslationPrototypeList {

	public static final String DEFAULT_URI_PREFIX = "http://";

	final List<Constraint> prototypes = new ArrayList<Constraint>();

	/**
	 * Constructs a new list of translation prototypes.
	 */
	public TranslationPrototypeList() {
		add("P1", "{{Constraint:Single value}}");
		add("P30", "{{Constraint:Unique value}}");
		add("P1", "{{Constraint:Format | pattern=str}}");
		add("P30", "{{Constraint:One of | values=q1,      q1000}}");
		add("P1110", "{{Constraint:One of | values=1,      1000}}");
		add("P220", "{{Constraint:One of | values=a,      z}}");
		add("P1", "{{Constraint:Target required claim | property=p2}}");
		add("P1",
				"{{Constraint:Target required claim | property=p2 | item=q1}}");
		add("P1", "{{Constraint:Item | property=p2}}");
		add("P1", "{{Constraint:Item | property=p2 | item=q1}}");
		add("P1", "{{Constraint:Item | items=q1,      q1000}}");
		add("P1", "{{Constraint:Type | class=q1 | relation=instance}}");
		add("P1", "{{Constraint:Type | class=q1 | relation=subclass}}");
		add("P1", "{{Constraint:Value type | class=q1 | relation=instance}}");
		add("P1", "{{Constraint:Value type | class=q1 | relation=subclass}}");
		add("P1110", "{{Constraint:Range | min=1 | max=1000}}");
		add("P580", "{{Constraint:Range | min=1500 | max=now}}");
		add("P1", "{{Constraint:Multi value}}");
		add("P1", "{{Constraint:Conflicts with | list=p2}}");
		add("P1", "{{Constraint:Conflicts with | list=p2 : q1}}");
	}

	/**
	 * Returns a new constraint based on the property and the template.
	 * 
	 * @param propertyStr
	 *            constrained property
	 * @param templateStr
	 *            template
	 * @return a new constraint based on the property and the template
	 */
	Constraint createConstraint(String propertyStr, String templateStr) {
		PropertyIdValue constrainedProperty = (new DataObjectFactoryImpl())
				.getPropertyIdValue(propertyStr, DEFAULT_URI_PREFIX);
		Template template = (new TemplateParser()).parse(templateStr);
		return (new ConstraintMainBuilder()).parse(constrainedProperty,
				template);
	}

	/**
	 * Returns the translation of the given constraint.
	 * 
	 * @param constraint
	 *            constraint
	 * @return the translation of the given constraint
	 */
	public String render(Constraint constraint) {
		StringWriter writer = new StringWriter();
		RendererFormat rendererFormat = new Owl2FunctionalRendererFormat(writer);
		ConstraintMainRenderer renderer = new ConstraintMainRenderer(
				rendererFormat);
		constraint.accept(renderer);
		writer.flush();
		return writer.getBuffer().toString();
	}

	/**
	 * Adds the given constraint to the list of constraints.
	 * 
	 * @param propertyStr
	 *            constrained property
	 * @param templateStr
	 *            template
	 */
	void add(String propertyStr, String templateStr) {
		this.prototypes.add(createConstraint(propertyStr, templateStr));
	}

	/**
	 * Returns the prototypes, i.e. very simple examples to clarify how the
	 * rules work.
	 * 
	 * @return the prototypes
	 */
	public List<Constraint> getPrototypes() {
		return Collections.unmodifiableList(this.prototypes);
	}

	@Override
	public String toString() {
		StringBuilder sbuf = new StringBuilder();
		for (Constraint prototype : this.prototypes) {
			sbuf.append(prototype.getConstrainedProperty().getId());
			sbuf.append(" : ");
			sbuf.append(prototype.getTemplate());
			sbuf.append(" -> \n");
			sbuf.append(render(prototype));
			sbuf.append("\n");
		}
		return sbuf.toString();
	}

}
