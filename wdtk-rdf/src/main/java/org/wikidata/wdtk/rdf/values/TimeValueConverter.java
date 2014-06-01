package org.wikidata.wdtk.rdf.values;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.rio.RDFHandlerException;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.rdf.PropertyTypes;
import org.wikidata.wdtk.rdf.OwlDeclarationBuffer;
import org.wikidata.wdtk.rdf.RdfWriter;
import org.wikidata.wdtk.rdf.Vocabulary;

public class TimeValueConverter extends BufferedValueConverter<TimeValue> {

	public TimeValueConverter(RdfWriter rdfWriter, PropertyTypes propertyTypes,
			OwlDeclarationBuffer rdfConversionBuffer) {
		super(rdfWriter, propertyTypes, rdfConversionBuffer);
	}

	@Override
	public Value getRdfValue(TimeValue value, PropertyIdValue propertyIdValue) {

		String datatype = this.propertyTypes.setPropertyTypeFromTimeValue(
				propertyIdValue, value);

		switch (datatype) {
		case DatatypeIdValue.DT_TIME:
			URI valueUri = this.rdfWriter.getUri(Vocabulary
					.getTimeValueUri(value));

			this.rdfConversionBuffer.addObjectProperty(propertyIdValue);
			addValue(value, valueUri);

			return valueUri;
		default:
			logIncompatibleValueError(propertyIdValue, datatype, "time");
			return null;
		}
	}

	/**
	 * Write the auxiliary RDF data for encoding the given value.
	 * <p>
	 * Times with limited precision are exported using limited-precision XML
	 * Schema datatypes, such as gYear, if available. Wikidata encodes the year
	 * 1BCE as 0000, while XML Schema, even in version 2, does not allow 0000
	 * and interprets -0001 as 1BCE. Thus all negative years must be shifted by
	 * 1, but we only do this if the year is precise.
	 * 
	 * @param value
	 *            the value to write
	 * @param resource
	 *            the (subject) URI to use to represent this value in RDF
	 * @throws RDFHandlerException
	 */
	@Override
	public void writeValue(TimeValue value, Resource resource)
			throws RDFHandlerException {
		this.rdfWriter.writeTripleValueObject(resource, RdfWriter.RDF_TYPE,
				RdfWriter.WB_TIME_VALUE);

		String xsdYearString;
		if (value.getYear() == 0
				|| (value.getYear() < 0 && value.getPrecision() >= TimeValue.PREC_YEAR)) {
			xsdYearString = String.format("%05d", value.getYear() - 1);
		} else {
			xsdYearString = String.format("%04d", value.getYear());
		}

		if (value.getPrecision() >= TimeValue.PREC_DAY) {
			if (value.getPrecision() > TimeValue.PREC_DAY) {
				logger.warn("Time values with times of day not supported yet. Exporting only date of "
						+ value.toString());
			}
			this.rdfWriter.writeTripleLiteralObject(
					resource,
					RdfWriter.WB_TIME,
					xsdYearString + "-"
							+ String.format("%02d", value.getMonth()) + "-"
							+ String.format("%02d", value.getDay()),
					RdfWriter.XSD_DATE);
		} else if (value.getPrecision() == TimeValue.PREC_MONTH) {
			this.rdfWriter.writeTripleLiteralObject(
					resource,
					RdfWriter.WB_TIME,
					xsdYearString + "-"
							+ String.format("%02d", value.getMonth()),
					RdfWriter.XSD_G_YEAR_MONTH);
		} else if (value.getPrecision() <= TimeValue.PREC_YEAR) {
			this.rdfWriter.writeTripleLiteralObject(resource,
					RdfWriter.WB_TIME, xsdYearString, RdfWriter.XSD_G_YEAR);
		}

		this.rdfWriter.writeTripleIntegerObject(resource,
				RdfWriter.WB_TIME_PRECISION, value.getPrecision());
		this.rdfWriter.writeTripleUriObject(resource,
				RdfWriter.WB_PREFERRED_CALENDAR,
				value.getPreferredCalendarModel());
	}

}
