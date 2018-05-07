package org.wikidata.wdtk.rdf.values;

/*
 * #%L
 * Wikidata Toolkit RDF
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

import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.rdf.OwlDeclarationBuffer;
import org.wikidata.wdtk.rdf.PropertyRegister;
import org.wikidata.wdtk.rdf.RdfWriter;

public abstract class AbstractValueConverter<V extends org.wikidata.wdtk.datamodel.interfaces.Value>
		implements ValueConverter<V> {

	final PropertyRegister propertyRegister;
	final RdfWriter rdfWriter;
	final OwlDeclarationBuffer rdfConversionBuffer;

	static final Logger logger = LoggerFactory.getLogger(ValueConverter.class);

	public AbstractValueConverter(RdfWriter rdfWriter,
			PropertyRegister propertyRegister,
			OwlDeclarationBuffer rdfConversionBuffer) {
		this.rdfWriter = rdfWriter;
		this.propertyRegister = propertyRegister;
		this.rdfConversionBuffer = rdfConversionBuffer;
	}

	@Override
	public void writeAuxiliaryTriples() throws RDFHandlerException {
		// default implementation: no auxiliary triples
	}

	/**
	 * Logs a message for a case where the value of a property does not fit to
	 * its declared datatype.
	 *
	 * @param propertyIdValue
	 *            the property that was used
	 * @param datatype
	 *            the declared type of the property
	 * @param valueType
	 *            a string to denote the type of value
	 */
	protected void logIncompatibleValueError(PropertyIdValue propertyIdValue,
			String datatype, String valueType) {
		logger.warn("Property " + propertyIdValue.getId() + " has type \""
				+ datatype + "\" but a value of type " + valueType
				+ ". Data ignored.");
	}
}
