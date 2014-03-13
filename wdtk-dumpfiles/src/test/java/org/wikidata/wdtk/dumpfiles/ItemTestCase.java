package org.wikidata.wdtk.dumpfiles;

import org.json.JSONException;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;

class ItemTestCase extends TestCase {

	ItemTestCase(String filePath, JsonConverter converter) {
		super(filePath, converter);
	}
	private ItemDocument result;
	
	void convert() throws JSONException {
		this.result = this.converter.convertToItemDocument(this.json);
	}
	
	ItemDocument getResult(){
		return this.result;
	}
}
