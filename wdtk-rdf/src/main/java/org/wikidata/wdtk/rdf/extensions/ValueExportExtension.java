package org.wikidata.wdtk.rdf.extensions;

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

import org.openrdf.model.Resource;
import org.openrdf.rio.RDFHandlerException;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Value;
import org.wikidata.wdtk.rdf.OwlDeclarationBuffer;
import org.wikidata.wdtk.rdf.RdfWriter;

/**
 * Classes that implement this interface export additional data for snaks with
 * certain values.
 *
 * @author Markus Kroetzsch
 *
 */
public interface ValueExportExtension<V extends Value> {

	/**
	 * Writes additional RDF data for the given data.
	 *
	 * @param resource
	 *            the subject for which to write the data
	 * @param propertyIdValue
	 *            the property that was used for writing
	 * @param value
	 *            the value used with the property
	 * @param rdfWriter
	 *            the writer to write the data to
	 * @param owlDeclarationBuffer
	 *            the buffer to write necessary vocabulary declarations to
	 * @throws RDFHandlerException
	 *             if the data could not be written
	 */
	void writeExtensionData(Resource resource, PropertyIdValue propertyIdValue,
			V value, RdfWriter rdfWriter,
			OwlDeclarationBuffer owlDeclarationBuffer)
			throws RDFHandlerException;
}
