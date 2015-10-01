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
 * Exception to indicate a MediaWiki API error caused by missing or invalid
 * token.
 *
 * @author Markus Kroetzsch
 *
 */
public class TokenErrorException extends MediaWikiApiErrorException {

	private static final long serialVersionUID = 3603929976083601076L;

	/**
	 * Creates a new exception.
	 *
	 * @param errorCode
	 *            the error code reported by MediaWiki
	 * @param errorMessage
	 *            the error message reported by MediaWiki, or any other
	 *            meaningful message for the user
	 */
	public TokenErrorException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
