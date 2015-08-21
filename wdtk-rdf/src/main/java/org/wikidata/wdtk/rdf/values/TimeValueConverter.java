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

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.rio.RDFHandlerException;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.rdf.OwlDeclarationBuffer;
import org.wikidata.wdtk.rdf.PropertyRegister;
import org.wikidata.wdtk.rdf.RdfWriter;
import org.wikidata.wdtk.rdf.Vocabulary;

public class TimeValueConverter extends BufferedValueConverter<TimeValue> {

	public TimeValueConverter(RdfWriter rdfWriter,
			PropertyRegister PropertyRegister,
			OwlDeclarationBuffer rdfConversionBuffer) {
		super(rdfWriter, PropertyRegister, rdfConversionBuffer);
	}

	@Override
	public Value getRdfValue(TimeValue value, PropertyIdValue propertyIdValue,
			boolean simple) {

		String datatype = this.propertyRegister.setPropertyTypeFromTimeValue(
				propertyIdValue, value);

		switch (datatype) {
		case DatatypeIdValue.DT_TIME:
			if (simple) {
				this.rdfConversionBuffer.addDatatypeProperty(propertyIdValue);
				return TimeValueConverter.getTimeLiteral(value, this.rdfWriter);
			} else {
				URI valueUri = this.rdfWriter.getUri(Vocabulary
						.getTimeValueUri(value,
								this.propertyRegister.getUriPrefix()));
				this.rdfConversionBuffer.addObjectProperty(propertyIdValue);
				addValue(value, valueUri);

				return valueUri;
			}
		default:
			logIncompatibleValueError(propertyIdValue, datatype, "time");
			return null;
		}
	}

	/**
	 * Write the auxiliary RDF data for encoding the given value.
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

		this.rdfWriter.writeTripleValueObject(resource, RdfWriter.WB_TIME,
				TimeValueConverter.getTimeLiteral(value, this.rdfWriter));

		this.rdfWriter.writeTripleIntegerObject(resource,
				RdfWriter.WB_TIME_PRECISION, value.getPrecision());
		this.rdfWriter.writeTripleUriObject(resource,
				RdfWriter.WB_PREFERRED_CALENDAR,
				value.getPreferredCalendarModel());
	}

	/**
	 * Returns the RDF literal to encode the time component of a given time
	 * value.
	 * <p>
	 * Times with limited precision are encoded using limited-precision XML
	 * Schema datatypes, such as gYear, if available. Wikidata encodes the year
	 * 1BCE as 0000, while XML Schema, even in version 2, does not allow 0000
	 * and interprets -0001 as 1BCE. Thus all negative years must be shifted by
	 * 1, but we only do this if the year is precise.
	 *
	 * @param value
	 *            the value to convert
	 * @param rdfWriter
	 *            the object to use for creating the literal
	 * @return the RDF literal
	 */
	public static Literal getTimeLiteral(TimeValue value, RdfWriter rdfWriter) {
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
			return rdfWriter.getLiteral(
					xsdYearString + "-"
							+ String.format("%02d", value.getMonth()) + "-"
							+ String.format("%02d", value.getDay()),
					RdfWriter.XSD_DATE);
		} else if (value.getPrecision() == TimeValue.PREC_MONTH) {
			return rdfWriter.getLiteral(
					xsdYearString + "-"
							+ String.format("%02d", value.getMonth()),
					RdfWriter.XSD_G_YEAR_MONTH);
		} else { // (value.getPrecision() <= TimeValue.PREC_YEAR)
			return rdfWriter.getLiteral(xsdYearString, RdfWriter.XSD_G_YEAR);
		}
	}

}
