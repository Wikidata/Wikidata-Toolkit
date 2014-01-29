package org.wikidata.wdtk.datamodel.implementation;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeId;
import org.wikidata.wdtk.datamodel.interfaces.EntityId;
import org.wikidata.wdtk.datamodel.interfaces.PropertyId;
import org.wikidata.wdtk.datamodel.interfaces.PropertyRecord;

/**
 * Implementation of {@link PropertyRecord}.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class PropertyRecordImpl extends EntityRecordImpl implements
		PropertyRecord {

	final PropertyId propertyId;
	final DatatypeId datatypeId;

	/**
	 * Constructor.
	 * 
	 * @param propertyId
	 *            the id of the property that data is about
	 * @param labels
	 *            the labels of this property by language code
	 * @param descriptions
	 *            the descriptions of this property by language code
	 * @param aliases
	 *            the alias lists of this property by language code
	 * @param datatypeId
	 *            the datatype of that property
	 */
	public PropertyRecordImpl(PropertyId propertyId,
			Map<String, String> labels, Map<String, String> descriptions,
			Map<String, List<String>> aliases, DatatypeId datatypeId) {
		super(labels, descriptions, aliases);
		Validate.notNull(propertyId, "property ID cannot be null");
		Validate.notNull(datatypeId, "datatype ID cannot be null");
		this.propertyId = propertyId;
		this.datatypeId = datatypeId;
	}

	@Override
	public EntityId getEntityId() {
		return propertyId;
	}

	@Override
	public PropertyId getPropertyId() {
		return propertyId;
	}

	@Override
	public DatatypeId getDatatype() {
		return datatypeId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + datatypeId.hashCode();
		result = prime * result + propertyId.hashCode();
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof PropertyRecordImpl)) {
			return false;
		}
		PropertyRecordImpl other = (PropertyRecordImpl) obj;
		return datatypeId.equals(other.datatypeId)
				&& propertyId.equals(other.propertyId);
	}

}
