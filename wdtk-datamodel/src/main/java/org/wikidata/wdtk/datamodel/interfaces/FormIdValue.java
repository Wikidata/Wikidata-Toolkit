package org.wikidata.wdtk.datamodel.interfaces;

import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;

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
 * The id of a Wikibase Lexeme Form. Objects implementing this interface always return
 * {@link EntityIdValue#ET_FORM} for {@link EntityIdValue#getEntityType()
 * getEntityType}.
 *
 * @author Thomas Pellissier Tanon
 *
 */
public interface FormIdValue extends EntityIdValue {

	/**
	 * Returns the id of the lexeme of which it is a form.
	 *
	 * @return A lexeme id
	 */
	LexemeIdValue getLexemeId();
}
