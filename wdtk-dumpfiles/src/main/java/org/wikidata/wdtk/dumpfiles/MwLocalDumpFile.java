package org.wikidata.wdtk.dumpfiles;

/*
 * #%L
 * Wikidata Toolkit Dump File Handling
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


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.util.CompressionType;
import org.wikidata.wdtk.util.DirectoryManager;
import org.wikidata.wdtk.util.DirectoryManagerImpl;

/**
 * Class for representing dump files that has been previously downloaded and
 * should be processed locally.
 * 
 * @author Markus Damm
 *
 */
public class MwLocalDumpFile implements MwDumpFile {

	static final Logger logger = LoggerFactory
			.getLogger(DumpProcessingController.class);

	/**
	 * DateStamp when the dump file was created. If there is no special
	 * dateStamp given in the Constructor, it is set to "YYYYMMDD"
	 */
	protected final String dateStamp;
	/**
	 * Project name of the dump file
	 */
	protected final String projectName;
	/**
	 * Name of the dump file in the file system
	 */
	protected String dumpFileName;
	/**
	 * Path of the dump file presented as Path.
	 */
	protected final Path dumpFilePath;

	/**
	 * DirectoryManager for this local dumpfile
	 */
	DirectoryManager localDumpfileDirectoryManager;

	/**
	 * Type of this dumpfile
	 */
	DumpContentType dumpContentType;

	/**
	 * Stores whether this object is already prepared or not
	 */
	boolean isPrepared;

	/**
	 * Hash map defining the compression type of each type of dump.
	 */
	static final Map<DumpContentType, CompressionType> COMPRESSION_TYPE = new HashMap<DumpContentType, CompressionType>();
	static {
		MwLocalDumpFile.COMPRESSION_TYPE.put(DumpContentType.DAILY,
				CompressionType.BZ2);
		MwLocalDumpFile.COMPRESSION_TYPE.put(DumpContentType.CURRENT,
				CompressionType.BZ2);
		MwLocalDumpFile.COMPRESSION_TYPE.put(DumpContentType.FULL,
				CompressionType.BZ2);
		MwLocalDumpFile.COMPRESSION_TYPE.put(DumpContentType.SITES,
				CompressionType.GZIP);
		MwLocalDumpFile.COMPRESSION_TYPE.put(DumpContentType.JSON,
				CompressionType.GZIP);
	}

	/**
	 * Constructor. The DumpContentType will be inferred by the name of the
	 * file, if possible. If it is not possible, it will be set to JSON by
	 * default.
	 * 
	 * @param filepath
	 *                Path to the dump file in the file system
	 */
	public MwLocalDumpFile(String filepath) {
		this(filepath, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param filepath
	 *                Path to the dump file in the file system
	 * @param dumpContentType
	 *                DumpContentType of the dump file
	 */
	public MwLocalDumpFile(String filepath, DumpContentType dumpContentType) {
		this.dumpContentType = dumpContentType;
		this.dumpFileName = "";
		this.dumpFilePath = Paths.get(filepath);
		this.dateStamp = "YYYYMMDD";
		this.projectName = "LocalDumpFile";
		this.isPrepared = false;
	}

	/**
	 * Constructor for test cases with a MockDirectoryManager
	 * 
	 * @param dumpFileDirectoryManager
	 *                the directory manager for the directory where dump is
	 *                stored
	 * @param dumpContentType
	 *                the type of dump this represents
	 * @param dumpFileName
	 *                name of the dumpFile
	 * @param dateStamp
	 *                dump date in format YYYYMMDD
	 * @param projectName
	 *                project name string
	 * @throws IOException
	 *                 if there was a problem finding the path
	 */
	MwLocalDumpFile(DirectoryManager dumpFileDirectoryManager,
			DumpContentType dumpContentType, String dumpFileName,
			String dateStamp, String projectName)
			throws IOException {
		this.dateStamp = dateStamp;
		this.projectName = projectName;
		this.localDumpfileDirectoryManager = dumpFileDirectoryManager;
		this.dumpContentType = dumpContentType;
		this.dumpFileName = dumpFileName;
		this.dumpFilePath = Paths.get(dumpFileName);
		this.isPrepared = false;
	}

	@Override
	public boolean isAvailable() {
		if (!isPrepared) {
			prepareDumpFile();
		}
		if (this.localDumpfileDirectoryManager != null
				&& this.dumpContentType != null) {
			return this.localDumpfileDirectoryManager
					.hasFile(dumpFileName);
		}
		return false;
	}

	@Override
	public String getProjectName() {
		return this.projectName;
	}

	@Override
	public String getDateStamp() {
		return this.dateStamp;
	}

	/**
	 * Returns the name of the dump file.
	 * 
	 * @return Name of the dump file.
	 */
	public String getDumpFileName() {
		return this.dumpFileName;
	}

	@Override
	public DumpContentType getDumpContentType() {
		return this.dumpContentType;
	}

	@Override
	public InputStream getDumpFileStream() throws IOException {
		if (!isAvailable()) {
			throw new IOException();
		}
		return this.localDumpfileDirectoryManager
				.getInputStreamForFile(
						dumpFileName,
						MwLocalDumpFile.COMPRESSION_TYPE
								.get(dumpContentType));
	}

	@Override
	public BufferedReader getDumpFileReader() throws IOException {
		if (!isAvailable()) {
			throw new IOException();
		}
		return new BufferedReader(new InputStreamReader(
				getDumpFileStream(), StandardCharsets.UTF_8));
	}

	@Override
	public void prepareDumpFile() {
		configureDirectoryManager();
		inferDumpContentType();
	}

	@Override
	public String toString() {
		return this.projectName + "-"
				+ getDumpContentType().toString().toLowerCase()
				+ "-" + this.dateStamp;
	}

	/**
	 * Configures the DirectoryManager. It checks whether the given path and
	 * file exists. If the given file does not exist, the DirectoryManager
	 * will be set to null, otherwise a corresponding DirectoryManager will
	 * be created.
	 */
	void configureDirectoryManager() {
		if (this.localDumpfileDirectoryManager == null) {
			if (Files.exists(dumpFilePath)) {
				try {
					this.localDumpfileDirectoryManager = new DirectoryManagerImpl(
							dumpFilePath.getParent());
					dumpFileName = dumpFilePath
							.getFileName()
							.toString();
				} catch (IOException e) {
					logger.error("An error occured while creating the DirectryManager.");
				}
			}
		}
	}

	/**
	 * Tries to infer the DumpContentType by filename. If it is not
	 * possible, the DumpContentType will be set to JSON by default. If the
	 * DumpContentType has already been set or inferred before, this method
	 * will not do it again.
	 */
	void inferDumpContentType() {
		if (this.dumpContentType == null) {
			String lcDumpName = this.dumpFileName.toLowerCase();
			if (lcDumpName.contains(".json.gz")) {
				this.dumpContentType = DumpContentType.JSON;
				return;
			}
			if (lcDumpName.contains(".sql.gz")){
				this.dumpContentType = DumpContentType.SITES;
				return;
			}
			if (lcDumpName.contains(".xml.bz2")){
				if (lcDumpName.contains("daily")){
					this.dumpContentType = DumpContentType.DAILY;
					return;
				}
				if (lcDumpName.contains("current")) {
					this.dumpContentType = DumpContentType.CURRENT;
					return;
				}
				this.dumpContentType = DumpContentType.FULL;
			}

		}
	}

}
