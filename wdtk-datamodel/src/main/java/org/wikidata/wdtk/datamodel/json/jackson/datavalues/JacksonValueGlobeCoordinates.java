package org.wikidata.wdtk.datamodel.json.jackson.datavalues;

/*
 * #%L
 * Wikidata Toolkit Data Model
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

import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Jackson implementation of {@link GlobeCoordinatesValue}.
 *
 * @author Fredo Erxleben
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JacksonValueGlobeCoordinates extends JacksonValue implements
		GlobeCoordinatesValue {

	/**
	 * Inner helper object to store the actual data. Used to get the nested JSON
	 * structure that is required here.
	 */
	private JacksonInnerGlobeCoordinates value;

	/**
	 * Constructor. Creates an empty object that can be populated during JSON
	 * deserialization. Should only be used by Jackson for this very purpose.
	 */
	public JacksonValueGlobeCoordinates() {
		super(JSON_VALUE_TYPE_GLOBE_COORDINATES);
	}

	/**
	 * Returns the inner value helper object. Only for use by Jackson during
	 * serialization.
	 *
	 * @return the inner globe coordinates value
	 */
	public JacksonInnerGlobeCoordinates getValue() {
		return value;
	}

	/**
	 * Sets the inner value helper object to the given value. Only for use by
	 * Jackson during deserialization.
	 *
	 * @param value
	 *            new value
	 */
	public void setValue(JacksonInnerGlobeCoordinates value) {
		this.value = value;
	}

	@JsonIgnore
	@Override
	public long getLatitude() {
		return (long)(this.value.getLatitude() * GlobeCoordinatesValue.PREC_DEGREE);
	}

	@JsonIgnore
	@Override
	public long getLongitude() {
		return (long)(this.value.getLongitude() * GlobeCoordinatesValue.PREC_DEGREE);
	}

	@JsonIgnore
	@Override
	public long getPrecision() {
		return (long)(this.value.getPrecision() * GlobeCoordinatesValue.PREC_DEGREE);
	}

	@JsonIgnore
	@Override
	public String getGlobe() {
		return this.value.getGlobe();
	}

	@Override
	public <T> T accept(ValueVisitor<T> valueVisitor) {
		return valueVisitor.visit(this);
	}

	@Override
	public int hashCode() {
		return Hash.hashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return Equality.equalsGlobeCoordinatesValue(this, obj);
	}

	@Override
	public String toString() {
		return ToString.toString(this);
	}
}
