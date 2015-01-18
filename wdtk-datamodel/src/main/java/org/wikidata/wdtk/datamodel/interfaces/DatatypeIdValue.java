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
	static final String DT_ITEM = "http://www.wikidata.org/ontology#propertyTypeItem";
	/**
	 * IRI of the property datatype in Wikibase.
	 */
	static final String DT_PROPERTY = "http://www.wikidata.org/ontology#propertyTypeProperty";
	/**
	 * IRI of the string datatype in Wikibase.
	 */
	static final String DT_STRING = "http://www.wikidata.org/ontology#propertyTypeString";
	/**
	 * IRI of the URL datatype in Wikibase.
	 */
	static final String DT_URL = "http://www.wikidata.org/ontology#propertyTypeUrl";
	/**
	 * IRI of the Commons media datatype in Wikibase.
	 */
	static final String DT_COMMONS_MEDIA = "http://www.wikidata.org/ontology#propertyTypeCommonsMedia";
	/**
	 * IRI of the time datatype in Wikibase.
	 */
	static final String DT_TIME = "http://www.wikidata.org/ontology#propertyTypeTime";
	/**
	 * IRI of the globe coordinates datatype in Wikibase.
	 */
	static final String DT_GLOBE_COORDINATES = "http://www.wikidata.org/ontology#propertyTypeGlobeCoordinates";
	/**
	 * IRI of the quantity datatype in Wikibase.
	 */
	static final String DT_QUANTITY = "http://www.wikidata.org/ontology#propertyTypeQuantity";
	/**
	 * IRI of the monolingual text datatype in Wikibase.
	 */
	static final String DT_MONOLINGUAL_TEXT = "http://www.wikidata.org/ontology#propertyTypeMonolingualText";

}
