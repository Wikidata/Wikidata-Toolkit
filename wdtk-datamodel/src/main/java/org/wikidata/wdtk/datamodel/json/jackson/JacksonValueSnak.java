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

import com.fasterxml.jackson.annotation.JsonIgnore;


public class JacksonValueSnak extends JacksonSnak implements ValueSnak {

	static final String value = "value";
	
	// the names used in the JSON
	public static final String datatypeString = "string";
	public static final String datatypeTime = "time";
	public static final String datatypeCoordinate = "globe-coordinate";
	public static final String datatypeEntity = "wikibase-item";
	public static final String datatypeCommons = "commonsMedia";
	
	private JacksonValue datavalue;
	private String datatype; // should correspond to "type" in datavalue
	
	public JacksonValueSnak(){
		super();
		this.setSnakType(value);
	}
	
	public JacksonValueSnak(String propertyId, String datatype, JacksonValue datavalue){
		super(propertyId);
		this.setSnakType(value);
		this.setDatatype(datatype);
		this.setDatavalue(datavalue);
	}
	
	public String getDatatype(){
		return this.datatype;
	}
	
	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	@JsonIgnore
	@Override
	public Value getValue() {
		return this.datavalue;
	}
	
	
	public void setDatavalue(JacksonValue datavalue){
		this.datavalue = datavalue;
	}
	
	public JacksonValue getDatavalue(){
		return this.datavalue;
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
