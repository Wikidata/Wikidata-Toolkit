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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.wikidata.wdtk.storage.datastructure.impl.BitVectorImpl;
import org.wikidata.wdtk.storage.datastructure.intf.BitVector;

/**
 * This MwRevisionPRocessor distributes revisions to subscribers that register
 * their interest in some type of message (revision). Duplicate revisions are
 * filtered.
 * 
 * The broker also allows subscribers to receive only the most current revision
 * of a page rather than all revisions. To compute this efficiently, the broker
 * assumes that blocks of revisions are processed in inverse chronological
 * order, as it is the case when processing MediaWiki dump files in inverse
 * chronological order. Revisions within a single block of revisions for one
 * page do not need to be ordered in any specific way.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class MwRevisionProcessorBroker implements MwRevisionProcessor {

	/**
	 * Simple data container to store information about the registered
	 * subscribers.
	 * 
	 * @author Markus Kroetzsch
	 * 
	 */
	class RevisionSubscription {
		MwRevisionProcessor mwRevisionProcessor;
		String model;
		boolean onlyCurrentRevisions;
	}

	final List<MwRevisionProcessorBroker.RevisionSubscription> revisionSubscriptions;

	MwRevisionImpl currentRevision;

	BitVector encounteredPages;
	BitVector encounteredRevisions;

	public MwRevisionProcessorBroker() {
		this.revisionSubscriptions = new ArrayList<MwRevisionProcessorBroker.RevisionSubscription>();
		this.currentRevision = new MwRevisionImpl();
		// TODO these initial sizes need to be configurable
		encounteredPages = new BitVectorImpl(20000000);
		encounteredRevisions = new BitVectorImpl(200000000);
	}

	/**
	 * Registers an MwRevisionProcessor, which will henceforth be notified of
	 * all revisions that are encountered in the dump.
	 * <p>
	 * Importantly, the {@link MwRevision} that the registered processors will
	 * receive is owned by this {@link MwRevisionProcessorBroker}. Its data is
	 * valid only during the execution of
	 * {@link MwRevisionProcessor#processRevision(MwRevision, boolean)}, but it
	 * will not be permanent. If the data is to be retained permanently, the
	 * revision processor needs to make its own copy.
	 * 
	 * @param mwRevisionProcessor
	 *            the revision processor to register
	 * @param model
	 *            the content model that the processor is registered for; it
	 *            will only be notified of revisions in that model; if null is
	 *            given, all revisions will be processed whatever their model
	 * @param onlyCurrentRevisions
	 *            if true, then the subscriber is only notified of the most
	 *            current revisions; if false, then it will receive all
	 *            revisions, current or not
	 */
	public void registerMwRevisionProcessor(
			MwRevisionProcessor mwRevisionProcessor, String model,
			boolean onlyCurrentRevisions) {
		MwRevisionProcessorBroker.RevisionSubscription rs = new MwRevisionProcessorBroker.RevisionSubscription();
		rs.mwRevisionProcessor = mwRevisionProcessor;
		rs.model = model;
		rs.onlyCurrentRevisions = onlyCurrentRevisions;
		this.revisionSubscriptions.add(rs);
	}

	@Override
	public void startRevisionProcessing(String siteName, String baseUrl,
			Map<Integer, String> namespaces) {
		for (MwRevisionProcessorBroker.RevisionSubscription rs : this.revisionSubscriptions) {
			rs.mwRevisionProcessor.startRevisionProcessing(siteName, baseUrl,
					namespaces);
		}
	}

	@Override
	public void processRevision(MwRevision mwRevision) {

		boolean revisionIsNew = !this.encounteredRevisions.getBit(mwRevision
				.getRevisionId());
		if (revisionIsNew) {
			this.encounteredRevisions.setBit(mwRevision.getRevisionId(), true);
		} else {
			return;
		}

		boolean pageIsNew = !this.encounteredPages.getBit(mwRevision
				.getPageId());
		if (pageIsNew) {
			this.encounteredPages.setBit(mwRevision.getPageId(), true);
		}

		if (mwRevision.getPageId() != this.currentRevision.getPageId()) {
			notifyMwRevisionProcessors(this.currentRevision, true);
			this.currentRevision = new MwRevisionImpl(mwRevision);
		} else if (pageIsNew
				&& mwRevision.getRevisionId() > this.currentRevision
						.getRevisionId()) {
			this.currentRevision = new MwRevisionImpl(mwRevision);
		}

		notifyMwRevisionProcessors(mwRevision, false);
	}

	/**
	 * Notifies all interested subscribers of the given revision.
	 * 
	 * @param mwRevision
	 *            the given revision
	 * @param isCurrent
	 *            true if this is guaranteed to be the most current revision
	 */
	void notifyMwRevisionProcessors(MwRevision mwRevision, boolean isCurrent) {
		if (mwRevision.getPageId() <= 0) {
			return;
		}
		for (MwRevisionProcessorBroker.RevisionSubscription rs : this.revisionSubscriptions) {
			if (rs.onlyCurrentRevisions == isCurrent
					&& (rs.model == null || mwRevision.getModel().equals(
							rs.model))) {
				rs.mwRevisionProcessor.processRevision(mwRevision);
			}
		}
	}

	/**
	 * Finalises the processing of one dump file (and hence of the current block
	 * of pages). In particular, this means that the most current revision found
	 * up to this point is really the most current one, so that subscribers
	 * should be notified.
	 */
	@Override
	public void finishRevisionProcessing() {
		notifyMwRevisionProcessors(this.currentRevision, true);
		this.currentRevision = new MwRevisionImpl();

		for (MwRevisionProcessorBroker.RevisionSubscription rs : this.revisionSubscriptions) {
			rs.mwRevisionProcessor.finishRevisionProcessing();
		}
	}

}
