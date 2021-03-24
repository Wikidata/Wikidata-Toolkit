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

package org.wikidata.wdtk.wikibaseapi.apierrors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

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

	@Test
	public void testNoTokenError() throws MediaWikiApiErrorException {
		assertThrows(TokenErrorException.class, () -> MediaWikiApiErrorHandler.throwMediaWikiApiErrorException(
				MediaWikiApiErrorHandler.ERROR_NO_TOKEN, "some message"));
	}

	@Test
	public void testBadTokenError() throws MediaWikiApiErrorException {
		assertThrows(TokenErrorException.class, () -> MediaWikiApiErrorHandler.throwMediaWikiApiErrorException(
				MediaWikiApiErrorHandler.ERROR_INVALID_TOKEN, "some message"));
	}

	@Test
	public void testEditConflictError() throws MediaWikiApiErrorException {
		assertThrows(EditConflictErrorException.class, () -> MediaWikiApiErrorHandler.throwMediaWikiApiErrorException(
				MediaWikiApiErrorHandler.ERROR_EDIT_CONFLICT, "some message"));
	}

	@Test
	public void testNoSuchEntityError() throws MediaWikiApiErrorException {
		assertThrows(NoSuchEntityErrorException.class, () -> MediaWikiApiErrorHandler.throwMediaWikiApiErrorException(
				MediaWikiApiErrorHandler.ERROR_NO_SUCH_ENTITY, "some message"));
	}

	@Test
	public void testMaxlagError() throws MediaWikiApiErrorException {
		assertThrows(MaxlagErrorException.class, () -> MediaWikiApiErrorHandler.throwMediaWikiApiErrorException(
				MediaWikiApiErrorHandler.ERROR_MAXLAG,
				"Waiting for 10.64.16.27: 2 seconds lagged"));
	}

}
