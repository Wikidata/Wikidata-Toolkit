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

import org.mapdb.DataOutput2;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer.PropertyTargets;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer.TargetQualifiers;
import org.wikidata.wdtk.storage.datamodel.PropertyValuePair;
import org.wikidata.wdtk.storage.datamodel.Sort;
import org.wikidata.wdtk.storage.db.DatabaseManager;

public class EdgeContainerForSerialization {

	final DatabaseManager databaseManager;
	final int[] properties;
	final DataOutput2 values = new DataOutput2();
	final DataOutput2 refs = new DataOutput2();

	public EdgeContainerForSerialization(EdgeContainer edgeContainer,
			DatabaseManager databaseManager) throws IOException {
		this.databaseManager = databaseManager;

		int sourceId = databaseManager.getOrCreateValueId(edgeContainer
				.getSource());
		int edgeCount = edgeContainer.getEdgeCount();
		this.properties = new int[2 + 2 * edgeContainer.getEdgeCount()];
		this.properties[0] = sourceId;
		this.properties[1] = edgeCount;

		int iProperty = 0;
		for (PropertyTargets pts : edgeContainer) {
			encodePropertyTargets(pts, iProperty, edgeContainer.getSource()
					.getSort());
			iProperty++;
		}
	}

	public int getSourceId() {
		return this.properties[0];
	}

	public int[] getProperties() {
		return this.properties;
	}

	public byte[] getRefs() {
		return this.refs.copyBytes();
	}

	public byte[] getValues() {
		return this.values.copyBytes();
	}

	protected void encodePropertyTargets(PropertyTargets pts, int iProperty,
			Sort sourceSort) throws IOException {
		String valueSortName = null;
		boolean isRefSort = false;
		for (TargetQualifiers tqs : pts) {

			if (valueSortName == null) {
				valueSortName = tqs.getTarget().getSort().getName();
				isRefSort = this.databaseManager.getSortSchema().useDictionary(
						valueSortName);

				this.properties[2 + 2 * iProperty] = databaseManager
						.getOrCreatePropertyId(pts.getProperty(),
								sourceSort.getName(), valueSortName);

				if (isRefSort) {
					this.properties[3 + 2 * iProperty] = this.refs.pos;
					this.refs.writeInt(pts.getTargetCount());
				} else {
					this.properties[3 + 2 * iProperty] = this.values.pos;
					this.values.writeInt(pts.getTargetCount());
				}
			}

			if (isRefSort) {
				encodeRefTargetQualifiers(tqs, valueSortName);
			} else {
				encodeValueTargetQualifiers(tqs, valueSortName);
			}

		}
	}

	protected void encodeValueTargetQualifiers(TargetQualifiers tqs,
			String valueSortName) throws IOException {

		this.values.writeInt(tqs.getQualifierCount());
		Serialization.serializeValue(this.values, tqs.getTarget(),
				databaseManager);

		for (PropertyValuePair pvp : tqs.getQualifiers()) {
			this.values.writeInt(databaseManager.getOrCreatePropertyId(
					pvp.getProperty(), valueSortName, pvp.getValue().getSort()
							.getName()));
			Serialization.serializeValue(this.values, pvp.getValue(),
					databaseManager);
		}
	}

	protected void encodeRefTargetQualifiers(TargetQualifiers tqs,
			String valueSortName) throws IOException {

		this.refs.writeInt(tqs.getQualifierCount());
		// We already know that the value is in a dictionary:
		this.refs.writeInt(this.databaseManager.getOrCreateValueId(tqs
				.getTarget()));

		for (PropertyValuePair pvp : tqs.getQualifiers()) {
			this.refs.writeInt(databaseManager.getOrCreatePropertyId(
					pvp.getProperty(), valueSortName, pvp.getValue().getSort()
							.getName()));
			if (databaseManager.getSortSchema().useDictionary(
					pvp.getValue().getSort().getName())) {
				this.refs.writeInt(this.databaseManager.getOrCreateValueId(pvp
						.getValue()));
			} else {
				this.refs.writeInt(this.values.pos);
				Serialization.serializeInlineValue(this.values, pvp.getValue(),
						databaseManager);
			}
		}
	}
}
