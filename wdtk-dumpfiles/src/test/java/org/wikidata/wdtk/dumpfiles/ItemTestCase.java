package org.wikidata.wdtk.dumpfiles;

import org.json.JSONException;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;

class ItemTestCase extends TestCase {

	ItemTestCase(String filePath, JsonConverter converter) {
		super(filePath, converter);
	}
	private ItemDocument result;
	private ItemDocument expected = null;
	
	void convert() throws JSONException {
		this.result = this.converter.convertToItemRecord(this.json);
	}
	
	boolean resultMatchesExpected(){
		if(this.expected != null){
			return false;
		}
		
		return this.result.equals(this.expected);
	}
	
	void setExpected(ItemDocument expected){
		this.expected = expected;
	}
	
	ItemDocument getResult(){
		return this.result;
	}
}
