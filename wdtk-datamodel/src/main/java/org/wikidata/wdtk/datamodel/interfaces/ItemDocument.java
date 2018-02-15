package org.wikidata.wdtk.datamodel.interfaces;

import java.util.List;

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

import java.util.Map;
import java.util.Set;

/**
 * Interface for datasets that describe items. It extends {@link EntityDocument}
 * with information about site links and statements.
 *
 * @author Markus Kroetzsch
 *
 */
public interface ItemDocument extends TermedStatementDocument {

	/**
	 * Return the ID of the item that the data refers to.
	 *
	 * @return item id
	 */
	@Override
	ItemIdValue getEntityId();

	/**
	 * @deprecated Use {@link #getEntityId()}
	 *
	 * Return the ID of the item that the data refers to. The result is the same
	 * as that of {@link EntityDocument#getEntityId()}, but declared with a more
	 * specific result type.
	 *
	 * @return item id
	 */
	@Deprecated
	ItemIdValue getItemId();

	/**
	 * Get a Map of site keys to {@link SiteLink} objects.
	 *
	 * @return map of SiteLinks
	 */
	Map<String, SiteLink> getSiteLinks();
	
	@Override
	ItemDocument withRevisionId(long newRevisionId);

	@Override
	ItemDocument withLabel(MonolingualTextValue newLabel);
	
	@Override
	ItemDocument withDescription(MonolingualTextValue newDescription);
	
	@Override
	ItemDocument withAliases(String language, List<MonolingualTextValue> aliases);
	
	@Override
	ItemDocument withStatement(Statement statement);
	
	@Override
	ItemDocument withoutStatementIds(Set<String> statementIds);
}
