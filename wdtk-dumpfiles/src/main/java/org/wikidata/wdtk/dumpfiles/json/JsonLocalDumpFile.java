package org.wikidata.wdtk.dumpfiles.json;

import org.wikidata.wdtk.dumpfiles.DumpContentType;
import org.wikidata.wdtk.dumpfiles.WmfLocalDumpFile;
import org.wikidata.wdtk.util.DirectoryManager;

/**
 * A version of the local dump file especially used for JSON dump files.
 * @author Fredo Erxleben
 *
 */
public class JsonLocalDumpFile extends WmfLocalDumpFile {

	public JsonLocalDumpFile(String dateStamp, String projectName,
			DirectoryManager dumpfileDirectoryManager) {
		super(dateStamp, projectName, dumpfileDirectoryManager, DumpContentType.JSON);
	}
	

}
