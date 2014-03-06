package org.wikidata.wdtk.dumpfiles;

import org.json.JSONException;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;

class PropertyTestCase extends TestCase {

	PropertyTestCase(String filePath, JsonConverter converter) {
		super(filePath, converter);
	}
	private PropertyDocument result;
	
	void convert() throws JSONException {
		this.result = this.converter.convertToPropertyDocument(this.json);
	}
	
	PropertyDocument getResult(){
		return this.result;
	}
}
