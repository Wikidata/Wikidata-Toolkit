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

import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.storage.datamodel.ObjectValue;
import org.wikidata.wdtk.storage.datamodel.PropertyValuePair;
import org.wikidata.wdtk.storage.datamodel.PropertyValuePairImpl;
import org.wikidata.wdtk.storage.datamodel.Sort;
import org.wikidata.wdtk.storage.datamodel.StringValueImpl;

public class MonolingualTextValueAdaptor implements ObjectValue,
		Iterator<PropertyValuePair> {

	final MonolingualTextValue monolingualTextValue;
	final WdtkAdaptorHelper helper;
	int iteratorPos = 0;

	public MonolingualTextValueAdaptor(
			MonolingualTextValue monolingualTextValue, WdtkAdaptorHelper helper) {
		this.monolingualTextValue = monolingualTextValue;
		this.helper = helper;
	}

	@Override
	public Sort getSort() {
		return WdtkSorts.SORT_MTV;
	}

	@Override
	public Iterator<PropertyValuePair> iterator() {
		this.iteratorPos = 0;
		return this;
	}

	@Override
	public boolean hasNext() {
		return (this.iteratorPos < 2);
	}

	@Override
	public PropertyValuePair next() {
		this.iteratorPos++;
		if (this.iteratorPos == 1) {
			return new PropertyValuePairImpl(WdtkSorts.PROP_MTV_TEXT,
					new StringValueImpl(this.monolingualTextValue.getText(),
							Sort.SORT_STRING));
		} else if (this.iteratorPos == 2) {
			return new PropertyValuePairImpl(WdtkSorts.PROP_MTV_LANG,
					new StringValueImpl(
							this.monolingualTextValue.getLanguageCode(),
							Sort.SORT_STRING));
		} else {
			return null;
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
