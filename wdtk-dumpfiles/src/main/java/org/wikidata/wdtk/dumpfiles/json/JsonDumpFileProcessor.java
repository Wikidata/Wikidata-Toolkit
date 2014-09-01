package org.wikidata.wdtk.dumpfiles.json;

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
