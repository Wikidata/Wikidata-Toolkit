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
 * Exception to indicate that we tried to perform an action while our login
 * credentials have expired. See
 * <a href="https://www.mediawiki.org/wiki/API:Assert">MediaWiki documentation</a>.
 * 
 * @author Antonin Delpeuch
 *
 */
public class AssertUserFailedException extends MediaWikiApiErrorException {

	public AssertUserFailedException(String errorMessage) {
		super(MediaWikiApiErrorHandler.ERROR_ASSERT_USER_FAILED, errorMessage);
	}

}
