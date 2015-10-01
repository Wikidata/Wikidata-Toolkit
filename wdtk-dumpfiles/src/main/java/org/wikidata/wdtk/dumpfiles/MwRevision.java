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

/**
 * Representation of one revision of a page in MediaWiki.
 * 
 * @author Markus Kroetzsch
 * 
 */
public interface MwRevision {

	/**
	 * The model used for MediaWiki revisions in traditional Wikitext. Revisions
	 * with this format should always use "text/x-wiki" as their format.
	 */
	String MODEL_WIKITEXT = "wikitext";
	/**
	 * The model used for MediaWiki revisions representing Wikibase items.
	 * Revisions with this format should always use "application/json" as their
	 * format.
	 */
	String MODEL_WIKIBASE_ITEM = "wikibase-item";
	/**
	 * The model used for MediaWiki revisions representing Wikibase properties.
	 * Revisions with this format should always use "application/json" as their
	 * format.
	 */
	String MODEL_WIKIBASE_PROPERTY = "wikibase-property";

	/**
	 * Returns the title string of the revised page, including namespace
	 * prefixes and subpages, if any. The string is formatted as it would be on
	 * an HTML page and not as in the URL used by MediaWiki for the page. For
	 * example, spaces are represented as spaces and not as underscores. For
	 * example
	 * <p>
	 * On a single MediaWiki site, the prefixed page title is a key for a page
	 * at any given moment. However, users may change the title and namespace by
	 * moving pages. The page id provides a better clue to identify pages across
	 * history.
	 * 
	 * @return title string
	 */
	String getPrefixedTitle();

	/**
	 * Returns the title string of the revised page without any namespace
	 * prefixes. The string is formatted as it would be on an HTML page and not
	 * as in the URL used by MediaWiki for the page. For example, spaces are
	 * represented as spaces and not as underscores. For example
	 * <p>
	 * On a single MediaWiki site, the combination of page title and page
	 * namespace is a key for a page at any given moment. However, users may
	 * change the title and namespace by moving pages. The page id provides a
	 * better clue to identify pages across history.
	 * 
	 * @return title string
	 */
	String getTitle();

	/**
	 * Returns the id of the MediaWiki namespace of the revised page. The
	 * meaning of this id depends on the configuration of the site that the page
	 * is from. Usually, 0 is the main namespace. Even ids usually refer to
	 * normal article pages while their odd successors represent the
	 * corresponding talk namespace.
	 * <p>
	 * On a single MediaWiki site, the combination of page title and page
	 * namespace is a key for a page at any given moment. However, users may
	 * change the title and namespace by moving pages. The page id provides a
	 * better clue to identify pages across history.
	 * 
	 * @return integer namespace id
	 */
	int getNamespace();

	/**
	 * Returns the numeric page id of the revised page. For any given MediaWiki
	 * site, pages are uniquely identified by their page id. MediaWiki will try
	 * to preserve the page id even across title changes (moves).
	 * 
	 * @return integer page id
	 */
	int getPageId();

	/**
	 * Returns the numeric id of the current revision. For any given MediaWiki
	 * site, revisions are uniquely identified by their revision id. In
	 * particular, two distinct revisions can never have the same id, even if
	 * they belong to different pages.
	 * 
	 * @return long revision id
	 */
	long getRevisionId();

	/**
	 * Returns the time stamp at which the current revision was made. The time
	 * stamp is a string that is formatted according to ISO 8601, such as
	 * "2014-02-19T23:34:16Z".
	 * 
	 * @return time stamp string
	 */
	String getTimeStamp();

	/**
	 * Returns the text content of the current revision. Traditionally, this is
	 * a wiki text that is edited by users. More recently, however, other
	 * formats, such as JSON, have been introduced by extensions like Wikibase.
	 * The format of the text is specified by {@link #getFormat()}. To interpret
	 * it properly, one should also know the content model, obtained from
	 * {@link #getModel()}.
	 * 
	 * @return text content of the revision
	 */
	String getText();

	/**
	 * Returns the content model of the revision. This specifies how the text
	 * content should be interpreted. Content models are usually configured for
	 * namespaces and thus remain rather stable across the history of a page.
	 * However, a page could in principle change its content model over time and
	 * every revision therefore specifies its own content model. All known
	 * models require a single format, obtained from {@link #getFormat()}.
	 * 
	 * @return content model as a string
	 */
	String getModel();

	/**
	 * Returns the format of the revision text. This string should be formatted
	 * as a MIME media type. Typical examples are "application/json" (JSON) and
	 * "text/x-wiki" (MediaWiki wikitext). To interpret the meaning of this
	 * format, one should also consider the content model obtained by
	 * {@link #getModel()}. Like the content model, the format might change
	 * between revisions of a page, but this is very rare in practice.
	 * 
	 * @return MIME type for revision text
	 */
	String getFormat();

	/**
	 * Returns the comment string that was used for making the edit that led to
	 * this revision.
	 * 
	 * @return comment string
	 */
	String getComment();

	/**
	 * Returns the name for the contributor that made the edit that led to this
	 * revision. This might be a user name or an IP address. This can be checked
	 * using {@link #hasRegisteredContributor()}.
	 * 
	 * @return contributor name or IP address
	 */
	String getContributor();

	/**
	 * Returns the user id of the contributor who made the edit that led to this
	 * revision, or -1 if the edit was not made by a registered user.
	 * 
	 * @return user id or -1 for anonymous users
	 */
	int getContributorId();

	/**
	 * Returns true if the contributor who made the edit that led to this
	 * revision was logged in with a user account. False is returned if the
	 * contributor was not logged in (in which case there is only an IP
	 * address).
	 * 
	 * @return true if the contributor was looged in
	 */
	boolean hasRegisteredContributor();

}
