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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.Claim;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer.TargetQualifiers;
import org.wikidata.wdtk.storage.datamodel.ObjectValue;
import org.wikidata.wdtk.storage.datamodel.PropertyValuePair;
import org.wikidata.wdtk.storage.datamodel.StringValue;
import org.wikidata.wdtk.storage.wdtkbindings.WdtkSorts;
import org.wikidata.wdtk.util.NestedIterator;

public class StatementFromTargetQualifiers implements Statement {

	private class ClaimFromTargetQualifiers implements Claim {

		@Override
		public EntityIdValue getSubject() {
			return StatementFromTargetQualifiers.this.parentEntityDocument
					.getEntityId();
		}

		@Override
		public Snak getMainSnak() {
			return new ValueSnakFromValue(
					StatementFromTargetQualifiers.this.propertyName,
					StatementFromTargetQualifiers.this.targetQualifiers
					.getTarget());
		}

		@Override
		public List<SnakGroup> getQualifiers() {
			if (StatementFromTargetQualifiers.this.qualifiers == null) {
				StatementFromTargetQualifiers.this.qualifiers = new ArrayList<>(
						StatementFromTargetQualifiers.this.qualifierPropertyValuePairs
						.size());
				for (List<PropertyValuePair> pvpList : StatementFromTargetQualifiers.this.qualifierPropertyValuePairs
						.values()) {
					List<Snak> snaks = new ArrayList<>(pvpList.size());
					for (PropertyValuePair pvp : pvpList) {
						snaks.add(new ValueSnakFromValue(pvp.getProperty(), pvp
								.getValue()));
					}
					StatementFromTargetQualifiers.this.qualifiers.add(Datamodel
							.makeSnakGroup(snaks));
				}
			}

			return StatementFromTargetQualifiers.this.qualifiers;
		}

		@Override
		public Iterator<Snak> getAllQualifiers() {
			return new NestedIterator<>(this.getQualifiers());
		}

		@Override
		public int hashCode() {
			return Hash.hashCode(this);
		}

		@Override
		public boolean equals(Object obj) {
			return Equality.equalsClaim(this, obj);
		}

		@Override
		public String toString() {
			return ToString.toString(this);
		}

	}

	final EntityDocument parentEntityDocument;
	final String propertyName;
	final TargetQualifiers targetQualifiers;

	StringValue rankStringValue = null;
	List<ObjectValue> referenceObjectValues;
	Map<String, List<PropertyValuePair>> qualifierPropertyValuePairs;

	List<SnakGroup> qualifiers = null;
	List<Reference> references = null;

	public StatementFromTargetQualifiers(TargetQualifiers targetQualifiers,
			String propertyName, EntityDocument parentEntityDocument) {

		this.targetQualifiers = targetQualifiers;
		this.parentEntityDocument = parentEntityDocument;
		this.propertyName = propertyName;

		this.referenceObjectValues = new ArrayList<>();
		this.qualifierPropertyValuePairs = new HashMap<>(
				targetQualifiers.getQualifierCount());

		for (PropertyValuePair pvp : targetQualifiers.getQualifiers()) {
			switch (pvp.getProperty()) {
			case WdtkSorts.PROP_RANK:
				this.rankStringValue = (StringValue) pvp.getValue();
				break;
			case WdtkSorts.PROP_REFERENCE:
				this.referenceObjectValues.add((ObjectValue) pvp.getValue());
				break;
			case WdtkSorts.PROP_NOVALUE:
				// TODO
				break;
			case WdtkSorts.PROP_SOMEVALUE:
				// TODO
				break;
			default: // normal property
				List<PropertyValuePair> qualifierList = this.qualifierPropertyValuePairs
				.get(pvp.getProperty());
				if (qualifierList == null) {
					qualifierList = new ArrayList<>(1); // usually a short list
					this.qualifierPropertyValuePairs.put(pvp.getProperty(),
							qualifierList);
				}
				qualifierList.add(pvp);
			}
		}
	}

	@Override
	public Claim getClaim() {
		return new ClaimFromTargetQualifiers();
	}

	@Override
	public StatementRank getRank() {
		if (this.rankStringValue == null) {
			return StatementRank.NORMAL;
		} else if (StatementRank.PREFERRED.name().equals(
				this.rankStringValue.toString())) {
			return StatementRank.PREFERRED;
		} else if (StatementRank.DEPRECATED.name().equals(
				this.rankStringValue.toString())) {
			return StatementRank.DEPRECATED;
		} else {
			throw new RuntimeException("I don't know how to interpret rank "
					+ this.rankStringValue.getString());
		}
	}

	@Override
	public List<? extends Reference> getReferences() {
		if (this.references == null) {
			this.references = new ArrayList<>(this.referenceObjectValues.size());
			for (ObjectValue rov : this.referenceObjectValues) {
				this.references.add(new ReferenceFromObjectValue(rov));
			}
		}

		return this.references;
	}

	@Override
	public String getStatementId() {
		return null; // TODO not stored
	}

	@Override
	public int hashCode() {
		return Hash.hashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return Equality.equalsStatement(this, obj);
	}

	@Override
	public String toString() {
		return ToString.toString(this);
	}

}
