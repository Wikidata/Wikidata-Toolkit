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
 * Interface for classes that are able to process {@link EntityDocument} objects
 * in some way. Classes that implement this can subscribe to receive entity
 * documents as obtained, e.g., from parsing dump files.
 *
 * @author Markus Kroetzsch
 *
 */
public interface EntityDocumentProcessor {

	/**
	 * Processes the given ItemDocument.
	 *
	 * @param itemDocument
	 *            the ItemDocument
	 */
	default void processItemDocument(ItemDocument itemDocument) {
	}

	/**
	 * Processes the given PropertyDocument.
	 *
	 * @param propertyDocument
	 *            the PropertyDocument
	 */
	default void processPropertyDocument(PropertyDocument propertyDocument) {
	}

	/**
	 * Processes the given LexemeDocument.
	 *
	 * @param lexemeDocument
	 *            the LexemeDocument
	 */
	default void processLexemeDocument(LexemeDocument lexemeDocument) {
	}

	/**
	 * Processes the given MediaInfoDocument.
	 *
	 * @param mediaInfoDocument
	 *            the MediaInfoDocument
	 */
	default void processMediaInfoDocument(MediaInfoDocument mediaInfoDocument) {
	}
}
