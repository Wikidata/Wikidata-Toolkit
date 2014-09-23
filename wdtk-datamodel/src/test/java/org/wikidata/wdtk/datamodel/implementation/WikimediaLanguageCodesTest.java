package org.wikidata.wdtk.datamodel.implementation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.WikimediaLanguageCodes;

public class WikimediaLanguageCodesTest {

	@Test
	public void getSomeLanguageCodes() {
		assertEquals("gsw", WikimediaLanguageCodes.getLanguageCode("als"));
		assertEquals("en", WikimediaLanguageCodes.getLanguageCode("en"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void getUnknownLanguageCode() {
		WikimediaLanguageCodes.getLanguageCode("unknown");
	}

}
