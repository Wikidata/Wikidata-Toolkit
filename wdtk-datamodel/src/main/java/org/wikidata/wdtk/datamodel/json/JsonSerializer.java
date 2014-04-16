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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentsSerializer;

/**
 * This class implements {@link EntityDocumentsSerializer} to provide a
 * JsonSerializer for {@link EntityDocument} objects.
 * 
 * @author Michael Günther
 * 
 */
public class JsonSerializer implements EntityDocumentsSerializer {

	static final Logger logger = LoggerFactory.getLogger(JsonSerializer.class);

	JsonProcessor processor;

	public JsonSerializer(JsonProcessor processor) {
		this.processor = processor;
	}

	@Override
	public void startSerialisation() {
		processor.restartProcess();
		try {
			processor.getOutput().write("{\"entities\": {".getBytes());
		} catch (IOException e) {
			JsonSerializer.logger.error(e.toString());
		}
	}

	@Override
	public void finishSerialisation() {
		try {
			processor.getOutput().write("}}".getBytes());
		} catch (IOException e) {
			JsonSerializer.logger.error(e.toString());
		}
		processor.finishProcessingEntityDocuments();
	}

	@Override
	public EntityDocumentProcessor getEntityDocumentProcessor() {
		return processor;
	}

}
