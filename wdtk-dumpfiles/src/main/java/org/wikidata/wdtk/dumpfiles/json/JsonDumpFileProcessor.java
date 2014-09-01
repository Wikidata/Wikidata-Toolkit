package org.wikidata.wdtk.dumpfiles.json;

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


import java.io.IOException;
import java.io.InputStream;

import org.wikidata.wdtk.datamodel.json.jackson.documents.EntityDocumentImpl;
import org.wikidata.wdtk.datamodel.json.jackson.documents.ItemDocumentImpl;
import org.wikidata.wdtk.datamodel.json.jackson.documents.PropertyDocumentImpl;
import org.wikidata.wdtk.dumpfiles.MwDumpFile;
import org.wikidata.wdtk.dumpfiles.MwDumpFileProcessor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

public class JsonDumpFileProcessor implements MwDumpFileProcessor {

	private static ObjectMapper mapper = new ObjectMapper();
	private static ObjectReader documentReader = mapper
			.reader(EntityDocumentImpl.class);

	@Override
	public void processDumpFileContents(InputStream inputStream,
			MwDumpFile dumpFile) {
		
		try {
			MappingIterator<EntityDocumentImpl> documentIter = documentReader
					.readValues(inputStream);

			while (documentIter.hasNextValue()) {
				EntityDocumentImpl document = documentIter.nextValue();
				if (document != null) {
					if (document instanceof ItemDocumentImpl) {
						this.handleItemDocument((ItemDocumentImpl) document);
					} else if (document instanceof PropertyDocumentImpl) {
						this.handlePropertyDocument((PropertyDocumentImpl) document);
					}
				}
			}

		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void handleItemDocument(ItemDocumentImpl document) {
		// TODO
	}

	private void handlePropertyDocument(PropertyDocumentImpl document) {
		// TODO
	}

}
