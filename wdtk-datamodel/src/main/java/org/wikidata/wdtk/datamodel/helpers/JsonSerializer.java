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

package org.wikidata.wdtk.datamodel.helpers;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentDumpProcessor;
import org.wikidata.wdtk.datamodel.interfaces.EntityUpdate;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.LexemeDocument;
import org.wikidata.wdtk.datamodel.interfaces.MediaInfoDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.Statement;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

/**
 * This class implements {@link EntityDocumentDumpProcessor} to provide a
 * serializer for {@link EntityDocument} objects in JSON.
 * <p>
 * The implementation does not check if {@link #open()} has been called before
 * the first document is serialized. It is the responsibility of the caller to
 * do this.
 * <p>
 * Implementations of the data model are expected to be appropriately serializable
 * to JSON with Jackson.
 *
 * @author Markus Kroetzsch
 *
 */
public class JsonSerializer implements EntityDocumentDumpProcessor {

	private static final Logger logger = LoggerFactory.getLogger(JsonSerializer.class);

	private static final byte[] JSON_START_LIST = "[\n".getBytes(StandardCharsets.UTF_8);
	private static final byte[] JSON_SEP = ",\n".getBytes(StandardCharsets.UTF_8);
	private static final byte[] JSON_END_LIST = "\n]".getBytes(StandardCharsets.UTF_8);

	/**
	 * The stream that the resulting JSON is written to.
	 */
	private final OutputStream outputStream;

	/**
	 * Object mapper that is used to serialize JSON.
	 */
	protected static final ObjectMapper mapper = new ObjectMapper();
	static {
		mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
		/*
		 * Support for Optional properties.
		 */
		mapper.registerModule(new Jdk8Module());
	}

	/**
	 * Counter for the number of documents serialized so far.
	 */
	private int entityDocumentCount;

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
		serializeEntityDocument(itemDocument);
	}

	@Override
	public void processPropertyDocument(PropertyDocument propertyDocument) {
		serializeEntityDocument(propertyDocument);
	}

	@Override
	public void processLexemeDocument(LexemeDocument lexemeDocument) {
		serializeEntityDocument(lexemeDocument);
	}

	@Override
	public void processMediaInfoDocument(MediaInfoDocument mediaInfoDocument) {
		serializeEntityDocument(mediaInfoDocument);
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
	private void reportException(Exception e) {
		logger.error("Failed to write JSON export: " + e.toString());
		throw new RuntimeException(e.toString(), e);
	}

	/**
	 * Writes the JSON serialization of the given {@link EntityDocument}.
	 *
	 * @param entityDocument
	 *            the document to serialize
	 */
	private void serializeEntityDocument(EntityDocument entityDocument) {
		try {
			if (this.entityDocumentCount > 0) {
				this.outputStream.write(JSON_SEP);
			}
			mapper.writeValue(this.outputStream, entityDocument);
		} catch (IOException e) {
			reportException(e);
		}
		this.entityDocumentCount++;
	}

	/**
	 * Serializes the given object in JSON and returns the resulting string.
	 * Throws if the serialization fails.
	 *
	 * @param entityDocument
	 *            object to serialize
	 * @return JSON serialization
	 * @throws JsonProcessingException if the object cannot be serialized
	 */
	public static String getJsonString(EntityDocument entityDocument) throws JsonProcessingException {
		return mapper.writeValueAsString(entityDocument);
	}

	/**
	 * Serializes the given object in JSON and returns the resulting string.
	 * Throws if the serialization fails.
	 *
	 * @param itemDocument
	 *            object to serialize
	 * @return JSON serialization
	 * @throws JsonProcessingException if the object cannot be serialized
	 */
	public static String getJsonString(ItemDocument itemDocument) throws JsonProcessingException {
		return mapper.writeValueAsString(itemDocument);
	}

	/**
	 * Serializes the given object in JSON and returns the resulting string.
	 * Throws if the serialization fails.
	 *
	 * @param propertyDocument
	 *            object to serialize
	 * @return JSON serialization
	 * @throws JsonProcessingException if the object cannot be serialized
	 */
	public static String getJsonString(PropertyDocument propertyDocument) throws JsonProcessingException {
		return mapper.writeValueAsString(propertyDocument);
	}

	/**
	 * Serializes the given object in JSON and returns the resulting string.
	 * Throws if the serialization fails.
	 *
	 * @param mediaInfoDocument
	 *            object to serialize
	 * @return JSON serialization
	 * @throws JsonProcessingException if the object cannot be serialized
	 */
	public static String getJsonString(MediaInfoDocument mediaInfoDocument) throws JsonProcessingException {
		return mapper.writeValueAsString(mediaInfoDocument);
	}

	/**
	 * Serializes the given object in JSON and returns the resulting string.
	 * Throws if the serialization fails.
	 *
	 * @param statement
	 *            object to serialize
	 * @return JSON serialization
	 * @throws JsonProcessingException if the object cannot be serialized
	 */
	public static String getJsonString(Statement statement) throws JsonProcessingException {
		return mapper.writeValueAsString(statement);
	}

	/**
	 * Serializes the given object in JSON and returns the resulting string.
	 * Throws if the serialization fails.
	 *
	 * @param update
	 *            object to serialize
	 * @return JSON serialization
	 * @throws JsonProcessingException if the object cannot be serialized
	 */
	public static String getJsonString(EntityUpdate update) throws JsonProcessingException {
		return mapper.writeValueAsString(update);
	}

}
