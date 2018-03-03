package org.wikidata.wdtk.datamodel.implementation;

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
import org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Jackson implementation of {@link SomeValueSnak}.
 *
 * @author Fredo Erxleben
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SomeValueSnakImpl extends SnakImpl implements SomeValueSnak {
	
	/**
	 * Constructor.
	 * 
	 * @param property
	 * 		the id of the property used for this some value snak
	 */
	public SomeValueSnakImpl(PropertyIdValue property) {
		super(property);
	}

	/**
	 * Constructor for deserialization from JSON with Jackson.
	 */
	@JsonCreator
	protected SomeValueSnakImpl(
			@JsonProperty("property") String property,
			@JacksonInject("siteIri") String siteIri) {
		super(property, siteIri);
	}
	@Override
	@JsonProperty("snaktype")
	public String getSnakType() {
		return SnakImpl.JSON_SNAK_TYPE_SOMEVALUE;
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
		return Equality.equalsSomeValueSnak(this, obj);
	}

	@Override
	public String toString() {
		return ToString.toString(this);
	}

}
