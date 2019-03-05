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

/**
 * Class to interpret MediaWiki API errors.
 *
 * @author Markus Kroetzsch
 *
 */
public class MediaWikiApiErrorHandler {

	public final static String ERROR_EDIT_CONFLICT = "editconflict";
	public final static String ERROR_NO_TOKEN = "notoken";
	public final static String ERROR_INVALID_TOKEN = "badtoken";
	public final static String ERROR_NO_SUCH_ENTITY = "no-such-entity";
	public final static String ERROR_MAXLAG = "maxlag";
	public final static String ERROR_ASSERT_USER_FAILED = "assertuserfailed";

	/**
	 * Creates and throws a suitable {@link MediaWikiApiErrorException} for the
	 * given error code and message.
	 *
	 * @param errorCode
	 *            the error code reported by MediaWiki
	 * @param errorMessage
	 *            the error message reported by MediaWiki, or any other
	 *            meaningful message for the user
	 * @throws MediaWikiApiErrorException
	 *             in all cases, but may throw a subclass for some errors
	 */
	public static void throwMediaWikiApiErrorException(String errorCode,
			String errorMessage) throws MediaWikiApiErrorException {
		switch (errorCode) {
		case ERROR_NO_TOKEN:
		case ERROR_INVALID_TOKEN:
			throw new TokenErrorException(errorCode, errorMessage);
		case ERROR_EDIT_CONFLICT:
			throw new EditConflictErrorException(errorMessage);
		case ERROR_NO_SUCH_ENTITY:
			throw new NoSuchEntityErrorException(errorMessage);
		case ERROR_MAXLAG:
			throw new MaxlagErrorException(errorMessage);
		case ERROR_ASSERT_USER_FAILED:
			throw new AssertUserFailedException(errorMessage);
		default:
			throw new MediaWikiApiErrorException(errorCode, errorMessage);
		}
	}
}
