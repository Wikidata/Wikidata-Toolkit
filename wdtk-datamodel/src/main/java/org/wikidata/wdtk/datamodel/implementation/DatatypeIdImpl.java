package org.wikidata.wdtk.datamodel.implementation;

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

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeId;

/**
 * Implementation of {@link DatatypeId}
 * 
 * @author Markus Kroetzsch
 * 
 */
public class DatatypeIdImpl implements DatatypeId {

	final String iri;

	/**
	 * Constructor. The datatype IRI is usually one of the constants defined in
	 * {@link DatatypeId}, but this is not enforced, since there might be
	 * extensions that provide additional types.
	 * 
	 * @param datatypeIri
	 *            the IRI string that identifies the datatype
	 */
	DatatypeIdImpl(String datatypeIri) {
		Validate.notNull(datatypeIri, "Datatype IRIs cannot be null");
		this.iri = datatypeIri;
	}

	@Override
	public String getIri() {
		return iri;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return iri.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof DatatypeId)) {
			return false;
		}

		return iri.equals(((DatatypeId) obj).getIri());
	}

}
