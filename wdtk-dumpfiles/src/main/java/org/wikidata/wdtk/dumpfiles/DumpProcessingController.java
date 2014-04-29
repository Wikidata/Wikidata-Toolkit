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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.Sites;
import org.wikidata.wdtk.util.DirectoryManager;
import org.wikidata.wdtk.util.DirectoryManagerImpl;
import org.wikidata.wdtk.util.WebResourceFetcher;
import org.wikidata.wdtk.util.WebResourceFetcherImpl;

/**
 * A class for controlling the processing of dump files through a unified
 * interface. The settings of the controller specify how dump files should be
 * fetched and processed.
 * <p>
 * The methods for registering listeners to process dump files that contain
 * revisions are
 * {@link #registerMwRevisionProcessor(MwRevisionProcessor, String, boolean)}
 * and
 * {@link #registerEntityDocumentProcessor(EntityDocumentProcessor, String, boolean)}.
 * <p>
 * The methods for processing selected dump files (and downloading them first,
 * finding out which ones are relevant) are
 * {@link #processAllRecentRevisionDumps()},
 * {@link #processMostRecentMainDump()}, and
 * {@link #processMostRecentMainDump()}.
 * <p>
 * To extract the most recent sitelinks information, the method
 * {@link #getSitesInformation()} can be used. To get information about the
 * revision dump files that the main methods will process, one can use
 * {@link #getWmfDumpFileManager()} to get access to the underlying dump file
 * manager, which can be used to get access to dump file data.
 * <p>
 * The controller will also catch exceptions that may occur when trying to
 * download and read dump files. They will be turned into logged errors.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class DumpProcessingController {

	static final Logger logger = LoggerFactory
			.getLogger(DumpProcessingController.class);

	/**
	 * The name of the project whose dumps are processed here.
	 */
	final String projectName;

	/**
	 * Broker object to distribute revisions to several listeners.
	 */
	final MwRevisionProcessorBroker mwRevisionProcessorBroker = new MwRevisionProcessorBroker();

	/**
	 * Should only current dumps be considered? This is changed automatically if
	 * some registered listener is interested in non-current dumps.
	 */
	boolean preferCurrent = true;

	/**
	 * The object used to access the Web or null if Web access is disabled. This
	 * is stored permanently here so that tests in this package can set the
	 * value to a mock object. This class should not need to be tested outside
	 * this package.
	 */
	WebResourceFetcher webResourceFetcher;

	/**
	 * The object used to access the download directory where dump files are
	 * stored. This is stored permanently here so that tests in this package can
	 * set the value to a mock object. This class should not need to be tested
	 * outside this package.
	 */
	DirectoryManager downloadDirectoryManager;

	/**
	 * Creates a new DumpFileProcessingController for the project of the given
	 * name. By default, the dump file directory will be assumed to be in the
	 * current directory and the object will access the Web to fetch the most
	 * recent files.
	 * 
	 * @param projectName
	 *            Wikimedia projectname, e.g., "wikidatawiki" or "enwiki"
	 */
	public DumpProcessingController(String projectName) {
		this.projectName = projectName;

		try {
			setDownloadDirectory(System.getProperty("user.dir"));
		} catch (IOException e) {
			// The user.dir should always exist, so this is highly unusual.
			throw new RuntimeException(e.toString(), e);
		}

		setOfflineMode(false);
	}

	/**
	 * Sets the directory where dumpfiles are stored locally. If it does not
	 * exist yet, this directory will be created. Dumpfiles will later be stored
	 * in a subdirectory "dumpfiles", but this will only be created when needed.
	 * 
	 * @param downloadDirectory
	 *            the download base directory
	 * @throws IOException
	 *             if the existence of the directory could not be checked or if
	 *             it did not exists and could not be created either
	 */
	public void setDownloadDirectory(String downloadDirectory)
			throws IOException {
		this.downloadDirectoryManager = new DirectoryManagerImpl(
				downloadDirectory);
	}

	/**
	 * Disables or enables Web access.
	 * 
	 * @param offlineModeEnabled
	 *            if true, all Web access is disabled and only local files will
	 *            be processed
	 */
	public void setOfflineMode(boolean offlineModeEnabled) {
		if (offlineModeEnabled) {
			this.webResourceFetcher = null;
		} else {
			this.webResourceFetcher = new WebResourceFetcherImpl();
		}
	}

	/**
	 * Registers an MwRevisionProcessor, which will henceforth be notified of
	 * all revisions that are encountered in the dump.
	 * <p>
	 * Importantly, the {@link MwRevision} that the registered processors will
	 * receive is is valid only during the execution of
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
		this.preferCurrent = this.preferCurrent && onlyCurrentRevisions;
		this.mwRevisionProcessorBroker.registerMwRevisionProcessor(
				mwRevisionProcessor, model, onlyCurrentRevisions);
	}

	/**
	 * Registers an EntityDocumentProcessor, which will henceforth be notified
	 * of all entity documents that are encountered in the dump.
	 * 
	 * @param entityDocumentProcessor
	 *            the entity document processor to register
	 * @param model
	 *            the content model that the processor is registered for; it
	 *            will only be notified of revisions in that model; if null is
	 *            given, all revisions will be processed whatever their model
	 * @param onlyCurrentRevisions
	 *            if true, then the subscriber is only notified of the most
	 *            current revisions; if false, then it will receive all
	 *            revisions, current or not
	 */
	public void registerEntityDocumentProcessor(
			EntityDocumentProcessor entityDocumentProcessor, String model,
			boolean onlyCurrentRevisions) {
		registerMwRevisionProcessor(new WikibaseRevisionProcessor(
				entityDocumentProcessor), model, onlyCurrentRevisions);
	}

	/**
	 * Processes the most recent dump of the sites table to extract information
	 * about registered sites.
	 * 
	 * @return a Sites objects that contains the extracted information
	 * @throws IOException
	 *             if there was a problem accessing the sites table dump or the
	 *             dump download directory
	 */
	public Sites getSitesInformation() throws IOException {

		WmfDumpFileManager wmfDumpFileManager = getWmfDumpFileManager();

		// Get a handle for the most recent dump file of the sites table:
		MwDumpFile sitesTableDump = wmfDumpFileManager
				.findMostRecentDump(DumpContentType.SITES);

		// Create a suitable processor for such dumps and process the file:
		MwSitesDumpFileProcessor sitesDumpFileProcessor = new MwSitesDumpFileProcessor();
		sitesDumpFileProcessor.processDumpFileContents(
				sitesTableDump.getDumpFileStream(), sitesTableDump);

		return sitesDumpFileProcessor.getSites();
	}

	/**
	 * Processes all relevant page revision dumps in order. The registered
	 * listeners (MwRevisionProcessor or EntityDocumentProcessor objects) will
	 * be notified of all data they registered for.
	 * 
	 * @see DumpProcessingController#processMostRecentDailyDump()
	 * @see DumpProcessingController#processMostRecentMainDump()
	 * @see DumpProcessingController#processMostRecentDump(DumpContentType,
	 *      MwDumpFileProcessor)
	 */
	public void processAllRecentRevisionDumps() {
		WmfDumpFileManager wmfDumpFileManager;
		try {
			wmfDumpFileManager = getWmfDumpFileManager();
		} catch (IOException e) {
			logger.error("Could not create dump file manager: " + e.toString());
			return;
		}

		MwDumpFileProcessor dumpFileProcessor = getRevisionDumpFileProcessor();

		for (MwDumpFile dumpFile : wmfDumpFileManager
				.findAllRelevantRevisionDumps(this.preferCurrent)) {
			processDumpFile(dumpFile, dumpFileProcessor);
		}
	}

	/**
	 * Processes the most recent incremental (daily) dump that is available.
	 * This is mainly useful for testing, since these dumps are much smaller
	 * than the main dumps. The registered listeners (MwRevisionProcessor or
	 * EntityDocumentProcessor objects) will be notified of all data they
	 * registered for.
	 * 
	 * @see DumpProcessingController#processMostRecentMainDump()
	 * @see DumpProcessingController#processAllRecentRevisionDumps()
	 * @see DumpProcessingController#processMostRecentDump(DumpContentType,
	 *      MwDumpFileProcessor)
	 */
	public void processMostRecentDailyDump() {
		processMostRecentDump(DumpContentType.DAILY,
				getRevisionDumpFileProcessor());
	}

	/**
	 * Processes the most recent main (complete) dump that is available. The
	 * registered listeners (MwRevisionProcessor or EntityDocumentProcessor
	 * objects) will be notified of all data they registered for.
	 * <p>
	 * This method is useful to obtain reliable results given that single
	 * incremental dump files are sometimes missing, even if earlier and later
	 * incremental dumps are available. In such a case, processing all recent
	 * dumps will miss some (random) revisions, thus reflecting a state that the
	 * wiki has never really been in. If this is considered a problem, then it
	 * is better to use this method instead.
	 * 
	 * @see DumpProcessingController#processMostRecentDailyDump()
	 * @see DumpProcessingController#processAllRecentRevisionDumps()
	 * @see DumpProcessingController#processMostRecentDump(DumpContentType,
	 *      MwDumpFileProcessor)
	 */
	public void processMostRecentMainDump() {
		DumpContentType dumpContentType;
		if (this.preferCurrent) {
			dumpContentType = DumpContentType.CURRENT;
		} else {
			dumpContentType = DumpContentType.FULL;
		}

		processMostRecentDump(dumpContentType, getRevisionDumpFileProcessor());
	}

	/**
	 * Processes the most recent dump of the given type using the given dump
	 * processor.
	 * 
	 * @see DumpProcessingController#processMostRecentMainDump()
	 * @see DumpProcessingController#processMostRecentDailyDump()
	 * @see DumpProcessingController#processAllRecentRevisionDumps()
	 * 
	 * @param dumpContentType
	 *            the type of dump to process
	 * @param dumpFileProcessor
	 *            the processor to use
	 */
	public void processMostRecentDump(DumpContentType dumpContentType,
			MwDumpFileProcessor dumpFileProcessor) {
		WmfDumpFileManager wmfDumpFileManager;
		try {
			wmfDumpFileManager = getWmfDumpFileManager();
		} catch (IOException e) {
			logger.error("Could not create dump file manager: " + e.toString());
			return;
		}

		MwDumpFile dumpFile = wmfDumpFileManager
				.findMostRecentDump(dumpContentType);
		processDumpFile(dumpFile, dumpFileProcessor);
	}

	/**
	 * Processes one dump file with the given dump file processor, handling
	 * exceptions appropriately.
	 * 
	 * @param dumpFile
	 *            the dump file to process
	 * @param dumpFileProcessor
	 *            the dump file processor to use
	 */
	void processDumpFile(MwDumpFile dumpFile,
			MwDumpFileProcessor dumpFileProcessor) {
		try (InputStream inputStream = dumpFile.getDumpFileStream()) {
			dumpFileProcessor.processDumpFileContents(inputStream, dumpFile);
		} catch (FileAlreadyExistsException e) {
			logger.error("Dump file "
					+ dumpFile.toString()
					+ " could not be processed since file "
					+ e.getFile()
					+ " already exists. Try deleting the file or dumpfile directory to attempt a new download.");
		} catch (IOException e) {
			logger.error("Dump file " + dumpFile.toString()
					+ " could not be processed: " + e.toString());
		}
	}

	/**
	 * Returns a WmfDumpFileManager based on the current settings. This object
	 * can be used to get direct access to dump files, e.g., to gather more
	 * information. Most basic operations can also be performed using the
	 * interface of the {@link DumpProcessingController} and this is often
	 * preferable.
	 * <p>
	 * This dump file manager will not be updated if the settings change later.
	 * 
	 * @return a WmfDumpFileManager for the current settings
	 * @throws IOException
	 *             if there was a problem, usually owing to some problem when
	 *             accessing the dumpfile directory
	 */
	public WmfDumpFileManager getWmfDumpFileManager() throws IOException {
		return new WmfDumpFileManager(this.projectName,
				this.downloadDirectoryManager, this.webResourceFetcher);
	}

	/**
	 * Return the main dump file processor that should be used to process
	 * revisions.
	 * 
	 * @return the main MwDumpFileProcessor for revisions
	 */
	MwDumpFileProcessor getRevisionDumpFileProcessor() {
		return new MwRevisionDumpFileProcessor(this.mwRevisionProcessorBroker);
	}

}
