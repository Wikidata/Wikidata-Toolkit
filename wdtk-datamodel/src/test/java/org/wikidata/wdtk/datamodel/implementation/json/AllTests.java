package org.wikidata.wdtk.datamodel.implementation.json;

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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * A test suite that runs all the JSON conversion tests.
 *
 * @author Fredo Erxleben
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ TestItemDocument.class, TestMonolingualTextValue.class,
		TestSiteLink.class, TestSnakJson.class, TestValue.class,
		TestStatement.class, TestDatatypeId.class, TestAliasBug.class })
public class AllTests {
	// nothing
}
