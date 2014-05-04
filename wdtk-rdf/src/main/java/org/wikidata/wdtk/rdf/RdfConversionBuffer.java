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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.openrdf.model.Resource;
import org.openrdf.rio.RDFHandlerException;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
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

	/**
	 * Local value class for storing information about property restrictions.
	 * 
	 * @author Markus Kroetzsch
	 * 
	 */
	private class PropertyRestriction {

		final Resource subject;
		final String propertyUri;
		final String rangeUri;

		PropertyRestriction(Resource subject, String propertyUri,
				String rangeUri) {
			this.subject = subject;
			this.propertyUri = propertyUri;
			this.rangeUri = rangeUri;
		}
	}

	final List<QuantityValue> quantityValueQueue;
	final List<Resource> quantityValueSubjectQueue;
	final List<TimeValue> timeValueQueue;
	final List<Resource> timeValueSubjectQueue;
	final List<GlobeCoordinatesValue> coordinatesValueQueue;
	final List<Resource> coordinatesValueSubjectQueue;
	final List<PropertyIdValue> objectPropertyQueue;
	final List<PropertyIdValue> datatypePropertyQueue;
	final HashSet<PropertyIdValue> declaredProperties;
	final HashSet<Resource> declaredValues;
	final List<PropertyRestriction> someValuesQueue;
	final List<PropertyRestriction> noValuesQueue;
	final List<Reference> referenceQueue;
	final List<Resource> referenceSubjectQueue;
	final HashSet<Resource> declaredReferences;

	public RdfConversionBuffer() {
		this.quantityValueQueue = new ArrayList<QuantityValue>();
		this.quantityValueSubjectQueue = new ArrayList<Resource>();
		this.timeValueQueue = new ArrayList<TimeValue>();
		this.timeValueSubjectQueue = new ArrayList<Resource>();
		this.coordinatesValueQueue = new ArrayList<GlobeCoordinatesValue>();
		this.coordinatesValueSubjectQueue = new ArrayList<Resource>();
		this.objectPropertyQueue = new ArrayList<PropertyIdValue>();
		this.datatypePropertyQueue = new ArrayList<PropertyIdValue>();
		this.declaredProperties = new HashSet<PropertyIdValue>();
		this.declaredValues = new HashSet<Resource>();
		this.someValuesQueue = new ArrayList<PropertyRestriction>();
		this.noValuesQueue = new ArrayList<PropertyRestriction>();
		this.referenceQueue = new ArrayList<Reference>();
		this.referenceSubjectQueue = new ArrayList<Resource>();
		this.declaredReferences = new HashSet<Resource>();
	}

	/**
	 * Adds the given some-value restriction to the list of restrictions that
	 * should still be serialized. The given resource will be used as a subject.
	 * 
	 * @param subject
	 * @param propertyUri
	 * @param rangeUri
	 */
	public void addSomeValuesRestriction(Resource subject, String propertyUri,
			String rangeUri) {
		this.someValuesQueue.add(new PropertyRestriction(subject, propertyUri,
				rangeUri));
	}

	/**
	 * Adds the given no-value restriction to the list of restrictions that
	 * should still be serialized. The given resource will be used as a subject.
	 * 
	 * @param subject
	 * @param propertyUri
	 * @param rangeUri
	 */
	public void addNoValuesRestriction(Resource subject, String propertyUri,
			String rangeUri) {
		this.noValuesQueue.add(new PropertyRestriction(subject, propertyUri,
				rangeUri));
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

	/**
	 * Adds the given reference to the list of references that should still be
	 * serialized. The given RDF resource will be used as a subject.
	 * 
	 * @param reference
	 *            the reference to be serialized
	 * @param resource
	 *            the RDF resource that is used as a subject for serialization
	 */
	public void addReference(Reference reference, Resource resource) {
		this.referenceQueue.add(reference);
		this.referenceSubjectQueue.add(resource);
	}

	/**
	 * Adds the given property id value to the list of properties that should be
	 * declared as OWL object properties.
	 * 
	 * @param propertyIdValue
	 *            the property to declare
	 */
	public void addObjectProperty(PropertyIdValue propertyIdValue) {
		if (!this.declaredProperties.contains(propertyIdValue)) {
			this.objectPropertyQueue.add(propertyIdValue);
		}
	}

	/**
	 * Adds the given property id value to the list of properties that should be
	 * declared as OWL datatype properties.
	 * 
	 * @param propertyIdValue
	 *            the property to declare
	 */
	public void addDatatypeProperty(PropertyIdValue propertyIdValue) {
		if (!this.declaredProperties.contains(propertyIdValue)) {
			this.datatypePropertyQueue.add(propertyIdValue);
		}
	}

	/**
	 * Writes OWL declarations for properties that have been added recently.
	 * Declared properties are stored so that duplicate declarations are
	 * avoided.
	 * 
	 * @param rdfWriter
	 *            the writer to write the declarations to
	 * @throws RDFHandlerException
	 *             if there was a problem writing the declarations
	 */
	public void writePropertyDeclarations(RdfWriter rdfWriter)
			throws RDFHandlerException {
		for (PropertyIdValue propertyIdValue : this.objectPropertyQueue) {
			if (!this.declaredProperties.add(propertyIdValue)) {
				continue;
			}
			rdfWriter.writeTripleUriObject(Vocabulary.getPropertyUri(
					propertyIdValue, PropertyContext.STATEMENT),
					Vocabulary.RDF_TYPE, Vocabulary.OWL_OBJECT_PROPERTY);
			rdfWriter.writeTripleUriObject(Vocabulary.getPropertyUri(
					propertyIdValue, PropertyContext.VALUE),
					Vocabulary.RDF_TYPE, Vocabulary.OWL_OBJECT_PROPERTY);
			rdfWriter.writeTripleUriObject(Vocabulary.getPropertyUri(
					propertyIdValue, PropertyContext.QUALIFIER),
					Vocabulary.RDF_TYPE, Vocabulary.OWL_OBJECT_PROPERTY);
			rdfWriter.writeTripleUriObject(Vocabulary.getPropertyUri(
					propertyIdValue, PropertyContext.REFERENCE),
					Vocabulary.RDF_TYPE, Vocabulary.OWL_OBJECT_PROPERTY);
		}
		this.objectPropertyQueue.clear();

		for (PropertyIdValue propertyIdValue : this.datatypePropertyQueue) {
			if (!this.declaredProperties.add(propertyIdValue)) {
				continue;
			}
			rdfWriter.writeTripleUriObject(Vocabulary.getPropertyUri(
					propertyIdValue, PropertyContext.STATEMENT),
					Vocabulary.RDF_TYPE, Vocabulary.OWL_OBJECT_PROPERTY);
			rdfWriter.writeTripleUriObject(Vocabulary.getPropertyUri(
					propertyIdValue, PropertyContext.VALUE),
					Vocabulary.RDF_TYPE, Vocabulary.OWL_DATATYPE_PROPERTY);
			rdfWriter.writeTripleUriObject(Vocabulary.getPropertyUri(
					propertyIdValue, PropertyContext.QUALIFIER),
					Vocabulary.RDF_TYPE, Vocabulary.OWL_DATATYPE_PROPERTY);
			rdfWriter.writeTripleUriObject(Vocabulary.getPropertyUri(
					propertyIdValue, PropertyContext.REFERENCE),
					Vocabulary.RDF_TYPE, Vocabulary.OWL_DATATYPE_PROPERTY);
		}
		this.datatypePropertyQueue.clear();
	}

	/**
	 * Writes RDF for encoding complex values that have been added recently.
	 * Written values are stored so that duplicate definitions are avoided.
	 * 
	 * @param valueRdfConverter
	 *            the object to use for writing values
	 * @throws RDFHandlerException
	 *             if there was a problem writing the values
	 */
	public void writeValues(ValueRdfConverter valueRdfConverter)
			throws RDFHandlerException {
		Iterator<QuantityValue> quantitiyValueIterator = this.quantityValueQueue
				.iterator();
		for (Resource resource : this.quantityValueSubjectQueue) {
			if (!this.declaredValues.add(resource)) {
				continue;
			}
			QuantityValue quantityValue = quantitiyValueIterator.next();
			valueRdfConverter.writeQuantityValue(quantityValue, resource);
		}
		this.quantityValueSubjectQueue.clear();
		this.quantityValueQueue.clear();

		Iterator<TimeValue> timeValueIterator = this.timeValueQueue.iterator();
		for (Resource resource : this.timeValueSubjectQueue) {
			if (!this.declaredValues.add(resource)) {
				continue;
			}
			TimeValue timeValue = timeValueIterator.next();
			valueRdfConverter.writeTimeValue(timeValue, resource);
		}
		this.timeValueSubjectQueue.clear();
		this.timeValueQueue.clear();

		Iterator<GlobeCoordinatesValue> globeCoordinatesValueIterator = this.coordinatesValueQueue
				.iterator();
		for (Resource resource : this.coordinatesValueSubjectQueue) {
			if (!this.declaredValues.add(resource)) {
				continue;
			}
			GlobeCoordinatesValue globeCoordinatesValue = globeCoordinatesValueIterator
					.next();
			valueRdfConverter.writeGlobeCoordinatesValue(globeCoordinatesValue,
					resource);
		}
		this.coordinatesValueSubjectQueue.clear();
		this.coordinatesValueQueue.clear();
	}

	/**
	 * Writes OWL property restrictions that have been added recently.
	 * 
	 * @param snakRdfConverter
	 *            the object to use for writing restrictions
	 * @throws RDFHandlerException
	 *             if there was a problem writing the restrictions
	 */
	public void writePropertyRestrictions(SnakRdfConverter snakRdfConverter)
			throws RDFHandlerException {
		for (PropertyRestriction pr : this.someValuesQueue) {
			snakRdfConverter.writeSomeValueRestriction(pr.propertyUri,
					pr.rangeUri, pr.subject);
		}
		this.someValuesQueue.clear();

		for (PropertyRestriction pr : this.noValuesQueue) {
			snakRdfConverter.writeNoValueRestriction(pr.propertyUri,
					pr.rangeUri, pr.subject);
		}
		this.noValuesQueue.clear();
	}

	/**
	 * Writes references that have been added recently.
	 * 
	 * @param rdfConverter
	 *            the object to use for writing references
	 * @throws RDFHandlerException
	 *             if there was a problem writing the restrictions
	 */
	public void writeReferences(RdfConverter rdfConverter)
			throws RDFHandlerException {
		Iterator<Reference> referenceIterator = this.referenceQueue.iterator();
		for (Resource resource : this.referenceSubjectQueue) {
			if (!this.declaredReferences.add(resource)) {
				continue;
			}
			Reference reference = referenceIterator.next();
			rdfConverter.writeReference(reference, resource);
		}
		this.referenceSubjectQueue.clear();
		this.referenceQueue.clear();
	}

}
