package org.wikidata.wdtk.rdf.values;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.openrdf.model.Resource;
import org.openrdf.rio.RDFHandlerException;
import org.wikidata.wdtk.rdf.PropertyTypes;
import org.wikidata.wdtk.rdf.OwlDeclarationBuffer;
import org.wikidata.wdtk.rdf.RdfWriter;

public abstract class BufferedValueConverter<V extends org.wikidata.wdtk.datamodel.interfaces.Value>
		extends AbstractValueConverter<V> {

	final List<V> valueQueue;
	final List<Resource> valueSubjectQueue;
	final HashSet<Resource> declaredValues;

	public BufferedValueConverter(RdfWriter rdfWriter,
			PropertyTypes propertyTypes, OwlDeclarationBuffer rdfConversionBuffer) {
		super(rdfWriter, propertyTypes, rdfConversionBuffer);
		this.valueQueue = new ArrayList<V>();
		this.valueSubjectQueue = new ArrayList<Resource>();
		this.declaredValues = new HashSet<Resource>();
	}

	/**
	 * Adds the given value to the list of values that should still be
	 * serialized. The given RDF resource will be used as a subject.
	 * 
	 * @param value
	 *            the value to be serialized
	 * @param resource
	 *            the RDF resource that is used as a subject for serialization
	 */
	void addValue(V value, Resource resource) {
		this.valueQueue.add(value);
		this.valueSubjectQueue.add(resource);
	}

	@Override
	public void writeAuxiliaryTriples() throws RDFHandlerException {
		Iterator<V> valueIterator = this.valueQueue.iterator();
		for (Resource resource : this.valueSubjectQueue) {
			if (!this.declaredValues.add(resource)) {
				continue;
			}
			writeValue(valueIterator.next(), resource);
		}
		this.valueSubjectQueue.clear();
		this.valueQueue.clear();
	}

	/**
	 * Writes the triples for a single value, using the given resource as
	 * subject.
	 * 
	 * @param value
	 * @param resource
	 * @throws RDFHandlerException
	 */
	public abstract void writeValue(V value, Resource resource)
			throws RDFHandlerException;

}
