package org.wikidata.wdtk.datamodel.implementation;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

/**
 * Implementation of {@link Reference}.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class ReferenceImpl implements Reference {

	List<? extends ValueSnak> valueSnaks;

	ReferenceImpl(List<? extends ValueSnak> valueSnaks) {
		Validate.notNull(valueSnaks, "List of value snaks cannot be null");
		this.valueSnaks = valueSnaks;
	}

	@Override
	public List<? extends ValueSnak> getSnaks() {
		return Collections.unmodifiableList(this.valueSnaks);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return valueSnaks.hashCode();
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
		if (!(obj instanceof ReferenceImpl)) {
			return false;
		}
		ReferenceImpl other = (ReferenceImpl) obj;
		return other.valueSnaks.equals(this.valueSnaks);
	}

}
