/*
 * #%L
 * Wikidata Toolkit Data Model
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
package org.wikidata.wdtk.datamodel.interfaces;

import java.util.Map;
import java.util.Set;

/**
 * Collection of changes that can be applied to item entity.
 */
public interface ItemUpdate extends TermedStatementDocumentUpdate {

	@Override
	ItemIdValue getEntityId();

	/**
	 * Returns site links added or modified in this update. Existing site links are
	 * preserved if their site key is not listed here.
	 * 
	 * @return added or modified site links indexed by site key
	 */
	Map<String, SiteLink> getModifiedSiteLinks();

	/**
	 * Returns site keys of site links removed in this update.
	 * 
	 * @return site keys of removed site links
	 */
	Set<String> getRemovedSiteLinks();

}
