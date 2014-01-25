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
 * 
 * The full IRI of an entity is used in export formats like RDF, but also
 * internally, e.g., for identifying the calendar model of time values.
 * 
 * @author Markus Kroetzsch
 * 
 */
public interface EntityId extends IriValue {

	/**
	 * Enum for the possible entity types, currently Item and Property.
	 * 
	 * @author Markus Kroetzsch
	 */
	enum EntityType {
		ITEM, PROPERTY
	}

	/**
	 * Get the type of this entity.
	 * 
	 * @return EntityType
	 */
	EntityType getEntityType();

	/**
	 * Get the id of this entity.
	 * 
	 * @return String id of this entity
	 */
	String getId();

}
