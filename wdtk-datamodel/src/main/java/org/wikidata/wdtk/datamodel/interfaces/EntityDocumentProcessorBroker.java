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

import java.util.HashSet;
import java.util.Set;

/**
 * Simple broker implementation of {@link EntityDocumentProcessor} which
 * distributes entity documents to multiple registered listeners.
 *
 * @author Markus Kroetzsch
 *
 */
public class EntityDocumentProcessorBroker implements EntityDocumentProcessor {

	private final Set<EntityDocumentProcessor> entityDocumentProcessors = new HashSet<>();

	/**
	 * Registers a listener which will be called for all entity documents that
	 * are processed. The method avoids duplicates in the sense that the exact
	 * same object cannot be registered twice.
	 *
	 * @param entityDocumentProcessor
	 *            the listener to register
	 */
	public void registerEntityDocumentProcessor(EntityDocumentProcessor entityDocumentProcessor) {
		entityDocumentProcessors.add(entityDocumentProcessor);
	}

	@Override
	public void processItemDocument(ItemDocument itemDocument) {
		for (EntityDocumentProcessor entityDocumentProcessor : entityDocumentProcessors) {
			entityDocumentProcessor.processItemDocument(itemDocument);
		}
	}

	@Override
	public void processPropertyDocument(PropertyDocument propertyDocument) {
		for (EntityDocumentProcessor entityDocumentProcessor : entityDocumentProcessors) {
			entityDocumentProcessor.processPropertyDocument(propertyDocument);
		}
	}

	@Override
	public void processLexemeDocument(LexemeDocument lexemeDocument) {
		for (EntityDocumentProcessor entityDocumentProcessor : entityDocumentProcessors) {
			entityDocumentProcessor.processLexemeDocument(lexemeDocument);
		}
	}

}
