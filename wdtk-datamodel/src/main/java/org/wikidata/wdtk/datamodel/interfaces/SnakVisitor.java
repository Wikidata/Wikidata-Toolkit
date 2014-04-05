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
 * A visitor for the various types of snaks in the datamodel. This should be
 * used to avoid any type casting or instanceof checks when processing snaks.
 * 
 * @author Markus Kroetzsch
 * 
 * @param <T>
 *            the return type of the visitor
 */
public interface SnakVisitor<T> {

	/**
	 * Visits a ValueSnak and returns a result.
	 * 
	 * @param snak
	 *            the snak to visit
	 * @return the result for this snak
	 */
	T visit(ValueSnak snak);

	/**
	 * Visits a SomeValueSnak and returns a result.
	 * 
	 * @param snak
	 *            the snak to visit
	 * @return the result for this snak
	 */
	T visit(SomeValueSnak snak);

	/**
	 * Visits a NoValueSnak and returns a result.
	 * 
	 * @param snak
	 *            the snak to visit
	 * @return the result for this snak
	 */
	T visit(NoValueSnak snak);

}
