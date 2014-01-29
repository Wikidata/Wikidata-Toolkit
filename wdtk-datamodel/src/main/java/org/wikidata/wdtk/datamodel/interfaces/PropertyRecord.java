package org.wikidata.wdtk.datamodel.interfaces;

/**
 * Interface for datasets that describe properties. It extends
 * {@link EntityRecord} with information about the datatype of a property.
 * 
 * Claims or Statements on properties might be supported in the future.
 * 
 * @author Markus Kroetzsch
 * 
 */
public interface PropertyRecord extends EntityRecord {

	/**
	 * Return the ID of the property that the data refers to. The result is the
	 * same as that of {@link EntityRecord#getEntityId()}, but declared with a
	 * more specific result type.
	 * 
	 * @return property id
	 */
	public PropertyId getPropertyId();

	/**
	 * Get the datatype id of the datatype defined for this property.
	 * 
	 * @return
	 */
	public DatatypeId getDatatype();

}
