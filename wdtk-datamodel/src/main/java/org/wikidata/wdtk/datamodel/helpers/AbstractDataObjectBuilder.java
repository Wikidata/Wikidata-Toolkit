package org.wikidata.wdtk.datamodel.helpers;

import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;

/*
 * #%L
 * Wikidata Toolkit Data Model
 * %%
 * Copyright (C) 2014 - 2015 Wikidata Toolkit Developers
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

import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;

/**
 * Abstract base class for all builder objects that create data model objects.
 *
 * @author Markus Kroetzsch
 *
 * @param <T>
 *            the type of the eventual concrete builder implementation
 * @param <O>
 *            the type of the object that is being built
 */
public abstract class AbstractDataObjectBuilder<T extends AbstractDataObjectBuilder<T, O>, O> {

	static DataObjectFactory factory = new DataObjectFactoryImpl();

	private boolean isBuilt = false;

	/**
	 * Returns the object that has been built.
	 *
	 * @return constructed object
	 * @throws IllegalStateException
	 *             if the object was built already
	 */
	public abstract O build();

	/**
	 * Checks if the object has already been built, and throws an exception if
	 * yes. If no, then the object is recorded as having been built.
	 *
	 * @throws IllegalStateException
	 *             if the object was built already
	 */
	protected void prepareBuild() {
		if (this.isBuilt) {
			throw new IllegalStateException("The entity has been built");
		}
		this.isBuilt = true;
	}

	/**
	 * Returns the current object with the correct builder type.
	 *
	 * @return this
	 */
	abstract protected T getThis();
}
