package org.wikidata.wdtk.storage.db;

/*
 * #%L
 * Wikidata Toolkit Storage
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
 * A dictionary for objects of type T. Objects that are inserted into the
 * dictionary are assigned a numeric id. Objects can be retrieved by id and ids
 * can be found for existing objects.
 * 
 * @author Markus Kroetzsch
 * 
 * @param <T>
 */
public interface Dictionary<T> extends Iterable<T> {

	/**
	 * Returns the value for the given id, or null if no object in the
	 * dictionary has this id.
	 * 
	 * @param id
	 *            the object id to look up
	 * @return the object for the id, or null
	 */
	T getValue(int id);

	/**
	 * Returns the id of the given object, or -1L if the object is not in the
	 * dictionary.
	 * 
	 * @param value
	 *            the object to look up
	 * @return the id of the object, or -1L
	 */
	int getId(T value);

	/**
	 * Returns an id for the given object, inserting it into the dictionary if
	 * it is not there yet.
	 * 
	 * @param value
	 *            the object to look up or insert
	 * @return the existing or newly assigned id
	 */
	int getOrCreateId(T value);

}
