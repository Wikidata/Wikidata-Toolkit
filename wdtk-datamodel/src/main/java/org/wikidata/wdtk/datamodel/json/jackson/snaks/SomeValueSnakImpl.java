package org.wikidata.wdtk.datamodel.json.jackson.snaks;

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

import org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak;

public class SomeValueSnakImpl extends SnakImpl implements SomeValueSnak {
	
	static final String somevalue = "somevalue";
	
	public SomeValueSnakImpl(){
		super();
		this.setSnakType(somevalue);
	}
	
	public SomeValueSnakImpl(String propertyId){
		super(propertyId);
		this.setSnakType(somevalue);
	}
}
