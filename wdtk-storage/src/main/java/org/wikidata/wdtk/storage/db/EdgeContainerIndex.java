package org.wikidata.wdtk.storage.db;

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

import org.mapdb.Bind;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer.PropertyTargets;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer.TargetQualifiers;
import org.wikidata.wdtk.storage.datamodel.PropertyValuePair;
import org.wikidata.wdtk.storage.datamodel.Sort;

public class EdgeContainerIndex
		implements
		Iterable<EdgeContainer>,
		InnerToOuterObjectConverter<EdgeContainerForSerialization, EdgeContainer> {

	final Sort sourceSort;
	final DatabaseManager databaseManager;

	final Bind.MapWithModificationListener<Long, EdgeContainerForSerialization> values;

	public EdgeContainerIndex(Sort sourceSort, DatabaseManager databaseManager) {
		this.sourceSort = sourceSort;
		this.databaseManager = databaseManager;

		this.values = databaseManager.getDb()
				.createHashMap("edges-spo-" + sourceSort.getName())
				.valueSerializer(new EdgeContainerSerializer()).makeOrGet();

	}

	public void updateEdges(EdgeContainer edgeContainer) {
		long source = this.databaseManager.getOrCreateValueId(edgeContainer
				.getSource());
		this.values.put(source,
				getEdgeContainerForSerialization(edgeContainer, source));
	}

	public EdgeContainer getEdgeContainer(long id) {
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

	EdgeContainerForSerialization getEdgeContainerForSerialization(
			EdgeContainer edgeContainer, long source) {

		int propertyCount = edgeContainer.getEdgeCount();

		long[] properties = new long[propertyCount];
		long[][][] targetQualifiers = new long[propertyCount][][];
		int i = 0;
		for (PropertyTargets pts : edgeContainer) {
			targetQualifiers[i] = new long[pts.getTargetCount()][];

			int j = 0;
			String rangeSort = null;
			for (TargetQualifiers tqs : pts) {
				if (j == 0) {
					rangeSort = tqs.getTarget().getSort().getName();
				}
				targetQualifiers[i][j] = new long[2 * tqs.getQualifierCount() + 1];
				targetQualifiers[i][j][0] = this.databaseManager
						.getOrCreateValueId(tqs.getTarget());
				int k = 0;
				for (PropertyValuePair pvp : tqs.getQualifiers()) {
					targetQualifiers[i][j][2 * k + 1] = this.databaseManager
							.getOrCreatePropertyId(pvp.getProperty(),
									rangeSort, pvp.getValue().getSort()
											.getName());
					targetQualifiers[i][j][2 * k + 2] = this.databaseManager
							.getOrCreateValueId(pvp.getValue());
					k++;
				}
				j++;
			}

			properties[i] = this.databaseManager.getOrCreatePropertyId(
					pts.getProperty(), this.sourceSort.getName(), rangeSort);
			i++;
		}

		return new EdgeContainerForSerialization(source, properties,
				targetQualifiers);
	}
}
