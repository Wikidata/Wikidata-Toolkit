package org.wikidata.wdtk.dumpfiles;

import java.io.BufferedReader;

/**
 * General interface for classes that process dump files, typically for parsing
 * them.
 * 
 * @author Markus Kroetzsch
 * 
 */
public interface DumpFileProcessor {

	/**
	 * Process dump file data from the given buffered reader.
	 * <p>
	 * The buffered reader is obtained from the given dump file via
	 * {@link MediaWikiDumpFile#getDumpFileReader()}. It will be closed by the
	 * caller.
	 * 
	 * @param bufferedReader
	 *            to access the contents of the dump
	 * @param dumpFile
	 *            to access further information about this dump
	 */
	public void processDumpFileContents(BufferedReader bufferedReader,
			MediaWikiDumpFile dumpFile);
}
