package org.wikidata.wdtk.datamodel.interfaces;

/*
 * #%L
 * Wikidata Toolkit Data Model
 * %%
 * Copyright (C) 2014 - 2018 Wikidata Toolkit Developers
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
import java.util.Set;

/**
 * This interface just joins {@link TermedDocument} and {@link StatementDocument}.
 * 
 * It is necessary to introduce this interface because the conflict between 
 * the return types of the withRevisionId method in both interfaces needs to be resolved.
 * @author antonin
 *
 */
public interface TermedStatementDocument extends TermedDocument, LabeledStatementDocument {

	@Override
	TermedStatementDocument withRevisionId(long newRevisionId);
	
	@Override
	TermedStatementDocument withLabel(MonolingualTextValue newLabel);
	
	@Override
	TermedStatementDocument withDescription(MonolingualTextValue newDescription);
	
	@Override
	TermedStatementDocument withAliases(String language, List<MonolingualTextValue> aliases);
	
	@Override
	TermedStatementDocument withStatement(Statement statement);
	
	@Override
	TermedStatementDocument withoutStatementIds(Set<String> statementIds);
}
