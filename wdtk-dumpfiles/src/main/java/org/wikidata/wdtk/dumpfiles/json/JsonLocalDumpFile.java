package org.wikidata.wdtk.dumpfiles.json;
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

import org.wikidata.wdtk.dumpfiles.DumpContentType;
import org.wikidata.wdtk.dumpfiles.wmf.WmfDumpFile;

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
