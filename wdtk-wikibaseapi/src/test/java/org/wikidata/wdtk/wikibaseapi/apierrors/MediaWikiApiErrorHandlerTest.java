package org.wikidata.wdtk.wikibaseapi.apierrors;

/*
 * #%L
 * Wikidata Toolkit Wikibase API
 * %%
 * Copyright (C) 2014 - 2015 Wikidata Toolkit Developers
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

import org.junit.Test;

public class MediaWikiApiErrorHandlerTest {

	@Test
	public void testUnknownError() {
		String code = "";
		String message = "";
		try {
			MediaWikiApiErrorHandler.throwMediaWikiApiErrorException("unknown",
					"some message");
		} catch (MediaWikiApiErrorException e) {
			code = e.getErrorCode();
			message = e.getErrorMessage();
		}

		assertEquals("unknown", code);
		assertEquals("some message", message);
	}

	@Test(expected = TokenErrorException.class)
	public void testNoTokenError() throws MediaWikiApiErrorException {
		MediaWikiApiErrorHandler.throwMediaWikiApiErrorException(
				MediaWikiApiErrorHandler.ERROR_NO_TOKEN, "some message");
	}

	@Test(expected = TokenErrorException.class)
	public void testBadTokenError() throws MediaWikiApiErrorException {
		MediaWikiApiErrorHandler.throwMediaWikiApiErrorException(
				MediaWikiApiErrorHandler.ERROR_INVALID_TOKEN, "some message");
	}

	@Test(expected = EditConflictErrorException.class)
	public void testEditConflictError() throws MediaWikiApiErrorException {
		MediaWikiApiErrorHandler.throwMediaWikiApiErrorException(
				MediaWikiApiErrorHandler.ERROR_EDIT_CONFLICT, "some message");
	}

	@Test(expected = NoSuchEntityErrorException.class)
	public void testNoSuchEntityError() throws MediaWikiApiErrorException {
		MediaWikiApiErrorHandler.throwMediaWikiApiErrorException(
				MediaWikiApiErrorHandler.ERROR_NO_SUCH_ENTITY, "some message");
	}

	@Test(expected = MaxlagErrorException.class)
	public void testMaxlagError() throws MediaWikiApiErrorException {
		MediaWikiApiErrorHandler.throwMediaWikiApiErrorException(
				MediaWikiApiErrorHandler.ERROR_MAXLAG,
				"Waiting for 10.64.16.27: 2 seconds lagged");
	}

}
