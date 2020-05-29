package org.wikidata.wdtk.datamodel.implementation;

import java.util.*;

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

import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.Value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Abstract Jackson implementation of {@link Snak}.
 *
 * @author Fredo Erxleben
 * @author Antonin Delpeuch
 *
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "snaktype")
@JsonSubTypes({
		@Type(value = NoValueSnakImpl.class, name = SnakImpl.JSON_SNAK_TYPE_NOVALUE),
		@Type(value = SomeValueSnakImpl.class, name = SnakImpl.JSON_SNAK_TYPE_SOMEVALUE),
		@Type(value = ValueSnakImpl.class, name = SnakImpl.JSON_SNAK_TYPE_VALUE) })
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class SnakImpl implements Snak {

	/**
	 * Type string used to denote value snaks in JSON.
	 */
	public static final String JSON_SNAK_TYPE_VALUE = "value";
	/**
	 * Type string used to denote somevalue snaks in JSON.
	 */
	public static final String JSON_SNAK_TYPE_SOMEVALUE = "somevalue";
	/**
	 * Type string used to denote novalue snaks in JSON.
	 */
	public static final String JSON_SNAK_TYPE_NOVALUE = "novalue";

	/**
	 * The property used by this snak.
	 */
	private final PropertyIdValue property;
	
	/**
	 * The wikidata hash of this Snak . null if we don't have knowledge about the hash.
	 */
	private String hash;
	
	
	/**
	 * The snaktype of this snak.
	 */
	private final String snaktype;
	
	/**
	 * The datavalue of this snak.
	 */
	private final HashMap<String, String> datavalue;
	
	/**
	 * The datatype of this snak.
	 */
	private final String datatype;
	
	
	
	/**
	 * Constructor.
	 */
	public SnakImpl(PropertyIdValue property) {
		this.datatype = "";
		this.hash = null;
		this.snaktype = "";
		this.datavalue = new HashMap<String,String>();
		Validate.notNull(property);
		this.property = property;
	}

	/**
	 * Constructor. Creates an empty object that can be populated during JSON
	 * deserialization. Should only be used by Jackson for this very purpose.
	 * 
	 * This is not marked as JsonCreator because only concrete subclasses will
	 * be deserialized directly.
	 */
	protected SnakImpl(
			String id,
			String siteIri) {
		this.datatype = "";
		this.snaktype = "";
		this.hash = null;
		this.datavalue = new HashMap<String,String>();
		Validate.notNull(id);
		Validate.notNull(siteIri);
		this.property = new PropertyIdValueImpl(id, siteIri);
	}
	
	/**
	 * Constructor for snak deserialization from JSON with @JsonCreator
	 */
	@JsonCreator
	protected SnakImpl(
			@JsonProperty("snaktype") String snaktype,
			@JsonProperty("property") PropertyIdValue property,
			@JsonProperty("hash") String hash,
			@JsonProperty("datavalue") HashMap<String,String> datavalue,
			@JsonProperty("datatype") String datatype) {

		
		
		this.snaktype = snaktype;
		this.property = property;
		this.hash = hash;
		this.datavalue = datavalue;
		this.datatype = datatype;

	}
	
	
	
	
	/**
	 * Returns the hash as found in JSON. Only for use by Jackson during
	 * serialization.
	 * The wikidata hash of this snak. null if we don't have knowledge about the hash.
	 * @return the hash
	 */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public String getHash() {
		return hash;
	}
	
	
	
	/**
	 * Returns the property id string. Only for use by Jackson during
	 * serialization.
	 *
	 * @return the property id string
	 */
	@JsonProperty("property")
	public String getProperty() {
		return this.property.getId();
	}

	@JsonIgnore
	@Override
	public PropertyIdValue getPropertyId() {
		return property;
	}

	@JsonIgnore
	@Override
	public Value getValue() {
		return null;
	}
	
	@JsonProperty("snaktype")
	public String getSnakType() {
		return this.snaktype;	
	}
	
	@JsonProperty("datatype")
	public String getDataType() {
		return this.datatype;	
	}
	
}
