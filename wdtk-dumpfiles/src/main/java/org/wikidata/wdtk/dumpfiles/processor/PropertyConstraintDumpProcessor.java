package org.wikidata.wdtk.dumpfiles.processor;

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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.dumpfiles.DumpContentType;
import org.wikidata.wdtk.dumpfiles.DumpProcessingController;
import org.wikidata.wdtk.dumpfiles.constraint.Constraint;
import org.wikidata.wdtk.dumpfiles.parser.constraint.ConstraintMainParser;
import org.wikidata.wdtk.dumpfiles.parser.constraint.ConstraintParserConstant;
import org.wikidata.wdtk.dumpfiles.parser.template.Template;
import org.wikidata.wdtk.dumpfiles.renderer.constraint.ConstraintMainRenderer;
import org.wikidata.wdtk.dumpfiles.renderer.format.Owl2FunctionalRendererFormat;
import org.wikidata.wdtk.dumpfiles.renderer.format.RendererFormat;

/**
 * 
 * @author Julian Mendez
 * 
 */
public class PropertyConstraintDumpProcessor {

	static final Logger logger = LoggerFactory
			.getLogger(PropertyConstraintDumpProcessor.class);

	public static final String DEFAULT_DUMP_DATE = "20140331";
	public static final String DEFAULT_FILE_NAME = "constraints.owl";
	public static final String WIKIDATAWIKI = "wikidatawiki";

	public static void main(String[] args) throws IOException {
		(new PropertyConstraintDumpProcessor()).run(args);
	}

	public String escapeChars(String str) {
		return str.replaceAll("&", "&amp;").replaceAll("\"", "&quot;")
				.replaceAll("<", "&lt;").replaceAll("'", "&apos;")
				.replaceAll("\n", "  ");
	}

	private List<Template> getConstraintTemplates(List<Template> list) {
		ConstraintMainParser mainParser = new ConstraintMainParser();
		List<Template> ret = new ArrayList<Template>();
		for (Template template : list) {
			String templateId = mainParser.normalize(template.getId());
			String prefix = mainParser
					.normalize(ConstraintParserConstant.T_CONSTRAINT);
			if (templateId.startsWith(prefix)) {
				ret.add(template);
			}
		}
		return ret;
	}

	public void printConstraintTemplates(
			Map<String, List<Template>> templateMap, BufferedWriter output,
			RendererFormat rendererFormat) throws IOException {
		for (String key : templateMap.keySet()) {
			List<Template> templates = getConstraintTemplates(templateMap
					.get(key));
			String statement = rendererFormat.aAnnotationComment(key,
					escapeChars(templates.toString()));
			output.write(statement);
			output.newLine();
		}
		output.flush();
	}

	void printLines(List<String> lines, BufferedWriter output)
			throws IOException {
		if (lines != null) {
			for (String line : lines) {
				output.write(line);
				output.newLine();
			}
		}
		output.flush();
	}

	public void processDumps(BufferedWriter output) throws IOException {
		DumpProcessingController controller = new DumpProcessingController(
				WIKIDATAWIKI);

		// set offline mode true to read only offline dumps
		// controller.setOfflineMode(true);

		RendererFormat rendererFormat = new Owl2FunctionalRendererFormat();

		PropertyTalkTemplateMwRevisionProcessor propertyTalkTemplateProcessor = new PropertyTalkTemplateMwRevisionProcessor();
		controller.registerMwRevisionProcessor(propertyTalkTemplateProcessor,
				null, true);

		controller.processAllDumps(DumpContentType.CURRENT, DEFAULT_DUMP_DATE,
				DEFAULT_DUMP_DATE);

		output.write(rendererFormat.getStart());
		printConstraintTemplates(propertyTalkTemplateProcessor.getMap(),
				output, rendererFormat);
		processTemplates(propertyTalkTemplateProcessor.getMap(), output,
				rendererFormat);
		output.write(rendererFormat.getEnd());
	}

	public void processTemplates(Map<String, List<Template>> templateMap,
			BufferedWriter output, RendererFormat rendererFormat)
			throws IOException {
		ConstraintMainParser parser = new ConstraintMainParser();
		ConstraintMainRenderer renderer = new ConstraintMainRenderer(
				rendererFormat);
		for (String key : templateMap.keySet()) {
			output.newLine();
			List<Template> templates = templateMap.get(key);
			for (Template template : templates) {

				Constraint constraint = null;
				try {
					constraint = parser.parse(template);
				} catch (Exception e) {
					System.out.println("Exception while parsing " + key);
					System.out.println("Template: " + template.toString());
					e.printStackTrace();
				}

				List<String> owlLines = null;
				try {
					if (constraint != null) {
						owlLines = constraint.accept(renderer);
					}
				} catch (Exception e) {
					System.out.println("Exception while rendering " + key);
					System.out.println("Template: " + template.toString());
					System.out.println("Constraint: " + constraint.toString());
					e.printStackTrace();
				}

				printLines(owlLines, output);
			}
		}
	}

	public void run(String[] args) throws IOException {
		ExampleHelpers.configureLogging();
		String fileName = DEFAULT_FILE_NAME;
		if (args.length > 0) {
			fileName = args[0];
		}
		BufferedWriter output = new BufferedWriter(new FileWriter(fileName));
		processDumps(output);
		output.close();
	}

}
