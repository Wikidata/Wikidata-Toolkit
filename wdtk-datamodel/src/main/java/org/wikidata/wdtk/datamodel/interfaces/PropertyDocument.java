package org.wikidata.wdtk.datamodel.interfaces;

import java.util.List;
import java.util.Set;

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

/**
 * Interface for datasets that describe properties. It extends
 * {@link EntityDocument} with information about the datatype of a property.
 * <p>
 * Claims or Statements on properties might be supported in the future.
 *
 * @author Markus Kroetzsch
 *
 */
public interface PropertyDocument extends TermedStatementDocument {

	/**
	 * Return the ID of the item that the data refers to.
	 *
	 * @return item id
	 */
	@Override
	PropertyIdValue getEntityId();

	/**
	 * Get the datatype id of the datatype defined for this property.
	 *
	 * @return {@link DatatypeIdValue}
	 */
	DatatypeIdValue getDatatype();

	/**
	 * Returns a new version of this document with updated ID.
	 * 
	 * @param newEntityId
	 *            new ID of the document
	 * @return document with updated ID
	 */
	PropertyDocument withEntityId(PropertyIdValue newEntityId);

	@Override
	PropertyDocument withRevisionId(long newRevisionId);

	@Override
	PropertyDocument withLabel(MonolingualTextValue newLabel);
	
	@Override
	PropertyDocument withDescription(MonolingualTextValue newDescription);
	
	@Override
	PropertyDocument withAliases(String language, List<MonolingualTextValue> aliases);
	
	@Override
	PropertyDocument withStatement(Statement statement);
	
	@Override
	PropertyDocument withoutStatementIds(Set<String> statementIds);
}
