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

import java.util.Iterator;
import java.util.List;

/**
 * An interface for references in Wikidata. A reference is currently defined by
 * a list of ValueSnaks, encoding property-value pairs.
 *
 * @author Markus Kroetzsch
 *
 */
public interface Reference {

	/**
	 * Get the list of snak groups associated with this reference. Objects of
	 * this class are immutable, and the list should therefore not be
	 * modifiable.
	 *
	 * @return list of SnakGroups
	 */
	List<SnakGroup> getSnakGroups();

	/**
	 * Returns an interator over all snaks, without considering snak groups. The
	 * relative order of snaks is preserved.
	 *
	 * @return iterator of snaks
	 */
	Iterator<Snak> getAllSnaks();

	/**
	 * Wikibase calculates a hash for each reference based on the content of the reference.
	 * This hash appears in the RDF serialization of the reference.
	 * Since the calculation is hard to reproduce, this is only available if the reference was read
	 * from a dump that contains the hash.
	 *
	 * @return the hash of the reference, if available, otherwise null.
	 */
	String getHash();
}
