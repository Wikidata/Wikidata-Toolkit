package org.wikidata.wdtk.wikibaseapi;

/*-
 * #%L
 * Wikidata Toolkit Wikibase API
 * %%
 * Copyright (C) 2014 - 2023 Wikidata Toolkit Developers
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

import static org.junit.Assert.assertEquals;

import java.util.OptionalLong;

import org.junit.Test;

public class EditingResultTest {

    @Test
    public void testGetRevisionId() {
        EditingResult SUT = new EditingResult(1234L);
        
        assertEquals(SUT.getLastRevisionId(), OptionalLong.of(1234L));
    }
    
    @Test
    public void testNoRevisionId() {
        EditingResult SUT = new EditingResult(0L);
        
        assertEquals(SUT.getLastRevisionId(), OptionalLong.empty());
    }
}
