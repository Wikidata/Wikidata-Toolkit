package org.wikidata.wdtk.datamodel.interfaces;

/**
 * Interface for entity redirect.
 *
 * @author Thomas Pellissier Tanon
 *
 */
public interface EntityRedirectDocument extends EntityDocument {

	/**
	 * Returns the ID of the entity that the redirection target to.
	 *
	 * @return entity id
	 */
	EntityIdValue getTargetId();


	@Override
	EntityRedirectDocument withRevisionId(long newRevisionId);
}
