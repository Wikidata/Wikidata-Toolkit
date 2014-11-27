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

import org.wikidata.wdtk.storage.datamodel.EdgeContainer;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer.PropertyTargets;
import org.wikidata.wdtk.storage.datamodel.PropertyValuePair;
import org.wikidata.wdtk.storage.datamodel.Value;
import org.wikidata.wdtk.storage.db.PropertySignature;

public class LazyEdgeContainer implements EdgeContainer,
		Iterator<PropertyTargets> {

	class LazyPropertyTargets implements PropertyTargets,
			Iterator<TargetQualifiers> {

		int iTarget;
		final int propertyId;
		final int[][] targets;

		PropertySignature propertySignature = null;

		public LazyPropertyTargets(int propertyId, int[][] targets) {
			this.propertyId = propertyId;
			this.targets = targets;
		}

		@Override
		public Iterator<TargetQualifiers> iterator() {
			this.iTarget = -1;
			return this;
		}

		@Override
		public String getProperty() {
			return getPropertySignature().getPropertyName();
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
					getPropertySignature().getRangeId());
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		PropertySignature getPropertySignature() {
			if (this.propertySignature == null) {
				this.propertySignature = LazyEdgeContainer.this.edgeContainerIndex
						.getDatabaseManager().fetchPropertySignature(
								this.propertyId);
			}
			return this.propertySignature;
		}

	}

	class LazyTargetQualifiers implements TargetQualifiers,
			Iterable<PropertyValuePair>, Iterator<PropertyValuePair>,
			PropertyValuePair {

		final int[] targetQualifiers;
		final int sortId;
		final int qualifierCount;

		int iQualifier;
		PropertySignature qualifierPropertySignature;

		public LazyTargetQualifiers(int[] targetQualifiers, int sortId) {
			this.targetQualifiers = targetQualifiers;
			this.sortId = sortId;
			this.qualifierCount = (this.targetQualifiers.length - 1) / 2;
		}

		@Override
		public Value getTarget() {
			return LazyEdgeContainer.this.edgeContainerIndex
					.getDatabaseManager().fetchValue(this.targetQualifiers[0],
							sortId);
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
			this.qualifierPropertySignature = null;
			return this;
		}

		@Override
		public boolean hasNext() {
			return this.iQualifier + 1 < this.qualifierCount;
		}

		@Override
		public PropertyValuePair next() {
			this.iQualifier++;
			this.qualifierPropertySignature = null;
			return this;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getProperty() {
			return getPropertySignature().getPropertyName();
		}

		@Override
		public Value getValue() {
			return LazyEdgeContainer.this.edgeContainerIndex
					.getDatabaseManager().fetchValue(
							this.targetQualifiers[2 + (2 * this.iQualifier)],
							getPropertySignature().getRangeId());
		}

		PropertySignature getPropertySignature() {
			if (this.qualifierPropertySignature == null) {
				this.qualifierPropertySignature = LazyEdgeContainer.this.edgeContainerIndex
						.getDatabaseManager()
						.fetchPropertySignature(
								this.targetQualifiers[1 + (2 * this.iQualifier)]);
			}
			return this.qualifierPropertySignature;
		}

	}

	final EdgeContainerForSerialization ecfs;
	final EdgeContainerIndex edgeContainerIndex;

	int iProperty;

	public LazyEdgeContainer(EdgeContainerForSerialization ecfs,
			EdgeContainerIndex edgeContainerIndex) {
		this.ecfs = ecfs;
		this.edgeContainerIndex = edgeContainerIndex;
	}

	@Override
	public Iterator<PropertyTargets> iterator() {
		this.iProperty = -1;
		return this;
	}

	@Override
	public Value getSource() {
		return this.edgeContainerIndex.databaseManager.fetchValue(
				this.ecfs.source, this.edgeContainerIndex.getSourceSort()
						.getName());
	}

	@Override
	public int getEdgeCount() {
		return this.ecfs.getProperties().length;
	}

	@Override
	public boolean hasNext() {
		return this.iProperty + 1 < this.ecfs.getProperties().length;
	}

	@Override
	public PropertyTargets next() {
		this.iProperty++;
		return new LazyPropertyTargets(
				this.ecfs.getProperties()[LazyEdgeContainer.this.iProperty],
				this.ecfs.getTargetQualifiers()[LazyEdgeContainer.this.iProperty]);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
