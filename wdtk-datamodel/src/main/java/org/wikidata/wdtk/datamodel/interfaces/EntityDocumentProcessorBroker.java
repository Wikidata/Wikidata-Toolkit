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

import java.util.ArrayList;
import java.util.List;

/**
 * Simple broker implementation of {@link EntityDocumentProcessor} which
 * distributes entity documents to multiple registered listeners.
 *
 * @author Markus Kroetzsch
 *
 */
public class EntityDocumentProcessorBroker implements EntityDocumentProcessor {

	final List<EntityDocumentProcessor> entityDocumentProcessors = new ArrayList<EntityDocumentProcessor>();

	/**
	 * Registers a listener which will be called for all entity documents that
	 * are processed.
	 *
	 * @param entityDocumentProcessor
	 *            the listener to register
	 */
	public void registerEntityDocumentProcessor(
			EntityDocumentProcessor entityDocumentProcessor) {
		this.entityDocumentProcessors.add(entityDocumentProcessor);
	}

	@Override
	public void processItemDocument(ItemDocument itemDocument) {
		for (EntityDocumentProcessor entityDocumentProcessor : this.entityDocumentProcessors) {
			entityDocumentProcessor.processItemDocument(itemDocument);
		}
	}

	@Override
	public void processPropertyDocument(PropertyDocument propertyDocument) {
		for (EntityDocumentProcessor entityDocumentProcessor : this.entityDocumentProcessors) {
			entityDocumentProcessor.processPropertyDocument(propertyDocument);
		}
	}

}
