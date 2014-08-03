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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer.PropertyTargets;
import org.wikidata.wdtk.storage.wdtkbindings.WdtkSorts;
import org.wikidata.wdtk.util.NestedIterator;

public class EdgeContainerAsItemDocument implements ItemDocument {

	final DataObjectFactory dataObjectFactory;

	PropertyTargets labels = null;
	PropertyTargets descriptions = null;
	PropertyTargets aliases = null;
	PropertyTargets siteLinks = null;

	public EdgeContainerAsItemDocument(EdgeContainer edgeContainer,
			DataObjectFactory dataObjectFactory) {
		this.dataObjectFactory = dataObjectFactory;

		for (PropertyTargets pts : edgeContainer) {
			switch (pts.getProperty()) {
			case WdtkSorts.PROP_DOCTYPE:
				// expect this to be item? -> the whole switch could be in a
				// factory method
				break;
			case WdtkSorts.PROP_LABEL:
				this.labels = pts;
				break;
			case WdtkSorts.PROP_DESCRIPTION:
				this.descriptions = pts;
				break;
			case WdtkSorts.PROP_ALIAS:
				this.aliases = pts;
				break;
			case WdtkSorts.PROP_SITE_LINK:
				this.siteLinks = pts;
				break;
			default:
				// some statement property
				break;
			}
		}

	}

	@Override
	public Map<String, MonolingualTextValue> getLabels() {
		return WdtkFromDb.getMtvMapFromPropertyTargets(this.labels,
				this.dataObjectFactory);
	}

	@Override
	public Map<String, MonolingualTextValue> getDescriptions() {
		return WdtkFromDb.getMtvMapFromPropertyTargets(this.descriptions,
				this.dataObjectFactory);
	}

	@Override
	public Map<String, List<MonolingualTextValue>> getAliases() {
		return WdtkFromDb.getMtvListMapFromPropertyTargets(this.aliases,
				this.dataObjectFactory);
	}

	@Override
	public EntityIdValue getEntityId() {
		return getItemId();
	}

	@Override
	public ItemIdValue getItemId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<StatementGroup> getStatementGroups() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Statement> getAllStatements() {
		return new NestedIterator<>(getStatementGroups());
	}

	@Override
	public Map<String, SiteLink> getSiteLinks() {
		return WdtkFromDb.getSiteLinkMapFromPropertyTargets(this.siteLinks,
				this.dataObjectFactory);
	}

}
