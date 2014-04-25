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
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentsSerializer;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;

/**
 * This class implements {@link EntityDocumentsSerializer} to provide a
 * JsonSerializer for {@link EntityDocument} objects.
 * 
 * @author Michael Günther
 * 
 */
public class JsonSerializer implements EntityDocumentsSerializer {

	static final Logger logger = LoggerFactory.getLogger(JsonSerializer.class);

	final JsonConverter converter = new JsonConverter();

	OutputStream out;
	Boolean atFirst;

	public JsonSerializer(OutputStream out) {
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

	/**
	 * Sends the json encoding to an OutputStream.
	 * 
	 * @param jsonDocument
	 * @param id
	 */
	void writeEntityDocument(String jsonDocument, String id) {
		StringBuilder builder = new StringBuilder();
		if (!atFirst) {
			builder.append(",");
		} else {
			atFirst = false;
		}
		builder.append("\"");
		builder.append(id);
		builder.append("\"");
		builder.append(":");
		builder.append(jsonDocument);
		builder.append("\n");
		try {
			out.write(builder.toString().getBytes(Charset.forName("UTF-8")));
		} catch (IOException e) {
			logger.error(e.toString());
		}
	}

	@Override
	public void processItemDocument(ItemDocument itemDocument) {
		writeEntityDocument(converter.getJsonForItemDocument(itemDocument)
				.toString(), itemDocument.getItemId().getId());
	}

	@Override
	public void processPropertyDocument(PropertyDocument propertyDocument) {
		writeEntityDocument(
				converter.getJsonForPropertyDocument(propertyDocument)
						.toString(), propertyDocument.getEntityId().getId());
	}

	@Override
	public void finishProcessingEntityDocuments() {
		// do nothing
	}

	@Override
	public void startSerialization() {
		restartProcess();
		try {
			out.write("{\"entities\": {\n".getBytes(Charset.forName("UTF-8")));
		} catch (IOException e) {
			JsonSerializer.logger.error(e.toString());
		}
	}

	@Override
	public void finishSerialization() {
		try {
			out.write("}}".getBytes(Charset.forName("UTF-8")));
		} catch (IOException e) {
			JsonSerializer.logger.error(e.toString());
		}
	}

}
