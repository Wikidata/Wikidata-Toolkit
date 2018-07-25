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

import java.util.Collection;
import java.util.List;

/**
 * A snak group represents an ordered list of {@link Snak} objects that use the
 * same property.
 * 
 * @author Markus Kroetzsch
 * 
 */
public interface SnakGroup extends Collection<Snak> {

	/**
	 * Get the list of Snaks of this group.
	 * 
	 * @return a list of Snaks
	 */
	List<Snak> getSnaks();

	/**
	 * Get the property used by each snak in this group.
	 * 
	 * @return a PropertyIdValue
	 */
	PropertyIdValue getProperty();

}