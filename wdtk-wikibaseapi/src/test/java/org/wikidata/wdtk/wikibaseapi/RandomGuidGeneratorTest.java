package org.wikidata.wdtk.wikibaseapi;

import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

/*
 * #%L
 * Wikidata Toolkit Wikibase API
 * %%
 * Copyright (C) 2014 - 2018 Wikidata Toolkit Developers
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

import org.junit.Test;

public class RandomGuidGeneratorTest {
	@Test
	public void testGuidFormat() {
		GuidGenerator generator = new RandomGuidGenerator();
		String guid = generator.freshStatementId("Q42");
		Pattern re = Pattern.compile("^Q42\\$\\{?[A-Z\\d]{8}-[A-Z\\d]{4}-[A-Z\\d]{4}-[A-Z\\d]{4}-[A-Z\\d]{12}\\}?\\z");
		assertTrue(re.matcher(guid).matches());
	}
}
