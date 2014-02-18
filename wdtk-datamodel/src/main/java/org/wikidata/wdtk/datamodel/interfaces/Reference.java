package org.wikidata.wdtk.datamodel.interfaces;

import java.util.List;

/**
 * An interface for references in Wikidata. A reference is currently defined by
 * a list of ValueSnaks, encoding property-value pairs.
 * 
 * @author Markus Kroetzsch
 * 
 */
public interface Reference {

	/**
	 * Get the list of property-value pairs associated with this reference.
	 * Objects of this class are immutable, and the list should therefore not be
	 * modifiable.
	 * 
	 * @return list of ValueSnaks
	 */
	List<? extends ValueSnak> getSnaks();

}
