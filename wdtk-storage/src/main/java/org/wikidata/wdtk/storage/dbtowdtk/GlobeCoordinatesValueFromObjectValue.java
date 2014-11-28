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

import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;
import org.wikidata.wdtk.storage.datamodel.LongValue;
import org.wikidata.wdtk.storage.datamodel.ObjectValue;
import org.wikidata.wdtk.storage.datamodel.PropertyValuePair;
import org.wikidata.wdtk.storage.datamodel.StringValue;
import org.wikidata.wdtk.storage.wdtkbindings.WdtkSorts;

public class GlobeCoordinatesValueFromObjectValue implements
GlobeCoordinatesValue {

	private static final long serialVersionUID = -7982463379083763692L;

	final ObjectValue value;

	String globe = null;
	long latitude;
	long longitude;
	long precision;

	public GlobeCoordinatesValueFromObjectValue(ObjectValue value) {
		this.value = value;
	}

	private void initialize() {
		if (this.globe == null) {
			for (PropertyValuePair pvp : this.value) {
				switch (pvp.getProperty()) {
				case WdtkSorts.PROP_COORDINATES_GLOBE:
					this.globe = ((StringValue) pvp.getValue()).getString();
					break;
				case WdtkSorts.PROP_COORDINATES_LATITUDE:
					this.latitude = ((LongValue) pvp.getValue()).getLong();
					break;
				case WdtkSorts.PROP_COORDINATES_LONGITUDE:
					this.longitude = ((LongValue) pvp.getValue()).getLong();
					break;
				case WdtkSorts.PROP_COORDINATES_PRECISION:
					this.precision = ((LongValue) pvp.getValue()).getLong();
					break;
				default:
					throw new RuntimeException("Unexpected property "
							+ pvp.getProperty() + " in coordinates record.");
				}
			}
		}
	}

	@Override
	public <T> T accept(ValueVisitor<T> valueVisitor) {
		return valueVisitor.visit(this);
	}

	@Override
	public long getLatitude() {
		initialize();
		return this.latitude;
	}

	@Override
	public long getLongitude() {
		initialize();
		return this.longitude;
	}

	@Override
	public long getPrecision() {
		initialize();
		return this.precision;
	}

	@Override
	public String getGlobe() {
		initialize();
		return this.globe;
	}

	@Override
	public int hashCode() {
		return Hash.hashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return Equality.equalsGlobeCoordinatesValue(this, obj);
	}

	@Override
	public String toString() {
		return ToString.toString(this);
	}

}
