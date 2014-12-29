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
 * A Value is the most general kind of object in the Wikibase datamodel. It can
 * represent anything that can be the value of a user-defined property or of a
 * system property that is not represented to the user (e.g., the datatype or
 * list of aliases might be represented as a value, even though there is no user
 * property with values of this type).
 * 
 * @author Markus Kroetzsch
 * 
 */
public interface Value {

	/**
	 * Accept a ValueVisitor and return its output.
	 * 
	 * @param valueVisitor
	 *            the ValueVisitor
	 * @return output of the visitor
	 */
	<T> T accept(ValueVisitor<T> valueVisitor);
}
