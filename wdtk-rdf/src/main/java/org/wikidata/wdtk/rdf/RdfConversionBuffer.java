package org.wikidata.wdtk.rdf;

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
import java.util.Iterator;
import java.util.List;

import org.openrdf.model.Resource;
import org.openrdf.rio.RDFHandlerException;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;

/**
 * This class stores information about data that should be serialized in RDF
 * later on. This is done for two reasons: (1) to produce output where triples
 * are ordered by subject, (2) to avoid some duplicate triples for things that
 * are needed in many places. Due to memory constraints, this class does not
 * provide perfect duplicate elimination.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class RdfConversionBuffer {

	List<QuantityValue> quantityValueQueue;
	List<Resource> quantityValueSubjectQueue;

	public RdfConversionBuffer() {
		this.quantityValueQueue = new ArrayList<QuantityValue>();
		this.quantityValueSubjectQueue = new ArrayList<Resource>();
	}

	/**
	 * Adds the given quantity value to the list of values that should still be
	 * serialized. The given RDF resource will be used as a subject.
	 * 
	 * @param quantitiyValue
	 *            the value to be serialized
	 * @param resource
	 *            the RDF resource that is used as a subject for serialization
	 */
	public void addQuantityValue(QuantityValue quantitiyValue, Resource resource) {
		this.quantityValueQueue.add(quantitiyValue);
		this.quantityValueSubjectQueue.add(resource);
	}

	public void writeValues(ValueRdfConverter valueRdfConverter)
			throws RDFHandlerException {
		Iterator<QuantityValue> valueIterator = this.quantityValueQueue
				.iterator();
		for (Resource resource : this.quantityValueSubjectQueue) {
			QuantityValue quantityValue = valueIterator.next();
			valueRdfConverter.writeQuantityValue(quantityValue, resource);
		}
		this.quantityValueSubjectQueue.clear();
		this.quantityValueQueue.clear();

	}
}
