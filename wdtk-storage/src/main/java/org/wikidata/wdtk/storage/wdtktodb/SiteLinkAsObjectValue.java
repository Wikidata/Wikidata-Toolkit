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

import java.util.Iterator;

import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.storage.datamodel.ObjectValue;
import org.wikidata.wdtk.storage.datamodel.PropertyValuePair;
import org.wikidata.wdtk.storage.datamodel.PropertyValuePairImpl;
import org.wikidata.wdtk.storage.datamodel.Sort;
import org.wikidata.wdtk.storage.datamodel.StringValueImpl;
import org.wikidata.wdtk.storage.wdtkbindings.WdtkSorts;

public class SiteLinkAsObjectValue implements ObjectValue,
		Iterator<PropertyValuePair> {

	final SiteLink siteLink;

	int iteratorPos;

	public SiteLinkAsObjectValue(SiteLink siteLink) {
		this.siteLink = siteLink;
	}

	@Override
	public Sort getSort() {
		return WdtkSorts.SORT_SITE_LINK;
	}

	@Override
	public Iterator<PropertyValuePair> iterator() {
		this.iteratorPos = 0;
		return this;
	}

	@Override
	public boolean hasNext() {
		return this.iteratorPos < getSort().getPropertyRanges().size();
	}

	@Override
	public PropertyValuePair next() {
		this.iteratorPos++;
		if (this.iteratorPos == 1) {
			return new PropertyValuePairImpl(WdtkSorts.PROP_SITE_PAGE,
					new StringValueImpl(this.siteLink.getPageTitle(),
							Sort.SORT_STRING));
		} else if (this.iteratorPos == 2) {
			return new PropertyValuePairImpl(WdtkSorts.PROP_SITE_KEY,
					new StringValueImpl(this.siteLink.getSiteKey(),
							Sort.SORT_STRING));
		} else {
			return null;
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return getSort().getPropertyRanges().size();
	}

}
