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
 * Exception for reporting general MediaWiki API errors.
 *
 * @author Markus Kroetzsch
 *
 */
public class MediaWikiApiErrorException extends Exception {

	final String errorCode;
	final String errorMessage;

	private static final long serialVersionUID = 7834254856687745000L;

	/**
	 * Creates a new exception for the given error code and message.
	 *
	 * @param errorCode
	 *            MediaWiki reported error code
	 * @param errorMessage
	 *            MediaWiki reported error message, or any other human-readable
	 *            message string generated locally
	 */
	public MediaWikiApiErrorException(String errorCode, String errorMessage) {
		super("[" + errorCode + "] " + errorMessage);
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	/**
	 * Returns the MediaWiki code of the error that has causes this exception.
	 *
	 * @return error code
	 */
	public String getErrorCode() {
		return this.errorCode;
	}

	/**
	 * Returns the MediaWiki message string for the error that has causes this
	 * exception. Note that this is only part of the exception message obtained
	 * by {@link #getMessage()}.
	 *
	 * @return error message
	 */
	public String getErrorMessage() {
		return this.errorMessage;
	}

}
