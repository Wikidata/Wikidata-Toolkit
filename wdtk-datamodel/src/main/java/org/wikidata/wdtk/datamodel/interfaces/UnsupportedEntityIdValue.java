package org.wikidata.wdtk.datamodel.interfaces;

/**
 * Represents a entity id value of an unsupported type.
 * We can still "deserialize" it by just storing its
 * JSON representation, so that it can be serialized
 * back to its original representation.
 * This avoids parsing failures on documents containing
 * these values.
 * 
 * @author Antonin Delpeuch
 */
public interface UnsupportedEntityIdValue extends EntityIdValue {
	/**
	 * The type of entity as represented in the JSON serialization.
	 */
	public String getEntityTypeString();
}
