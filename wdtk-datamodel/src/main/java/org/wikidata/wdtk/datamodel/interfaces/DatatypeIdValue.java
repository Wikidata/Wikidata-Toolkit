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
 * {@link IriIdentifiedValue#getIri() getIri()} will always return one of the
 * datatype IRIs defined in this interface.
 *
 * @author Markus Kroetzsch
 *
 */
public interface DatatypeIdValue extends IriIdentifiedValue {
	/**
	 * IRI of the item datatype in Wikibase.
	 */
	String DT_ITEM = "http://wikiba.se/ontology#propertyTypeItem";
	/**
	 * IRI of the property datatype in Wikibase.
	 */
	String DT_PROPERTY = "http://wikiba.se/ontology#propertyTypeProperty";
	/**
	 * IRI of the string datatype in Wikibase.
	 */
	String DT_STRING = "http://wikiba.se/ontology#propertyTypeString";
	/**
	 * IRI of the URL datatype in Wikibase.
	 */
	String DT_URL = "http://wikiba.se/ontology#propertyTypeUrl";
	/**
	 * IRI of the Commons media datatype in Wikibase.
	 */
	String DT_COMMONS_MEDIA = "http://wikiba.se/ontology#propertyTypeCommonsMedia";
	/**
	 * IRI of the time datatype in Wikibase.
	 */
	String DT_TIME = "http://wikiba.se/ontology#propertyTypeTime";
	/**
	 * IRI of the globe coordinates datatype in Wikibase.
	 */
	String DT_GLOBE_COORDINATES = "http://wikiba.se/ontology#propertyTypeGlobeCoordinates";
	/**
	 * IRI of the quantity datatype in Wikibase.
	 */
	String DT_QUANTITY = "http://wikiba.se/ontology#propertyTypeQuantity";
	/**
	 * IRI of the monolingual text datatype in Wikibase.
	 */
	String DT_MONOLINGUAL_TEXT = "http://wikiba.se/ontology#propertyTypeMonolingualText";

}
