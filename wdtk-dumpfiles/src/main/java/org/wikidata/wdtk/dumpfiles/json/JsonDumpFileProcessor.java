package org.wikidata.wdtk.dumpfiles.json;

import java.io.InputStream;

import org.wikidata.wdtk.datamodel.json.jackson.documents.ItemDocumentImpl;
import org.wikidata.wdtk.dumpfiles.MwDumpFile;
import org.wikidata.wdtk.dumpfiles.MwDumpFileProcessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

public class JsonDumpFileProcessor implements MwDumpFileProcessor {
	
	private static ObjectMapper mapper = new ObjectMapper();
	private static ObjectReader itemDocumentReader = mapper.reader(ItemDocumentImpl.class);

	@Override
	public void processDumpFileContents(InputStream inputStream,
			MwDumpFile dumpFile) {
		// TODO Auto-generated method stub
		
	}

}
