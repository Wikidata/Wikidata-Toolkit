package org.wikidata.wdtk.datamodel.json.jackson;

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

import org.apache.commons.compress.utils.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.helpers.DatamodelConverter;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentDumpProcessor;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class implements {@link EntityDocumentDumpProcessor} to provide a
 * serializer for {@link EntityDocument} objects in JSON.
 * <p>
 * The implementation does not check if {@link #open()} has been called before
 * the first document is serialized. It is the responsibility of the caller to
 * do this.
 * <p>
 * To generate the correct official JSON serialization used by Wikidata,
 * serialized entity documents must be based on the Jackson implementations,
 * i.e., be instances of {@link JacksonItemDocument} or
 * {@link JacksonPropertyDocument}. The serializer checks is this is the case
 * and converts the data if not. If the caller can provide data based on Jackson
 * objects or has the choice of generating data in this format (e.g., if
 * documents are converted for filtering purposes anyway), then this will
 * improve performance since no conversion is needed there.
 *
 * @author Markus Kroetzsch
 *
 */
public class JsonSerializer implements EntityDocumentDumpProcessor {

	static final Logger logger = LoggerFactory.getLogger(JsonSerializer.class);

	static final byte[] JSON_START_LIST = "[\n".getBytes(Charsets.UTF_8);
	static final byte[] JSON_SEP = ",\n".getBytes(Charsets.UTF_8);
	static final byte[] JSON_END_LIST = "\n]".getBytes(Charsets.UTF_8);

	/**
	 * The stream that the resulting JSON is written to.
	 */
	protected final OutputStream outputStream;

	/**
	 * Object used to convert given entity documents to Jackson implementations
	 * for serialization whenever needed.
	 */
	protected final DatamodelConverter datamodelConverter;

	/**
	 * Object mapper that is used to serialize JSON.
	 */
	protected final ObjectMapper mapper;

	/**
	 * Counter for the number of documents serialized so far.
	 */
	protected int entityDocumentCount;

	/**
	 * Creates a new JSON serializer that writes its output to the given stream.
	 * The output stream will be managed by the object, i.e., it will be closed
	 * when {@link #close()} is call ed.
	 *
	 * @param outputStream
	 *            the output stream to write to
	 */
	public JsonSerializer(OutputStream outputStream) {
		this.outputStream = outputStream;

		this.mapper = new ObjectMapper();
		this.mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);

		this.datamodelConverter = new DatamodelConverter(
				new JacksonObjectFactory());
	}

	@Override
	public void open() {
		this.entityDocumentCount = 0;

		try {
			this.outputStream.write(JSON_START_LIST);
		} catch (IOException e) {
			reportException(e);
		}
	}

	@Override
	public void processItemDocument(ItemDocument itemDocument) {
		if (!(itemDocument instanceof JacksonItemDocument)) {
			itemDocument = this.datamodelConverter.copy(itemDocument);
		}
		serializeEntityDocument(itemDocument);
	}

	@Override
	public void processPropertyDocument(PropertyDocument propertyDocument) {
		if (!(propertyDocument instanceof JacksonPropertyDocument)) {
			propertyDocument = this.datamodelConverter.copy(propertyDocument);
		}
		serializeEntityDocument(propertyDocument);
	}

	@Override
	public void close() {
		try {
			this.outputStream.write(JSON_END_LIST);
			this.outputStream.close();
		} catch (IOException e) {
			reportException(e);
		}
	}

	/**
	 * Returns the number of entity documents serialized so far.
	 *
	 * @return number of serialized entity documents
	 */
	public int getEntityDocumentCount() {
		return this.entityDocumentCount;
	}

	/**
	 * Reports a given exception as a RuntimeException, since the interface does
	 * not allow us to throw checked exceptions directly.
	 *
	 * @param e
	 *            the exception to report
	 * @throws RuntimeException
	 *             in all cases
	 */
	protected void reportException(Exception e) {
		logger.error("Failed to write JSON export: " + e.toString());
		throw new RuntimeException(e.toString(), e);
	}

	/**
	 * Writes the JSON serialization of the given {@link EntityDocument}.
	 *
	 * @param entityDocument
	 *            the document to serialize
	 */
	protected void serializeEntityDocument(EntityDocument entityDocument) {
		try {
			if (this.entityDocumentCount > 0) {
				this.outputStream.write(JSON_SEP);
			}
			this.mapper.writeValue(this.outputStream, entityDocument);
		} catch (IOException e) {
			reportException(e);
		}
		this.entityDocumentCount++;
	}

}
