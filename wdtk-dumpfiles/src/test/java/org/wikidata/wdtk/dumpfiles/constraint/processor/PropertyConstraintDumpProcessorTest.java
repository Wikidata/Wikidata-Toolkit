package org.wikidata.wdtk.dumpfiles.constraint.processor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.wikidata.wdtk.dumpfiles.DumpProcessingController;
import org.wikidata.wdtk.dumpfiles.constraint.format.Owl2FunctionalRendererFormat;
import org.wikidata.wdtk.dumpfiles.constraint.format.RdfRendererFormat;
import org.wikidata.wdtk.dumpfiles.constraint.format.RendererFormat;

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

	final String RESOURCES_PATH = "src/test/resources";

	public PropertyConstraintDumpProcessorTest() {
	}

	@Test
	public void testEscapeChars() {
		PropertyConstraintDumpProcessor processor = new PropertyConstraintDumpProcessor();
		Assert.assertEquals("", processor.escapeChars(""));
		Assert.assertEquals("&lt;test>", processor.escapeChars("<test>"));
		Assert.assertEquals("&amp;lt;", processor.escapeChars("&lt;"));
		Assert.assertEquals("&quot;test&quot;",
				processor.escapeChars("\"test\""));
		Assert.assertEquals("unit  test", processor.escapeChars("unit\ntest"));
	}

	@Test
	public void testProcessDump() throws IOException {
		StringWriter owl2FunctionalOutput = new StringWriter();
		ByteArrayOutputStream rdfOutput = new ByteArrayOutputStream();

		List<RendererFormat> rendererFormats = new ArrayList<RendererFormat>();
		rendererFormats.add(new Owl2FunctionalRendererFormat(
				owl2FunctionalOutput));
		rendererFormats.add(new RdfRendererFormat(rdfOutput));

		PropertyConstraintDumpProcessor processor = new PropertyConstraintDumpProcessor();

		DumpProcessingController controller = new DumpProcessingController(
				PropertyConstraintDumpProcessor.WIKIDATAWIKI);
		controller.setOfflineMode(true);
		controller.setDownloadDirectory(RESOURCES_PATH);
		// PropertyConstraintDumpProcessor.configureLogging();
		processor.processDump(controller, rendererFormats);

		owl2FunctionalOutput.flush();
		rdfOutput.flush();
		owl2FunctionalOutput.close();
		rdfOutput.close();
	}

}
