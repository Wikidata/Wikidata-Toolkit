package org.wikidata.wdtk.clt;

/*
 * #%L
 * Wikidata Toolkit Command-line Tool
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import org.wikidata.wdtk.datamodel.interfaces.Sites;
import org.wikidata.wdtk.datamodel.json.JsonSerializer;
import org.wikidata.wdtk.dumpfiles.DumpProcessingController;
import org.wikidata.wdtk.dumpfiles.MwRevision;

public class JsonConfiguration extends OutputConfiguration {

	/**
	 * Constructor. See {@link OutputConfiguration} for more details.
	 * 
	 * @param conversionProperties
	 */
	public JsonConfiguration(ConversionProperties conversionProperties) {
		super(conversionProperties);
	}

	@Override
	public String getOutputFormat() {
		return "json";
	}

	/**
	 * Builds up a serializer for JSON.
	 * 
	 * @param conversionConfiguration
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Override
	public void setupSerializer(
			DumpProcessingController dumpProcessingController, Sites sites)
			throws FileNotFoundException, IOException {
		if (this.outputDestination.equals("")) {
			setDefaultDestination();
		}
		OutputStream outputStream = getCompressorOutputStream();

		// Create an object for managing the serialization process
		JsonSerializer serializer = new JsonSerializer(outputStream);

		// Subscribe to the most recent entity documents of type wikibase item
		// and property:
		dumpProcessingController.registerEntityDocumentProcessor(serializer,
				MwRevision.MODEL_WIKIBASE_ITEM, true);
		dumpProcessingController.registerEntityDocumentProcessor(serializer,
				MwRevision.MODEL_WIKIBASE_PROPERTY, true);
	}

	/**
	 * Sets a default value to output destination.
	 */
	void setDefaultDestination() {
		this.outputDestination = "WikidataDump.json";
	}

	@Override
	public void startSerializer() {
		// TODO Auto-generated method stub

	}

	@Override
	public void closeSerializer() {
		// TODO Auto-generated method stub

	}

}
