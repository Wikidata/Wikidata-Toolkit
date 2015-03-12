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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.dumpfiles.DumpProcessingController;
import org.wikidata.wdtk.dumpfiles.constraint.builder.ConstraintBuilderConstant;
import org.wikidata.wdtk.dumpfiles.constraint.builder.ConstraintMainBuilder;
import org.wikidata.wdtk.dumpfiles.constraint.format.Owl2FunctionalRendererFormat;
import org.wikidata.wdtk.dumpfiles.constraint.format.RdfRendererFormat;
import org.wikidata.wdtk.dumpfiles.constraint.format.RendererFormat;
import org.wikidata.wdtk.dumpfiles.constraint.format.StringResource;
import org.wikidata.wdtk.dumpfiles.constraint.model.Constraint;
import org.wikidata.wdtk.dumpfiles.constraint.renderer.ConstraintMainRenderer;
import org.wikidata.wdtk.dumpfiles.constraint.template.Template;

/**
 * An object of this class is the entry point to process a dump file a returns
 * the representation of constraints.
 * 
 * @author Julian Mendez
 * 
 */
public class PropertyConstraintDumpProcessor {

	static final Logger logger = LoggerFactory
			.getLogger(PropertyConstraintDumpProcessor.class);

	public static final String DEFAULT_FILE_NAME = "constraints";
	public static final String OWL_FILE_EXTENSION = ".owl";
	public static final String RDF_FILE_EXTENSION = ".rdf";
	public static final String WIKIDATAWIKI = "wikidatawiki";

	/**
	 * This is the entry point for this class.
	 * 
	 * @param args
	 *            arguments
	 * @throws IOException
	 *             when something went wrong during input/output
	 */
	public static void main(String[] args) throws IOException {
		(new PropertyConstraintDumpProcessor()).run(args);
	}

	/**
	 * Constructs a new property constraint dump processor.
	 */
	public PropertyConstraintDumpProcessor() {
	}

	/**
	 * Returns a possibly empty sublist of templates that are also constraints.
	 * 
	 * @param list
	 *            list of templates
	 * @return a possibly empty sublist of templates that are also constraints
	 */
	List<Template> getConstraintTemplates(List<Template> list) {
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

	/**
	 * Returns a string representation of a map from property to list of
	 * templates.
	 * 
	 * @param templateMap
	 *            map
	 * @return a string representation of a map from property to list of
	 *         templates
	 */
	String getConstraintTemplatesString(
			Map<PropertyIdValue, List<Template>> templateMap) {

		List<PropertyIdValue> listOfProperties = getListOfProperties(templateMap
				.keySet());
		StringBuilder strb = new StringBuilder();
		for (PropertyIdValue constrainedProperty : listOfProperties) {
			List<Template> templates = getConstraintTemplates(templateMap
					.get(constrainedProperty));
			strb.append(constrainedProperty.getId());
			strb.append("=");
			strb.append(StringResource.escapeChars(templates.toString()));
			strb.append("\n");
		}
		return strb.toString();
	}

	/**
	 * Returns a sorted list of property identifiers.
	 * 
	 * @param collectionOfProperties
	 *            a collection of property identifiers
	 * @return a sorted list of property identifiers
	 */
	List<PropertyIdValue> getListOfProperties(
			Collection<PropertyIdValue> collectionOfProperties) {
		List<PropertyIdValue> list = new ArrayList<PropertyIdValue>();
		list.addAll(collectionOfProperties);
		Collections.sort(list, new Comparator<PropertyIdValue>() {

			@Override
			public int compare(PropertyIdValue prop0, PropertyIdValue prop1) {
				int ret = prop0.toString().compareTo(prop1.toString());
				try {
					int num0 = Integer.parseInt(prop0.getId().substring(1));
					int num1 = Integer.parseInt(prop1.getId().substring(1));
					ret = num0 - num1;
				} catch (NumberFormatException e) {
				}
				return ret;
			}

		});
		return list;
	}

	/**
	 * Processes the dump.
	 * 
	 * @param controller
	 *            dump processing controller
	 * @param rendererFormats
	 *            list of formats
	 * @throws IOException
	 *             when something went wrong during input/output
	 */
	public void processDump(DumpProcessingController controller,
			List<RendererFormat> rendererFormats) throws IOException {

		PropertyTalkTemplateMwRevisionProcessor propertyTalkTemplateProcessor = new PropertyTalkTemplateMwRevisionProcessor();
		controller.registerMwRevisionProcessor(propertyTalkTemplateProcessor,
				null, true);

		controller.processMostRecentMainDump();

		start(rendererFormats);

		logger.info(getConstraintTemplatesString(propertyTalkTemplateProcessor
				.getMap()));

		processTemplates(propertyTalkTemplateProcessor.getMap(),
				rendererFormats);

		finish(rendererFormats);
	}

	/**
	 * Starts the output.
	 * 
	 * @param rendererFormats
	 *            formats
	 */
	public void start(List<RendererFormat> rendererFormats) {
		for (RendererFormat rendererFormat : rendererFormats) {
			rendererFormat.start();
		}
	}

	/**
	 * Processes the templates.
	 * 
	 * @param templateMap
	 * @param rendererFormats
	 * @throws IOException
	 */
	public void processTemplates(
			Map<PropertyIdValue, List<Template>> templateMap,
			List<RendererFormat> rendererFormats) throws IOException {
		ConstraintMainBuilder parser = new ConstraintMainBuilder();
		List<PropertyIdValue> listOfProperties = getListOfProperties(templateMap
				.keySet());
		for (PropertyIdValue constrainedProperty : listOfProperties) {

			List<Template> originalTemplates = templateMap
					.get(constrainedProperty);

			for (Template template : originalTemplates) {

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

	/**
	 * Finishes the output.
	 * 
	 * @param rendererFormats
	 *            formats
	 */
	public void finish(List<RendererFormat> rendererFormats) {
		for (RendererFormat rendererFormat : rendererFormats) {
			rendererFormat.finish();
		}
	}

	/**
	 * Configures logging.
	 */
	public static void configureLogging() {
		ConsoleAppender consoleAppender = new ConsoleAppender();
		String pattern = "%d{yyyy-MM-dd HH:mm:ss} %-5p - %m%n";
		consoleAppender.setLayout(new PatternLayout(pattern));
		consoleAppender.setThreshold(Level.INFO);
		consoleAppender.activateOptions();
		org.apache.log4j.Logger.getRootLogger().addAppender(consoleAppender);
	}

	/**
	 * This method is the main entry point for the execution to create the
	 * output files.
	 * 
	 * @param args
	 *            arguments sent from the console
	 * @throws IOException
	 *             when something went wrong during input/output
	 */
	public void run(String[] args) throws IOException {
		configureLogging();
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

		DumpProcessingController controller = new DumpProcessingController(
				WIKIDATAWIKI);

		// set offline mode true to read only offline dumps
		// controller.setOfflineMode(true);

		processDump(controller, rendererFormats);

		owl2FunctionalOutput.flush();
		rdfOutput.flush();
		owl2FunctionalOutput.close();
		rdfOutput.close();
	}

}
