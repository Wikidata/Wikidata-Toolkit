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
 * Interface for lexemes forms.
 *
 * @author Thomas Pellissier Tanon
 *
 */
public interface FormDocument extends StatementDocument {

	/**
	 * Returns the ID of the entity that the data refers to
	 *
	 * @return form id
	 */
	@Override
	FormIdValue getEntityId();

	/**
	 * Return the human readable representations of the form indexed by Wikimedia language code
	 *
	 * @return a map from Wikimedia language code to the representations
	 */
	Map<String,MonolingualTextValue> getRepresentations();

	/**
	 * Return the IDs of the grammatical features of the form (masculine, singular...)
	 *
	 * @return item ids
	 */
	List<ItemIdValue> getGrammaticalFeatures();
}
