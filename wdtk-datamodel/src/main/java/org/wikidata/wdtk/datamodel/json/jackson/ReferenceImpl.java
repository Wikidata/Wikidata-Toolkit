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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.json.jackson.snaks.SnakImpl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class ReferenceImpl implements Reference {

	private Map<String, List<SnakImpl>> snaks;
	
	@JsonIgnore // not in the actual JSON, just to satisfy the interface
	@Override
	public List<SnakGroup> getSnakGroups() {
		return Helper.buildSnakGroups(this.snaks);
	}
	
	public void setSnaks(Map<String, List<SnakImpl>> snaks){
		this.snaks = snaks;
	}

	public Map<String, List<SnakImpl>> getSnaks(){
		return this.snaks;
	}

	@Override
	public Iterator<Snak> getAllSnaks() {
		// have to create a new list to have something to get an iterator from
		List<Snak> tempSnaks = new ArrayList<>();
		for( List<SnakImpl> entry : this.snaks.values()){
			tempSnaks.addAll(entry);
		}
		return tempSnaks.iterator();
	}
	
	@Override
	public int hashCode() {
		return Hash.hashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return Equality.equalsReference(this, obj);
	}

	@Override
	public String toString() {
		return ToString.toString(this);
	}
}
