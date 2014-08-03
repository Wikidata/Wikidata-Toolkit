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

import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.storage.datamodel.PropertyValuePair;
import org.wikidata.wdtk.storage.datamodel.PropertyValuePairImpl;
import org.wikidata.wdtk.storage.datamodel.Sort;
import org.wikidata.wdtk.storage.datamodel.StringValueImpl;
import org.wikidata.wdtk.storage.wdtkbindings.WdtkSorts;

public class MonolingualTextValueAsValue extends BaseValueAsValue {

	final MonolingualTextValue monolingualTextValue;

	public MonolingualTextValueAsValue(
			MonolingualTextValue monolingualTextValue, Sort sort) {
		super(sort);
		this.monolingualTextValue = monolingualTextValue;
	}

	@Override
	public PropertyValuePair next() {
		this.iteratorPos++;
		if (this.iteratorPos == 1) {
			Sort valueSort;
			// if (this.sort.getName().equals(WdtkSorts.SORTNAME_LABEL)) {
			// valueSort = WdtkSorts.SORT_LABEL_STRING;
			// } else {
			valueSort = Sort.SORT_STRING;
			// }
			return new PropertyValuePairImpl(WdtkSorts.PROP_MTV_TEXT,
					new StringValueImpl(this.monolingualTextValue.getText(),
							valueSort));
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
	public int size() {
		return 2;
	}

}
