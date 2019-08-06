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

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MediaInfoDocument;

/**
 * Abstract Jackson implementation of {@link EntityDocument}. Like all Jackson
 * objects, it is not technically immutable, but it is strongly recommended to
 * treat it as such in all contexts: the setters are for Jackson; never call
 * them in your code.
 *
 * @author Thomas Pellissier Tanon
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
		@Type(value = ItemDocumentImpl.class, name = EntityDocumentImpl.JSON_TYPE_ITEM),
		@Type(value = LexemeDocumentImpl.class, name = EntityDocumentImpl.JSON_TYPE_LEXEME),
		@Type(value = FormDocumentImpl.class, name = EntityDocumentImpl.JSON_TYPE_FORM),
		@Type(value = SenseDocumentImpl.class, name = EntityDocumentImpl.JSON_TYPE_SENSE),
		@Type(value = MediaInfoDocumentImpl.class, name = EntityDocumentImpl.JSON_TYPE_MEDIA_INFO),
		@Type(value = PropertyDocumentImpl.class, name = EntityDocumentImpl.JSON_TYPE_PROPERTY) })
public abstract class EntityDocumentImpl implements EntityDocument {

	/**
	 * String used to refer to items in JSON.
	 */
	static final String JSON_TYPE_ITEM = "item";
	/**
	 * String used to refer to properties in JSON.
	 */
	static final String JSON_TYPE_PROPERTY = "property";
	/**
	 * String used to refer to lexemes in JSON.
	 */
	static final String JSON_TYPE_LEXEME = "lexeme";
	/**
	 * String used to refer to forms in JSON.
	 */
	static final String JSON_TYPE_FORM = "form";
	/**
	 * String used to refer to forms in JSON.
	 */
	static final String JSON_TYPE_SENSE = "sense";
	/**
	 * String used to refer to forms in JSON.
	 */
	static final String JSON_TYPE_MEDIA_INFO = "mediainfo";

	/**
	 * The id of the entity that the document refers to. This is not mapped to
	 * JSON directly by Jackson but split into two fields, "type" and "id". The
	 * type field is ignored during deserialization since the type is clear for
	 * a concrete document. For serialization, the type is hard-coded.
	 * <p>
	 * The site IRI, which would also be required to create a complete
	 * {@link EntityIdValue}, is not encoded in JSON. It needs to be injected
	 * from the outside (if not, we default to Wikidata).
	 */
	@JsonIgnore
	protected final String entityId;

	/**
	 * The site IRI that this document refers to, or null if not specified. In
	 * the latter case, we assume Wikidata as the default.
	 *
	 * @see EntityIdValue#getSiteIri()
	 */
	@JsonIgnore
	protected final String siteIri;

	/**
	 * The revision id of this document.
	 *
	 * @see EntityDocument#getRevisionId()
	 */
	@JsonIgnore
	protected final long revisionId;

	/**
	 * Constructor.
	 *
	 * @param id
	 * 		the identifier of the subject of this document
	 * @param revisionId
	 * 		the id of the last revision of this document
	 */
	EntityDocumentImpl(EntityIdValue id, long revisionId) {
		Validate.notNull(id);
		this.entityId = id.getId();
		this.siteIri = id.getSiteIri();
		this.revisionId = revisionId;
	}

	/**
	 * Constructor used for JSON deserialization with Jackson.
	 */
	EntityDocumentImpl(
			@JsonProperty("id") String jsonId,
			@JsonProperty("lastrevid") long revisionId,
			@JacksonInject("siteIri") String siteIri) {
		Validate.notNull(jsonId);
		this.entityId = jsonId;
		Validate.notNull(siteIri);
		this.siteIri = siteIri;
		this.revisionId = revisionId;
	}

	/**
	 * Returns the string id of the entity that this document refers to. Only
	 * for use by Jackson during serialization.
	 *
	 * @return string id
	 */
	@JsonInclude(Include.NON_EMPTY)
	@JsonProperty("id")
	public String getJsonId() {
		if (!EntityIdValue.SITE_LOCAL.equals(this.siteIri)) {
			return this.entityId;
		} else {
			return null;
		}
	}

	@JsonIgnore
	public String getSiteIri() {
		return this.siteIri;
	}
	
	private static class NonZeroFilter {
		@Override
		public boolean equals(Object other) {
			return (other instanceof Long) && (long)other == 0;
		}

		@Override
		public int hashCode() {
			return 0;
		}
	}

	@Override
	@JsonInclude(value=Include.CUSTOM, valueFilter=NonZeroFilter.class)
	@JsonProperty("lastrevid")
	public long getRevisionId() {
		return this.revisionId;

	}
}
