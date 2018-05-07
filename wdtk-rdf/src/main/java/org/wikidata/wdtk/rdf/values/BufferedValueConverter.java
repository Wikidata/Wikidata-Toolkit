package org.wikidata.wdtk.rdf.values;

/*
 * #%L
 * Wikidata Toolkit RDF
 * %%
 * Copyright (C) 2014 Wikidata Toolkit Developers
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.wikidata.wdtk.rdf.OwlDeclarationBuffer;
import org.wikidata.wdtk.rdf.PropertyRegister;
import org.wikidata.wdtk.rdf.RdfWriter;

public abstract class BufferedValueConverter<V extends org.wikidata.wdtk.datamodel.interfaces.Value>
		extends AbstractValueConverter<V> {

	final List<V> valueQueue = new ArrayList<>();
	final List<Resource> valueSubjectQueue = new ArrayList<>();
	final HashSet<Resource> declaredValues = new HashSet<>();

	public BufferedValueConverter(RdfWriter rdfWriter,
			PropertyRegister propertyRegister,
			OwlDeclarationBuffer rdfConversionBuffer) {
		super(rdfWriter, propertyRegister, rdfConversionBuffer);
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
				valueIterator.next();
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
