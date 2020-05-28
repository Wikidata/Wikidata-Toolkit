package org.wikidata.wdtk.wikibaseapi;

/*
 * #%L
 * Wikidata Toolkit Wikibase API
 * %%
 * Copyright (C) 2014 - 2016 Wikidata Toolkit Developers
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

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Jackson implementation of {@link WbSearchEntitiesResult}
 *
 * @author Sören Brunk
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class JacksonWbSearchEntitiesResult implements WbSearchEntitiesResult {

    /**
     * Jackson implementation of {@link Match}
     */
    static class JacksonMatch implements Match {

        @JsonCreator
        JacksonMatch(
                @JsonProperty("type") String type,
                @JsonProperty("language") String language,
                @JsonProperty("text") String text
        ) {
            this.type = type;
            this.language = language;
            this.text = text;
        }
        /**
         * The type (field) of the matching term
         * e.g "entityId", "label" or "alias".
         */
        @JsonProperty("type")
        private String type;
        /**
         * Language of the matching term field.
         */
        @JsonProperty("language")
        private String language;
        /**
         * Text of the matching term.
         */
        @JsonProperty("text")
        private String text;

        public String getType() {
            return type;
        }

        public String getLanguage() {
            return language;
        }

        public String getText() {
            return text;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                    + ((language == null) ? 0 : language.hashCode());
            result = prime * result + ((text == null) ? 0 : text.hashCode());
            result = prime * result + ((type == null) ? 0 : type.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if(!(obj instanceof JacksonMatch)) {
                return false;
            }
            JacksonMatch other = (JacksonMatch) obj;
            return Objects.equals(language, other.language) &&
                    Objects.equals(text, other.text) &&
                    Objects.equals(type, other.type);
        }
    }

    /**
     * Constructor. Creates an empty object that can be populated during JSON
     * deserialization. Should only be used by Jackson for this very purpose.
     */
    JacksonWbSearchEntitiesResult() {}

    /**
     * The id of the entity that the document refers to.
     */
    @JsonProperty("id")
    private String entityId;

    /**
     * The full concept URI (the site IRI with entity ID).
     */
    @JsonProperty("concepturi")
    private String conceptUri;

    /**
     * The URL of the wiki site that shows the concept.
     */
    @JsonProperty("url")
    private String url;

    /**
     * Title of the entity (currently is the same as the entity ID).
     */
    @JsonProperty("title")
    private String title;

    /**
     * The internal Mediawiki pageid of the entity.
     */
    @JsonProperty("pageid")
    private long pageId;

    /**
     * Label of the entity
     *
     * The language of the returned label depends on the HTTP
     * <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.4">
     * Accept-Language header or the uselang URL parameter.
     */
    @JsonProperty("label")
    private String label;

    /**
     * Description of the entity
     *
     * The language of the returned description depends on the HTTP
     * <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.4">
     * Accept-Language header or the uselang URL parameter.
     */
    @JsonProperty("description")
    private String description;

    /**
     * Detailed information about how a document matched the query
     */
    @JsonProperty("match")
    private JacksonMatch match;

    /**
     * A list of alias labels (returned only when an alias matched the query)
     */
    @JsonProperty("aliases")
    private List<String> aliases;

    public void setEntityId(String id) {
        this.entityId = id;
    }

    /* (non-Javadoc)
     * @see org.wikidata.wdtk.wikibaseapi.IWbSearchInterfaceResult#getEntityId()
     */
    @Override
    public String getEntityId() {
        return this.entityId;
    }

    /* (non-Javadoc)
     * @see org.wikidata.wdtk.wikibaseapi.IWbSearchInterfaceResult#getConceptUri()
     */
    @Override
    public String getConceptUri() {
        return conceptUri;
    }

    public void setConceptUri(String conceptUri) {
        this.conceptUri = conceptUri;
    }

    /* (non-Javadoc)
     * @see org.wikidata.wdtk.wikibaseapi.IWbSearchInterfaceResult#getUrl()
     */
    @Override
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /* (non-Javadoc)
     * @see org.wikidata.wdtk.wikibaseapi.IWbSearchInterfaceResult#getTitle()
     */
    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /* (non-Javadoc)
     * @see org.wikidata.wdtk.wikibaseapi.IWbSearchInterfaceResult#getPageId()
     */
    @Override
    public long getPageId() {
        return pageId;
    }

    public void setPageId(long pageId) {
        this.pageId = pageId;
    }

    /* (non-Javadoc)
     * @see org.wikidata.wdtk.wikibaseapi.IWbSearchInterfaceResult#getLabel()
     */
    @Override
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    /* (non-Javadoc)
     * @see org.wikidata.wdtk.wikibaseapi.IWbSearchInterfaceResult#getDescription()
     */
    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /* (non-Javadoc)
     * @see org.wikidata.wdtk.wikibaseapi.IWbSearchInterfaceResult#getMatch()
     */
    @Override
    public Match getMatch() {
        return match;
    }

    public void setMatch(JacksonMatch match) {
        this.match = match;
    }

    /* (non-Javadoc)
     * @see org.wikidata.wdtk.wikibaseapi.IWbSearchInterfaceResult#getAliases()
     */
    @Override
    public List<String> getAliases() {
        return aliases;
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((aliases == null) ? 0 : aliases.hashCode());
        result = prime * result
                + ((conceptUri == null) ? 0 : conceptUri.hashCode());
        result = prime * result
                + ((description == null) ? 0 : description.hashCode());
        result = prime * result
                + ((entityId == null) ? 0 : entityId.hashCode());
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        result = prime * result + ((match == null) ? 0 : match.hashCode());
        result = prime * result + (int) (pageId ^ (pageId >>> 32));
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if(!(obj instanceof JacksonWbSearchEntitiesResult)) {
            return false;
        }
        JacksonWbSearchEntitiesResult other = (JacksonWbSearchEntitiesResult) obj;
        return Objects.equals(aliases, other.aliases) &&
                Objects.equals(conceptUri, other.conceptUri) &&
                Objects.equals(description, other.description) &&
                Objects.equals(entityId, other.entityId) &&
                Objects.equals(label, other.label) &&
                Objects.equals(match, other.match) &&
                pageId == other.pageId &&
                Objects.equals(title, other.title) &&
                Objects.equals(url, other.url);
    }


}
