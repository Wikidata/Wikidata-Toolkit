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

/**
 * A monolingual text value represents a text (string) in a certain language.
 * 
 * @author Markus Kroetzsch
 * 
 */
public interface MonolingualTextValue extends Value {

	/**
	 * Get the text of this value.
	 * 
	 * @return a string
	 */
	String getText();

	/**
	 * Get the language code of this value. The codes are usually based on the
	 * codes used internally in Wikibase, which in turn are the codes used in
	 * the Universal Language Selector extension. However, the data model as
	 * such does not restrict the strings that might be used here.
	 * 
	 * @return a string that represents language
	 */
	String getLanguageCode();

}