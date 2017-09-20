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

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class processes MediaWiki dumpfiles that contain lists of page revisions
 * in the specific XML format used by MediaWiki for exporting pages. It extracts
 * all revisions and forwards them to any registered revision processor. The
 * class also keeps track of whether or not a certain article respectively
 * revision has already been encountered. Therefore, no revision is processed
 * twice and the registered revision processors can be informed whether the
 * revision is the first of the given article or not. The first revision of an
 * article that is encountered in a MediaWiki dump file is usually the most
 * recent one. If multiple dump files are processed in reverse chronological
 * order, the first revision that is encountered is also the most recent one
 * overall.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class MwRevisionDumpFileProcessor implements MwDumpFileProcessor {

	static final String E_MEDIAWIKI = "mediawiki";
	static final String E_SITEINFO = "siteinfo";
	static final String E_SITENAME = "sitename";
	static final String E_BASEURL = "base";
	static final String E_NAMESPACE = "namespace";
	static final String A_NSKEY = "key";

	static final String E_PAGE = "page";
	static final String E_PAGE_TITLE = "title";
	static final String E_PAGE_ID = "id";
	static final String E_PAGE_NAMESPACE = "ns";
	static final String E_PAGE_REVISION = "revision";
	static final String E_PAGE_REDIRECT = "redirect";

	static final String E_REV_ID = "id";
	static final String E_REV_PARENT_ID = "parentid";
	static final String E_REV_TIMESTAMP = "timestamp";
	static final String E_REV_COMMENT = "comment";
	static final String E_REV_MODEL = "model";
	static final String E_REV_TEXT = "text";
	static final String E_REV_CONTRIBUTOR = "contributor";
	static final String E_REV_FORMAT = "format";
	static final String E_REV_SHA1 = "sha1";
	static final String E_REV_MINOR = "minor";

	static final String E_CONTRIBUTOR_NAME = "username";
	static final String E_CONTRIBUTOR_ID = "id";
	static final String E_CONTRIBUTOR_IP = "ip";

	static final Logger logger = LoggerFactory
			.getLogger(MwRevisionDumpFileProcessor.class);

	final XMLInputFactory xmlFactory;
	XMLStreamReader xmlReader;

	/**
	 * Map from integer namespace ids to namespace prefixes. Namespace strings
	 * do not include the final ":" used in MediaWiki to separate namespace
	 * prefixes from article titles. Moreover, the prefixes use spaces, not
	 * underscores as in MediaWiki URLs.
	 */
	final Map<Integer, String> namespaces;
	/**
	 * Name of the site as set in the dump file.
	 */
	String sitename = "";
	/**
	 * Base URL of the site as set in the dump file.
	 */
	String baseUrl = "";

	/**
	 * Object used to store data about the current revision.
	 */
	final MwRevisionImpl mwRevision;
	/**
	 * Object used to report all revisions to.
	 */
	final MwRevisionProcessor mwRevisionProcessor;

	/**
	 * Constructor.
	 * 
	 * @param mwRevisionProcessor
	 *            the revision processor to which all revisions will be reported
	 */
	public MwRevisionDumpFileProcessor(MwRevisionProcessor mwRevisionProcessor) {
		this.xmlFactory = XMLInputFactory.newInstance();
		this.namespaces = new HashMap<Integer, String>();
		this.mwRevision = new MwRevisionImpl();
		this.mwRevisionProcessor = mwRevisionProcessor;
		reset();
	}

	/**
	 * Resets the internal state of the object. All information gathered from
	 * previously processed dumps and all related statistics will be forgotten.
	 * If this method is not called, then consecutive invocations of
	 * {@link #processDumpFileContents(InputStream, MwDumpFile)} will continue
	 * to add to the internal state. This is useful for processing dumps that
	 * are split into several parts.
	 * <p>
	 * This will not unregister any MwRevisionProcessors.
	 */
	public void reset() {
		this.namespaces.clear();
	}

	@Override
	public void processDumpFileContents(InputStream inputStream,
			MwDumpFile dumpFile) {

		logger.info("Processing revision dump file " + dumpFile.toString());

		this.namespaces.clear();
		this.sitename = "";
		this.baseUrl = "";

		this.xmlReader = null;

		try {
			this.xmlReader = this.xmlFactory.createXMLStreamReader(inputStream);
			processXmlMediawiki();
		} catch (XMLStreamException | MwDumpFormatException e) {
			MwRevisionDumpFileProcessor.logger.error(e.toString());
		} finally { // unfortunately, xmlReader does not implement AutoClosable
			if (this.xmlReader != null) {
				try {
					this.xmlReader.close();
				} catch (XMLStreamException e) {
					throw new RuntimeException(
							"Problem closing XML Reader. This hides an earlier exception.",
							e);
				}
			}
		}

		this.mwRevisionProcessor.finishRevisionProcessing();
	}

	/**
	 * Processes current XML starting from a &lt;mediawiki&gt; start tag up to
	 * the corresponding end tag. This method uses the current state of
	 * {@link #xmlReader} and stores its results in according member fields.
	 * When the method has finished, {@link #xmlReader} will be at the next
	 * element after the closing tag of this block.
	 * 
	 * @throws XMLStreamException
	 *             if there was a problem reading the XML or if the XML is
	 *             malformed
	 * @throws MwDumpFormatException
	 *             if the contents of the XML file did not match our
	 *             expectations of a MediaWiki XML dump
	 */
	void processXmlMediawiki() throws XMLStreamException, MwDumpFormatException {

		while (this.xmlReader.hasNext()) {
			switch (this.xmlReader.getEventType()) {

			case XMLStreamConstants.START_ELEMENT:
				switch (this.xmlReader.getLocalName()) {
				case MwRevisionDumpFileProcessor.E_MEDIAWIKI:
					break;
				case MwRevisionDumpFileProcessor.E_SITEINFO:
					processXmlSiteinfo();
					this.mwRevisionProcessor.startRevisionProcessing(
							this.sitename, this.baseUrl, this.namespaces);
					break;
				case MwRevisionDumpFileProcessor.E_PAGE:
					tryProcessXmlPage();
					break;
				}
				break;

			case XMLStreamConstants.END_ELEMENT:
				if (!"mediawiki".equals(this.xmlReader.getLocalName())) {
					throw new MwDumpFormatException("Unexpected end element </"
							+ this.xmlReader.getLocalName() + ">.");
				}
				break;

			}

			this.xmlReader.next();
		}
	}

	/**
	 * Processes current XML starting from a &lt;siteinfo&gt; start tag up to
	 * the corresponding end tag. This method uses the current state of
	 * {@link #xmlReader} and stores its results in according member fields.
	 * When the method has finished, {@link #xmlReader} will be at the next
	 * element after the closing tag of this block.
	 * 
	 * @throws XMLStreamException
	 *             if there was a problem reading the XML or if the XML is
	 *             malformed
	 * @throws MwDumpFormatException
	 *             if the contents of the XML file did not match our
	 *             expectations of a MediaWiki XML dump
	 */
	void processXmlSiteinfo() throws XMLStreamException {

		this.xmlReader.next(); // skip current start tag
		while (this.xmlReader.hasNext()) {
			switch (this.xmlReader.getEventType()) {

			case XMLStreamConstants.START_ELEMENT:
				switch (xmlReader.getLocalName()) {
				case MwRevisionDumpFileProcessor.E_SITENAME:
					this.sitename = this.xmlReader.getElementText();
					break;
				case MwRevisionDumpFileProcessor.E_NAMESPACE:
					Integer namespaceKey = new Integer(
							this.xmlReader.getAttributeValue(null,
									MwRevisionDumpFileProcessor.A_NSKEY));
					this.namespaces.put(namespaceKey,
							this.xmlReader.getElementText());
					break;
				case MwRevisionDumpFileProcessor.E_BASEURL:
					this.baseUrl = this.xmlReader.getElementText();
					break;
				}
				break;

			case XMLStreamConstants.END_ELEMENT:
				if (MwRevisionDumpFileProcessor.E_SITEINFO
						.equals(this.xmlReader.getLocalName())) {
					return;
				}
				break;

			}

			this.xmlReader.next();
		}
	}

	/**
	 * Tries to processes current XML starting from a &lt;page&gt; start tag up
	 * to the corresponding end tag using {@link #processXmlPage()}. If this
	 * fails for some reason, it tries to recover to read all remaining page
	 * blocks nonetheless.
	 * 
	 * @throws XMLStreamException
	 *             if there was a problem reading the XML
	 */
	void tryProcessXmlPage() throws XMLStreamException {
		try {
			processXmlPage();
		} catch (MwDumpFormatException e) {
			MwRevisionDumpFileProcessor.logger
					.error("Error when trying to process revision block for page \""
							+ this.mwRevision.getPrefixedTitle()
							+ "\" (namespace "
							+ this.mwRevision.getNamespace()
							+ ", id "
							+ this.mwRevision.getPageId()
							+ "): "
							+ e.toString());

			MwRevisionDumpFileProcessor.logger.info("Trying to recover ...");
			while (this.xmlReader.hasNext()) {
				this.xmlReader.next();
				if (this.xmlReader.getEventType() == XMLStreamConstants.END_ELEMENT
						&& this.xmlReader.getLocalName().equals(MwRevisionDumpFileProcessor.E_PAGE)) {
					MwRevisionDumpFileProcessor.logger
							.info("... recovery successful. Continuing processing.");
					return;
				}
			}
			MwRevisionDumpFileProcessor.logger
					.error("Recovery failed. Could not process remaining XML.");
		}
	}

	/**
	 * Processes current XML starting from a &lt;page&gt; start tag up to the
	 * corresponding end tag. This method uses the current state of
	 * {@link #xmlReader} and stores its results in according member fields.
	 * When the method has finished, {@link #xmlReader} will be at the next
	 * element after the closing tag of this block.
	 * 
	 * @throws XMLStreamException
	 *             if there was a problem reading the XML or if the XML is
	 *             malformed
	 * @throws MwDumpFormatException
	 *             if the contents of the XML file did not match our
	 *             expectations of a MediaWiki XML dump
	 */
	void processXmlPage() throws XMLStreamException, MwDumpFormatException {

		this.mwRevision.resetCurrentPageData();

		this.xmlReader.next(); // skip current start tag
		while (this.xmlReader.hasNext()) {
			switch (this.xmlReader.getEventType()) {

			case XMLStreamConstants.START_ELEMENT:
				switch (this.xmlReader.getLocalName()) {
				case MwRevisionDumpFileProcessor.E_PAGE_TITLE:
					this.mwRevision.prefixedTitle = this.xmlReader.getElementText();
					break;
				case MwRevisionDumpFileProcessor.E_PAGE_NAMESPACE:
					this.mwRevision.namespace = new Integer(
							this.xmlReader.getElementText());
					break;
				case MwRevisionDumpFileProcessor.E_PAGE_ID:
					this.mwRevision.pageId = new Integer(
							this.xmlReader.getElementText());
					break;
				case MwRevisionDumpFileProcessor.E_PAGE_REVISION:
					processXmlRevision();
					break;
				case MwRevisionDumpFileProcessor.E_PAGE_REDIRECT:
					break;
				default:
					throw new MwDumpFormatException("Unexpected element \""
							+ this.xmlReader.getLocalName() + "\" in page.");
				}
				break;

			case XMLStreamConstants.END_ELEMENT:
				if (MwRevisionDumpFileProcessor.E_PAGE.equals(xmlReader
						.getLocalName())) {
					return;
				}
				break;
			}

			this.xmlReader.next();
		}
	}

	/**
	 * Processes current XML starting from a &lt;revision&gt; start tag up to
	 * the corresponding end tag. This method uses the current state of
	 * {@link #xmlReader} and stores its results in according member fields.
	 * When the method has finished, {@link #xmlReader} will be at the next
	 * element after the closing tag of this block.
	 * 
	 * @throws XMLStreamException
	 *             if there was a problem reading the XML or if the XML is
	 *             malformed
	 * @throws MwDumpFormatException
	 *             if the contents of the XML file did not match our
	 *             expectations of a MediaWiki XML dump
	 */
	void processXmlRevision() throws XMLStreamException, MwDumpFormatException {

		this.mwRevision.resetCurrentRevisionData();

		this.xmlReader.next(); // skip current start tag
		while (this.xmlReader.hasNext()) {
			switch (this.xmlReader.getEventType()) {

			case XMLStreamConstants.START_ELEMENT:
				switch (this.xmlReader.getLocalName()) {
				case MwRevisionDumpFileProcessor.E_REV_COMMENT:
					this.mwRevision.comment = this.xmlReader.getElementText();
					break;
				case MwRevisionDumpFileProcessor.E_REV_TEXT:
					this.mwRevision.text = this.xmlReader.getElementText();
					break;
				case MwRevisionDumpFileProcessor.E_REV_TIMESTAMP:
					this.mwRevision.timeStamp = this.xmlReader.getElementText();
					break;
				case MwRevisionDumpFileProcessor.E_REV_FORMAT:
					this.mwRevision.format = this.xmlReader.getElementText();
					break;
				case MwRevisionDumpFileProcessor.E_REV_MODEL:
					this.mwRevision.model = this.xmlReader.getElementText();
					break;
				case MwRevisionDumpFileProcessor.E_REV_CONTRIBUTOR:
					processXmlContributor();
					break;
				case MwRevisionDumpFileProcessor.E_REV_ID:
					this.mwRevision.revisionId = new Long(
							this.xmlReader.getElementText());
					break;
				case MwRevisionDumpFileProcessor.E_REV_PARENT_ID:
				case MwRevisionDumpFileProcessor.E_REV_SHA1:
				case MwRevisionDumpFileProcessor.E_REV_MINOR:
					break;
				default:
					throw new MwDumpFormatException("Unexpected element \""
							+ this.xmlReader.getLocalName() + "\" in revision.");
				}

				break;

			case XMLStreamConstants.END_ELEMENT:
				if (MwRevisionDumpFileProcessor.E_PAGE_REVISION
						.equals(this.xmlReader.getLocalName())) {
					this.mwRevisionProcessor.processRevision(this.mwRevision);
					return;
				}
				break;
			}

			this.xmlReader.next();
		}
	}

	/**
	 * Processes current XML starting from a &lt;contributor&gt; start tag up to
	 * the corresponding end tag. This method uses the current state of
	 * {@link #xmlReader} and stores its results in according member fields.
	 * When the method has finished, {@link #xmlReader} will be at the next
	 * element after the closing tag of this block.
	 * 
	 * @throws XMLStreamException
	 *             if there was a problem reading the XML or if the XML is
	 *             malformed
	 * @throws MwDumpFormatException
	 *             if the contents of the XML file did not match our
	 *             expectations of a MediaWiki XML dump
	 */
	void processXmlContributor() throws XMLStreamException,
			MwDumpFormatException {

		this.xmlReader.next(); // skip current start tag
		while (this.xmlReader.hasNext()) {
			switch (this.xmlReader.getEventType()) {

			case XMLStreamConstants.START_ELEMENT:
				switch (this.xmlReader.getLocalName()) {
				case MwRevisionDumpFileProcessor.E_CONTRIBUTOR_NAME:
					this.mwRevision.contributor = this.xmlReader
							.getElementText();
					break;
				case MwRevisionDumpFileProcessor.E_CONTRIBUTOR_ID:
					this.mwRevision.contributorId = new Integer(
							this.xmlReader.getElementText());
					break;
				case MwRevisionDumpFileProcessor.E_CONTRIBUTOR_IP:
					this.mwRevision.contributor = this.xmlReader
							.getElementText();
					this.mwRevision.contributorId = -1;
					break;
				default:
					throw new MwDumpFormatException("Unexpected element \""
							+ this.xmlReader.getLocalName()
							+ "\" in contributor.");
				}

				break;

			case XMLStreamConstants.END_ELEMENT:
				if (MwRevisionDumpFileProcessor.E_REV_CONTRIBUTOR
						.equals(this.xmlReader.getLocalName())) {
					return;
				}
				break;
			}

			this.xmlReader.next();
		}
	}

}
