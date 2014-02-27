package org.wikidata.wdtk.dumpfiles;

import org.json.JSONException;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;

class PropertyTestCase extends TestCase {

	PropertyTestCase(String filePath, JsonConverter converter) {
		super(filePath, converter);
	}
	private PropertyDocument result;
	private PropertyDocument expected = null;
	
	void convert() throws JSONException {
		this.result = this.converter.convertToPropertyDocument(this.json);
	}
	
	boolean resultMatchesExpected(){
		if(this.expected != null){
			return false;
		}
		
		return this.result.equals(this.expected);
	}
	
	void setExpected(PropertyDocument expected){
		this.expected = expected;
	}
	
	PropertyDocument getResult(){
		return this.result;
	}
}
