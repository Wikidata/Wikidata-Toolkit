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

import org.mapdb.DataInput2;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer.PropertyTargets;
import org.wikidata.wdtk.storage.datamodel.PropertyValuePair;
import org.wikidata.wdtk.storage.datamodel.Sort;
import org.wikidata.wdtk.storage.datamodel.Value;
import org.wikidata.wdtk.storage.db.DatabaseManager;
import org.wikidata.wdtk.storage.db.PropertySignature;

public class EdgeContainerFromSerialization implements EdgeContainer,
Iterator<PropertyTargets> {

	class PropertyTargetsFromSerialization implements PropertyTargets,
	Iterator<TargetQualifiers> {

		final int iProperty;
		PropertySignature propertySignature = null;
		Sort valueSort = null;
		boolean isRefSort;
		int targetIdx = -1;
		int targetCount;
		DataInput2 targetInput;

		int iTarget;

		public PropertyTargetsFromSerialization(int iProperty) {
			this.iProperty = iProperty;
		}

		@Override
		public Iterator<TargetQualifiers> iterator() {
			initTargetData();
			this.iTarget = -1;
			return this;
		}

		@Override
		public boolean hasNext() {
			return this.iTarget + 1 < this.targetCount;
		}

		@Override
		public TargetQualifiers next() {
			this.iTarget++;

			try {
				if (this.isRefSort) {
					return getNextRefTargetQualifiers();
				} else {
					return getNextValueTargetQualifiers();
				}
			} catch (IOException e) {
				throw new RuntimeException(e.toString(), e);
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getProperty() {
			return getPropertySignature().getPropertyName();
		}

		public int getPropertyId() {
			return this.iProperty;
		}

		@Override
		public int getTargetCount() {
			initTargetData();
			return this.targetCount;
		}

		private PropertySignature getPropertySignature() {
			if (this.propertySignature == null) {
				this.propertySignature = getDatabaseManager()
						.fetchPropertySignature(
								getPropertyIdInternal(iProperty));
			}
			return this.propertySignature;
		}

		private void initSortInformation() {
			this.valueSort = getDatabaseManager().getSortSchema().getSort(
					getPropertySignature().getRangeId());
			this.isRefSort = getDatabaseManager().getSortSchema()
					.useDictionary(this.valueSort.getName());
		}

		private void initTargetData() {
			if (this.targetIdx < 0) {
				initSortInformation();
				this.targetIdx = getProperties()[3 + 2 * iProperty];
				// TODO Use DataInputByteArray as soon as available in stable
				// MapDB:
				// new DataIO.DataInputByteArray(getValues()); etc
				if (this.isRefSort) {
					this.targetInput = new DataInput2(getRefs());
				} else {
					this.targetInput = new DataInput2(getValues());
				}
			}

			// Do that in any case to reset the position of targetInput:
			try {
				this.targetInput.pos = this.targetIdx;
				this.targetCount = this.targetInput.readInt();
			} catch (IOException e) {
				throw new RuntimeException(e.toString(), e);
			}
		}

		private TargetQualifiers getNextValueTargetQualifiers()
				throws IOException {
			int qualifierCount = this.targetInput.readInt();

			Object[] targetQualifiers = new Object[1 + 2 * qualifierCount];

			targetQualifiers[0] = Serialization.deserializeInlineValue(
					this.targetInput, this.valueSort, getDatabaseManager());

			for (int k = 0; k < qualifierCount; k++) {
				int qualifierPropertyId = this.targetInput.readInt();
				PropertySignature qualifierSignature = getDatabaseManager()
						.fetchPropertySignature(qualifierPropertyId);
				targetQualifiers[2 * k + 1] = qualifierSignature;
				if (qualifierSignature == null) { // DEBUG
					System.err.println("Something bad has happened: "
							+ qualifierPropertyId
							+ " not found. We are in object " + sourceId
							+ " reading statement " + this.iTarget
							+ " of property "
							+ getProperties()[2 + 2 * iProperty] + " ("
							+ this.propertySignature.getPropertyName()
							+ "), processing the qualifier " + k + " of "
							+ qualifierCount);
				}
				Sort qualifierSort = getDatabaseManager().getSortSchema()
						.getSort(qualifierSignature.getRangeId());
				if (getDatabaseManager().getSortSchema().useDictionary(
						qualifierSort.getName())) {
					targetQualifiers[2 * k + 2] = this.targetInput.readInt();
				} else {
					targetQualifiers[2 * k + 2] = Serialization
							.deserializeInlineValue(this.targetInput,
									qualifierSort, getDatabaseManager());
				}
			}

			return new ValueTargetQualifiersFromSerialization(targetQualifiers,
					this.propertySignature.getRangeId());
		}

		private TargetQualifiers getNextRefTargetQualifiers()
				throws IOException {
			int qualifierCount = this.targetInput.readInt();
			int[] targetQualifiers = new int[1 + 2 * qualifierCount];
			targetQualifiers[0] = this.targetInput.readInt();

			for (int k = 0; k < qualifierCount; k++) {
				targetQualifiers[2 * k + 1] = this.targetInput.readInt();
				targetQualifiers[2 * k + 2] = this.targetInput.readInt();
			}

			return new RefTargetQualifiersFromSerialization(targetQualifiers,
					this.propertySignature.getRangeId());
		}
	}

	class ValueTargetQualifiersFromSerialization implements TargetQualifiers,
	Iterable<PropertyValuePair>, Iterator<PropertyValuePair>,
	PropertyValuePair {

		final Object[] targetQualifiers;
		final int sortId;
		final int qualifierCount;

		int iQualifier;

		public ValueTargetQualifiersFromSerialization(
				Object[] targetQualifiers, int sortId) {
			this.targetQualifiers = targetQualifiers;
			this.sortId = sortId;
			this.qualifierCount = (this.targetQualifiers.length - 1) / 2;
		}

		@Override
		public Value getTarget() {
			if (this.targetQualifiers[0] instanceof Integer) {
				return getDatabaseManager().fetchValue(
						(Integer) this.targetQualifiers[0], this.sortId);
			} else {
				return (Value) this.targetQualifiers[0];
			}
		}

		@Override
		public Iterable<PropertyValuePair> getQualifiers() {
			return this;
		}

		@Override
		public int getQualifierCount() {
			return this.qualifierCount;
		}

		@Override
		public Iterator<PropertyValuePair> iterator() {
			this.iQualifier = -1;
			return this;
		}

		@Override
		public boolean hasNext() {
			return this.iQualifier + 1 < this.qualifierCount;
		}

		@Override
		public PropertyValuePair next() {
			this.iQualifier++;
			return this;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getProperty() {
			return getCurrentPropertySignature().getPropertyName();
		}

		@Override
		public Value getValue() {
			Object valueObject = this.targetQualifiers[2 + (2 * this.iQualifier)];
			if (valueObject instanceof Integer) {
				return getDatabaseManager().fetchValue((Integer) valueObject,
						getCurrentPropertySignature().getRangeId());
			} else {
				return (Value) valueObject;
			}

		}

		PropertySignature getCurrentPropertySignature() {
			return (PropertySignature) this.targetQualifiers[1 + (2 * this.iQualifier)];
		}
	}

	class RefTargetQualifiersFromSerialization implements TargetQualifiers,
	Iterable<PropertyValuePair>, Iterator<PropertyValuePair>,
	PropertyValuePair {

		final int[] targetQualifiers;
		final int sortId;
		final int qualifierCount;

		int iQualifier;
		PropertySignature currentQualifierPropertySignature;

		public RefTargetQualifiersFromSerialization(int[] targetQualifiers,
				int sortId) {
			this.targetQualifiers = targetQualifiers;
			this.sortId = sortId;
			this.qualifierCount = (this.targetQualifiers.length - 1) / 2;
		}

		@Override
		public Value getTarget() {
			return getDatabaseManager().fetchValue(this.targetQualifiers[0],
					this.sortId);
		}

		@Override
		public Iterable<PropertyValuePair> getQualifiers() {
			return this;
		}

		@Override
		public int getQualifierCount() {
			return this.qualifierCount;
		}

		@Override
		public Iterator<PropertyValuePair> iterator() {
			this.iQualifier = -1;
			this.currentQualifierPropertySignature = null;
			return this;
		}

		@Override
		public boolean hasNext() {
			return this.iQualifier + 1 < this.qualifierCount;
		}

		@Override
		public PropertyValuePair next() {
			this.iQualifier++;
			this.currentQualifierPropertySignature = null;
			return this;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getProperty() {
			return getCurrentPropertySignature().getPropertyName();
		}

		@Override
		public Value getValue() {
			int valueObject = this.targetQualifiers[2 + (2 * this.iQualifier)];

			Sort currentSort = getDatabaseManager().getSortSchema().getSort(
					getCurrentPropertySignature().getRangeId());

			if (getDatabaseManager().getSortSchema().useDictionary(
					currentSort.getName())) {
				return getDatabaseManager().fetchValue(valueObject,
						getCurrentPropertySignature().getRangeId());
			} else {
				try {
					return deserializeValueFromIndex(valueObject, currentSort);
				} catch (IOException e) {
					throw new RuntimeException(e.toString(), e);
				}
			}

		}

		private PropertySignature getCurrentPropertySignature() {
			if (this.currentQualifierPropertySignature == null) {
				this.currentQualifierPropertySignature = getDatabaseManager()
						.fetchPropertySignature(
								this.targetQualifiers[1 + (2 * this.iQualifier)]);
			}
			return this.currentQualifierPropertySignature;
		}

		private Value deserializeValueFromIndex(int valueIndex, Sort sort)
				throws IOException {
			DataInput2 in = new DataInput2(getValues());
			in.pos = valueIndex;
			return Serialization.deserializeInlineValue(in, sort,
					getDatabaseManager());
		}
	}

	final int sourceId;
	final EdgeContainerIndex edgeContainerIndex;

	int[] properties = null;
	byte[] values = null;
	byte[] refs = null;

	int iProperty;

	public EdgeContainerFromSerialization(int[] properties,
			EdgeContainerIndex edgeContainerIndex) {
		this.properties = properties;
		this.sourceId = properties[0];
		this.edgeContainerIndex = edgeContainerIndex;
	}

	public EdgeContainerFromSerialization(int sourceId,
			EdgeContainerIndex edgeContainerIndex) {
		this.sourceId = sourceId;
		this.edgeContainerIndex = edgeContainerIndex;
	}

	@Override
	public Iterator<PropertyTargets> iterator() {
		this.iProperty = -1;
		return this;
	}

	@Override
	public boolean hasNext() {
		return this.iProperty + 1 < (getProperties().length - 1) / 2;
	}

	@Override
	public PropertyTargets next() {
		this.iProperty++;
		return new PropertyTargetsFromSerialization(this.iProperty);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Value getSource() {
		return getDatabaseManager().fetchValue(this.sourceId,
				this.edgeContainerIndex.getSourceSort().getName());
	}

	@Override
	public int getEdgeCount() {
		return getProperties()[1];
	}

	public int getPropertyIdInternal(int index) {
		return getProperties()[2 + 2 * index];
	}

	public PropertyTargetsFromSerialization getPropertyTargetsInternal(int index) {
		return new PropertyTargetsFromSerialization(index);
	}

	protected DatabaseManager getDatabaseManager() {
		return this.edgeContainerIndex.getDatabaseManager();
	}

	protected int[] getProperties() {
		if (this.properties == null) {
			this.properties = this.edgeContainerIndex
					.getProperties(this.sourceId);
		}
		return this.properties;
	}

	protected byte[] getRefs() {
		if (this.refs == null) {
			this.refs = this.edgeContainerIndex.getRefs(this.sourceId);
		}
		return this.refs;
	}

	protected byte[] getValues() {
		if (this.values == null) {
			this.values = this.edgeContainerIndex.getValues(this.sourceId);
		}
		return this.values;
	}

}
