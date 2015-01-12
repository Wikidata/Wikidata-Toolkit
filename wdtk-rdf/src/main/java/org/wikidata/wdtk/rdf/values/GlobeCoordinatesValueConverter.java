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

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.rio.RDFHandlerException;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.rdf.OwlDeclarationBuffer;
import org.wikidata.wdtk.rdf.PropertyTypes;
import org.wikidata.wdtk.rdf.RdfWriter;
import org.wikidata.wdtk.rdf.Vocabulary;

public class GlobeCoordinatesValueConverter extends
		BufferedValueConverter<GlobeCoordinatesValue> {

	public GlobeCoordinatesValueConverter(RdfWriter rdfWriter,
			PropertyTypes propertyTypes,
			OwlDeclarationBuffer rdfConversionBuffer) {
		super(rdfWriter, propertyTypes, rdfConversionBuffer);
	}

	@Override
	public Value getRdfValue(GlobeCoordinatesValue value,
			PropertyIdValue propertyIdValue, boolean simple) {
		String datatype = this.propertyTypes
				.setPropertyTypeFromGlobeCoordinatesValue(propertyIdValue,
						value);

		switch (datatype) {
		case DatatypeIdValue.DT_GLOBE_COORDINATES:
			URI valueUri = this.rdfWriter.getUri(Vocabulary
					.getGlobeCoordinatesValueUri(value));

			this.rdfConversionBuffer.addObjectProperty(propertyIdValue);
			addValue(value, valueUri);

			return valueUri;
		default:
			logIncompatibleValueError(propertyIdValue, datatype,
					"globe coordinates");
			return null;
		}
	}

	@Override
	public void writeValue(GlobeCoordinatesValue value, Resource resource)
			throws RDFHandlerException {

		this.rdfWriter.writeTripleValueObject(resource, RdfWriter.RDF_TYPE,
				RdfWriter.WB_GLOBE_COORDINATES_VALUE);

		this.rdfWriter.writeTripleLiteralObject(resource,
				RdfWriter.WB_LATITUDE, Double.valueOf(value.getLatitude())
						.toString(), RdfWriter.XSD_DOUBLE);
		this.rdfWriter.writeTripleLiteralObject(resource,
				RdfWriter.WB_LONGITUDE, Double.valueOf(value.getLongitude())
						.toString(), RdfWriter.XSD_DOUBLE);
		this.rdfWriter.writeTripleLiteralObject(resource,
				RdfWriter.WB_GC_PRECISION, Double.valueOf(value.getPrecision())
						.toString(), RdfWriter.XSD_DOUBLE);

		URI globeUri;
		try {
			globeUri = this.rdfWriter.getUri(value.getGlobe());
		} catch (IllegalArgumentException e) {
			logger.warn("Invalid globe URI \"" + value.getGlobe()
					+ "\". Assuming Earth ("
					+ GlobeCoordinatesValue.GLOBE_EARTH + ").");
			globeUri = this.rdfWriter.getUri(GlobeCoordinatesValue.GLOBE_EARTH);
		}

		this.rdfWriter.writeTripleValueObject(resource, RdfWriter.WB_GLOBE,
				globeUri);
	}

}
