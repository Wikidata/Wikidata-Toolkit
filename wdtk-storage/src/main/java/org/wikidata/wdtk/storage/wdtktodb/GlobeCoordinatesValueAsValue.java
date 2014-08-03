package org.wikidata.wdtk.storage.wdtktodb;

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

import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.storage.datamodel.LongValueImpl;
import org.wikidata.wdtk.storage.datamodel.PropertyValuePair;
import org.wikidata.wdtk.storage.datamodel.PropertyValuePairImpl;
import org.wikidata.wdtk.storage.datamodel.Sort;
import org.wikidata.wdtk.storage.datamodel.StringValueImpl;
import org.wikidata.wdtk.storage.wdtkbindings.WdtkSorts;

public class GlobeCoordinatesValueAsValue extends BaseValueAsValue {

	GlobeCoordinatesValue value;

	public GlobeCoordinatesValueAsValue(GlobeCoordinatesValue value, Sort sort) {
		super(sort);
		this.value = value;
	}

	@Override
	public PropertyValuePair next() {
		this.iteratorPos++;
		if (this.iteratorPos == 1) {
			return new PropertyValuePairImpl(
					WdtkSorts.PROP_COORDINATES_LATITUDE, new LongValueImpl(
							this.value.getLatitude(), Sort.SORT_LONG));
		} else if (this.iteratorPos == 2) {
			return new PropertyValuePairImpl(
					WdtkSorts.PROP_COORDINATES_LONGITUDE, new LongValueImpl(
							this.value.getLongitude(), Sort.SORT_LONG));
		} else if (this.iteratorPos == 3) {
			return new PropertyValuePairImpl(
					WdtkSorts.PROP_COORDINATES_PRECISION, new LongValueImpl(
							this.value.getPrecision(), Sort.SORT_LONG));
		} else if (this.iteratorPos == 4) {
			return new PropertyValuePairImpl(WdtkSorts.PROP_COORDINATES_GLOBE,
					new StringValueImpl(this.value.getGlobe(),
							WdtkSorts.SORT_ENTITY));
		} else {
			return null;
		}
	}

	@Override
	public int size() {
		return 4;
	}

}
