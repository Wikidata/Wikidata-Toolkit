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

/**
 * General interface for classes that process dump files, typically for parsing
 * them.
 * 
 * @author Markus Kroetzsch
 * 
 */
public interface MwDumpFileProcessor {

	/**
	 * Process dump file data from the given input stream.
	 * <p>
	 * The input stream is obtained from the given dump file via
	 * {@link MwDumpFile#getDumpFileStream()}. It will be closed by the
	 * caller.
	 * 
	 * @param inputStream
	 *            to access the contents of the dump
	 * @param dumpFile
	 *            to access further information about this dump
	 */
	void processDumpFileContents(InputStream inputStream,
			MwDumpFile dumpFile);
}
