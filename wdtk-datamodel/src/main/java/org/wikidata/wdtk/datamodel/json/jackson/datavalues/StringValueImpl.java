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


/**
 * This represents a string value.
 * The <i>type</i> is <i>"string"</i>.
 * 
 * This is used in string data values as well as commoms media data values.
 * @author Fredo Erxleben
 *
 */
public class StringValueImpl extends ValueImpl {

	private String value;
	
	public StringValueImpl(){
		super(typeString);
	}
	public StringValueImpl(String value){
		super(typeString);
		this.value = value;
	}
	
	public String getValue(){
		return this.value;
	}
	
	public void setValue(String value){
		this.value = value;
	}
}
