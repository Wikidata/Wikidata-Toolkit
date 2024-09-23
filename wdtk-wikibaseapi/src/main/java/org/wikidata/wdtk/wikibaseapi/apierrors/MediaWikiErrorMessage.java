package org.wikidata.wdtk.wikibaseapi.apierrors;

import java.util.Objects;

/*-
 * #%L
 * Wikidata Toolkit Wikibase API
 * %%
 * Copyright (C) 2014 - 2024 Wikidata Toolkit Developers
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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An error message returned as part of the `messages` field of a broader
 * MediaWiki error, providing more details on the source of the problem.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MediaWikiErrorMessage {
    
    final String name;
    final String htmlText;
    
    /**
     * Constructor
     * 
     * @param name    a code for the error message, such as "spam-blacklisted-link"
     * @param htmlText the text of the error in HTML
     */
    public MediaWikiErrorMessage(String name, String htmlText) {
        this.name = name;
        this.htmlText = htmlText;
    }
    
    /**
     * Constructor for deserialization purposes (via Jackson).
     */
    @JsonCreator
    MediaWikiErrorMessage(@JsonProperty("name") String name, @JsonProperty("html") MessageWrapper wrapper) {
        this.name = name;
        this.htmlText = wrapper.message;
    }
    
    // Wrapper class provided for deserialization purposes
    protected static class MessageWrapper {
        final String message;

        @JsonCreator
        public MessageWrapper(@JsonProperty("*") String message) {
            this.message = message;
        }
    }

    /**
     * A code for the error message, such as "spam-blacklisted-link"
     */
    public String getName() {
        return name;
    }

    /**
     * A human-readable error text in HTML
     */
    public String getHTMLText() {
        return htmlText;
    }

    @Override
    public int hashCode() {
        return Objects.hash(htmlText, name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MediaWikiErrorMessage other = (MediaWikiErrorMessage) obj;
        return Objects.equals(htmlText, other.htmlText) && Objects.equals(name, other.name);
    }

    @Override
    public String toString() {
        return "MediaWikiErrorMessage [name=" + name + ", htmlText=" + htmlText + "]";
    }
    
}
