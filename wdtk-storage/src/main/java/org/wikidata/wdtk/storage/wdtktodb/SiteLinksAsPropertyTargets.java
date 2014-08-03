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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer.PropertyTargets;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer.TargetQualifiers;
import org.wikidata.wdtk.storage.datamodel.PropertyValuePair;
import org.wikidata.wdtk.storage.datamodel.Value;
import org.wikidata.wdtk.storage.wdtkbindings.WdtkSorts;

public class SiteLinksAsPropertyTargets implements PropertyTargets,
		Iterator<TargetQualifiers>, TargetQualifiers {

	final Iterator<SiteLink> siteLinks;
	final int targetCount;

	SiteLink currentValue;

	public SiteLinksAsPropertyTargets(Collection<SiteLink> siteLinks) {
		this.targetCount = siteLinks.size();
		this.siteLinks = siteLinks.iterator();
	}

	@Override
	public Iterator<TargetQualifiers> iterator() {
		return this;
	}

	@Override
	public String getProperty() {
		return WdtkSorts.PROP_SITE_LINK;
	}

	@Override
	public int getTargetCount() {
		return this.targetCount;
	}

	@Override
	public boolean hasNext() {
		return this.siteLinks.hasNext();
	}

	@Override
	public TargetQualifiers next() {
		this.currentValue = this.siteLinks.next();
		return this;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Value getTarget() {
		return new SiteLinkAsObjectValue(this.currentValue);
	}

	@Override
	public Iterable<PropertyValuePair> getQualifiers() {
		return Collections.<PropertyValuePair> emptyList();
	}

	@Override
	public int getQualifierCount() {
		return 0;
	}

}
