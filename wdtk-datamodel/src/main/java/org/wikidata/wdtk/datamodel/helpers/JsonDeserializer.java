package org.wikidata.wdtk.datamodel.helpers;

import java.io.IOException;

import org.wikidata.wdtk.datamodel.implementation.EntityDocumentImpl;
import org.wikidata.wdtk.datamodel.implementation.ItemDocumentImpl;
import org.wikidata.wdtk.datamodel.implementation.LexemeDocumentImpl;
import org.wikidata.wdtk.datamodel.implementation.MediaInfoDocumentImpl;
import org.wikidata.wdtk.datamodel.implementation.PropertyDocumentImpl;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.LexemeDocument;
import org.wikidata.wdtk.datamodel.interfaces.MediaInfoDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;

import com.fasterxml.jackson.databind.DeserializationFeature;

/**
 * Helper to deserialize datamodel objects from their
 * JSON representation.
 * 
 * We accept empty arrays as empty maps since there has
 * been a confusion in the past between the two:
 * https://phabricator.wikimedia.org/T138104
 * 
 * @author Antonin Delpeuch
 */
public class JsonDeserializer {
	
	protected DatamodelMapper mapper;
	
	/**
	 * Constructs a new JSON deserializer for the 
	 * designated site.
	 * 
	 * @param siteIri
	 * 		Root IRI of the site to deserialize for
	 */
	public JsonDeserializer(String siteIri) {
		mapper = new DatamodelMapper(siteIri);
	}
	
	/**
	 * Deserializes a JSON string into an {@class ItemDocument}.
	 * @throws IOException 
			if the JSON payload is invalid
	 */
	public ItemDocument deserializeItemDocument(String json) throws IOException {
		return mapper.readerFor(ItemDocumentImpl.class)
				.with(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
				.readValue(json);
	}
	
	/**
	 * Deserializes a JSON string into a {@class PropertyDocument}.
	 * @throws IOException 
			if the JSON payload is invalid
	 */
	public PropertyDocument deserializePropertyDocument(String json) throws IOException {
		return mapper.readerFor(PropertyDocumentImpl.class)
				.with(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
				.readValue(json);
	}

	/**
	 * Deserializes a JSON string into a {@class LexemeDocument}.
	 * @throws IOException 
			if the JSON payload is invalid
	 */
	public LexemeDocument deserializeLexemeDocument(String json) throws IOException {
		return mapper.readerFor(LexemeDocumentImpl.class)
				.with(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
				.readValue(json);
	}
	
	/**
	 * Deserializes a JSON string into a {@class MediaInfoDocument}.
	 * @throws IOException 
			if the JSON payload is invalid
	 */
	public MediaInfoDocument deserializeMediaInfoDocument(String json) throws IOException {
		return mapper.readerFor(MediaInfoDocumentImpl.class)
				.with(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
				.readValue(json);
	}
	
	/**
	 * Deserializes a JSON string into a {@class EntityDocument}.
	 * @throws IOException 
			if the JSON payload is invalid
	 */
	public EntityDocument deserializeEntityDocument(String json) throws IOException {
		return mapper.readerFor(EntityDocumentImpl.class)
				.with(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
				.readValue(json);
	}
}
