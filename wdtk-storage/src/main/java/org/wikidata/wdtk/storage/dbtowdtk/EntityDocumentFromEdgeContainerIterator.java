package org.wikidata.wdtk.storage.dbtowdtk;

/*
 * #%L
 * Wikidata Toolkit Storage
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

import java.util.Iterator;

import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer;

public class EntityDocumentFromEdgeContainerIterator implements
Iterator<EntityDocument> {

	final Iterator<? extends EdgeContainer> edgeContainers;
	final DataObjectFactory dataObjectFactory;

	public EntityDocumentFromEdgeContainerIterator(
			Iterator<? extends EdgeContainer> iterator) {
		this.edgeContainers = iterator;
		this.dataObjectFactory = new DataObjectFactoryImpl();
	}

	@Override
	public boolean hasNext() {
		return this.edgeContainers.hasNext();
	}

	@Override
	public EntityDocument next() {
		return WdtkFromDb.EntityDocumentFromEdgeContainer(
				this.edgeContainers.next(), this.dataObjectFactory);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
