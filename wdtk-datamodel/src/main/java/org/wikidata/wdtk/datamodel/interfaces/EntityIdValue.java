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
 * An entity is a Value that is represented by a page in Wikibase. It is
 * identified by its id, corresponding to the title of that page. Typical
 * entities are Items (with identifiers of the form Q1234) and Properties (with
 * identifiers of the form P1234).
 * <p>
 * When considering entities from multiple sites, the (local) ID alone is not
 * enough to identify an entity unambiguously. In this case, the site IRI also
 * needs to be taken into account.
 * <p>
 * An alternative to using the local ID and site IRI together is to use the full
 * IRI. By default, this is computed by appending the local ID to the site IRI.
 * However, for some sites and some entity types, more elaborate computations
 * might be required, so this construction scheme for IRIs should not be
 * presumed.
 * <p>
 * The full IRI of an entity is used in export formats like RDF, but also
 * internally, e.g., for identifying the calendar model of time values.
 *
 * @author Markus Kroetzsch
 *
 */
public interface EntityIdValue extends IriIdentifiedValue {

	/**
	 * IRI of the type of an entity that is an item.
	 */
	String ET_ITEM = "http://www.wikidata.org/ontology#Item";
	/**
	 * IRI of the type of an entity that is a property.
	 */
	String ET_PROPERTY = "http://www.wikidata.org/ontology#Property";
	/**
	 * IRI of the type of an entity that is a lexeme.
	 */
	String ET_LEXEME = "http://www.wikidata.org/ontology#Lexeme";
	/**
	 * IRI of the type of an entity that is a form.
	 */
	String ET_FORM = "http://www.wikidata.org/ontology#Form";
	/**
	 * IRI of the type of an entity that is a sense.
	 */
	String ET_SENSE = "http://www.wikidata.org/ontology#Sense";
	/**
	 * IRI of the type of an entity that is a media info.
	 */
	String ET_MEDIA_INFO = "http://www.wikidata.org/ontology#MediaInfo";
	/**
	 * IRI of the type of an unsupported entity, when no type could be
	 * detected from the JSON representation. The IRI for ids associated
	 * with type information are constructed using the same format as above.
	 */
	String ET_UNSUPPORTED = "http://www.wikidata.org/ontology#Unsupported";
	
	/**
	 * The site IRI of "local" identifiers. These are used to mark internal ids
	 * that are not found on any external site. Components that send data to
	 * external services or that create data exports should omit such ids, if
	 * possible.
	 */
	String SITE_LOCAL = "http://localhost/entity/";

	/**
	 * Returns the type of this entity. This should be an IRI that identifies an
	 * entity type, such as {@link EntityIdValue#ET_ITEM} or
	 * {@link EntityIdValue#ET_PROPERTY}.
	 *
	 * @return IRI string to identify the type of the entity
	 */
	String getEntityType();

	/**
	 * Returns the id of this entity.
	 *
	 * @return String id of this entity
	 */
	String getId();

	/**
	 * Returns an IRI that identifies the site that this entity comes from,,
	 * e.g., "http://www.wikidata.org/entity/" for Wikidata.
	 *
	 * @return the site IRI string
	 */
	String getSiteIri();

}
