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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.util.CompressionType;
import org.wikidata.wdtk.util.DirectoryManager;
import org.wikidata.wdtk.util.DirectoryManagerFactory;

/**
 * Class for representing dump files that are found at arbitrary (local) file
 * paths. The meta-data for the dump file (content type, time stamp, etc.) can
 * be set explicitly, or be guessed from the file name (to the extent possible).
 *
 * @author Markus Damm
 * @author Markus Kroetzsch
 */
public class MwLocalDumpFile implements MwDumpFile {

	static final Logger logger = LoggerFactory.getLogger(MwLocalDumpFile.class);

	/**
	 * Date stamp when the dump file was created. If there is no date stamp
	 * given or found, it is set to "YYYYMMDD"
	 */
	final String dateStamp;
	/**
	 * Project name of the dump file
	 */
	final String projectName;
	/**
	 * Name of the dump file in the file system
	 */
	final String dumpFileName;
	/**
	 * Absolute path to the dump file
	 */
	final Path dumpFilePath;
	/**
	 * Type of this dumpfile
	 */
	final DumpContentType dumpContentType;

	/**
	 * DirectoryManager for accessing the dumpfile
	 */
	DirectoryManager directoryManager;

	/**
	 * True if the given file is available (exists).
	 */
	final boolean isAvailable;

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
	 *            Path to the dump file in the file system
	 */
	public MwLocalDumpFile(String filepath) {
		this(filepath, null, null, null);
	}

	/**
	 * Constructor.
	 *
	 * @param filePath
	 *            Path to the dump file in the file system
	 * @param dumpContentType
	 *            DumpContentType of the dump file, or null if not known to
	 *            guess it from file name; this information is essential to
	 *            invoke the correct processing code to read the dump file
	 * @param dateStamp
	 *            dump date in format YYYYMMDD, or null if not known to guess it
	 *            from file name; this is mainly used for logs and messages
	 * @param projectName
	 *            project name string, or null to use a default string; this is
	 *            mainly used for logs and messages
	 */
	public MwLocalDumpFile(String filePath, DumpContentType dumpContentType,
			String dateStamp, String projectName) {
		this.dumpFilePath = Paths.get(filePath).toAbsolutePath();
		this.dumpFileName = this.dumpFilePath.getFileName().toString();

		try {
			this.directoryManager = DirectoryManagerFactory
					.createDirectoryManager(this.dumpFilePath.getParent(), true);
		} catch (IOException e) {
			this.directoryManager = null;
			logger.error("Could not access local dump file: " + e.toString());
		}

		if (dumpContentType == null) {
			this.dumpContentType = guessDumpContentType(this.dumpFileName);
		} else {
			this.dumpContentType = dumpContentType;
		}

		if (dateStamp == null) {
			this.dateStamp = guessDumpDate(this.dumpFileName);
		} else {
			this.dateStamp = dateStamp;
		}

		if (projectName == null) {
			this.projectName = "LOCAL";
		} else {
			this.projectName = projectName;
		}

		this.isAvailable = this.directoryManager != null
				&& this.directoryManager.hasFile(this.dumpFileName);
	}

	/**
	 * Returns the absolute path to this dump file.
	 *
	 * @return path
	 */
	public Path getPath() {
		return this.dumpFilePath;
	}

	@Override
	public boolean isAvailable() {
		return this.isAvailable;
	}

	@Override
	public String getProjectName() {
		return this.projectName;
	}

	@Override
	public String getDateStamp() {
		return this.dateStamp;
	}

	@Override
	public DumpContentType getDumpContentType() {
		return this.dumpContentType;
	}

	@Override
	public InputStream getDumpFileStream() throws IOException {
		if (!isAvailable()) {
			throw new IOException("Local dump file \""
					+ this.dumpFilePath.toString()
					+ "\" is not available for reading.");
		}
		return this.directoryManager.getInputStreamForFile(this.dumpFileName,
				MwLocalDumpFile.COMPRESSION_TYPE.get(this.dumpContentType));
	}

	@Override
	public BufferedReader getDumpFileReader() throws IOException {
		return new BufferedReader(new InputStreamReader(getDumpFileStream(),
				StandardCharsets.UTF_8));
	}

	@Override
	public void prepareDumpFile() {
		// nothing to do
	}

	@Override
	public String toString() {
		return this.dumpFilePath.toString() + " (" + this.projectName + "/"
				+ getDumpContentType().toString().toLowerCase() + "/"
				+ this.dateStamp + ")";
	}

	/**
	 * Guess the type of the given dump from its filename.
	 *
	 * @param fileName
	 * @return dump type, defaulting to JSON if no type was found
	 */
	private static DumpContentType guessDumpContentType(String fileName) {
		String lcDumpName = fileName.toLowerCase();
		if (lcDumpName.contains(".json.gz")) {
			return DumpContentType.JSON;
		} else if (lcDumpName.contains(".json.bz2")) {
			return DumpContentType.JSON;
		} else if (lcDumpName.contains(".sql.gz")) {
			return DumpContentType.SITES;
		} else if (lcDumpName.contains(".xml.bz2")) {
			if (lcDumpName.contains("daily")) {
				return DumpContentType.DAILY;
			} else if (lcDumpName.contains("current")) {
				return DumpContentType.CURRENT;
			} else {
				return DumpContentType.FULL;
			}
		} else {
			logger.warn("Could not guess type of the dump file \"" + fileName
					+ "\". Defaulting to json.gz.");
			return DumpContentType.JSON;
		}
	}

	/**
	 * Guess the date of the dump from the given dump file name.
	 *
	 * @param fileName
	 * @return 8-digit date stamp or YYYYMMDD if none was found
	 */
	private static String guessDumpDate(String fileName) {
		Pattern p = Pattern.compile("([0-9]{8})");
		Matcher m = p.matcher(fileName);

		if (m.find()) {
			return m.group(1);
		} else {
			logger.info("Could not guess date of the dump file \"" + fileName
					+ "\". Defaulting to YYYYMMDD.");
			return "YYYYMMDD";
		}
	}
}
