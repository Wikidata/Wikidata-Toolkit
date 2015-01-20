package org.wikidata.wdtk.datamodel.json.jackson;

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
import org.wikidata.wdtk.datamodel.interfaces.SnakVisitor;
import org.wikidata.wdtk.datamodel.interfaces.Value;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonValue;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonValueEntityId;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Jackson implementation of {@link ValueSnak}.
 *
 * @author Fredo Erxleben
 *
 */
public class JacksonValueSnak extends JacksonSnak implements ValueSnak {

	/**
	 * The {@link Value} assigned to this snak.
	 */
	private JacksonValue datavalue;

	/**
	 * The property datatype of the property used for this value snak. This is
	 * redundant information provided in the JSON but not represented in the
	 * datamodel. We keep it and serialize it if given, but if we do not have
	 * it, we set it to null and it will be omitted in the serialization.
	 */
	@JsonInclude(value = JsonInclude.Include.NON_NULL)
	private String datatype = null;

	/**
	 * Constructor. Creates an empty object that can be populated during JSON
	 * deserialization. Should only be used by Jackson for this very purpose.
	 */
	public JacksonValueSnak() {
		super();
	}

	@JsonIgnore
	@Override
	public Value getValue() {
		return this.datavalue;
	}

	/**
	 * Returns the JSON datatype string. Only for use by Jackson during
	 * serialization.
	 *
	 * @return the JSON datatype string
	 */
	public String getDatatype() {
		return this.datatype;
	}

	/**
	 * Sets the JSON datatype string to the given value. Only for use by Jackson
	 * during deserialization.
	 *
	 * @param datatype
	 *            new value
	 */
	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	/**
	 * Sets the snak value to the given value. Only for use by Jackson during
	 * deserialization.
	 *
	 * @param datavalue
	 *            new value
	 */
	public void setDatavalue(JacksonValue datavalue) {
		this.datavalue = datavalue;
	}

	/**
	 * Returns the snak value. Only for use by Jackson during serialization.
	 *
	 * @return the snak value
	 */
	public JacksonValue getDatavalue() {
		return this.datavalue;
	}

	@Override
	void setSiteIri(String siteIri) {
		super.setSiteIri(siteIri);
		if (this.datavalue instanceof JacksonValueEntityId) {
			((JacksonValueEntityId) this.datavalue).setSiteIri(siteIri);
			;
		}
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
