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
	 */
	String DT_ITEM = "http://wikiba.se/ontology#WikibaseItem";
	/**
	 * IRI of the property datatype in Wikibase.
	 */
	String DT_PROPERTY = "http://wikiba.se/ontology#WikibaseProperty";
	/**
	 * IRI of the lexeme datatype in Wikibase.
	 */
	String DT_LEXEME = "http://wikiba.se/ontology#WikibaseLexeme";
	/**
	 * IRI of the form datatype in Wikibase.
	 */
	String DT_FORM = "http://wikiba.se/ontology#WikibaseForm";
	/**
	 * IRI of the sense datatype in Wikibase.
	 */
	String DT_SENSE = "http://wikiba.se/ontology#WikibaseSense";
	/**
	 * IRI of the media info datatype in Wikibase.
	 */
	String DT_MEDIA_INFO = "http://wikiba.se/ontology#WikibaseMediaInfo";
	/**
	 * IRI of the string datatype in Wikibase.
	 */
	String DT_STRING = "http://wikiba.se/ontology#String";
	/**
	 * IRI of the URL datatype in Wikibase.
	 */
	String DT_URL = "http://wikiba.se/ontology#Url";
	/**
	 * IRI of the Commons media datatype in Wikibase.
	 */
	String DT_COMMONS_MEDIA = "http://wikiba.se/ontology#CommonsMedia";
	/**
	 * IRI of the time datatype in Wikibase.
	 */
	String DT_TIME = "http://wikiba.se/ontology#Time";
	/**
	 * IRI of the globe coordinates datatype in Wikibase.
	 */
	String DT_GLOBE_COORDINATES = "http://wikiba.se/ontology#GlobeCoordinate";
	/**
	 * IRI of the quantity datatype in Wikibase.
	 */
	String DT_QUANTITY = "http://wikiba.se/ontology#Quantity";
	/**
	 * IRI of the monolingual text datatype in Wikibase.
	 */
	String DT_MONOLINGUAL_TEXT = "http://wikiba.se/ontology#Monolingualtext";
	/**
	 * IRI of the external identifier datatype in Wikibase.
	 */
	String DT_EXTERNAL_ID = "http://wikiba.se/ontology#ExternalId";
	/**
	 * IRI of the math datatype in Wikibase.
	 */
	String DT_MATH = "http://wikiba.se/ontology#Math";
	/**
	 * IRI of the geo shape datatype in Wikibase.
	 */
	String DT_GEO_SHAPE = "http://wikiba.se/ontology#GeoShape";
	/**
	 * IRI of the tabular data datatype in Wikibase.
	 */
	String DT_TABULAR_DATA = "http://wikiba.se/ontology#TabularData";

	/**
	 * Get the IRI of this entity.
	 * 
	 * @return String with the IRI
	 */
	String getIri();
}
