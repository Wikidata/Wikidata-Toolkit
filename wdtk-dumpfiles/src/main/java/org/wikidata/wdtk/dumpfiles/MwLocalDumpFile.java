package org.wikidata.wdtk.dumpfiles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

import org.wikidata.wdtk.util.DirectoryManager;

public class MwLocalDumpFile implements MwDumpFile {

	protected final String dateStamp;
	protected final String projectName;

	final DirectoryManager localDumpfileDirectoryManager;

	final DumpContentType dumpContentType;

	public MwLocalDumpFile(String dateStamp, String projectName,
			DirectoryManager dumpFileDirectoryManager,
			DumpContentType dumpContentType) {
		this.dateStamp = dateStamp;
		this.projectName = projectName;
		this.localDumpfileDirectoryManager = dumpFileDirectoryManager;
		this.dumpContentType = dumpContentType;

	}

	@Override
	public boolean isAvailable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getProjectName() {
		return projectName;
	}

	@Override
	public String getDateStamp() {
		return dateStamp;
	}

	@Override
	public DumpContentType getDumpContentType() {
		return dumpContentType;
	}

	@Override
	public InputStream getDumpFileStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BufferedReader getDumpFileReader() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void prepareDumpFile() throws IOException {
		// TODO Auto-generated method stub

	}

}