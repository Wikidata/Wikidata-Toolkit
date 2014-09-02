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

import org.wikidata.wdtk.datamodel.interfaces.Value;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="type")
@JsonSubTypes({  
    @Type(value = StringValueImpl.class, name = "string"),  
    @Type(value = TimeValueImpl.class, name = "time"),  
    @Type(value = EntityIdValueImpl.class, name = "wikibase-entityid"),
    @Type(value = GlobeCoordinateValueImpl.class, name = "globecoordinate"),
    @Type(value = QuantityValueImpl.class, name = "quantity"),
    @Type(value = MonolingualTextDatavalueImpl.class, name = "monolingualtext")}) 
public abstract class ValueImpl 
implements Value {
	
	public static final String typeString = "string";
	public static final String typeTime = "time";
	public static final String typeCoordinate = "globecoordinate";
	public static final String typeEntity = "wikibase-entityid";
	public static final String typeQuantity = "quantity";
	public static final String typeMonolingualText = "monolingualtextvalue";
	
	private String type;
	
	public ValueImpl(){}
	public ValueImpl(String type){
		this.type = type;
	}

	@Override
	abstract public <T> T accept(ValueVisitor<T> valueVisitor);
	
	public void setType(String type){
		this.type = type;
	}
	
	public String getType(){
		return this.type;
	}
}
