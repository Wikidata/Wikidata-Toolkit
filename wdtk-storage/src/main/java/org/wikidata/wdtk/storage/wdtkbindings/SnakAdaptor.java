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

import org.wikidata.wdtk.datamodel.interfaces.NoValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.SnakVisitor;
import org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;
import org.wikidata.wdtk.storage.datamodel.PropertyValuePair;
import org.wikidata.wdtk.storage.datamodel.Value;

public class SnakAdaptor implements SnakVisitor<PropertyValuePair>,
		PropertyValuePair {

	// TODO delete
	interface MutablePropertyValuePair extends PropertyValuePair {
		void setProperty(String property);

		void setValue(Value value);
	}

	String currentProperty;
	Value currentValue;

	final WdtkAdaptorHelper helpers;

	public SnakAdaptor(WdtkAdaptorHelper helpers) {
		this.helpers = helpers;
	}

	@Override
	public PropertyValuePair visit(ValueSnak snak) {
		this.currentProperty = snak.getPropertyId().getIri();
		this.currentValue = snak.getValue().accept(
				this.helpers.getValueAdaptor());
		return this;
	}

	@Override
	public PropertyValuePair visit(SomeValueSnak snak) {
		this.currentProperty = snak.getPropertyId().getIri();
		this.currentValue = null;
		return this;
	}

	@Override
	public PropertyValuePair visit(NoValueSnak snak) {
		this.currentProperty = WdtkDatabaseManager.PROP_NOVALUE;
		this.currentValue = snak.getPropertyId().accept(
				this.helpers.getValueAdaptor());
		return this;
	}

	@Override
	public String getProperty() {
		return this.currentProperty;
	}

	@Override
	public Value getValue() {
		return this.currentValue;
	}

}
