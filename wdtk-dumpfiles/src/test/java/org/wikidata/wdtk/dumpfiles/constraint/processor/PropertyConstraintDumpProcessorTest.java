package org.wikidata.wdtk.dumpfiles.constraint.processor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.dumpfiles.DumpProcessingController;
import org.wikidata.wdtk.dumpfiles.constraint.format.Owl2FunctionalRendererFormat;
import org.wikidata.wdtk.dumpfiles.constraint.format.RdfRendererFormat;
import org.wikidata.wdtk.dumpfiles.constraint.format.RendererFormat;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintTestHelper;
import org.wikidata.wdtk.dumpfiles.constraint.renderer.ConstraintRendererTestHelper;
import org.wikidata.wdtk.dumpfiles.constraint.template.Template;
import org.wikidata.wdtk.dumpfiles.constraint.template.TemplateParser;
import org.wikidata.wdtk.util.DirectoryManagerFactory;
import org.wikidata.wdtk.util.DirectoryManagerImpl;

/*
 * #%L
 * Wikidata Toolkit RDF
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
 * Test class for {@link PropertyConstraintDumpProcessor}.
 * 
 * @author Julian Mendez
 *
 */
public class PropertyConstraintDumpProcessorTest {

	final String CONSTRAINTS_OWL = "translation/owl/constraints.owl";
	final String CONSTRAINTS_RDF = "translation/rdf/constraints.rdf";

	final String RESOURCES_PATH = "src/test/resources";

	final String TEMPLATE_31_0 = "{{Constraint:Target required claim|exceptions={{Q|35120}}|property=P279}}";
	final String TEMPLATE_31_1 = "{{Constraint:Conflicts with|list={{P|31}}: {{Q|8441}}, {{Q|467}}, {{Q|6581097}}, {{Q|6581072}}|mandatory=true}}";
	final String TEMPLATE_279_0 = "{{Constraint:Target required claim|exceptions={{Q|35120}}|property=P279}}";
	final String TEMPLATE_40_0 = "{{Constraint:Conflicts with|group property=P31|list={{P|31}}: {{Q|4167410}}, {{Q|101352}}, {{Q|12308941}},"
			+ " {{Q|11879590}}, {{Q|3409032}}, {{Q|202444}}, {{Q|577}}|mandatory=true}}";
	final String TEMPLATE_40_1 = "{{Constraint:Value type|class=Q215627|relation=instance}}";
	final String TEMPLATE_40_2 = "{{Constraint:Type|class=Q215627|relation=instance}}";
	final String TEMPLATE_40_3 = "{{Constraint:Target required claim|property=P21}}";
	final String TEMPLATE_40_4 = "{{Constraint:Item|property=P21}}";
	final String TEMPLATE_6_0 = "{{Constraint:Type|class=Q1048835|relation=instance}}";
	final String TEMPLATE_6_1 = "{{Constraint:Value type|class=Q5|exceptions={{Q|39}}|relation=instance}}";
	final String TEMPLATE_6_2 = "{{Constraint:Target required claim|property=P21}}";
	final String TEMPLATE_30_1 = "{{Constraint:One of|mandatory=true|values={{Q|46}}, {{Q|48}}, {{Q|15}}, {{Q|49}}, {{Q|18}}, {{Q|51}}, "
			+ "{{Q|3960}}, {{Q|5401}}, {{Q|538}}, {{Q|27611}}, {{Q|828}}, {{Q|664609}}}}";
	final String TEMPLATE_NO_CONSTRAINT = "{{Property documentation\n }}";

	public PropertyConstraintDumpProcessorTest() {
	}

	@Before
	public void setUp() throws Exception {
		DirectoryManagerFactory.setDirectoryManagerClass(DirectoryManagerImpl.class);
	}

	@Test
	public void testGetConstraintTemplates() {
		List<Template> expected = new ArrayList<Template>();
		TemplateParser parser = new TemplateParser();
		expected.add(parser.parse(TEMPLATE_6_0));
		expected.add(parser.parse(TEMPLATE_6_1));
		expected.add(parser.parse(TEMPLATE_6_2));
		expected.add(parser.parse(TEMPLATE_30_1));

		List<Template> input = new ArrayList<Template>();
		input.add(parser.parse(TEMPLATE_NO_CONSTRAINT));
		input.addAll(expected);
		PropertyConstraintDumpProcessor processor = new PropertyConstraintDumpProcessor();
		List<Template> obtained = processor.getConstraintTemplates(input);
		Assert.assertEquals(expected, obtained);
	}

	private Map<PropertyIdValue, List<Template>> getMapOfProperties() {
		TemplateParser parser = new TemplateParser();

		Map<PropertyIdValue, List<Template>> ret = new HashMap<PropertyIdValue, List<Template>>();

		List<Template> templates0 = new ArrayList<Template>();
		templates0.add(parser.parse(TEMPLATE_31_0));
		templates0.add(parser.parse(TEMPLATE_31_1));
		ret.put(ConstraintTestHelper.getPropertyIdValue("P31"), templates0);

		List<Template> templates1 = new ArrayList<Template>();
		templates1.add(parser.parse(TEMPLATE_279_0));
		ret.put(ConstraintTestHelper.getPropertyIdValue("P279"), templates1);

		List<Template> templates2 = new ArrayList<Template>();
		templates2.add(parser.parse(TEMPLATE_40_0));
		templates2.add(parser.parse(TEMPLATE_40_1));
		templates2.add(parser.parse(TEMPLATE_40_2));
		templates2.add(parser.parse(TEMPLATE_40_3));
		templates2.add(parser.parse(TEMPLATE_40_4));
		ret.put(ConstraintTestHelper.getPropertyIdValue("P40"), templates2);

		return ret;
	}

	@Test
	public void testGetConstraintTemplatesString() {
		PropertyConstraintDumpProcessor processor = new PropertyConstraintDumpProcessor();
		String expected = "P31=[" + TEMPLATE_31_0 + ", " + TEMPLATE_31_1
				+ "]\n" + "P40=[" + TEMPLATE_40_0 + ", " + TEMPLATE_40_1 + ", "
				+ TEMPLATE_40_2 + ", " + TEMPLATE_40_3 + ", " + TEMPLATE_40_4
				+ "]\n" + "P279=[" + TEMPLATE_279_0 + "]\n";
		String actual = processor
				.getConstraintTemplatesString(getMapOfProperties());
		Assert.assertEquals(expected, actual);
	}

	private List<PropertyIdValue> getListOfProperties(
			Collection<Integer> propertyIds) {
		List<PropertyIdValue> ret = new ArrayList<PropertyIdValue>();
		for (Integer propertyId : propertyIds) {
			ret.add(ConstraintTestHelper.getPropertyIdValue("P" + propertyId));
		}
		return ret;
	}

	@Test
	public void testGetListOfProperties() {
		List<Integer> list = Arrays.asList(36, 38, 412, 1088, 413, 1123, 141,
				105, 1031, 240);
		List<PropertyIdValue> input = getListOfProperties(list);
		Collections.sort(list);
		List<PropertyIdValue> expected = getListOfProperties(list);
		PropertyConstraintDumpProcessor processor = new PropertyConstraintDumpProcessor();
		List<PropertyIdValue> actual = processor.getListOfProperties(input);
		Assert.assertEquals(expected, actual);
	}

	private void processDump(List<RendererFormat> rendererFormats)
			throws IOException {
		PropertyConstraintDumpProcessor processor = new PropertyConstraintDumpProcessor();
		DumpProcessingController controller = new DumpProcessingController(
				PropertyConstraintDumpProcessor.WIKIDATAWIKI);
		controller.setOfflineMode(true);
		controller.setDownloadDirectory(RESOURCES_PATH);
		// PropertyConstraintDumpProcessor.configureLogging();
		processor.processDump(controller, rendererFormats);
	}

	/**
	 * This method tests the processing of a dump file.
	 * <p>
	 * This main purpose of this method is not to test the correctness of all
	 * translations but to test that it is possible to do an offline processing
	 * of a given dump file.
	 * 
	 * @throws IOException
	 *             when something went wrong with the input/output
	 * @throws RDFHandlerException
	 *             when something went wrong constructing an RDF model
	 * @throws RDFParseException
	 *             when something went wrong constructing an RDF model
	 */
	@Test
	public void testProcessDump() throws IOException, RDFParseException,
			RDFHandlerException {
		StringWriter owl2FunctionalOutput = new StringWriter();
		ByteArrayOutputStream rdfOutput = new ByteArrayOutputStream();

		List<RendererFormat> rendererFormats = new ArrayList<RendererFormat>();
		rendererFormats.add(new Owl2FunctionalRendererFormat(
				owl2FunctionalOutput));
		rendererFormats.add(new RdfRendererFormat(rdfOutput));

		processDump(rendererFormats);

		owl2FunctionalOutput.flush();
		rdfOutput.flush();

		String expectedOwl2Functional = ConstraintRendererTestHelper
				.getResourceFromFile(CONSTRAINTS_OWL);
		String actualOwl2Functional = owl2FunctionalOutput.getBuffer()
				.toString();
		Assert.assertEquals(expectedOwl2Functional, actualOwl2Functional);

		String expectedRdfStr = ConstraintRendererTestHelper
				.getResourceFromFile(CONSTRAINTS_RDF);
		String actualRdfStr = new String(rdfOutput.toByteArray());
		ConstraintRendererTestHelper.assertEqualsRdf(expectedRdfStr,
				actualRdfStr);

		owl2FunctionalOutput.close();
		rdfOutput.close();
	}

}
