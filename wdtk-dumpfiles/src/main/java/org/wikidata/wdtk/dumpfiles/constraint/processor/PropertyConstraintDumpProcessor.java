package org.wikidata.wdtk.dumpfiles.constraint.processor;

/*
 * #%L
 * Wikidata Toolkit Examples
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

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.dumpfiles.DumpContentType;
import org.wikidata.wdtk.dumpfiles.DumpProcessingController;
import org.wikidata.wdtk.dumpfiles.constraint.builder.ConstraintBuilderConstant;
import org.wikidata.wdtk.dumpfiles.constraint.builder.ConstraintMainBuilder;
import org.wikidata.wdtk.dumpfiles.constraint.format.Owl2FunctionalRendererFormat;
import org.wikidata.wdtk.dumpfiles.constraint.format.RdfRendererFormat;
import org.wikidata.wdtk.dumpfiles.constraint.format.RendererFormat;
import org.wikidata.wdtk.dumpfiles.constraint.model.Constraint;
import org.wikidata.wdtk.dumpfiles.constraint.renderer.ConstraintMainRenderer;
import org.wikidata.wdtk.dumpfiles.constraint.template.Template;

/**
 * 
 * @author Julian Mendez
 * 
 */
public class PropertyConstraintDumpProcessor {

	static final Logger logger = LoggerFactory
			.getLogger(PropertyConstraintDumpProcessor.class);

	public static final String DEFAULT_DUMP_DATE = "20140526";
	public static final String DEFAULT_FILE_NAME = "constraints";
	public static final String OWL_FILE_EXTENSION = ".owl";
	public static final String RDF_FILE_EXTENSION = ".rdf";
	public static final String WIKIDATAWIKI = "wikidatawiki";

	public static void main(String[] args) throws IOException {
		(new PropertyConstraintDumpProcessor()).run(args);
	}

	public PropertyConstraintDumpProcessor() {
	}

	public String escapeChars(String str) {
		return str.replaceAll("&", "&amp;").replaceAll("\"", "&quot;")
				.replaceAll("<", "&lt;").replaceAll("'", "&apos;")
				.replaceAll("\n", "  ");
	}

	private List<Template> getConstraintTemplates(List<Template> list) {
		ConstraintMainBuilder mainParser = new ConstraintMainBuilder();
		List<Template> ret = new ArrayList<Template>();
		for (Template template : list) {
			String templateId = mainParser.normalize(template.getName());
			String prefix = mainParser
					.normalize(ConstraintBuilderConstant.T_CONSTRAINT);
			if (templateId.startsWith(prefix)) {
				ret.add(template);
			}
		}
		return ret;
	}

	public void processDumps(List<RendererFormat> rendererFormats)
			throws IOException {
		DumpProcessingController controller = new DumpProcessingController(
				WIKIDATAWIKI);

		// set offline mode true to read only offline dumps
		// controller.setOfflineMode(true);

		PropertyTalkTemplateMwRevisionProcessor propertyTalkTemplateProcessor = new PropertyTalkTemplateMwRevisionProcessor();
		controller.registerMwRevisionProcessor(propertyTalkTemplateProcessor,
				null, true);

		controller.processAllDumps(DumpContentType.CURRENT, DEFAULT_DUMP_DATE,
				DEFAULT_DUMP_DATE);

		start(rendererFormats);

		logger.info(getConstraintTemplates(
				propertyTalkTemplateProcessor.getMap(), rendererFormats));

		processTemplates(propertyTalkTemplateProcessor.getMap(),
				rendererFormats);

		finish(rendererFormats);
	}

	public void start(List<RendererFormat> rendererFormats) {
		for (RendererFormat rendererFormat : rendererFormats) {
			rendererFormat.start();
		}
	}

	public String getConstraintTemplates(
			Map<PropertyIdValue, List<Template>> templateMap,
			List<RendererFormat> rendererFormats) throws IOException {
		StringBuilder sb = new StringBuilder();
		for (PropertyIdValue constrainedProperty : templateMap.keySet()) {
			List<Template> templates = getConstraintTemplates(templateMap
					.get(constrainedProperty));
			sb.append(constrainedProperty.getId());
			sb.append(escapeChars(templates.toString()));
			sb.append("\n");
		}
		return sb.toString();
	}

	public void processTemplates(
			Map<PropertyIdValue, List<Template>> templateMap,
			List<RendererFormat> rendererFormats) throws IOException {
		ConstraintMainBuilder parser = new ConstraintMainBuilder();
		for (PropertyIdValue constrainedProperty : templateMap.keySet()) {

			List<Template> templates = templateMap.get(constrainedProperty);
			for (Template template : templates) {

				Constraint constraint = null;
				constraint = parser.parse(constrainedProperty, template);

				if (constraint != null) {
					for (RendererFormat rendererFormat : rendererFormats) {
						ConstraintMainRenderer renderer = new ConstraintMainRenderer(
								rendererFormat);
						constraint.accept(renderer);
					}
				}
			}
		}
	}

	public void finish(List<RendererFormat> rendererFormats) {
		for (RendererFormat rendererFormat : rendererFormats) {
			rendererFormat.finish();
		}
	}

	public void run(String[] args) throws IOException {
		ExampleHelpers.configureLogging();
		String fileName = DEFAULT_FILE_NAME;
		if (args.length > 0) {
			fileName = args[0];
		}
		FileWriter owl2FunctionalOutput = new FileWriter(fileName
				+ OWL_FILE_EXTENSION);
		FileOutputStream rdfOutput = new FileOutputStream(fileName
				+ RDF_FILE_EXTENSION);

		List<RendererFormat> rendererFormats = new ArrayList<RendererFormat>();
		rendererFormats.add(new Owl2FunctionalRendererFormat(
				owl2FunctionalOutput));
		rendererFormats.add(new RdfRendererFormat(rdfOutput));
		processDumps(rendererFormats);

		owl2FunctionalOutput.flush();
		rdfOutput.flush();
		owl2FunctionalOutput.close();
		rdfOutput.close();
	}

}
