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

import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Jackson implementation of {@link PropertyDocument}. Like all Jackson objects,
 * it is not technically immutable, but it is strongly recommended to treat it
 * as such in all contexts: the setters are for Jackson; never call them in your
 * code.
 *
 * @author Fredo Erxleben
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JacksonPropertyDocument extends JacksonTermedDocument implements
		PropertyDocument {

	/**
	 * Datatype string as used in JSON. See {@link JacksonDatatypeId} for a
	 * mapping to WDTK datatype IRIs.
	 */
	private String datatype;

	/**
	 * Constructor. Creates an empty object that can be populated during JSON
	 * deserialization. Should only be used by Jackson for this very purpose.
	 */
	public JacksonPropertyDocument() {
	}

	/**
	 * Returns the JSON string version of the property's datatype. Note that
	 * {@link #getDatatype()} is already used for another function of the
	 * interface.
	 *
	 * @see #setJsonDatatype(String)
	 * @return string datatype
	 */
	@JsonProperty("datatype")
	public String getJsonDatatype() {
		return this.datatype;
	}

	/**
	 * Sets the datatype to the given JSON datatype string. Only for use by
	 * Jackson during deserialization.
	 *
	 * @see #getJsonDatatype()
	 * @param datatype
	 */
	@JsonProperty("datatype")
	public void setJsonDatatype(String datatype) {
		this.datatype = datatype;
	}

	@JsonIgnore
	@Override
	public PropertyIdValue getPropertyId() {
		if (this.siteIri == null) {
			return Datamodel.makeWikidataPropertyIdValue(this.entityId);
		} else {
			return Datamodel.makePropertyIdValue(this.entityId, this.siteIri);
		}
	}

	@JsonIgnore
	@Override
	public EntityIdValue getEntityId() {
		return getPropertyId();
	}

	@JsonIgnore
	@Override
	public DatatypeIdValue getDatatype() {
		return new JacksonDatatypeId(this.datatype);
	}

	@Override
	public String getJsonType() {
		return JacksonTermedDocument.JSON_TYPE_PROPERTY;
	}

	@Override
	public int hashCode() {
		return Hash.hashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return Equality.equalsPropertyDocument(this, obj);
	}

	@Override
	public String toString() {
		return ToString.toString(this);
	}
}
