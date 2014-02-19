package org.wikidata.wdtk.datamodel.interfaces;

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

import java.util.List;
import java.util.Map;

/**
 * Interface for datasets that describe items. It extends {@link EntityDocument}
 * with information about site links and statements.
 * 
 * @author Markus Kroetzsch
 * 
 */
public interface ItemDocument extends TermedDocument {

	/**
	 * Return the ID of the item that the data refers to. The result is the same
	 * as that of {@link EntityDocument#getEntityId()}, but declared with a more
	 * specific result type.
	 * 
	 * @return item id
	 */
	ItemIdValue getItemId();

	/**
	 * Return the list of all StatementGroups stored for this item. The order of
	 * StatementGroups is significant.
	 * 
	 * @return list of StatementGroups
	 */
	List<StatementGroup> getStatementGroups();

	/**
	 * Get a Map of site keys to {@link SiteLink} objects.
	 * 
	 * @return
	 */
	Map<String, SiteLink> getSiteLinks();

}
