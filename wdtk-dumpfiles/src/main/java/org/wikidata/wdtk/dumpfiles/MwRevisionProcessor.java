package org.wikidata.wdtk.dumpfiles;

/*
 * #%L
 * Wikidata Toolkit Dump File Handling
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

import java.util.Map;

/**
 * General interface for classes that process revisions of MediaWiki pages.
 *
 * @author Markus Kroetzsch
 *
 */
public interface MwRevisionProcessor {

	/**
	 * Initialises the revision processor for processing revisions. General
	 * information about the configuration of the site for which revisions are
	 * being processed is provided.
	 *
	 * @param siteName
	 *            the name of the site
	 * @param baseUrl
	 *            the base URL of the site
	 * @param namespaces
	 *            map from integer namespace ids to namespace prefixes;
	 *            namespace strings do not include the final ":" used in
	 *            MediaWiki to separate namespace prefixes from article titles,
	 *            and the prefixes use spaces, not underscores as in MediaWiki
	 *            URLs.
	 */
	void startRevisionProcessing(String siteName, String baseUrl,
			Map<Integer, String> namespaces);

	/**
	 * Process the given MediaWiki revision.
	 *
	 * @param mwRevision
	 *            the revision to process
	 */
	void processRevision(MwRevision mwRevision);

	/**
	 * Performs final actions that should be done after all revisions in a batch
	 * of revisions have been processed. This is usually called after a whole
	 * dumpfile is completely processed.
	 * <p>
	 * It is important to understand that this method might be called many times
	 * during one processing run. Its main purpose is to signal the completion
	 * of one file, not of the whole processing. This is used only to manage the
	 * control flow of revision processing (e.g., to be sure that the most
	 * recent revision of a page has certainly been found). This method must not
	 * be used to do things that should happen at the very end of a run, such as
	 * writing a file with results.
	 */
	void finishRevisionProcessing();

}
