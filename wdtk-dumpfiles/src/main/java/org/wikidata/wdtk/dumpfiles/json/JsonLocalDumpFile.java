package org.wikidata.wdtk.dumpfiles.json;

import java.io.IOException;
import java.io.InputStream;

import org.wikidata.wdtk.dumpfiles.DumpContentType;
import org.wikidata.wdtk.dumpfiles.WmfDumpFile;

public class JsonLocalDumpFile extends WmfDumpFile {

	public JsonLocalDumpFile(String dateStamp, String projectName) {
		super(dateStamp, projectName);
		// TODO Auto-generated constructor stub
	}

	@Override
	public DumpContentType getDumpContentType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getDumpFileStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void prepareDumpFile() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean fetchIsDone() {
		// TODO Auto-generated method stub
		return false;
	}

}
