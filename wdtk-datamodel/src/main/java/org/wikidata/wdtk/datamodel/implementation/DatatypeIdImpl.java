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

import java.io.Serializable;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

/**
 * Implementation of {@link DatatypeIdValue}
 *
 * @author Markus Kroetzsch
 *
 */
public class DatatypeIdImpl implements DatatypeIdValue, Serializable {

	private static final long serialVersionUID = 8021986102992714526L;
	
	final String iri;

	/**
	 * Constructor. The datatype IRI is usually one of the constants defined in
	 * {@link DatatypeIdValue}, but this is not enforced, since there might be
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

	@Override
	public <T> T accept(ValueVisitor<T> valueVisitor) {
		return valueVisitor.visit(this);
	}

	@Override
	public int hashCode() {
		return Hash.hashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return Equality.equalsDatatypeIdValue(this, obj);
	}

	@Override
	public String toString() {
		return ToString.toString(this);
	}

}
