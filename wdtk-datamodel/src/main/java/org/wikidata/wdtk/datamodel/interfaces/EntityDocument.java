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
 * Interface for datasets that describe an entity.
 *
 * @author Markus Kroetzsch
 *
 */
public interface EntityDocument {

	/**
	 * Returns the ID of the entity that the data refers to
	 *
	 * @return entity id
	 */
	EntityIdValue getEntityId();

	/**
	 * Returns the revision ID of this document, or 0 if no id is known. The
	 * revision ID is a number stored by MediaWiki to indicate the version of a
	 * document. It is based on a global counter that is incremented on each
	 * edit. Not all sources of entity document data may provide the revision
	 * ID, as it is not strictly part of the data, but part of the document
	 * metadata.
	 *
	 * @return revision id
	 */
	long getRevisionId();
	
	/**
	 * Returns a copy of this document with an updated revision id.
	 */
	EntityDocument withRevisionId(long newRevisionId);

}
