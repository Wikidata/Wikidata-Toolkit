package org.wikidata.wdtk.rdf.values;

import org.openrdf.model.Value;
import org.openrdf.rio.RDFHandlerException;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;

/**
 * Interface for classes that convert one type of Wikibase data value into a
 * RDF.
 * 
 * @author Markus Kroetzsch
 * 
 * @param <V>
 *            the type of Wikibase value converted by this class
 */
public interface ValueConverter<V extends org.wikidata.wdtk.datamodel.interfaces.Value> {

	/**
	 * Returns an RDF value that should be used to represent the given Wikibase
	 * data value in RDF.
	 * 
	 * @param value
	 *            the value to convert
	 * @param propertyIdValue
	 *            the property for which this value was used; this provides
	 *            important context information for the conversion
	 * @return the RDF value to use for representing the data value in RDF
	 */
	Value getRdfValue(V value, PropertyIdValue propertyIdValue);

	/**
	 * Writes auxiliary triples that might be needed to encode a Wikibase value
	 * in RDF.
	 * 
	 * @throws RDFHandlerException
	 *             if there is a problem writing the triples
	 */
	void writeAuxiliaryTriples() throws RDFHandlerException;
}
