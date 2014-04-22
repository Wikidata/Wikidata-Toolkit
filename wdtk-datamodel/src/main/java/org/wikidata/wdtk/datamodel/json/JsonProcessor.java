package org.wikidata.wdtk.datamodel.json;

/*
 * #%L
 * Wikidata Toolkit Data Model
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

import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;

/**
 * This class provides a {@link EntityDocumentProcessor} for serialize
 * {@link ItemDocument} and {@link PropertyDocument} objects in a Json format
 * compatible with the Wikibase Web API Json format. You should use this class
 * with {@linkJsonSerializer}.
 * 
 * @author Michael Günther
 * 
 */
public class JsonProcessor implements EntityDocumentProcessor {

	static final Logger logger = LoggerFactory.getLogger(JsonProcessor.class);

	final JsonConverter converter = new JsonConverter();

	OutputStream out;
	Boolean atFirst;

	public JsonProcessor(OutputStream out) {
		this.out = out;
		atFirst = true;
	}

	public void setOutput(OutputStream out) {
		this.out = out;
	}

	public OutputStream getOutput() {
		return out;
	}

	/**
	 * This function resets the processor. Json is not a flat format. So it is
	 * necessary to inform the processor if a new serialisation was initiated.
	 */
	public void restartProcess() {
		atFirst = true;
	}

	@Override
	public void processItemDocument(ItemDocument itemDocument) {
		StringBuilder builder = new StringBuilder();
		if (!atFirst) {
			builder.append(",");
		} else {
			atFirst = false;
		}
		builder.append("\"");
		builder.append(itemDocument.getItemId().getId());
		builder.append(":");
		builder.append(converter.getJsonForItemDocument(itemDocument));
		try {
			out.write(builder.toString().getBytes());
		} catch (IOException e) {
			JsonProcessor.logger.error(e.toString());
		}
	}

	@Override
	public void processPropertyDocument(PropertyDocument propertyDocument) {
		StringBuilder builder = new StringBuilder();
		if (!atFirst) {
			builder.append(",");
		} else {
			atFirst = false;
		}
		builder.append("\"");
		builder.append(propertyDocument.getEntityId().getId());
		builder.append("\"");
		builder.append(":");
		builder.append(converter.getJsonForPropertyDocument(propertyDocument));
		try {
			out.write(builder.toString().getBytes());
		} catch (IOException e) {
			JsonProcessor.logger.error(e.toString());
		}
	}

	@Override
	public void finishProcessingEntityDocuments() {
		try {
			out.close();
		} catch (IOException e) {
			JsonProcessor.logger.error(e.toString());
		}
	}

}
