package org.wikidata.wdtk.datamodel.json.jackson.datavalues;


/**
 * This represents a string value.
 * The <i>type</i> is <i>"string"</i>.
 * 
 * This is used in string data values as well as commoms media data values.
 * @author Fredo Erxleben
 *
 */
public class StringValueImpl extends ValueImpl {

	private String value;
	
	public StringValueImpl(){
		super(typeString);
	}
	public StringValueImpl(String value){
		super(typeString);
		this.value = value;
	}
	
	public String getValue(){
		return this.value;
	}
	
	public void setValue(String value){
		this.value = value;
	}
}
