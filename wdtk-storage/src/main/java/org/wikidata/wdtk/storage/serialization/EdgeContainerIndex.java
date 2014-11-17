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
import java.util.Map.Entry;
import java.util.NavigableMap;

import org.mapdb.BTreeKeySerializer;
import org.mapdb.Serializer;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer;
import org.wikidata.wdtk.storage.datamodel.Sort;
import org.wikidata.wdtk.storage.db.DatabaseManager;
import org.wikidata.wdtk.storage.db.InnerToOuterObjectConverter;
import org.wikidata.wdtk.storage.db.LazyOuterObjectIterator;
import org.wikidata.wdtk.util.Timer;

public class EdgeContainerIndex implements
Iterable<EdgeContainerFromSerialization>,
InnerToOuterObjectConverter<int[], EdgeContainerFromSerialization> {

	final Sort sourceSort;
	final DatabaseManager databaseManager;

	final NavigableMap<Integer, int[]> properties;
	final NavigableMap<Integer, byte[]> values;
	final NavigableMap<Integer, byte[]> refs;

	// Map<Integer, int[]> propertyCache = null;
	int[][] propertyCache = null;
	byte[][] refCache = null;

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

		// preloadRefs();
		// preloadProperties();
	}

	public void preloadProperties() {
		if (this.propertyCache != null) {
			return;
		}

		System.out.println("Preloading properties ...");
		Timer.startNamedTimer("Property preload");

		int maxKey = this.properties.lastKey();
		this.propertyCache = new int[maxKey + 1][];

		int size = 0;
		int count = 0;
		for (Entry<Integer, int[]> entry : this.properties.entrySet()) {
			this.propertyCache[entry.getKey()] = entry.getValue();
			size += entry.getValue().length;
			count++;
			if (count % 100000 == 0) {
				System.out.println("Preloaded " + count
						+ " edge property sets with " + size + " ints ...");
			}
		}

		Timer.stopNamedTimer("Property preload");
		System.out.println("Finished preloading " + count
				+ " edge property sets with " + size + " ints.");
		System.out.println(Timer.getNamedTimer("Property preload"));

		// this.propertyCache = DBMaker.newMemoryDirectDB().transactionDisable()
		// .make()
		// .createTreeMap("edges-props-cache-" + sourceSort.getName())
		// .keySerializer(BTreeKeySerializer.ZERO_OR_POSITIVE_INT)
		// .valueSerializer(new IntArraySerializer())
		// // .expireStoreSize(size)
		// // .counterEnable()
		// .make();

		// int size = 0;
		// int count = 0;
		// for (Entry<Integer, int[]> entry : this.properties.entrySet()) {
		// size += entry.getValue().length;
		// count++;
		// this.propertyCache.put(entry.getKey(), entry.getValue());
		// if (count % 100000 == 0) {
		// System.oMerzig(Saar)Merzig(Saar)ut.println("Preloaded " + count
		// + " edge property sets with " + size + " ints ...");
		// }
		// }
		// System.out.println("Preloaded " + count + " edge property sets with "
		// + size + " ints ...");
	}

	public void preloadRefs() {
		if (this.refCache != null) {
			return;
		}

		System.out.println("Preloading refs ...");
		Timer.startNamedTimer("Ref preload");

		int maxKey = this.refs.lastKey();
		this.refCache = new byte[maxKey + 1][];

		int size = 0;
		int count = 0;
		for (Entry<Integer, byte[]> entry : this.refs.entrySet()) {
			this.refCache[entry.getKey()] = entry.getValue();
			size += entry.getValue().length;
			count++;
			if (count % 100000 == 0) {
				System.out.println("Preloaded " + count
						+ " edge ref sets with " + size + " bytes ...");
			}
		}
		Timer.stopNamedTimer("Ref preload");
		System.out.println("Finished preloading " + count
				+ " edge ref sets with " + size + " bytes.");
		System.out.println(Timer.getNamedTimer("Ref preload"));
	}

	public void updateEdges(EdgeContainer edgeContainer) {
		try {
			EdgeContainerForSerialization ecfs = new EdgeContainerForSerialization(
					edgeContainer, databaseManager);
			this.properties.put(ecfs.getSourceId(), ecfs.getProperties());
			this.refs.put(ecfs.getSourceId(), ecfs.getRefs());
			this.values.put(ecfs.getSourceId(), ecfs.getValues());

			if (this.propertyCache != null) { // TODO: be smarter
				this.propertyCache = null;
			}
			if (this.refCache != null) { // TODO: be smarter
				this.refCache = null;
			}
		} catch (IOException e) {
			throw new RuntimeException(e.toString(), e);
		}
	}

	public EdgeContainer getEdgeContainer(int id) {
		return new EdgeContainerFromSerialization(id, this);
	}

	@Override
	public Iterator<EdgeContainerFromSerialization> iterator() {
		if (this.propertyCache != null) {
			return new LazyOuterObjectIterator<>(new IntIntArrayIterator(
					this.propertyCache), this);
		} else {
			return new LazyOuterObjectIterator<>(this.properties.values()
					.iterator(), this);
		}
	}

	@Override
	public EdgeContainerFromSerialization getOuterObject(int[] properties) {
		return new EdgeContainerFromSerialization(properties, this);
	}

	public DatabaseManager getDatabaseManager() {
		return this.databaseManager;
	}

	public Sort getSourceSort() {
		return this.sourceSort;
	}

	int[] getProperties(int sourceId) {
		if (this.propertyCache != null && sourceId < this.propertyCache.length) {
			return this.propertyCache[sourceId];
		} else {
			return this.properties.get(sourceId);
		}
	}

	byte[] getRefs(int sourceId) {
		if (this.refCache != null && sourceId < this.refCache.length) {
			return this.refCache[sourceId];
		} else {
			return this.refs.get(sourceId);
		}
	}

	byte[] getValues(int sourceId) {
		return this.values.get(sourceId);
	}
}
