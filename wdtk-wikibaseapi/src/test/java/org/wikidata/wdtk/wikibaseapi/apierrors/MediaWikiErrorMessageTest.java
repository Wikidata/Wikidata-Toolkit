package org.wikidata.wdtk.wikibaseapi.apierrors;

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

import static org.junit.Assert.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

public class MediaWikiErrorMessageTest {

    @Test
    public void testDeserialize() throws JsonMappingException, JsonProcessingException {
        String json = "{\"name\": \"wikibase-api-failed-save\","
                + "\"html\": {\"*\": \"The save has failed\"}}";
        
        ObjectMapper mapper = new ObjectMapper();
        MediaWikiErrorMessage message = mapper.readValue(json, MediaWikiErrorMessage.class);
        
        assertEquals("The save has failed", message.getHTMLText());
        assertEquals("wikibase-api-failed-save", message.getName());
    }
}
