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
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
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

	/**
	 * Creates a new JSON serializer that writes its output to the given stream.
	 * 
	 * @param out
	 *            the output stream to write to
	 */
	public JsonSerializer(OutputStream out) {
		this.out = out;
		this.atFirst = true;
	}

	/**
	 * This function resets the processor. Json is not a flat format. So it is
	 * necessary to inform the processor if a new serialisation was initiated.
	 */
	public void restartProcess() {
		this.atFirst = true;
	}

	/**
	 * Sends the JSON encoding to an OutputStream.
	 * 
	 * @param jsonDocument
	 *            JSON serialization for the entity document
	 * @param id
	 *            entity id of the entity document
	 */
	void writeEntityDocument(String jsonDocument, String id) {
		StringBuilder builder = new StringBuilder();
		if (!this.atFirst) {
			builder.append(",");
		} else {
			this.atFirst = false;
		}
		builder.append("\"");
		builder.append(id);
		builder.append("\"");
		builder.append(":");
		builder.append(jsonDocument);
		builder.append("\n");
		try {
			this.out.write(builder.toString().getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {
			logger.error(e.toString());
		}
	}

	@Override
	public void processItemDocument(ItemDocument itemDocument) {
		writeEntityDocument(this.converter.getJsonForItemDocument(itemDocument)
				.toString(), itemDocument.getItemId().getId());
	}

	@Override
	public void processPropertyDocument(PropertyDocument propertyDocument) {
		writeEntityDocument(
				this.converter.getJsonForPropertyDocument(propertyDocument)
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
			this.out.write("{\"entities\": {\n"
					.getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {
			// fail: we cannot produce useful JSON if some bytes are lost
			logger.error("Failed to write JSON export:" + e.toString());
			throw new RuntimeException(e.toString(), e);
		}
	}

	@Override
	public void finishSerialization() {
		try {
			this.out.write("}}".getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {
			// fail: we cannot produce useful JSON if some bytes are lost
			logger.error("Failed to write JSON export:" + e.toString());
			throw new RuntimeException(e.toString(), e);
		}
	}

}
