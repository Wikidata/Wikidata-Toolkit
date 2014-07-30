package org.wikidata.wdtk.storage.serialization;

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

import java.io.IOException;
import java.util.Iterator;

import org.mapdb.BTreeKeySerializer;
import org.mapdb.Bind;
import org.mapdb.Serializer;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer;
import org.wikidata.wdtk.storage.datamodel.Sort;
import org.wikidata.wdtk.storage.db.DatabaseManager;
import org.wikidata.wdtk.storage.db.InnerToOuterObjectConverter;
import org.wikidata.wdtk.storage.db.LazyOuterObjectIterator;

public class EdgeContainerIndex implements Iterable<EdgeContainer>,
		InnerToOuterObjectConverter<int[], EdgeContainer> {

	final Sort sourceSort;
	final DatabaseManager databaseManager;

	final Bind.MapWithModificationListener<Integer, int[]> properties;
	final Bind.MapWithModificationListener<Integer, byte[]> values;
	final Bind.MapWithModificationListener<Integer, byte[]> refs;

	public EdgeContainerIndex(Sort sourceSort, DatabaseManager databaseManager) {
		this.sourceSort = sourceSort;
		this.databaseManager = databaseManager;

		this.properties = databaseManager
				.getDb()
				// this.databaseManager.getAuxDb("edges-props")
				.createTreeMap("edges-props-" + sourceSort.getName())
				.keySerializer(BTreeKeySerializer.ZERO_OR_POSITIVE_INT)
				.valueSerializer(new IntArraySerializer()).makeOrGet();

		this.refs = databaseManager.getDb()
				// this.databaseManager.getAuxDb("edges-refs")
				.createTreeMap("edges-refs-" + sourceSort.getName())
				.keySerializer(BTreeKeySerializer.ZERO_OR_POSITIVE_INT)
				.valueSerializer(
						new Serializer.CompressionWrapper<>(
								Serializer.BYTE_ARRAY)).makeOrGet();

		this.values = databaseManager.getDb()
				// this.databaseManager.getAuxDb("edges-values")
				.createTreeMap("edges-values-" + sourceSort.getName())
				.keySerializer(BTreeKeySerializer.ZERO_OR_POSITIVE_INT)
				.valueSerializer(
						new Serializer.CompressionWrapper<>(
								Serializer.BYTE_ARRAY)).makeOrGet();

	}

	public void updateEdges(EdgeContainer edgeContainer) {
		try {
			EdgeContainerForSerialization ecfs = new EdgeContainerForSerialization(
					edgeContainer, databaseManager);
			this.properties.put(ecfs.getSourceId(), ecfs.getProperties());
			this.refs.put(ecfs.getSourceId(), ecfs.getRefs());
			this.values.put(ecfs.getSourceId(), ecfs.getValues());
		} catch (IOException e) {
			throw new RuntimeException(e.toString(), e);
		}
	}

	public EdgeContainer getEdgeContainer(int id) {
		return new EdgeContainerFromSerialization(id, this);
	}

	@Override
	public Iterator<EdgeContainer> iterator() {
		return new LazyOuterObjectIterator<>(this.properties.values()
				.iterator(), this);
	}

	@Override
	public EdgeContainer getOuterObject(int[] properties) {
		return new EdgeContainerFromSerialization(properties, this);
	}

	public DatabaseManager getDatabaseManager() {
		return this.databaseManager;
	}

	public Sort getSourceSort() {
		return this.sourceSort;
	}

	int[] getProperties(int sourceId) {
		return this.properties.get(sourceId);
	}

	byte[] getRefs(int sourceId) {
		return this.refs.get(sourceId);
	}

	byte[] getValues(int sourceId) {
		return this.values.get(sourceId);
	}
}
