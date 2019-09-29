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

import java.time.Month;

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
		/* we need to check for year zero before julian date conversion,
		 since that can change the year (if the date is 1 Jan 1 for example)
		*/
		boolean yearZero = value.getYear() == 0;

		value = value.toGregorian().orElse(value);

		long year = value.getYear();

		/* https://www.mediawiki.org/wiki/Wikibase/DataModel/JSON#time says the following about the JSON mapping:

		  The format used for Gregorian and Julian dates use a notation resembling ISO 8601. E.g. “+1994-01-01T00:00:00Z”.
		  The year is represented by at least four digits, zeros are added on the left side as needed.
		  Years BCE are represented as negative numbers, using the historical numbering, in which year 0 is undefined,
		   and the year 1 BCE is represented as -0001, the year 44 BCE is represented as -0044, etc.,
		   like XSD 1.0 (ISO 8601:1988) does.
		  In contrast, the RDF mapping relies on XSD 1.1 (ISO 8601:2004) dates that use the proleptic Gregorian calendar
		  and astronomical year numbering, where the year 1 BCE is represented as +0000 and the year 44 BCE
		  is represented as -0043.
		*/
		// map negative dates from historical numbering to XSD 1.1
		if (year < 0 && value.getPrecision() >= TimeValue.PREC_YEAR) {
			year++;
		}

		byte month = value.getMonth();
		byte day = value.getDay();

		if ((value.getPrecision() < TimeValue.PREC_MONTH || month == 0) && !yearZero) {
			month = 1;
		}

		if ((value.getPrecision() < TimeValue.PREC_DAY || day == 0) && !yearZero) {
			day = 1;
		}

		if (value.getPrecision() >= TimeValue.PREC_DAY && !yearZero) {
			int maxDays = Byte.MAX_VALUE;
			if (month > 0 && month < 13) {
				boolean leap =  (year % 4L) == 0L && (year % 100L != 0L || year % 400L == 0L);
				maxDays = Month.of(month).length(leap);
			}
			if (day > maxDays) {
				day = (byte)maxDays;
			}
		}

		String minus = year < 0 ? "-" : "";
		String timestamp = String.format("%s%04d-%02d-%02dT%02d:%02d:%02dZ",
				minus, Math.abs(year), month, day,
				value.getHour(), value.getMinute(), value.getSecond());
		if (yearZero) {
			return rdfWriter.getLiteral("+" + timestamp);
		}
		return rdfWriter.getLiteral(timestamp, RdfWriter.XSD_DATETIME);
	}

}
