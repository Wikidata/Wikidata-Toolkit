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
 * Implementation of {@link MwRevision}. The implementation is meant to be used
 * as a lightweight container that is reusable and thus mutable, but only using
 * package-private access. Even without the re-use (which might be reconsidered)
 * the package-private mutability makes sense during the stateful XML parsing
 * process.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class MwRevisionImpl implements MwRevision {
	// TODO It should be evaluated later on if there is any notable
	// penalty when not reusing this object and creating a 100 million
	// additional objects when parsing a Wikidata dump.

	String prefixedTitle;
	String timeStamp;
	String text;
	String model;
	String format;
	String comment;
	String contributor;
	int contributorId;
	int namespace;
	int pageId;
	long revisionId;

	/**
	 * Constructor.
	 */
	public MwRevisionImpl() {
		resetCurrentPageData();
		resetCurrentRevisionData();
	}

	/**
	 * Copy constructor.
	 */
	public MwRevisionImpl(MwRevision mwRevision) {
		this.prefixedTitle = mwRevision.getPrefixedTitle();
		this.timeStamp = mwRevision.getTimeStamp();
		this.text = mwRevision.getText();
		this.model = mwRevision.getModel();
		this.format = mwRevision.getFormat();
		this.comment = mwRevision.getComment();
		this.contributor = mwRevision.getContributor();
		this.contributorId = mwRevision.getContributorId();
		this.namespace = mwRevision.getNamespace();
		this.pageId = mwRevision.getPageId();
		this.revisionId = mwRevision.getRevisionId();
	}

	@Override
	public String getPrefixedTitle() {
		return this.prefixedTitle;
	}

	@Override
	public String getTitle() {
		// We assume that 0 is always the main namespace, which has no prefix.
		// Without this assumption, the method would need the namespace map.
		if (this.namespace == 0) {
			return this.prefixedTitle;
		} else {
			return this.prefixedTitle
					.substring(this.prefixedTitle.indexOf(':') + 1);
		}
	}

	@Override
	public int getNamespace() {
		return this.namespace;
	}

	@Override
	public int getPageId() {
		return this.pageId;
	}

	@Override
	public long getRevisionId() {
		return this.revisionId;
	}

	@Override
	public String getTimeStamp() {
		return this.timeStamp;
	}

	@Override
	public String getText() {
		return this.text;
	}

	@Override
	public String getModel() {
		return this.model;
	}

	@Override
	public String getFormat() {
		return this.format;
	}

	@Override
	public String getComment() {
		return this.comment;
	}

	@Override
	public String getContributor() {
		return this.contributor;
	}

	@Override
	public int getContributorId() {
		return this.contributorId;
	}

	@Override
	public boolean hasRegisteredContributor() {
		return (this.contributorId >= 0);
	}

	/**
	 * Resets all member fields that hold information about the page that is
	 * currently being processed.
	 */
	void resetCurrentPageData() {
		this.prefixedTitle = null;
		this.pageId = -1; // impossible as an id in MediaWiki
		this.namespace = 0; // possible value, but better than undefined
	}

	/**
	 * Resets all member fields that hold information about the revision that is
	 * currently being processed.
	 */
	void resetCurrentRevisionData() {
		this.revisionId = -1; // impossible as an id in MediaWiki
		this.text = null;
		this.comment = null;
		this.format = null;
		this.timeStamp = null;
		this.model = null;
	}

	@Override
	public String toString() {
		return "Revision " + this.revisionId + " of page " + this.prefixedTitle
				+ " (ns " + this.namespace + ", id " + this.pageId
				+ "). Created at " + this.timeStamp + " by " + this.contributor
				+ " (" + this.contributorId + ") with comment \""
				+ this.comment + "\". Model " + this.model + " (" + this.format
				+ "). Text length: " + this.text.length();
	}

}
