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

import java.util.Set;

/**
 * Interface for datasets that describe media entities.
 *
 * @author Thomas Pellissier Tanon
 *
 */
public interface MediaInfoDocument extends LabeledStatementDocument {

	/**
	 * Returns the ID of the entity that the data refers to
	 *
	 * @return media info id
	 */
	@Override
	MediaInfoIdValue getEntityId();

	@Override
	MediaInfoDocument withRevisionId(long newRevisionId);

	@Override
	MediaInfoDocument withLabel(MonolingualTextValue newLabel);

	@Override
	MediaInfoDocument withStatement(Statement statement);

	@Override
	MediaInfoDocument withoutStatementIds(Set<String> statementIds);
}
