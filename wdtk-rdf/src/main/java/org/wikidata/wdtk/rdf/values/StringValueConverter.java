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
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.rdf.OwlDeclarationBuffer;
import org.wikidata.wdtk.rdf.PropertyRegister;
import org.wikidata.wdtk.rdf.RdfWriter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class StringValueConverter extends AbstractValueConverter<StringValue> {

	public StringValueConverter(RdfWriter rdfWriter,
			PropertyRegister propertyRegister,
			OwlDeclarationBuffer rdfConversionBuffer) {
		super(rdfWriter, propertyRegister, rdfConversionBuffer);
	}

	@Override
	public Value getRdfValue(StringValue value,
			PropertyIdValue propertyIdValue, boolean simple) {
		String datatype = this.propertyRegister.setPropertyTypeFromStringValue(
				propertyIdValue, value);

		String valueUriString = null;
		switch (datatype) {
		case DatatypeIdValue.DT_STRING:
		case DatatypeIdValue.DT_EXTERNAL_ID:
		case DatatypeIdValue.DT_MATH:
			valueUriString = null;
			break;
		case DatatypeIdValue.DT_COMMONS_MEDIA:
			if (simple) {
				valueUriString = getCommonsFileUrl(value.getString());
			}
			break;
		case DatatypeIdValue.DT_URL:
			if (simple) {
				valueUriString = value.getString();
			}
			break;
		case DatatypeIdValue.DT_GEO_SHAPE:
		case DatatypeIdValue.DT_TABULAR_DATA:
			if (simple) {
				valueUriString = getCommonsDataUrl(value.getString());
			}
			break;
		default:
			logIncompatibleValueError(propertyIdValue, datatype, "string");
			return null;
		}

		if (valueUriString == null) {
			if (simple) {
				this.rdfConversionBuffer.addDatatypeProperty(propertyIdValue);
				return this.rdfWriter.getLiteral(value.getString());
			} else {
				return null; // or blank node
			}
		} else {
			this.rdfConversionBuffer.addObjectProperty(propertyIdValue);
			try {
				return this.rdfWriter.getUri(valueUriString);
			} catch (IllegalArgumentException e) {
				logger.error("Invalid URI \"" + valueUriString
						+ "\". Not serializing value.");
				return null;
			}
		}
	}

	/**
	 * Returns the Wikimedia Commons page URL for the given page name.
	 *
	 * @param pageName
	 *            name of a page on Wikimedia Commons
	 * @return URL of the page
	 */
	static String getCommonsFileUrl(String pageName) {
		try {
			return "http://commons.wikimedia.org/wiki/File:"
					+ URLEncoder.encode(pageName.replace(' ', '_'), StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			// can't happen
			throw new IllegalStateException(e);
		}
	}

	static String getCommonsDataUrl(String pageName) {
		try {
			return "http://commons.wikimedia.org/data/main/"
					+ URLEncoder.encode(pageName.replace(' ', '_'), StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

}
