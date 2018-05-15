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
 * ValueSnaks represent property-value pairs, where the property are represented
 * by a {@link PropertyIdValue} and the value is represented by a {@link Value}.
 * Objects of this type must always return a non-null value on
 * {@link Snak#getValue()}.
 *
 * @author Markus Kroetzsch
 *
 */
public interface ValueSnak extends Snak {

	/**
	 * Get the {@link Value} of this Snak
	 */
	@Override
	Value getValue();
}
