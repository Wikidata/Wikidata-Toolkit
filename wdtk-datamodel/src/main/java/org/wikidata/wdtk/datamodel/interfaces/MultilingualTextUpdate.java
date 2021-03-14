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

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.wikidata.wdtk.datamodel.implementation.MultilingualTextUpdateImpl;

/**
 * Collection of changes made to multilingual text value.
 */
public interface MultilingualTextUpdate {

	/**
	 * Empty update that does not alter or add any monolingual values.
	 */
	MultilingualTextUpdate NULL = new MultilingualTextUpdateImpl(Collections.emptyList(), Collections.emptyList());

	/**
	 * Returns values added or modified in this update. Existing values are
	 * preserved if their language code is not listed here.
	 * 
	 * @return added or modified values indexed by language code
	 */
	Map<String, MonolingualTextValue> getModifiedValues();

	/**
	 * Returns language codes of values removed in this update.
	 * 
	 * @return language codes of removed values
	 */
	Set<String> getRemovedValues();

}
