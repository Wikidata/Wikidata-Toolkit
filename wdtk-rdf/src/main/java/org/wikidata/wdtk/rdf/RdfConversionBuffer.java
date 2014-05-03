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
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;

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
	List<TimeValue> timeValueQueue;
	List<Resource> timeValueSubjectQueue;
	List<GlobeCoordinatesValue> coordinatesValueQueue;
	List<Resource> coordinatesValueSubjectQueue;

	public RdfConversionBuffer() {
		this.quantityValueQueue = new ArrayList<QuantityValue>();
		this.quantityValueSubjectQueue = new ArrayList<Resource>();
		this.timeValueQueue = new ArrayList<TimeValue>();
		this.timeValueSubjectQueue = new ArrayList<Resource>();
		this.coordinatesValueQueue = new ArrayList<GlobeCoordinatesValue>();
		this.coordinatesValueSubjectQueue = new ArrayList<Resource>();
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

	/**
	 * Adds the given time value to the list of values that should still be
	 * serialized. The given RDF resource will be used as a subject.
	 * 
	 * @param timeValue
	 *            the value to be serialized
	 * @param resource
	 *            the RDF resource that is used as a subject for serialization
	 */
	public void addTimeValue(TimeValue timeValue, Resource resource) {
		this.timeValueQueue.add(timeValue);
		this.timeValueSubjectQueue.add(resource);
	}

	/**
	 * Adds the given globe coordinates value to the list of values that should
	 * still be serialized. The given RDF resource will be used as a subject.
	 * 
	 * @param globeCoordinatesValue
	 *            the value to be serialized
	 * @param resource
	 *            the RDF resource that is used as a subject for serialization
	 */
	public void addGlobeCoordinatesValue(
			GlobeCoordinatesValue globeCoordinatesValue, Resource resource) {
		this.coordinatesValueQueue.add(globeCoordinatesValue);
		this.coordinatesValueSubjectQueue.add(resource);
	}

	public void writeValues(ValueRdfConverter valueRdfConverter)
			throws RDFHandlerException {
		Iterator<QuantityValue> quantitiyValueIterator = this.quantityValueQueue
				.iterator();
		for (Resource resource : this.quantityValueSubjectQueue) {
			QuantityValue quantityValue = quantitiyValueIterator.next();
			valueRdfConverter.writeQuantityValue(quantityValue, resource);
		}
		this.quantityValueSubjectQueue.clear();
		this.quantityValueQueue.clear();

		Iterator<TimeValue> timeValueIterator = this.timeValueQueue.iterator();
		for (Resource resource : this.timeValueSubjectQueue) {
			TimeValue timeValue = timeValueIterator.next();
			valueRdfConverter.writeTimeValue(timeValue, resource);
		}
		this.timeValueSubjectQueue.clear();
		this.timeValueQueue.clear();

		Iterator<GlobeCoordinatesValue> globeCoordinatesValueIterator = this.coordinatesValueQueue
				.iterator();
		for (Resource resource : this.coordinatesValueSubjectQueue) {
			GlobeCoordinatesValue globeCoordinatesValue = globeCoordinatesValueIterator
					.next();
			valueRdfConverter.writeGlobeCoordinatesValue(globeCoordinatesValue,
					resource);
		}
		this.coordinatesValueSubjectQueue.clear();
		this.coordinatesValueQueue.clear();
	}
}
