package org.wikidata.wdtk.datamodel.interfaces;

/*-
 * #%L
 * Wikidata Toolkit Data Model
 * %%
 * Copyright (C) 2014 - 2019 Wikidata Toolkit Developers
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
 * Represents a entity id value of an unsupported type.
 * We can still "deserialize" it by just storing its
 * JSON representation, so that it can be serialized
 * back to its original representation.
 * This avoids parsing failures on documents containing
 * these values.
 * 
 * @author Antonin Delpeuch
 */
public interface UnsupportedEntityIdValue extends EntityIdValue {
	/**
	 * The type of entity as represented in the JSON serialization.
	 */
	public String getEntityTypeJsonString();
}
