package org.wikidata.wdtk.datamodel.interfaces;

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

/**
 * A value that represents one of the available Wikibase datatypes. The method
 * {@link DatatypeIdValue#getIri() getIri()} will always return one of the
 * datatype IRIs defined in this interface.
 *
 * @author Markus Kroetzsch
 *
 */
public interface DatatypeIdValue {
	/**
	 * IRI of the item datatype in Wikibase.
	 * @deprecated use org.wikidata.wdtk.rdf.Vocabulary.DT_ITEM
	 */
	String DT_ITEM = "http://wikiba.se/ontology#WikibaseItem";
	/**
	 * IRI of the property datatype in Wikibase.
	 * @deprecated use org.wikidata.wdtk.rdf.Vocabulary.DT_PROPERTY
	 */
	String DT_PROPERTY = "http://wikiba.se/ontology#WikibaseProperty";
	/**
	 * IRI of the lexeme datatype in Wikibase.
	 * @deprecated use org.wikidata.wdtk.rdf.Vocabulary.DT_LEXEME
	 */
	String DT_LEXEME = "http://wikiba.se/ontology#WikibaseLexeme";
	/**
	 * IRI of the form datatype in Wikibase.
	 * @deprecated use org.wikidata.wdtk.rdf.Vocabulary.DT_FORM
	 */
	String DT_FORM = "http://wikiba.se/ontology#WikibaseForm";
	/**
	 * IRI of the sense datatype in Wikibase.
	 * @deprecated use org.wikidata.wdtk.rdf.Vocabulary.DT_SENSE
	 */
	String DT_SENSE = "http://wikiba.se/ontology#WikibaseSense";
	/**
	 * IRI of the media info datatype in Wikibase.
	 * @deprecated use org.wikidata.wdtk.rdf.Vocabulary.DT_MEDIA_INFO
	 */
	String DT_MEDIA_INFO = "http://wikiba.se/ontology#WikibaseMediaInfo";
	/**
	 * IRI of the string datatype in Wikibase.
	 * @deprecated use org.wikidata.wdtk.rdf.Vocabulary.DT_STRING
	 */
	String DT_STRING = "http://wikiba.se/ontology#String";
	/**
	 * IRI of the URL datatype in Wikibase.
	 * @deprecated use org.wikidata.wdtk.rdf.Vocabulary.DT_URL
	 */
	String DT_URL = "http://wikiba.se/ontology#Url";
	/**
	 * IRI of the Commons media datatype in Wikibase.
	 * @deprecated use org.wikidata.wdtk.rdf.Vocabulary.DT_COMMONS_MEDIA
	 */
	String DT_COMMONS_MEDIA = "http://wikiba.se/ontology#CommonsMedia";
	/**
	 * IRI of the time datatype in Wikibase.
	 * @deprecated use org.wikidata.wdtk.rdf.Vocabulary.DT_TIME
	 */
	String DT_TIME = "http://wikiba.se/ontology#Time";
	/**
	 * IRI of the globe coordinates datatype in Wikibase.
	 * @deprecated use org.wikidata.wdtk.rdf.Vocabulary.DT_GLOBE_COORDINATES
	 */
	String DT_GLOBE_COORDINATES = "http://wikiba.se/ontology#GlobeCoordinate";
	/**
	 * IRI of the quantity datatype in Wikibase.
	 * @deprecated use org.wikidata.wdtk.rdf.Vocabulary.DT_QUANTITY
	 */
	String DT_QUANTITY = "http://wikiba.se/ontology#Quantity";
	/**
	 * IRI of the monolingual text datatype in Wikibase.
	 * @deprecated use org.wikidata.wdtk.rdf.Vocabulary.DT_MONOLINGUAL_TEXT
	 */
	String DT_MONOLINGUAL_TEXT = "http://wikiba.se/ontology#Monolingualtext";
	/**
	 * IRI of the external identifier datatype in Wikibase.
	 * @deprecated use org.wikidata.wdtk.rdf.Vocabulary.DT_EXTERNAL_ID
	 */
	String DT_EXTERNAL_ID = "http://wikiba.se/ontology#ExternalId";
	/**
	 * IRI of the math datatype in Wikibase.
	 * @deprecated use org.wikidata.wdtk.rdf.Vocabulary.DT_MATH
	 */
	String DT_MATH = "http://wikiba.se/ontology#Math";
	/**
	 * IRI of the geo shape datatype in Wikibase.
	 * @deprecated use org.wikidata.wdtk.rdf.Vocabulary.DT_GEO_SHAPE
	 */
	String DT_GEO_SHAPE = "http://wikiba.se/ontology#GeoShape";
	/**
	 * IRI of the tabular data datatype in Wikibase.
	 * @deprecated use org.wikidata.wdtk.rdf.Vocabulary.DT_TABULAR_DATA
	 */
	String DT_TABULAR_DATA = "http://wikiba.se/ontology#TabularData";
	/**
	 * IRI of the extended date time format (EDTF) datatype in Wikibase.
	 * @deprecated use org.wikidata.wdtk.rdf.Vocabulary.DT_EDTF
	 */
	String DT_EDTF = "http://wikiba.se/ontology#Edtf";

	/**
	 * String used to refer to the property datatype for Wikibase items, in JSON.
	 */
	String JSON_DT_ITEM = "wikibase-item";
	/**
	 * String used to refer to the property datatype for Wikibase properties, in JSON.
	 */
	String JSON_DT_PROPERTY = "wikibase-property";
	/**
	 * String used to refer to the property datatype for globe coordinates, in JSON.
	 */
	String JSON_DT_GLOBE_COORDINATES = "globe-coordinate";
	/**
	 * String used to refer to the property datatype for urls, in JSON.
	 */
	String JSON_DT_URL = "url";
	/**
	 * String used to refer to the property datatype for Commons media files, in JSON.
	 */
	String JSON_DT_COMMONS_MEDIA = "commonsMedia";
	/**
	 * String used to refer to the property datatype for time values, in JSON.
	 */
	String JSON_DT_TIME = "time";
	/**
	 * String used to refer to the property datatype for quantities, in JSON.
	 */
	String JSON_DT_QUANTITY = "quantity";
	/**
	 * String used to refer to the property datatype for strings, in JSON.
	 */
	String JSON_DT_STRING = "string";
	/**
	 * String used to refer to the property datatype for monolingual text, in JSON.
	 */
	String JSON_DT_MONOLINGUAL_TEXT = "monolingualtext";
	/**
	 * String used to refer to the property datatype for external identifiers, in JSON.
	 */
	String JSON_DT_EXTERNAL_ID = "external-id";
	/**
	 * String used to refer to the property datatype for mathematical expressions, in JSON.
	 */
	String JSON_DT_MATH = "math";
	/**
	 * String used to refer to the property datatype for Geo shapes, in JSON.
	 */
	String JSON_DT_GEO_SHAPE = "geo-shape";
	/**
	 * String used to refer to the property datatype for EDTF dates, in JSON.
	 */
	String JSON_DT_EDTF = "edtf";

	/**
	 * Get the IRI of this entity.
	 * 
	 * @return String with the IRI
	 * @deprecated use org.wikidata.wdtk.rdf.AbstractRdfConverter.getDatatypeIri() from the wdtk-rdf module
	 */
	String getIri();

	/**
	 * The string identifying this datatype in the JSON serialization of a property.
	 */
    String getJsonString();
}
