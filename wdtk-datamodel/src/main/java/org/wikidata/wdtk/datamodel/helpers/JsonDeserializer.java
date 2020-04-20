package org.wikidata.wdtk.datamodel.helpers;

/*-
 * #%L
 * Wikidata Toolkit Data Model
 * %%
 * Copyright (C) 2014 - 2020 Wikidata Toolkit Developers
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

import com.fasterxml.jackson.databind.ObjectReader;
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

	private ObjectReader entityDocumentReader;
	private ObjectReader itemReader;
	private ObjectReader propertyReader;
	private ObjectReader lexemeReader;
	private ObjectReader mediaInfoReader;
	
	/**
	 * Constructs a new JSON deserializer for the 
	 * designated site.
	 * 
	 * @param siteIri
	 * 		Root IRI of the site to deserialize for
	 */
	public JsonDeserializer(String siteIri) {
		DatamodelMapper mapper = new DatamodelMapper(siteIri);
		entityDocumentReader = mapper.readerFor(EntityDocumentImpl.class)
				.with(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
		itemReader = mapper.readerFor(ItemDocumentImpl.class)
				.with(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
		propertyReader = mapper.readerFor(PropertyDocumentImpl.class)
				.with(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
		lexemeReader = mapper.readerFor(LexemeDocumentImpl.class)
				.with(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
		mediaInfoReader = mapper.readerFor(MediaInfoDocumentImpl.class)
				.with(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
	}
	
	/**
	 * Deserializes a JSON string into an {@class ItemDocument}.
	 * @throws IOException 
			if the JSON payload is invalid
	 */
	public ItemDocument deserializeItemDocument(String json) throws IOException {
		return itemReader.readValue(json);
	}
	
	/**
	 * Deserializes a JSON string into a {@class PropertyDocument}.
	 * @throws IOException 
			if the JSON payload is invalid
	 */
	public PropertyDocument deserializePropertyDocument(String json) throws IOException {
		return propertyReader.readValue(json);
	}

	/**
	 * Deserializes a JSON string into a {@class LexemeDocument}.
	 * @throws IOException 
			if the JSON payload is invalid
	 */
	public LexemeDocument deserializeLexemeDocument(String json) throws IOException {
		return lexemeReader.readValue(json);
	}
	
	/**
	 * Deserializes a JSON string into a {@class MediaInfoDocument}.
	 * @throws IOException 
			if the JSON payload is invalid
	 */
	public MediaInfoDocument deserializeMediaInfoDocument(String json) throws IOException {
		return mediaInfoReader.readValue(json);
	}
	
	/**
	 * Deserializes a JSON string into a {@class EntityDocument}.
	 * @throws IOException 
			if the JSON payload is invalid
	 */
	public EntityDocument deserializeEntityDocument(String json) throws IOException {
		return entityDocumentReader.readValue(json);
	}
}
