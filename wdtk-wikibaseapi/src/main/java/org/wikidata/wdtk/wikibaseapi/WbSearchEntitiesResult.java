package org.wikidata.wdtk.wikibaseapi;

/*
 * #%L
 * Wikidata Toolkit Wikibase API
 * %%
 * Copyright (C) 2014 - 2017 Wikidata Toolkit Developers
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

/**
 * Represents the result of a wbsearchentities action.
 *
 * @author Sören Brunk
 */
public interface WbSearchEntitiesResult {

    /**
     * Represents information about how a document matched the query
     */
    interface Match  {
        /**
         * Returns the type (field) of the matching term
         * e.g "entityId", "label" or "alias".
         *
         * @return type (field) of the match
         */
        String getType();

        /**
         * Returns the language of the matching term field.
         *
         * @return language of the match
         */
        String getLanguage();
        /**
         * Returns the text of the matching term.
         *
         * @return text of the match
         */
        String getText();
    }

    /**
     * Returns the id of the entity that the document refers to.
     *
     * @return the entity ID
     */
    String getEntityId();

    /**
     * Returns the full concept URI (the site IRI with entity ID).
     *
     * @return full concept URI
     */
    String getConceptUri();

    /**
     * The URL of the wiki site that shows the concept.
     *
     * @return wiki site URL
     */
    String getUrl();

    /**
     * Returns the title of the entity (currently the same as the entity ID).
     */
    String getTitle();

    /**
     * Returns the internal Mediawiki pageid of the entity.
     *
     * @return internal pageid
     */
    long getPageId();

    /**
     * Returns the label of the entity.
     *
     * The language of the returned label depends on the HTTP
     * <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.4">
     * Accept-Language header or the uselang URL parameter.
     *
     * @return the label of the entity
     */
    String getLabel();

    /**
     * Returns the description of the entity
     *
     * The language of the returned description depends on the HTTP
     * <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.4">
     * Accept-Language header or the uselang URL parameter.
     *
     * @return the description
     */
    String getDescription();

    /**
     * Returns detailed information about how a document matched the query.
     *
     * @return match information
     */
    Match getMatch();

    /**
     * A list of alias labels (returned only when an alias matched the query).
     *
     * @ return a list of aliases
     */
    List<String> getAliases();

}
