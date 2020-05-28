package org.wikidata.wdtk.dumpfiles;

/*
 * #%L
 * Wikidata Toolkit Dump File Handling
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.fasterxml.jackson.databind.DeserializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.helpers.DatamodelMapper;
import org.wikidata.wdtk.datamodel.implementation.EntityDocumentImpl;
import org.wikidata.wdtk.datamodel.interfaces.*;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;

/**
 * Processor for JSON dumpfiles.
 *
 * @author Markus Kroetzsch
 *
 */
public class JsonDumpFileProcessor implements MwDumpFileProcessor {

	static final Logger logger = LoggerFactory
			.getLogger(JsonDumpFileProcessor.class);

	private final ObjectReader documentReader;

	private final EntityDocumentProcessor entityDocumentProcessor;

	public JsonDumpFileProcessor(
			EntityDocumentProcessor entityDocumentProcessor, String siteIri) {
		this.entityDocumentProcessor = entityDocumentProcessor;
		this.documentReader = new DatamodelMapper(siteIri)
				.readerFor(EntityDocumentImpl.class)
				.with(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
	}

	/**
	 * Process dump file data from the given input stream. This method uses the
	 * efficient Jackson {@link MappingIterator}. However, this class cannot
	 * recover from processing errors. If an error occurs in one entity, the
	 * (presumably) less efficient processing method
	 * {@link #processDumpFileContentsRecovery(InputStream)} is used instead.
	 *
	 * @see MwDumpFileProcessor#processDumpFileContents(InputStream, MwDumpFile)
	 */
	@Override
	public void processDumpFileContents(InputStream inputStream,
			MwDumpFile dumpFile) {

		logger.info("Processing JSON dump file " + dumpFile.toString());

		try {
			try {
				MappingIterator<EntityDocument> documentIterator = documentReader.readValues(inputStream);
				documentIterator.getParser().disable(Feature.AUTO_CLOSE_SOURCE);

				while (documentIterator.hasNextValue()) {
					EntityDocument document = documentIterator.nextValue();
					handleDocument(document);
				}
				documentIterator.close();
			} catch (JsonProcessingException e) {
				logJsonProcessingException(e);
				processDumpFileContentsRecovery(inputStream);
			}
		} catch (IOException e) {
			throw new RuntimeException("Cannot read JSON input: "
					+ e.getMessage(), e);
		}

	}

	/**
	 * Reports the error of a JSON processing exception that was caught when
	 * trying to read an entity.
	 *
	 * @param exception
	 *            the exception to log
	 */
	private void logJsonProcessingException(JsonProcessingException exception) {
		JsonDumpFileProcessor.logger
				.error("Error when reading JSON for entity: "
						+ exception.getMessage());
	}

	/**
	 * Handles a {@link EntityDocument} that was retrieved by
	 * parsing the JSON input. It will call appropriate processing methods
	 * depending on the type of document.
	 *
	 * @param document
	 *            the document to process
	 */
	private void handleDocument(EntityDocument document) {
		if (document instanceof ItemDocument) {
			this.entityDocumentProcessor
					.processItemDocument((ItemDocument) document);
		} else if (document instanceof PropertyDocument) {
			this.entityDocumentProcessor
					.processPropertyDocument((PropertyDocument) document);
		} else if(document instanceof LexemeDocument) {
			this.entityDocumentProcessor
					.processLexemeDocument((LexemeDocument) document);
		} else if(document instanceof MediaInfoDocument) {
			this.entityDocumentProcessor
					.processMediaInfoDocument((MediaInfoDocument) document);
		}
	}

	/**
	 * Process dump file data from the given input stream. The method can
	 * recover from an errors that occurred while processing an input stream,
	 * which is assumed to contain the JSON serialization of a list of JSON
	 * entities, with each entity serialization in one line. To recover from the
	 * previous error, the first line is skipped.
	 *
	 * @param inputStream
	 *            the stream to read from
	 * @throws IOException
	 *             if there is a problem reading the stream
	 */
	private void processDumpFileContentsRecovery(InputStream inputStream)
			throws IOException {
		JsonDumpFileProcessor.logger
				.warn("Entering recovery mode to parse rest of file. This might be slightly slower.");

		BufferedReader br = new BufferedReader(new InputStreamReader(
				inputStream));

		String line = br.readLine();
		if (line == null) { // can happen if iterator already has consumed all
							// the stream
			return;
		}
		if (line.length() >= 100) {
			line = line.substring(0, 100) + "[...]"
					+ line.substring(line.length() - 50);
		}
		JsonDumpFileProcessor.logger.warn("Skipping rest of current line: "
				+ line);

		line = br.readLine();
		while (line != null && line.length() > 1) {
			try {
				EntityDocument document;
				if (line.charAt(line.length() - 1) == ',') {
					document = documentReader.readValue(line.substring(0,
							line.length() - 1));
				} else {
					document = documentReader.readValue(line);
				}
				handleDocument(document);
			} catch (JsonProcessingException e) {
				logJsonProcessingException(e);
				JsonDumpFileProcessor.logger.error("Problematic line was: "
						+ line.substring(0, Math.min(50, line.length()))
						+ "...");
			}

			line = br.readLine();
		}
	}
}
