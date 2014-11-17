package org.wikidata.wdtk.storage.endpoint.shared;

/**
 * This class represents a simple query for an item by a given ID of the form
 * "Q123".
 * 
 * @author Fredo Erxleben
 *
 */
public class WdtkItemQuery implements WdtkQuery {

	private static final long serialVersionUID = 2629825991482113215L;
	
	private long numericId;

	/**
	 * 
	 * @param numericId
	 * @throws IllegalArgumentException if the given argument is negative or 0
	 */
	public WdtkItemQuery(long numericId) throws IllegalArgumentException {
		if (numericId < 1) {
			throw new IllegalArgumentException(
					"Item id must be non-negative and not 0.");
		}
		this.numericId = numericId;
	}

	public long getNumericId() {
		return this.numericId;
	}

	public String getId() {
		return "Q" + this.numericId;
	}
	
	@Override
	public String toString(){
		return "WdtkItemQuery (" + this.getId() + ")";
	}
}
