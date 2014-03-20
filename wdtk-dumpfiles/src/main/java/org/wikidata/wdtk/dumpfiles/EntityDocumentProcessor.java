package org.wikidata.wdtk.dumpfiles;

import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;

/**
 * Interface for classes that are able to process {@link EntityDocument} objects
 * in some way. Classes that implement this can subscribe to receive entity
 * documents as obtained, e.g., from parsing dump files.
 * 
 * @author Markus Kroetzsch
 * 
 */
public interface EntityDocumentProcessor {

	/**
	 * Processes the given ItemDocument.
	 * 
	 * @param itemDocument
	 *            the ItemDocument
	 */
	public void processItemDocument(ItemDocument itemDocument);

	/**
	 * Processes the given PropertyDocument.
	 * 
	 * @param propertyDocument
	 *            the PropertyDocument
	 */
	public void processPropertyDocument(PropertyDocument propertyDocument);

	/**
	 * Performs final actions that should be done after all entity documents in
	 * a batch of entity documents have been processed. This is usually called
	 * after a whole dumpfile was completely processed.
	 */
	public void finishProcessingEntityDocuments();

}
