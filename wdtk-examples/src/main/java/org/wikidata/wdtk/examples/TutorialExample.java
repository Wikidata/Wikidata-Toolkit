package org.wikidata.wdtk.examples;

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

import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.dumpfiles.DumpProcessingController;
import org.wikidata.wdtk.dumpfiles.MwRevision;
import org.wikidata.wdtk.dumpfiles.StatisticsMwRevisionProcessor;

/**
 * This example application applies an {@link EntityDocumentProcessor} to all
 * documents in a Wikidata dump file. By default, the EntityDocumentProcessor is
 * {@link TutorialDocumentProcessor}.
 * <p>
 * This application is based on the regular data exports provide by Wikidata. By
 * default, it will run in offline mode. This will only work if you already have
 * some dump downloaded before. The easiest way of doing this is to disable
 * offline mode in the source code; the program will then do the downloading for
 * you.
 *
 * @author Markus Kroetzsch
 *
 */
public class TutorialExample {

	public static void main(String[] args) {
		ExampleHelpers.configureLogging();

		// Controller object for processing dumps:
		DumpProcessingController dumpProcessingController = new DumpProcessingController(
				"wikidatawiki");
		// Work offline. Only works if you already have a dump downloaded
		dumpProcessingController.setOfflineMode(true);

		// Example processor for item and property documents:
		TutorialDocumentProcessor documentProcessor = new TutorialDocumentProcessor();

		dumpProcessingController.registerEntityDocumentProcessor(
				documentProcessor, MwRevision.MODEL_WIKIBASE_ITEM, true);
		dumpProcessingController.registerEntityDocumentProcessor(
				documentProcessor, MwRevision.MODEL_WIKIBASE_PROPERTY, true);

		// Another processor for statistics & time keeping:
		dumpProcessingController.registerMwRevisionProcessor(
				new StatisticsMwRevisionProcessor("statistics", 10000), null,
				true);

		// Run the processing:
		dumpProcessingController.processMostRecentMainDump();

		// Store the results:
		documentProcessor.storeResults();
	}
}
