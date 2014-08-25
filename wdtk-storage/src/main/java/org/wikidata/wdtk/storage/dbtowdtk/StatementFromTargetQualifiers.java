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

import org.wikidata.wdtk.datamodel.interfaces.Claim;
import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
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

public class StatementFromTargetQualifiers implements Statement, Claim {

	final EntityDocument parentEntityDocument;
	final String propertyName;
	final TargetQualifiers targetQualifiers;
	final DataObjectFactory dataObjectFactory;

	StringValue rankStringValue = null;
	List<ObjectValue> referenceObjectValues;
	Map<String, List<PropertyValuePair>> qualifierPropertyValuePairs;

	List<SnakGroup> qualifiers = null;
	List<Reference> references = null;

	public StatementFromTargetQualifiers(TargetQualifiers targetQualifiers,
			String propertyName, EntityDocument parentEntityDocument,
			DataObjectFactory dataObjectFactory) {

		this.targetQualifiers = targetQualifiers;
		this.parentEntityDocument = parentEntityDocument;
		this.propertyName = propertyName;
		this.dataObjectFactory = dataObjectFactory;

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
		return this;
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
				this.references.add(new ReferenceFromObjectValue(rov,
						this.dataObjectFactory));
			}
		}

		return this.references;
	}

	@Override
	public String getStatementId() {
		return null; // not stored
	}

	@Override
	public EntityIdValue getSubject() {
		return this.parentEntityDocument.getEntityId();
	}

	@Override
	public Snak getMainSnak() {
		return new ValueSnakFromValue(this.propertyName,
				this.targetQualifiers.getTarget());
	}

	@Override
	public List<SnakGroup> getQualifiers() {
		if (this.qualifiers == null) {
			this.qualifiers = new ArrayList<>(
					this.qualifierPropertyValuePairs.size());
			for (List<PropertyValuePair> pvpList : this.qualifierPropertyValuePairs
					.values()) {
				List<Snak> snaks = new ArrayList<>(pvpList.size());
				for (PropertyValuePair pvp : pvpList) {
					snaks.add(new ValueSnakFromValue(pvp.getProperty(), pvp
							.getValue()));
				}
				this.qualifiers.add(this.dataObjectFactory.getSnakGroup(snaks));
			}
		}

		return this.qualifiers;
	}

	@Override
	public Iterator<Snak> getAllQualifiers() {
		return new NestedIterator<>(this.getQualifiers());
	}

}
