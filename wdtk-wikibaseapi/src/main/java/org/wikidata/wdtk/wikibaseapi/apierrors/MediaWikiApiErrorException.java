package org.wikidata.wdtk.wikibaseapi.apierrors;

import java.util.Collections;
import java.util.List;

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
	final List<MediaWikiErrorMessage> detailedMessages;

	private static final long serialVersionUID = 7834254856687745000L;

	/**
	 * Creates a new exception for the given error code and message, without
	 * any detailed messages.
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
		this.detailedMessages = Collections.emptyList();
	}
	
	/**
     * Creates a new exception for the given error code and message, together
     * with detailed messages giving more insights on the error.
     *
     * @param errorCode
     *            MediaWiki reported error code
     * @param errorMessage
     *            MediaWiki reported error message, or any other human-readable
     *            message string generated locally
     * @param detailedMessages
     *            list of error messages also returned by MediaWiki (possibly empty)
     */
    public MediaWikiApiErrorException(String errorCode, String errorMessage, List<MediaWikiErrorMessage> detailedMessages) {
        super("[" + errorCode + "] " + errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.detailedMessages = detailedMessages;
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
	
	/**
	 * Returns the list of additional error messages returned by MediaWiki.
	 */
	public List<MediaWikiErrorMessage> getDetailedMessages() {
	    return this.detailedMessages;
	}

}
