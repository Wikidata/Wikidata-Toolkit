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

package org.wikidata.wdtk.datamodel.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.wikidata.wdtk.datamodel.interfaces.WikimediaLanguageCodes;

public class WikimediaLanguageCodesTest {

	@Test
	public void getSomeLanguageCodes() {
		assertEquals("gsw", WikimediaLanguageCodes.getLanguageCode("als"));
		assertEquals("en", WikimediaLanguageCodes.getLanguageCode("en"));
	}

	@Test
	public void getUnknownLanguageCode() {
		assertThrows(IllegalArgumentException.class, () -> WikimediaLanguageCodes.getLanguageCode("unknown"));
	}

	@Test
	public void fixDeprecatedLanguageCode() {
		assertEquals("nb", WikimediaLanguageCodes.fixLanguageCodeIfDeprecated("no"));
		assertEquals("en", WikimediaLanguageCodes.fixLanguageCodeIfDeprecated("en"));
	}
	
	@Test
	public void fixUnknownLanguageCode() {
		assertEquals("unknown",
				WikimediaLanguageCodes.fixLanguageCodeIfDeprecated("unknown"));
	}
}
