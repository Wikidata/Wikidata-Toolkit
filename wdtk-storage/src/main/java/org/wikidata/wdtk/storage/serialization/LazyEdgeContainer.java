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

import java.util.Iterator;

import org.wikidata.wdtk.storage.datamodel.EdgeContainer;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer.PropertyTargets;
import org.wikidata.wdtk.storage.datamodel.PropertyValuePair;
import org.wikidata.wdtk.storage.datamodel.Sort;
import org.wikidata.wdtk.storage.datamodel.Value;
import org.wikidata.wdtk.storage.db.DatabaseManager;
import org.wikidata.wdtk.storage.db.PropertySignature;

public class LazyEdgeContainer implements EdgeContainer,
		Iterator<PropertyTargets> {

	class LazyPropertyTargets implements PropertyTargets,
			Iterator<TargetQualifiers> {

		int iTarget;
		final PropertySignature propertySignature;
		final Object[][] targets;

		public LazyPropertyTargets(PropertySignature propertySignature,
				Object[][] targets) {
			this.propertySignature = propertySignature;
			this.targets = targets;
		}

		@Override
		public Iterator<TargetQualifiers> iterator() {
			this.iTarget = -1;
			return this;
		}

		@Override
		public String getProperty() {
			return this.propertySignature.getPropertyName();
		}

		@Override
		public int getTargetCount() {
			return this.targets.length;
		}

		@Override
		public boolean hasNext() {
			return this.iTarget + 1 < this.targets.length;
		}

		@Override
		public TargetQualifiers next() {
			this.iTarget++;
			return new LazyTargetQualifiers(this.targets[this.iTarget],
					this.propertySignature.getRangeId());
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	class LazyTargetQualifiers implements TargetQualifiers,
			Iterable<PropertyValuePair>, Iterator<PropertyValuePair>,
			PropertyValuePair {

		final Object[] targetQualifiers;
		final int sortId;
		final int qualifierCount;

		int iQualifier;
		PropertySignature currentQualifierPropertySignature;

		public LazyTargetQualifiers(Object[] targetQualifiers, int sortId) {
			this.targetQualifiers = targetQualifiers;
			this.sortId = sortId;
			this.qualifierCount = (this.targetQualifiers.length - 1) / 2;
		}

		@Override
		public Value getTarget() {
			if (this.targetQualifiers[0] instanceof Integer) {
				return LazyEdgeContainer.this.databaseManager.fetchValue(
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
			Object valueObject = this.targetQualifiers[2 + (2 * this.iQualifier)];
			if (valueObject instanceof Integer) {
				return LazyEdgeContainer.this.databaseManager.fetchValue(
						(Integer) valueObject, getCurrentPropertySignature()
								.getRangeId());
			} else {
				return (Value) valueObject;
			}

		}

		PropertySignature getCurrentPropertySignature() {
			return (PropertySignature) this.targetQualifiers[1 + (2 * this.iQualifier)];
		}

	}

	final DatabaseManager databaseManager;
	final int sourceId;
	final Sort sourceSort;
	final PropertySignature[] properties;
	final Object[][][] targetQualifiers;

	int iProperty;

	public LazyEdgeContainer(int sourceId, Sort sourceSort,
			PropertySignature[] properties, Object[][][] targetQualifiers,
			DatabaseManager databaseManager) {
		this.sourceId = sourceId;
		this.sourceSort = sourceSort;
		this.properties = properties;
		this.targetQualifiers = targetQualifiers;
		this.databaseManager = databaseManager;
	}

	@Override
	public Iterator<PropertyTargets> iterator() {
		this.iProperty = -1;
		return this;
	}

	@Override
	public Value getSource() {
		return this.databaseManager.fetchValue(this.sourceId,
				this.sourceSort.getName());
	}

	@Override
	public int getEdgeCount() {
		return this.properties.length;
	}

	@Override
	public boolean hasNext() {
		return this.iProperty + 1 < this.properties.length;
	}

	@Override
	public PropertyTargets next() {
		this.iProperty++;
		return new LazyPropertyTargets(this.properties[this.iProperty],
				this.targetQualifiers[this.iProperty]);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
