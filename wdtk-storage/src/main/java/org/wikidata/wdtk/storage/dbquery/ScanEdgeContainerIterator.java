package org.wikidata.wdtk.storage.dbquery;

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

import org.wikidata.wdtk.storage.datamodel.EdgeContainer;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer.TargetQualifiers;
import org.wikidata.wdtk.storage.db.DatabaseManager;
import org.wikidata.wdtk.storage.serialization.EdgeContainerFromSerialization;
import org.wikidata.wdtk.storage.serialization.EdgeContainerFromSerialization.PropertyTargetsFromSerialization;
import org.wikidata.wdtk.storage.serialization.EdgeContainerFromSerialization.RefTargetQualifiersFromSerialization;

public class ScanEdgeContainerIterator implements Iterator<EdgeContainer> {

	final DatabaseManager databaseManager;
	final int searchProperty;
	final int searchValue;
	final Iterator<EdgeContainerFromSerialization> ecIterator;

	EdgeContainer nextEdgeContainer;
	EdgeContainer currentEdgeContainer;

	public ScanEdgeContainerIterator(String sortName, int searchProperty,
			int searchValue, DatabaseManager databaseManager) {
		this.searchProperty = searchProperty;
		this.searchValue = searchValue;
		this.databaseManager = databaseManager;
		this.ecIterator = databaseManager.edgeContainers(sortName).iterator();
		findNext();
	}

	private void findNext() {
		this.nextEdgeContainer = null;
		while (this.nextEdgeContainer == null && this.ecIterator.hasNext()) {
			EdgeContainerFromSerialization ec = this.ecIterator.next();

			if (this.searchProperty != -1) {
				for (int i = 0; i < ec.getEdgeCount(); i++) {
					if (this.searchProperty == ec.getPropertyIdInternal(i)) {
						if (this.searchValue == -1) {
							this.nextEdgeContainer = ec;
							break;
						} else {
							PropertyTargetsFromSerialization pts = ec
									.getPropertyTargetsInternal(i);
							for (TargetQualifiers tqs : pts) {
								if (tqs instanceof RefTargetQualifiersFromSerialization
										&& ((RefTargetQualifiersFromSerialization) tqs)
												.getInternalTargetId() == this.searchValue) {
									this.nextEdgeContainer = ec;
									break;
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public boolean hasNext() {
		return this.nextEdgeContainer != null;
	}

	@Override
	public EdgeContainer next() {
		this.currentEdgeContainer = this.nextEdgeContainer;
		findNext();
		return this.currentEdgeContainer;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
