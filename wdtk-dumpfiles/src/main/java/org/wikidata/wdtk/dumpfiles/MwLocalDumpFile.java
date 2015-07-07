package org.wikidata.wdtk.dumpfiles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.wikidata.wdtk.util.CompressionType;
import org.wikidata.wdtk.util.DirectoryManager;

/**
 * Class for representing dump files that has been previously downladed
 * 
 * @author Markus Damm
 *
 */
public class MwLocalDumpFile implements MwDumpFile {

	protected final String dateStamp;
	protected final String projectName;
	Boolean isDone;
	protected final String dumpFileName;

	/**
	 * DirectoryManager for this local dumpfile
	 */
	final DirectoryManager localDumpfileDirectoryManager;

	/**
	 * Type of this dumpfile
	 */
	final DumpContentType dumpContentType;

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
	 * Constructor
	 * 
	 * @param dumpFileDirectoryManager
	 *                the directory manager for the directory where dump is
	 *                stored
	 * @param dumpContentType
	 *                the type of dump this represents
	 * @param dumpFileName
	 *                name of the dumpFile
	 */

	public MwLocalDumpFile(DirectoryManager dumpFileDirectoryManager,
			DumpContentType dumpContentType, String dumpFileName) {
		this(dumpFileDirectoryManager, dumpContentType,
				dumpFileName, "", "");
	}

	/**
	 * Constructor
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
	 */
	public MwLocalDumpFile(DirectoryManager dumpFileDirectoryManager,
			DumpContentType dumpContentType, String dumpFileName,
			String dateStamp,
			String projectName) {
		this.dateStamp = dateStamp;
		this.projectName = projectName;
		this.localDumpfileDirectoryManager = dumpFileDirectoryManager;
		this.dumpContentType = dumpContentType;
		this.dumpFileName = dumpFileName;
	}

	@Override
	public boolean isAvailable() {
		if (this.isDone == null) {
			isDone = this.localDumpfileDirectoryManager
					.hasFile(dumpFileName);
		}
		return isDone;
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
		return this.localDumpfileDirectoryManager
				.getInputStreamForFile(
						dumpFileName,
						MwLocalDumpFile.COMPRESSION_TYPE
								.get(dumpContentType));
	}

	@Override
	public BufferedReader getDumpFileReader() throws IOException {
		return new BufferedReader(new InputStreamReader(
				getDumpFileStream(), StandardCharsets.UTF_8));
	}

	@Override
	public void prepareDumpFile() throws IOException {
		// nothing to do
	}

	@Override
	public String toString() {
		return this.projectName + "-"
				+ getDumpContentType().toString().toLowerCase()
				+ "-" + this.dateStamp;
	}

}
