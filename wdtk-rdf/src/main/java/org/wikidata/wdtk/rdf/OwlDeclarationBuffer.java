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
import java.util.List;
import java.util.Set;

import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;

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
public class OwlDeclarationBuffer {

	final List<PropertyIdValue> objectPropertyQueue = new ArrayList<>();
	final List<PropertyIdValue> datatypePropertyQueue = new ArrayList<>();
	final List<IRI> objectPropertyUriQueue = new ArrayList<>();
	final List<IRI> datatypePropertyUriQueue = new ArrayList<>();
	final Set<PropertyIdValue> declaredProperties = new HashSet<>();
	final Set<IRI> declaredPropertyUris = new HashSet<>();
	final List<EntityIdValue> classEntityQueue = new ArrayList<>();
	final Set<EntityIdValue> declaredClassEntities = new HashSet<>();

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
	 * Adds the given property URI string to the list of property URIs that
	 * should be declared as OWL object properties.
	 *
	 * @param propertyUri
	 *            the property to declare
	 */
	public void addObjectProperty(IRI propertyUri) {
		if (!this.declaredPropertyUris.contains(propertyUri)) {
			this.objectPropertyUriQueue.add(propertyUri);
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
	 * Adds the given property URI string to the list of property URIs that
	 * should be declared as OWL datatype properties.
	 *
	 * @param propertyUri
	 *            the property to declare
	 */
	public void addDatatypeProperty(IRI propertyUri) {
		if (!this.declaredPropertyUris.contains(propertyUri)) {
			this.datatypePropertyUriQueue.add(propertyUri);
		}
	}

	/**
	 * Adds the given entity id value to the list of entities that should be
	 * declared as OWL classes.
	 *
	 * @param entityIdValue
	 *            the property to declare
	 */
	public void addClass(EntityIdValue entityIdValue) {
		if (!this.declaredClassEntities.contains(entityIdValue)) {
			this.classEntityQueue.add(entityIdValue);
		}
	}

	/**
	 * Writes OWL declarations for properties that have been added recently.
	 * Declared properties are stored so that duplicate declarations are
	 * avoided.
	 *
	 * @param rdfWriter
	 *            the writer to write the declarations to
	 * @param fullStatements
	 *            if true, then properties need to export full statements (with
	 *            qualifiers and references) will be declared
	 * @param simpleClaims
	 *            if true, then properties to export simple claims (flat
	 *            triples) will be declared
	 * @throws RDFHandlerException
	 *             if there was a problem writing the declarations
	 */
	public void writePropertyDeclarations(RdfWriter rdfWriter,
			boolean fullStatements, boolean simpleClaims)
			throws RDFHandlerException {
		boolean anyStatements = fullStatements || simpleClaims;
		for (PropertyIdValue propertyIdValue : this.objectPropertyQueue) {
			if (!this.declaredProperties.add(propertyIdValue)) {
				continue;
			}
			if (anyStatements) {
				writeNoValueRestriction(rdfWriter, propertyIdValue.getIri(),
						Vocabulary.OWL_THING, Vocabulary.getPropertyUri(
								propertyIdValue, PropertyContext.NO_VALUE));
				writeNoValueRestriction(rdfWriter, propertyIdValue.getIri(),
						Vocabulary.OWL_THING, Vocabulary.getPropertyUri(
								propertyIdValue,
								PropertyContext.NO_QUALIFIER_VALUE));
			}
			if (fullStatements) {
				rdfWriter.writeTripleValueObject(Vocabulary.getPropertyUri(
						propertyIdValue, PropertyContext.STATEMENT),
						RdfWriter.RDF_TYPE, RdfWriter.OWL_OBJECT_PROPERTY);
				rdfWriter.writeTripleValueObject(Vocabulary.getPropertyUri(
						propertyIdValue, PropertyContext.VALUE_SIMPLE),
						RdfWriter.RDF_TYPE, RdfWriter.OWL_OBJECT_PROPERTY);
				rdfWriter.writeTripleValueObject(Vocabulary.getPropertyUri(
						propertyIdValue, PropertyContext.VALUE),
						RdfWriter.RDF_TYPE, RdfWriter.OWL_OBJECT_PROPERTY);
				rdfWriter.writeTripleValueObject(Vocabulary.getPropertyUri(
						propertyIdValue, PropertyContext.QUALIFIER),
						RdfWriter.RDF_TYPE, RdfWriter.OWL_OBJECT_PROPERTY);
				rdfWriter.writeTripleValueObject(Vocabulary.getPropertyUri(
						propertyIdValue, PropertyContext.REFERENCE),
						RdfWriter.RDF_TYPE, RdfWriter.OWL_OBJECT_PROPERTY);
				rdfWriter.writeTripleValueObject(Vocabulary.getPropertyUri(
						propertyIdValue, PropertyContext.QUALIFIER_SIMPLE),
						RdfWriter.RDF_TYPE, RdfWriter.OWL_OBJECT_PROPERTY);
				rdfWriter.writeTripleValueObject(Vocabulary.getPropertyUri(
						propertyIdValue, PropertyContext.REFERENCE_SIMPLE),
						RdfWriter.RDF_TYPE, RdfWriter.OWL_OBJECT_PROPERTY);
			}
			if (simpleClaims) {
				rdfWriter.writeTripleValueObject(Vocabulary.getPropertyUri(
						propertyIdValue, PropertyContext.DIRECT),
						RdfWriter.RDF_TYPE, RdfWriter.OWL_OBJECT_PROPERTY);
			}
		}
		this.objectPropertyQueue.clear();

		for (PropertyIdValue propertyIdValue : this.datatypePropertyQueue) {
			if (!this.declaredProperties.add(propertyIdValue)) {
				continue;
			}
			if (anyStatements) {
				writeNoValueRestriction(rdfWriter, propertyIdValue.getIri(),
						Vocabulary.XSD_STRING, Vocabulary.getPropertyUri(
								propertyIdValue, PropertyContext.NO_VALUE));
				writeNoValueRestriction(rdfWriter, propertyIdValue.getIri(),
						Vocabulary.XSD_STRING, Vocabulary.getPropertyUri(
								propertyIdValue,
								PropertyContext.NO_QUALIFIER_VALUE));
			}
			if (fullStatements) {
				rdfWriter.writeTripleValueObject(Vocabulary.getPropertyUri(
						propertyIdValue, PropertyContext.STATEMENT),
						RdfWriter.RDF_TYPE, RdfWriter.OWL_OBJECT_PROPERTY);
				rdfWriter.writeTripleValueObject(Vocabulary.getPropertyUri(
						propertyIdValue, PropertyContext.VALUE),
						RdfWriter.RDF_TYPE, RdfWriter.OWL_DATATYPE_PROPERTY);
				rdfWriter.writeTripleValueObject(Vocabulary.getPropertyUri(
						propertyIdValue, PropertyContext.QUALIFIER),
						RdfWriter.RDF_TYPE, RdfWriter.OWL_DATATYPE_PROPERTY);
				rdfWriter.writeTripleValueObject(Vocabulary.getPropertyUri(
						propertyIdValue, PropertyContext.REFERENCE),
						RdfWriter.RDF_TYPE, RdfWriter.OWL_DATATYPE_PROPERTY);
				rdfWriter.writeTripleValueObject(Vocabulary.getPropertyUri(
						propertyIdValue, PropertyContext.VALUE_SIMPLE),
						RdfWriter.RDF_TYPE, RdfWriter.OWL_DATATYPE_PROPERTY);
				rdfWriter.writeTripleValueObject(Vocabulary.getPropertyUri(
						propertyIdValue, PropertyContext.QUALIFIER_SIMPLE),
						RdfWriter.RDF_TYPE, RdfWriter.OWL_DATATYPE_PROPERTY);
				rdfWriter.writeTripleValueObject(Vocabulary.getPropertyUri(
						propertyIdValue, PropertyContext.REFERENCE_SIMPLE),
						RdfWriter.RDF_TYPE, RdfWriter.OWL_DATATYPE_PROPERTY);

			}
			if (simpleClaims) {
				rdfWriter.writeTripleValueObject(Vocabulary.getPropertyUri(
						propertyIdValue, PropertyContext.DIRECT),
						RdfWriter.RDF_TYPE, RdfWriter.OWL_DATATYPE_PROPERTY);
			}

		}
		this.datatypePropertyQueue.clear();

		for (IRI propertyUri : this.objectPropertyUriQueue) {
			if (!this.declaredPropertyUris.add(propertyUri)) {
				continue;
			}
			rdfWriter.writeTripleValueObject(propertyUri, RdfWriter.RDF_TYPE,
					RdfWriter.OWL_OBJECT_PROPERTY);
		}
		this.objectPropertyUriQueue.clear();

		for (IRI propertyUri : this.datatypePropertyUriQueue) {
			if (!this.declaredPropertyUris.add(propertyUri)) {
				continue;
			}
			rdfWriter.writeTripleValueObject(propertyUri, RdfWriter.RDF_TYPE,
					RdfWriter.OWL_DATATYPE_PROPERTY);
		}
		this.datatypePropertyUriQueue.clear();

	}

	/**
	 * Writes no-value restriction.
	 *
	 * @param rdfWriter
	 *            the writer to write the restrictions to
	 * @param propertyUri
	 *            URI of the property to which the restriction applies
	 * @param rangeUri
	 *            URI of the class or datatype to which the restriction applies
	 * @param subject
	 *            node representing the restriction
	 * @throws RDFHandlerException
	 *             if there was a problem writing the RDF triples
	 */
	void writeNoValueRestriction(RdfWriter rdfWriter, String propertyUri,
			String rangeUri, String subject) throws RDFHandlerException {

		Resource bnodeSome = rdfWriter.getFreshBNode();
		rdfWriter.writeTripleValueObject(subject, RdfWriter.RDF_TYPE,
				RdfWriter.OWL_CLASS);
		rdfWriter.writeTripleValueObject(subject, RdfWriter.OWL_COMPLEMENT_OF,
				bnodeSome);
		rdfWriter.writeTripleValueObject(bnodeSome, RdfWriter.RDF_TYPE,
				RdfWriter.OWL_RESTRICTION);
		rdfWriter.writeTripleUriObject(bnodeSome, RdfWriter.OWL_ON_PROPERTY,
				propertyUri);
		rdfWriter.writeTripleUriObject(bnodeSome,
				RdfWriter.OWL_SOME_VALUES_FROM, rangeUri);
	}

}
