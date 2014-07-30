package org.wikidata.wdtk.datamodel.json.jackson;

import org.junit.runners.Suite;
import org.junit.runner.RunWith;

/**
 * A test suite that runs all the JSON conversion tests.
 * @author Fredo Erxleben
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ TestItemDocument.class, TestMonolingualTextValue.class,
		TestSiteLink.class, TestSnakJson.class, TestValue.class })
public class AllTests {
	// nothing
}
