package org.wikidata.wdtk.client;

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

import java.io.IOException;
import java.io.OutputStream;

import org.wikidata.wdtk.datamodel.helpers.JsonSerializer;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.LexemeDocument;
import org.wikidata.wdtk.datamodel.interfaces.MediaInfoDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;

/**
 * This class represents an action of generating a JSON dump from data. It has
 * no specific options.
 *
 * @author Markus Kroetzsch
 *
 */
public class JsonSerializationAction extends DumpProcessingOutputAction {

	/**
	 * The base file name that will be used by default. File endings for
	 * indicating compression will be appended where required.
	 */
	public final static String DEFAULT_FILE_NAME = "{PROJECT}-{DATE}.json";

	/**
	 * default action name is used to separate different
	 * DumpProcessingOutputActions from each other.
	 */
	public final static String DEFAULT_ACTION_NAME = "JsonSerializationAction";

	/**
	 * The actual serializer used internally.
	 */
	JsonSerializer serializer;

	/**
	 * Constructor. See {@link DumpProcessingOutputAction} for more details.
	 */
	public JsonSerializationAction() {
		this.outputDestination = DEFAULT_FILE_NAME;
	}

	@Override
	public boolean needsSites() {
		return false;
	}

	@Override
	public boolean isReady() {
		return true; // can always run
	}

	@Override
	public void open() {
		OutputStream outputStream;
		try {
			outputStream = getOutputStream(this.useStdOut, getOutputFilename(),
					this.compressionType);
			this.serializer = new JsonSerializer(outputStream);
			this.serializer.open();
		} catch (IOException e) {
			// TODO rather print error and set a nonce processor here
			e.printStackTrace();
		}
	}

	@Override
	public void processItemDocument(ItemDocument itemDocument) {
		this.serializer.processItemDocument(itemDocument);
	}

	@Override
	public void processPropertyDocument(PropertyDocument propertyDocument) {
		this.serializer.processPropertyDocument(propertyDocument);
	}

	@Override
	public void processLexemeDocument(LexemeDocument lexemeDocument) {
		this.serializer.processLexemeDocument(lexemeDocument);
	}

	@Override
	public void processMediaInfoDocument(MediaInfoDocument mediaInfoDocument) {
		this.serializer.processMediaInfoDocument(mediaInfoDocument);
	}

	@Override
	public void close() {
		this.serializer.close();
		super.close();
	}

	@Override
	public String getReport() {
		String message = "Finished serialization of "
				+ this.serializer.getEntityDocumentCount()
				+ " EntityDocuments in file " + getOutputFilename();
		if (!this.compressionType.equals(COMPRESS_NONE)) {
			message += "." + this.compressionType;
		}
		return message;
	}

	@Override
	public String getDefaultActionName() {
		return DEFAULT_ACTION_NAME;
	}

}
