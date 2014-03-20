package org.wikidata.wdtk.dumpfiles;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;

/**
 * A revision processor that processes Wikibase entity content from a dump file.
 * Revisions are parsed to obtain EntityDocument objects.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class WikibaseRevisionProcessor implements MwRevisionProcessor {

	static final Logger logger = LoggerFactory
			.getLogger(WikibaseRevisionProcessor.class);

	JsonConverter jsonConverter;
	final DataObjectFactory dataObjectFactory;
	final EntityDocumentProcessor entityDocumentProcessor;

	public WikibaseRevisionProcessor(
			EntityDocumentProcessor entityDocumentProcessor) {
		this.dataObjectFactory = new DataObjectFactoryImpl();
		this.entityDocumentProcessor = entityDocumentProcessor;
	}

	@Override
	public void startRevisionProcessing(String siteName, String baseUrl,
			Map<Integer, String> namespaces) {
		// FIXME the baseUrl from the dump is not the baseIri we need here
		this.jsonConverter = new JsonConverter(baseUrl, this.dataObjectFactory);
	}

	@Override
	public void processRevision(MwRevision mwRevision) {
		if (MwRevision.MODEL_WIKIBASE_ITEM.equals(mwRevision.getModel())) {
			processItemRevision(mwRevision);
		} else if (MwRevision.MODEL_WIKIBASE_PROPERTY.equals(mwRevision
				.getModel())) {
			processPropertyRevision(mwRevision);
		} // else: ignore this revision
	}

	public void processItemRevision(MwRevision mwRevision) {
		try {
			JSONObject jsonObject = new JSONObject(mwRevision.getText());
			ItemDocument itemDocument = this.jsonConverter
					.convertToItemDocument(jsonObject, mwRevision.getTitle());
			this.entityDocumentProcessor.processItemDocument(itemDocument);
		} catch (JSONException e) {
			WikibaseRevisionProcessor.logger
					.error("Failed to process JSON for item "
							+ mwRevision.toString() + " (" + e.toString() + ")");
		}

	}

	public void processPropertyRevision(MwRevision mwRevision) {
		try {
			JSONObject jsonObject = new JSONObject(mwRevision.getText());
			PropertyDocument propertyDocument = this.jsonConverter
					.convertToPropertyDocument(jsonObject,
							mwRevision.getTitle());
			this.entityDocumentProcessor
					.processPropertyDocument(propertyDocument);
		} catch (JSONException e) {
			WikibaseRevisionProcessor.logger
					.error("Failed to process JSON for property "
							+ mwRevision.toString() + " (" + e.toString() + ")");
		}

	}

	@Override
	public void finishRevisionProcessing() {
		// Nothing to do
	}

}
