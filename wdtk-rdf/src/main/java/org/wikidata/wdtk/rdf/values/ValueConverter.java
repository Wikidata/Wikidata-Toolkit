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

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;

/**
 * Interface for classes that convert one type of Wikibase data value into a
 * RDF.
 * 
 * @author Markus Kroetzsch
 * 
 * @param <V>
 *            the type of Wikibase value converted by this class
 */
public interface ValueConverter<V extends org.wikidata.wdtk.datamodel.interfaces.Value> {

	/**
	 * Returns an RDF value that should be used to represent the given Wikibase
	 * data value in RDF.
	 * 
	 * @param value
	 *            the value to convert
	 * @param propertyIdValue
	 *            the property for which this value was used; this provides
	 *            important context information for the conversion
	 * @param simple
	 *            if true, use a simplified conversion to RDF and do not convert
	 *            values that are inherently complex
	 * @return the RDF value to use for representing the data value in RDF
	 */
	Value getRdfValue(V value, PropertyIdValue propertyIdValue, boolean simple);

	/**
	 * Writes auxiliary triples that might be needed to encode a Wikibase value
	 * in RDF.
	 * 
	 * @throws RDFHandlerException
	 *             if there is a problem writing the triples
	 */
	void writeAuxiliaryTriples() throws RDFHandlerException;
}
