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

import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;
import org.wikidata.wdtk.storage.datamodel.Sort;
import org.wikidata.wdtk.storage.datamodel.StringValueImpl;
import org.wikidata.wdtk.storage.datamodel.Value;
import org.wikidata.wdtk.storage.wdtkbindings.WdtkSorts;

public class ValueToValueVisitor implements ValueVisitor<Value> {

	@Override
	public Value visit(DatatypeIdValue value) {
		throw new UnsupportedOperationException(
				"DatatypeIdValues not supported yet");
	}

	@Override
	public Value visit(EntityIdValue value) {
		return new EntityValueAsValue(value);
	}

	@Override
	public Value visit(GlobeCoordinatesValue value) {
		return new GlobeCoordinatesValueAsValue(value,
				WdtkSorts.SORT_GLOBE_COORDINATES_VALUE);
	}

	@Override
	public Value visit(MonolingualTextValue value) {
		return new MonolingualTextValueAsValue(value, WdtkSorts.SORT_MTV);
	}

	@Override
	public Value visit(QuantityValue value) {
		// TODO Auto-generated method stub
		return new StringValueImpl("UNSUPPORTED Quantity", Sort.SORT_STRING);
	}

	@Override
	public Value visit(StringValue value) {
		return new StringValueImpl(value.getString(), Sort.SORT_STRING);
	}

	@Override
	public Value visit(TimeValue value) {
		return new TimeValueAsValue(value, WdtkSorts.SORT_TIME_VALUE);
	}

}
