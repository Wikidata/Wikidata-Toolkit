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

import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.rio.RDFHandlerException;
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
				IRI valueUri = this.rdfWriter.getUri(Vocabulary.getTimeValueUri(value));
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
		this.rdfWriter.writeTripleIntegerObject(resource,
				RdfWriter.WB_TIME_TIMEZONE,
				value.getTimezoneOffset());
		this.rdfWriter.writeTripleUriObject(resource,
				RdfWriter.WB_TIME_CALENDAR_MODEL,
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
	private static Literal getTimeLiteral(TimeValue value, RdfWriter rdfWriter) {
		long year = value.getYear();

		//Year normalization
		if (year == 0 || (year < 0 && value.getPrecision() >= TimeValue.PREC_YEAR)) {
			year--;
		}

		String timestamp = String.format("%04d-%02d-%02dT%02d:%02d:%02dZ",
				year, value.getMonth(), value.getDay(),
				value.getHour(), value.getMinute(), value.getSecond());
		return rdfWriter.getLiteral(timestamp, RdfWriter.XSD_DATETIME);
	}

}
