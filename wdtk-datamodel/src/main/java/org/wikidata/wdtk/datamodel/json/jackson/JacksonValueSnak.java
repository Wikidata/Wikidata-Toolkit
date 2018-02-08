package org.wikidata.wdtk.datamodel.json.jackson;

import org.apache.commons.lang3.Validate;

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
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.SnakVisitor;
import org.wikidata.wdtk.datamodel.interfaces.Value;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonValue;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Jackson implementation of {@link ValueSnak}.
 *
 * @author Fredo Erxleben
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JacksonValueSnak extends JacksonSnak implements ValueSnak {

	/**
	 * The {@link Value} assigned to this snak.
	 */
	@JsonDeserialize(as = JacksonValue.class)
	private final Value datavalue;

	/**
	 * The datatype of this property which determines
	 * the type of datavalue it stores. It can be null, in the case
	 * of string datavalues.
	 */
	private final String datatype;
	

	/**
	 * Constructor.
	 * @param property
	 * @param datatype
	 * @param value
	 */
	public JacksonValueSnak(PropertyIdValue property, String datatype, Value value) {
		super(property.getId(), property.getSiteIri());
		Validate.notNull(value, "A datavalue must be provided to create a value snak.");
		this.datavalue = value;
		this.datatype = datatype;
	}

	/**
	 * Constructor used to deserialize from JSON with Jackson.
	 */
	@JsonCreator
	protected JacksonValueSnak(
			@JsonProperty("property") String property,
			@JsonProperty("datatype") String datatype,
			@JsonProperty("datavalue") Value datavalue,
			@JacksonInject("siteIri") String siteIri) {
		super(property, siteIri);
		Validate.notNull(datavalue, "A datavalue must be provided to create a value snak.");
		this.datavalue = datavalue;
		this.datatype = datatype;
	}


	@JsonProperty("datavalue")
	public Value getDatavalue() {
		return this.datavalue;
	}
	
	@Override
	public Value getValue() {
		return this.datavalue;
	}

	/**
	 * Returns the JSON datatype string. Only for use by Jackson during
	 * serialization.
	 * 
	 * The property datatype of the property used for this value snak. This is
	 * redundant information provided in the JSON but not represented in the
	 * datamodel. We keep it and serialize it if given, but if we do not have
	 * it, we set it to null and it will be omitted in the serialization.
	 *
	 * @return the JSON datatype string
	 */
	@JsonProperty("datatype")
	@JsonInclude(value = JsonInclude.Include.NON_NULL)
	public String getDatatype() {
		return this.datatype;
	}
	
	@Override
	@JsonProperty("snaktype")
	public String getSnakType() {
		return JacksonSnak.JSON_SNAK_TYPE_VALUE;
	}

	@Override
	public <T> T accept(SnakVisitor<T> snakVisitor) {
		return snakVisitor.visit(this);
	}

	@Override
	public int hashCode() {
		return Hash.hashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return Equality.equalsValueSnak(this, obj);
	}

	@Override
	public String toString() {
		return ToString.toString(this);
	}
}
