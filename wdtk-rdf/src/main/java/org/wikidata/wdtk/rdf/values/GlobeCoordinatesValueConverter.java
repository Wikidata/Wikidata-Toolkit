package org.wikidata.wdtk.rdf.values;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.rio.RDFHandlerException;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.rdf.PropertyTypes;
import org.wikidata.wdtk.rdf.OwlDeclarationBuffer;
import org.wikidata.wdtk.rdf.RdfWriter;
import org.wikidata.wdtk.rdf.Vocabulary;

public class GlobeCoordinatesValueConverter extends
		BufferedValueConverter<GlobeCoordinatesValue> {

	public GlobeCoordinatesValueConverter(RdfWriter rdfWriter,
			PropertyTypes propertyTypes, OwlDeclarationBuffer rdfConversionBuffer) {
		super(rdfWriter, propertyTypes, rdfConversionBuffer);
	}

	@Override
	public Value getRdfValue(GlobeCoordinatesValue value,
			PropertyIdValue propertyIdValue) {
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
				RdfWriter.WB_LATITUDE,
				getDecimalStringForCoordinate(value.getLatitude()),
				RdfWriter.XSD_DECIMAL);
		this.rdfWriter.writeTripleLiteralObject(resource,
				RdfWriter.WB_LONGITUDE,
				getDecimalStringForCoordinate(value.getLongitude()),
				RdfWriter.XSD_DECIMAL);
		this.rdfWriter.writeTripleLiteralObject(resource,
				RdfWriter.WB_GC_PRECISION,
				getDecimalStringForCoordinate(value.getPrecision()),
				RdfWriter.XSD_DECIMAL);

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

	String getDecimalStringForCoordinate(long value) {
		String valueString;
		if (value >= 0) {
			valueString = String.format("%010d", value);
		} else {
			valueString = String.format("%011d", value);
		}
		return valueString.substring(0, valueString.length() - 9) + "."
				+ valueString.substring(valueString.length() - 9);
	}

}
