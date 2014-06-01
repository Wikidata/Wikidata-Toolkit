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
import org.openrdf.model.URI;
import org.openrdf.rio.RDFHandlerException;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.rdf.OwlDeclarationBuffer;
import org.wikidata.wdtk.rdf.RdfWriter;

/**
 * Simple helper class for implementing value export extensions for the common
 * case that a single Wikibase property of type string is to be translated to a
 * single link to an external URI.
 * 
 * @author Markus Kroetzsch
 * 
 */
public abstract class StringIdExportExtension implements
		ValueExportExtension<StringValue> {

	@Override
	public void writeExtensionData(Resource resource,
			PropertyIdValue propertyIdValue, StringValue value,
			RdfWriter rdfWriter, OwlDeclarationBuffer owlDeclarationBuffer)
			throws RDFHandlerException {
		URI propertyUri = rdfWriter.getUri(propertyIdValue.getIri() + "-"
				+ getPropertyPostfix());
		owlDeclarationBuffer.addObjectProperty(propertyUri);
		rdfWriter.writeTripleUriObject(resource, propertyUri,
				getValueUri(value));
	}

	/**
	 * Returns the postfix string that should be used for forming property URIs
	 * for the id properties created in this export extension.
	 * 
	 * @return a postfix string
	 */
	public abstract String getPropertyPostfix();

	/**
	 * Returns the string URI that the id property should link to.
	 * 
	 * @param value
	 *            the string value containing the id
	 * @return the URI to point to
	 */
	public abstract String getValueUri(StringValue value);

}
