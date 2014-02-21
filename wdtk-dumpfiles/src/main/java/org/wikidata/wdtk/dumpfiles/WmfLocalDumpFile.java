package org.wikidata.wdtk.dumpfiles;

import java.io.BufferedReader;
import java.io.IOException;

public class WmfLocalDumpFile extends WmfDumpFile {

	final DirectoryManager thisDumpfileDirectoryManager;
	final MediaWikiDumpFile.DumpContentType dumpContentType;

	public WmfLocalDumpFile(String dateStamp, String projectName,
			DirectoryManager dumpfileDirectoryManager,
			MediaWikiDumpFile.DumpContentType dumpContentType,
			String filePostfix) {
		super(dateStamp, projectName, filePostfix);

		String subdirectoryName = dumpContentType.toString().toLowerCase()
				+ "-" + dateStamp;
		if (!dumpfileDirectoryManager.hasSubdirectory(subdirectoryName)) {
			throw new IllegalArgumentException(
					"There is no local dump file directory at the specified location.");
		}
		try {
			this.thisDumpfileDirectoryManager = dumpfileDirectoryManager
					.getSubdirectoryManager(subdirectoryName);
		} catch (IOException e) {
			throw new IllegalArgumentException(
					"Could not change to dump file directory", e);
		}

		this.dumpContentType = dumpContentType;
	}

	/**
	 * Get the directory where this dump file data should be.
	 * 
	 * @return
	 */
	public String getDumpfileDirectory() {
		return this.thisDumpfileDirectoryManager.toString();
	}

	@Override
	public DumpContentType getDumpContentType() {
		return this.dumpContentType;
	}

	@Override
	public BufferedReader getDumpFileReader() throws IOException {
		return this.thisDumpfileDirectoryManager
				.getBufferedReaderForBz2File(getFileName());
	}

	@Override
	protected Long fetchMaximalRevisionId() {
		String inputLine;
		try (BufferedReader in = this.thisDumpfileDirectoryManager
				.getBufferedReaderForFile("maxrevid.txt")) {
			inputLine = in.readLine();
		} catch (IOException e) {
			return -1L;
		}

		if (inputLine != null) {
			try {
				return new Long(inputLine);
			} catch (NumberFormatException e) {
				// fall through
			}
		}
		return -1L;
	}

	@Override
	protected boolean fetchIsDone() {
		return this.thisDumpfileDirectoryManager.hasFile(getFileName())
				&& this.getMaximalRevisionId() >= 0;
	}

	/**
	 * Get the file name of this dump file.
	 * 
	 * @return
	 */
	String getFileName() {
		return this.projectName + "-" + this.dateStamp + filePostfix;
	}

}
