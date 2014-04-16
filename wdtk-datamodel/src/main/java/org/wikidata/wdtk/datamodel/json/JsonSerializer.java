package org.wikidata.wdtk.datamodel.json;

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
