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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.DatamodelConverter;
import org.wikidata.wdtk.datamodel.interfaces.DocumentDataFilter;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessorBroker;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessorFilter;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Sites;
import org.wikidata.wdtk.dumpfiles.wmf.WmfDumpFileManager;
import org.wikidata.wdtk.util.DirectoryManager;
import org.wikidata.wdtk.util.DirectoryManagerFactory;
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
 * For processing the content of wiki pages, there are two modes of operation:
 * revision-based and entity-document-based. The former is used when processing
 * dump files that contain revisions. These hold detailed information about each
 * revision (revision number, author, time, etc.) that could be used by revision
 * processors.
 * <p>
 * The entity-document-based operation is used when processing simplified dumps
 * that contain only the content of the current (entity) pages of a wiki. In
 * this case, no additional information is available and only the entity
 * document processors are called (since we have no revisions). Both modes use
 * the same entity document processors. In revision-based runs, it is possible
 * to restrict some entity document processors to certain content models only
 * (e.g., to process only properties). In entity-document-based runs, this is
 * ignored and all entity document processors get to see all the data.
 * <p>
 * The methods for revision-based processing of selected dump files (and
 * downloading them first, finding out which ones are relevant) are
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
	 * Helper value class to store the registration settings of one listener.
	 *
	 * @author Markus Kroetzsch
	 *
	 */
	class ListenerRegistration {
		final String model;
		final boolean onlyCurrentRevisions;

		ListenerRegistration(String model, boolean onlyCurrentRevisions) {
			this.model = model;
			this.onlyCurrentRevisions = onlyCurrentRevisions;
		}

		@Override
		public int hashCode() {
			if (this.model == null) {
				return (this.onlyCurrentRevisions ? 1 : 0);
			} else {
				return 2 * this.model.hashCode()
						+ (this.onlyCurrentRevisions ? 1 : 0);
			}
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof ListenerRegistration)) {
				return false;
			}
			ListenerRegistration other = (ListenerRegistration) obj;
			if (this.model == null) {
				return other.model == null
						&& this.onlyCurrentRevisions == other.onlyCurrentRevisions;
			} else {
				return this.model.equals(other.model)
						&& this.onlyCurrentRevisions == other.onlyCurrentRevisions;
			}
		}
	}

	/**
	 * Map of all {@link EntityDocumentProcessor} object registered so far,
	 * based on the model and revision (current or not) they are registered for.
	 */
	final HashMap<ListenerRegistration, List<EntityDocumentProcessor>> entityDocumentProcessors;

	/**
	 * Map of all {@link MwRevisionProcessor} object registered so far, based on
	 * the model and revision (current or not) they are registered for.
	 */
	final HashMap<ListenerRegistration, List<MwRevisionProcessor>> mwRevisionProcessors;

	/**
	 * The name of the project whose dumps are processed here.
	 */
	final String projectName;

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

	final DocumentDataFilter filter = new DocumentDataFilter();

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
		this.entityDocumentProcessors = new HashMap<ListenerRegistration, List<EntityDocumentProcessor>>();
		this.mwRevisionProcessors = new HashMap<ListenerRegistration, List<MwRevisionProcessor>>();

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
		this.downloadDirectoryManager = DirectoryManagerFactory
				.createDirectoryManager(downloadDirectory, false);
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
	 * Sets a property filter. If given, all data will be preprocessed to
	 * contain only statements for the given (main) properties.
	 *
	 * @see DatamodelConverter#setOptionPropertyFilter(Set)
	 * @param propertyFilter
	 *            set of language codes that should be retained (can be empty)
	 */
	public void setPropertyFilter(Set<PropertyIdValue> propertyFilter) {
		this.filter.setPropertyFilter(propertyFilter);
	}

	/**
	 * Sets a site link filter. If given, all data will be preprocessed to
	 * contain only data for the given site keys.
	 *
	 * @see DatamodelConverter#setOptionSiteLinkFilter(Set)
	 * @param siteLinkFilter
	 *            set of language codes that should be retained (can be empty)
	 */
	public void setSiteLinkFilter(Set<String> siteLinkFilter) {
		this.filter.setSiteLinkFilter(siteLinkFilter);
	}

	/**
	 * Sets a language filter. If given, all data will be preprocessed to
	 * contain only data for the given languages.
	 *
	 * @see DatamodelConverter#setOptionLanguageFilter(Set)
	 * @param languageFilter
	 *            set of language codes that should be retained (can be empty)
	 */
	public void setLanguageFilter(Set<String> languageFilter) {
		this.filter.setLanguageFilter(languageFilter);
	}

	/**
	 * Registers an MwRevisionProcessor, which will henceforth be notified of
	 * all revisions that are encountered in the dump.
	 * <p>
	 * This only is used when processing dumps that contain revisions. In
	 * particular, plain JSON dumps contain no revision information.
	 * <p>
	 * Importantly, the {@link MwRevision} that the registered processors will
	 * receive is valid only during the execution of
	 * {@link MwRevisionProcessor#processRevision(MwRevision)}, but it will not
	 * be permanent. If the data is to be retained permanently, the revision
	 * processor needs to make its own copy.
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
		registerProcessor(mwRevisionProcessor, model, onlyCurrentRevisions,
				this.mwRevisionProcessors);
	}

	/**
	 * Registers an EntityDocumentProcessor, which will henceforth be notified
	 * of all entity documents that are encountered in the dump.
	 * <p>
	 * It is possible to register processors for specific content types and to
	 * use either all revisions or only the most current ones. This
	 * functionality is only available when processing dumps that contain this
	 * information. In particular, plain JSON dumps do not specify content
	 * models at all and have only one (current) revision of each entity.
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
		registerProcessor(entityDocumentProcessor, model, onlyCurrentRevisions,
				this.entityDocumentProcessors);
	}

	/**
	 * Processes the most recent dump of the sites table to extract information
	 * about registered sites.
	 *
	 * @return a Sites objects that contains the extracted information, or null
	 *         if no sites dump was available (typically in offline mode without
	 *         having any previously downloaded sites dumps)
	 * @throws IOException
	 *             if there was a problem accessing the sites table dump or the
	 *             dump download directory
	 */
	public Sites getSitesInformation() throws IOException {
		MwDumpFile sitesTableDump = getMostRecentDump(DumpContentType.SITES);
		if (sitesTableDump == null) {
			return null;
		}

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
	 * <p>
	 * Note that this method may not always provide reliable results since
	 * single incremental dump files are sometimes missing, even if earlier and
	 * later incremental dumps are available. In such a case, processing all
	 * recent dumps will miss some (random) revisions, thus reflecting a state
	 * that the wiki has never really been in. It might thus be preferable to
	 * process only a single (main) dump file without any incremental dumps.
	 *
	 * @see DumpProcessingController#processMostRecentMainDump()
	 * @see DumpProcessingController#processDump(MwDumpFile)
	 * @see DumpProcessingController#getMostRecentDump(DumpContentType)
	 */
	public void processAllRecentRevisionDumps() {
		WmfDumpFileManager wmfDumpFileManager = getWmfDumpFileManager();
		if (wmfDumpFileManager == null) {
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
	 * @deprecated Use {@link #getMostRecentDump(DumpContentType)} with
	 *             {@link DumpContentType#DAILY} and
	 *             {@link #processDump(MwDumpFile)} instead; method will vanish
	 *             in WDTK 0.5
	 */
	@Deprecated
	public void processMostRecentDailyDump() {
		processDump(getMostRecentDump(DumpContentType.JSON));
	}

	/**
	 * Processes the most recent main (complete) dump that is available.
	 * Convenience method: same as retrieving a dump with
	 * {@link #getMostRecentDump(DumpContentType)} with
	 * {@link DumpContentType#CURRENT} or {@link DumpContentType#FULL}, and
	 * processing it with {@link #processDump(MwDumpFile)}. The individual
	 * methods should be used for better control and error handling.
	 *
	 * @see DumpProcessingController#processAllRecentRevisionDumps()
	 */
	public void processMostRecentMainDump() {
		DumpContentType dumpContentType;
		if (this.preferCurrent) {
			dumpContentType = DumpContentType.CURRENT;
		} else {
			dumpContentType = DumpContentType.FULL;
		}

		processDump(getMostRecentDump(dumpContentType));
	}

	/**
	 * Processes the most recent main (complete) dump in JSON form that is
	 * available. Convenience method: same as retrieving a dump with
	 * {@link #getMostRecentDump(DumpContentType)} with
	 * {@link DumpContentType#JSON}, and processing it with
	 * {@link #processDump(MwDumpFile)}. The individual methods should be used
	 * for better control and error handling.
	 *
	 * @see DumpProcessingController#processAllRecentRevisionDumps()
	 */
	public void processMostRecentJsonDump() {
		processDump(getMostRecentDump(DumpContentType.JSON));
	}

	/**
	 * Processes the contents of the given dump file. All registered processor
	 * objects will be notified of all data. Note that JSON dumps do not
	 * contains any revision information, so that registered
	 * {@link MwRevisionProcessor} objects will not be notified in this case.
	 * Dumps of type {@link DumpContentType#SITES} cannot be processed with this
	 * method; use {@link #getSitesInformation()} to process these dumps.
	 *
	 * @param dumpFile
	 *            the dump to process
	 */
	public void processDump(MwDumpFile dumpFile) {
		if (dumpFile == null) {
			return;
		}

		MwDumpFileProcessor dumpFileProcessor;
		switch (dumpFile.getDumpContentType()) {
		case CURRENT:
		case DAILY:
		case FULL:
			dumpFileProcessor = getRevisionDumpFileProcessor();
			break;
		case JSON:
			dumpFileProcessor = getJsonDumpFileProcessor();
			break;
		case SITES:
		default:
			logger.error("Dumps of type " + dumpFile.getDumpContentType()
					+ " cannot be processed as entity-document dumps.");
			return;
		}

		processDumpFile(dumpFile, dumpFileProcessor);
	}

	/**
	 * Processes the most recent dump of the given type using the given dump
	 * processor.
	 *
	 * @see DumpProcessingController#processMostRecentMainDump()
	 * @see DumpProcessingController#processAllRecentRevisionDumps()
	 *
	 * @param dumpContentType
	 *            the type of dump to process
	 * @param dumpFileProcessor
	 *            the processor to use
	 * @deprecated Use {@link #getMostRecentDump(DumpContentType)} and
	 *             {@link #processDump(MwDumpFile)} instead; method will vanish
	 *             in WDTK 0.5
	 */
	@Deprecated
	public void processMostRecentDump(DumpContentType dumpContentType,
			MwDumpFileProcessor dumpFileProcessor) {
		MwDumpFile dumpFile = getMostRecentDump(dumpContentType);
		if (dumpFile != null) {
			processDumpFile(dumpFile, dumpFileProcessor);
		}
	}

	/**
	 * Returns a handler for the most recent dump file of the given type that is
	 * available (under the current settings), or null if no dump file of this
	 * type could be retrieved.
	 *
	 * @param dumpContentType
	 *            the type of the dump, e.g., {@link DumpContentType#JSON}
	 * @return the most recent dump, or null if none was found
	 */
	public MwDumpFile getMostRecentDump(DumpContentType dumpContentType) {
		WmfDumpFileManager wmfDumpFileManager = getWmfDumpFileManager();
		if (wmfDumpFileManager == null) {
			return null;
		} else {
			MwDumpFile result = wmfDumpFileManager
					.findMostRecentDump(dumpContentType);
			if (result == null) {
				logger.warn("Could not find any dump of type "
						+ dumpContentType.toString() + ".");
			}
			return result;
		}
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
	 * @return a WmfDumpFileManager for the current settings or null if there
	 *         was a problem (e.g., since the current dump file directory could
	 *         not be accessed)
	 */
	public WmfDumpFileManager getWmfDumpFileManager() {
		try {
			return new WmfDumpFileManager(this.projectName,
					this.downloadDirectoryManager, this.webResourceFetcher);
		} catch (IOException e) {
			logger.error("Could not create dump file manager: " + e.toString());
			return null;
		}
	}

	/**
	 * Return the main dump file processor that should be used to process
	 * revisions.
	 *
	 * @return the main MwDumpFileProcessor for revisions
	 */
	MwDumpFileProcessor getRevisionDumpFileProcessor() {
		return new MwRevisionDumpFileProcessor(getMasterMwRevisionProcessor());
	}

	/**
	 * Return the main dump file processor that should be used to process the
	 * content of JSON dumps.
	 *
	 * @return the main MwDumpFileProcessor for JSON
	 */
	MwDumpFileProcessor getJsonDumpFileProcessor() {
		return new JsonDumpFileProcessor(getMasterEntityDocumentProcessor(),
				Datamodel.SITE_WIKIDATA);
	}

	/**
	 * Stores a registered processor object in a map of processors. Used
	 * internally to keep {@link EntityDocumentProcessor} and
	 * {@link MwRevisionProcessor} objects.
	 *
	 * @param processor
	 *            the processor object to register
	 * @param model
	 *            the content model that the processor is registered for; it
	 *            will only be notified of revisions in that model; if null is
	 *            given, all revisions will be processed whatever their model
	 * @param onlyCurrentRevisions
	 *            if true, then the subscriber is only notified of the most
	 *            current revisions; if false, then it will receive all
	 *            revisions, current or not
	 * @param processors
	 *            the map of lists of processors to store the processor in
	 */
	private <T> void registerProcessor(T processor, String model,
			boolean onlyCurrentRevisions,
			Map<ListenerRegistration, List<T>> processors) {
		this.preferCurrent = this.preferCurrent && onlyCurrentRevisions;

		ListenerRegistration listenerRegistration = new ListenerRegistration(
				model, onlyCurrentRevisions);
		if (!processors.containsKey(listenerRegistration)) {
			processors.put(listenerRegistration, new ArrayList<T>());
		}

		processors.get(listenerRegistration).add(processor);
	}

	/**
	 * Returns an {@link EntityDocumentProcessor} object that calls all
	 * registered processors and that takes filters into account if needed.
	 *
	 * @return the master processor
	 */
	private EntityDocumentProcessor getMasterEntityDocumentProcessor() {
		EntityDocumentProcessor result = null;
		EntityDocumentProcessorBroker broker = null;

		for (Map.Entry<ListenerRegistration, List<EntityDocumentProcessor>> entry : this.entityDocumentProcessors
				.entrySet()) {
			for (EntityDocumentProcessor edp : entry.getValue()) {
				if (result == null) {
					result = edp;
				} else {
					if (broker == null) {
						broker = new EntityDocumentProcessorBroker();
						broker.registerEntityDocumentProcessor(result);
						result = broker;
					}
					broker.registerEntityDocumentProcessor(edp);
				}
			}
		}

		return filterEntityDocumentProcessor(result);
	}

	/**
	 * Wraps the given processor into a {@link EntityDocumentProcessorFilter} if
	 * global filters are configured; otherwise just returns the processor
	 * unchanged.
	 *
	 * @param processor
	 *            the processor to wrap
	 * @return
	 */
	private EntityDocumentProcessor filterEntityDocumentProcessor(
			EntityDocumentProcessor processor) {
		if (this.filter.getPropertyFilter() == null
				&& this.filter.getSiteLinkFilter() == null
				&& this.filter.getLanguageFilter() == null) {
			return processor;
		} else {
			return new EntityDocumentProcessorFilter(
					processor, this.filter);
		}
	}

	/**
	 * Returns an {@link MwRevisionProcessor} object that calls all registered
	 * processors and that takes filters into account if needed.
	 *
	 * @return the master processor
	 */
	private MwRevisionProcessor getMasterMwRevisionProcessor() {
		MwRevisionProcessorBroker result = new MwRevisionProcessorBroker();

		for (Entry<ListenerRegistration, List<MwRevisionProcessor>> entry : this.mwRevisionProcessors
				.entrySet()) {
			for (MwRevisionProcessor mrp : entry.getValue()) {
				result.registerMwRevisionProcessor(mrp, entry.getKey().model,
						entry.getKey().onlyCurrentRevisions);
			}
		}

		for (Map.Entry<ListenerRegistration, List<EntityDocumentProcessor>> edpEntry : this.entityDocumentProcessors
				.entrySet()) {
			EntityDocumentProcessor resultEdp;
			if (edpEntry.getValue().size() == 1) {
				resultEdp = edpEntry.getValue().get(0);
			} else {
				EntityDocumentProcessorBroker edpb = new EntityDocumentProcessorBroker();
				for (EntityDocumentProcessor edp : edpEntry.getValue()) {
					edpb.registerEntityDocumentProcessor(edp);
				}
				resultEdp = edpb;
			}

			result.registerMwRevisionProcessor(new WikibaseRevisionProcessor(
					filterEntityDocumentProcessor(resultEdp),
					Datamodel.SITE_WIKIDATA), edpEntry.getKey().model, edpEntry
					.getKey().onlyCurrentRevisions);
		}

		return result;
	}

}
