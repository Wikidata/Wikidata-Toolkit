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
 * Snaks are the basic information structures used to describe Entities in
 * Wikibase. They are an integral part of each {@link Statement} (which can be
 * viewed as collection of Snaks about an Entity, together with a list of
 * references).
 * <p>
 * The most basic (and most common) form of Snaks are property-value pairs, but
 * other forms exist.
 *
 * @author Markus Kroetzsch
 *
 */
public interface Snak {

	/**
	 * Get the id of the property that this snak refers to.
	 *
	 * @return PropertyId of this Snak
	 */
	PropertyIdValue getPropertyId();

	/**
	 * @deprecated Only {@link ValueSnak} has a value.
	 *
	 * Get the {@link Value} of this Snak, or null if the snak has no specified
	 * value.
	 *
	 * @return Value if it is a {@link ValueSnak} or null if it is not
	 */
	@Deprecated
	Value getValue();

	/**
	 * Accept a SnakVisitor and return its output.
	 *
	 * @param snakVisitor
	 *            the SnakVisitor
	 * @return output of the visitor
	 */
	<T> T accept(SnakVisitor<T> snakVisitor);

}
