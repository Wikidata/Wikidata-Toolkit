package org.wikidata.wdtk.datamodel.implementation;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.interfaces.EntityRecord;

/**
 * Implementation of {@link EntityRecord}. This abstract class defines the code
 * for shared functionality of concrete types of EntityRecord.
 * 
 * @author Markus Kroetzsch
 * 
 */
public abstract class EntityRecordImpl implements EntityRecord {

	final Map<String, String> labels;
	final Map<String, String> descriptions;
	final Map<String, List<String>> aliases;

	/**
	 * Constructor.
	 * 
	 * @param labels
	 * @param descriptions
	 * @param aliases
	 */
	public EntityRecordImpl(Map<String, String> labels,
			Map<String, String> descriptions, Map<String, List<String>> aliases) {
		Validate.notNull(labels, "map of labels cannot be null");
		Validate.notNull(descriptions, "map of descriptions cannot be null");
		Validate.notNull(aliases, "map of aliases cannot be null");
		this.labels = labels;
		this.descriptions = descriptions;
		this.aliases = aliases;
	}

	@Override
	public Map<String, String> getLabels() {
		return Collections.unmodifiableMap(labels);
	}

	@Override
	public Map<String, String> getDescriptions() {
		return Collections.unmodifiableMap(descriptions);
	}

	@Override
	public Map<String, List<String>> getAliases() {
		// TODO This still allows inner lists of aliases to be modified. Do
		// we have to protect against this?
		return Collections.unmodifiableMap(aliases);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + aliases.hashCode();
		result = prime * result + descriptions.hashCode();
		result = prime * result + labels.hashCode();
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
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof EntityRecordImpl)) {
			return false;
		}
		EntityRecordImpl other = (EntityRecordImpl) obj;
		return aliases.equals(other.aliases)
				&& descriptions.equals(other.descriptions)
				&& labels.equals(other.labels);
	}

}
