package org.wikidata.wdtk.storage.dboldcode;

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

import org.mapdb.BTreeKeySerializer;
import org.mapdb.Bind;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer;
import org.wikidata.wdtk.storage.datamodel.Sort;
import org.wikidata.wdtk.storage.db.DatabaseManager;
import org.wikidata.wdtk.storage.db.InnerToOuterObjectConverter;
import org.wikidata.wdtk.storage.db.LazyOuterObjectIterator;

public class EdgeContainerIndex
		implements
		Iterable<EdgeContainer>,
		InnerToOuterObjectConverter<EdgeContainerForSerialization, EdgeContainer> {

	final Sort sourceSort;
	final DatabaseManager databaseManager;

	final Bind.MapWithModificationListener<Integer, EdgeContainerForSerialization> values;

	public EdgeContainerIndex(Sort sourceSort, DatabaseManager databaseManager) {
		this.sourceSort = sourceSort;
		this.databaseManager = databaseManager;

		this.values = // databaseManager.getDb()
		this.databaseManager.getAuxDb("edges-values")
				.createTreeMap("edges-spo-" + sourceSort.getName())
				.keySerializer(BTreeKeySerializer.ZERO_OR_POSITIVE_INT)
				.valueSerializer(new EdgeContainerSerializer()).makeOrGet();

	}

	public void updateEdges(EdgeContainer edgeContainer) {
		int sourceId = this.databaseManager.getOrCreateValueId(edgeContainer
				.getSource());
		this.values.put(sourceId, SerializationConverter
				.getEdgeContainerForSerialization(edgeContainer, sourceId,
						this.sourceSort, this.databaseManager));
	}

	public EdgeContainer getEdgeContainer(int id) {
		EdgeContainerForSerialization inner = this.values.get(id);
		if (inner == null) {
			return null;
		} else {
			return getOuterObject(inner);
		}
	}

	@Override
	public Iterator<EdgeContainer> iterator() {
		return new LazyOuterObjectIterator<>(this.values.values().iterator(),
				this);
	}

	@Override
	public EdgeContainer getOuterObject(EdgeContainerForSerialization inner) {
		return new LazyEdgeContainer(inner, this);
	}

	public DatabaseManager getDatabaseManager() {
		return this.databaseManager;
	}

	public Sort getSourceSort() {
		return this.sourceSort;
	}
}
