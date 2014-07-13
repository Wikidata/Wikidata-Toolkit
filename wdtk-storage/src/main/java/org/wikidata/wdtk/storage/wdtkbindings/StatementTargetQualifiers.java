package org.wikidata.wdtk.storage.wdtkbindings;

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

import org.wikidata.wdtk.datamodel.interfaces.NoValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer.TargetQualifiers;
import org.wikidata.wdtk.storage.datamodel.PropertyValuePair;
import org.wikidata.wdtk.storage.datamodel.Sort;
import org.wikidata.wdtk.storage.datamodel.StringValueImpl;
import org.wikidata.wdtk.storage.datamodel.Value;

public class StatementTargetQualifiers implements TargetQualifiers,
		Iterable<PropertyValuePair>, Iterator<PropertyValuePair>,
		PropertyValuePair {

	final Statement statement;

	boolean rankDone = false;
	final WdtkAdaptorHelper helpers;
	final Iterator<Snak> snakIterator;
	Iterator<? extends Reference> referenceIterator;

	String propertyForPropertyValuePair;
	Value valueForPropertyValuePair;

	public StatementTargetQualifiers(Statement statement,
			WdtkAdaptorHelper helpers) {
		this.statement = statement;
		this.helpers = helpers;
		this.referenceIterator = statement.getReferences().iterator();
		this.snakIterator = statement.getClaim().getAllQualifiers();
	}

	@Override
	public Value getTarget() {
		if (this.statement.getClaim().getMainSnak() instanceof ValueSnak) {
			return ((ValueSnak) this.statement.getClaim().getMainSnak())
					.getValue().accept(this.helpers.getValueAdaptor());
		} else if (this.statement.getClaim().getMainSnak() instanceof SomeValueSnak) {
			return null;
		} else if (this.statement.getClaim().getMainSnak() instanceof NoValueSnak) {
			return null; // TODO this is not correct
		} else {
			throw new RuntimeException("Unexpected snak type "
					+ this.statement.getClaim().getMainSnak().getClass());
		}

	}

	@Override
	public Iterable<PropertyValuePair> getQualifiers() {
		return this;
	}

	@Override
	public Iterator<PropertyValuePair> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		return (this.snakIterator.hasNext())
				|| this.referenceIterator.hasNext() || !this.rankDone;
	}

	@Override
	public PropertyValuePair next() {
		if (this.snakIterator.hasNext()) {
			Snak nextSnak = this.snakIterator.next();
			nextSnak.accept(this.helpers.getSnakAdaptor());
			return this.helpers.getSnakAdaptor();
		} else if (!this.rankDone) { // rank
			this.propertyForPropertyValuePair = WdtkSorts.PROP_RANK;
			this.valueForPropertyValuePair = new StringValueImpl(this.statement
					.getRank().name(), Sort.SORT_STRING);
			this.rankDone = true;
			return this;
		} else { // references
			this.propertyForPropertyValuePair = WdtkSorts.PROP_REFERENCE;
			this.valueForPropertyValuePair = new ReferenceAdaptor(
					this.referenceIterator.next(), this.helpers);
			return this;
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getProperty() {
		return propertyForPropertyValuePair;
	}

	@Override
	public Value getValue() {
		return valueForPropertyValuePair;
	}

}
