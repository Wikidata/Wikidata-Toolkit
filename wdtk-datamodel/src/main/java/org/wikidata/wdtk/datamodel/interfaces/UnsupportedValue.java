package org.wikidata.wdtk.datamodel.interfaces;

/**
 * Represents a value with an unsupported datatype.
 * We can still "deserialize" it by just storing its
 * JSON representation, so that it can be serialized
 * back to its original representation.
 * This avoids parsing failures on documents containing
 * these values.
 * 
 * @author Antonin Delpeuch
 */
public interface UnsupportedValue extends Value {
	
	/**
	 * Returns the type string found in the JSON representation
	 * of this value.
	 * 
	 * @return
	 * 		the value of "type" in the JSON representation of this value.
	 */
	public String getTypeString();
}
