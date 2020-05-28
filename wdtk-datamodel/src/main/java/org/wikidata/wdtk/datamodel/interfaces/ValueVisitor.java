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
 * A visitor for the various types of values in the datamodel. This should be
 * used to avoid any type casting or instanceof checks when processing values.
 * <p>
 * The visitor does not distinguish several types of {@link EntityIdValue},
 * since these are supposed to be extensible and therefore cannot be fixed in a
 * visitor interface.
 * 
 * @author Markus Kroetzsch
 * 
 * @param <T>
 *            the return type of the visitor
 */
public interface ValueVisitor<T> {

	/**
	 * Visits a EntityIdValue and returns a result. In practice, only specific
	 * subtypes of EntityIdValue are used, such as {@link ItemIdValue} and
	 * {@link PropertyIdValue}. Since the set of possible subtypes can be
	 * extended by extensions of Wikibase, the visitor only visits the general
	 * (abstract) supertype. Implementations will have to decide if the given
	 * specific type is supported and what to do with it.
	 * 
	 * @param value
	 *            the value to visit
	 * @return the result for this value
	 */
	T visit(EntityIdValue value);

	/**
	 * Visits a GlobeCoordinatesValue and returns a result.
	 * 
	 * @param value
	 *            the value to visit
	 * @return the result for this value
	 */
	T visit(GlobeCoordinatesValue value);

	/**
	 * Visits a MonolingualTextValue and returns a result.
	 * 
	 * @param value
	 *            the value to visit
	 * @return the result for this value
	 */
	T visit(MonolingualTextValue value);

	/**
	 * Visits a QuantityValue and returns a result.
	 * 
	 * @param value
	 *            the value to visit
	 * @return the result for this value
	 */
	T visit(QuantityValue value);

	/**
	 * Visits a StringValue and returns a result.
	 * 
	 * @param value
	 *            the value to visit
	 * @return the result for this value
	 */
	T visit(StringValue value);

	/**
	 * Visits a TimeValue and returns a result.
	 * 
	 * @param value
	 *            the value to visit
	 * @return the result for this value
	 */
	T visit(TimeValue value);
	
	/**
	 * Visits an UnsupportedValue and returns a result
	 * 
	 * @param value
	 *            the value to visit
	 * @return the result for this value
	 */
	T visit(UnsupportedValue value);

}
