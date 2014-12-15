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

import org.openrdf.model.URI;
import org.openrdf.rio.RDFHandlerException;
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

	final List<PropertyIdValue> objectPropertyQueue;
	final List<PropertyIdValue> datatypePropertyQueue;
	final List<URI> objectPropertyUriQueue;
	final List<URI> datatypePropertyUriQueue;
	final HashSet<PropertyIdValue> declaredProperties;
	final HashSet<URI> declaredPropertyUris;
	final List<EntityIdValue> classEntityQueue;
	final HashSet<EntityIdValue> declaredClassEntities;

	public OwlDeclarationBuffer() {
		this.objectPropertyQueue = new ArrayList<PropertyIdValue>();
		this.datatypePropertyQueue = new ArrayList<PropertyIdValue>();
		this.objectPropertyUriQueue = new ArrayList<URI>();
		this.datatypePropertyUriQueue = new ArrayList<URI>();
		this.declaredProperties = new HashSet<PropertyIdValue>();
		this.declaredPropertyUris = new HashSet<URI>();
		this.classEntityQueue = new ArrayList<EntityIdValue>();
		this.declaredClassEntities = new HashSet<EntityIdValue>();
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
	 * Adds the given property URI string to the list of property URIs that
	 * should be declared as OWL object properties.
	 *
	 * @param propertyUri
	 *            the property to declare
	 */
	public void addObjectProperty(URI propertyUri) {
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
	public void addDatatypeProperty(URI propertyUri) {
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
		for (PropertyIdValue propertyIdValue : this.objectPropertyQueue) {
			if (!this.declaredProperties.add(propertyIdValue)) {
				continue;
			}
			if (fullStatements) {
				rdfWriter.writeTripleValueObject(Vocabulary.getPropertyUri(
						propertyIdValue, PropertyContext.STATEMENT),
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
			}
			if (simpleClaims) {
				rdfWriter.writeTripleValueObject(Vocabulary.getPropertyUri(
						propertyIdValue, PropertyContext.SIMPLE_CLAIM),
						RdfWriter.RDF_TYPE, RdfWriter.OWL_OBJECT_PROPERTY);
			}
		}
		this.objectPropertyQueue.clear();

		for (PropertyIdValue propertyIdValue : this.datatypePropertyQueue) {
			if (!this.declaredProperties.add(propertyIdValue)) {
				continue;
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
			}
			if (simpleClaims) {
				rdfWriter.writeTripleValueObject(Vocabulary.getPropertyUri(
						propertyIdValue, PropertyContext.SIMPLE_CLAIM),
						RdfWriter.RDF_TYPE, RdfWriter.OWL_DATATYPE_PROPERTY);
			}
		}
		this.datatypePropertyQueue.clear();

		for (URI propertyUri : this.objectPropertyUriQueue) {
			if (!this.declaredPropertyUris.add(propertyUri)) {
				continue;
			}
			rdfWriter.writeTripleValueObject(propertyUri, RdfWriter.RDF_TYPE,
					RdfWriter.OWL_OBJECT_PROPERTY);
		}
		this.objectPropertyUriQueue.clear();

		for (URI propertyUri : this.datatypePropertyUriQueue) {
			if (!this.declaredPropertyUris.add(propertyUri)) {
				continue;
			}
			rdfWriter.writeTripleValueObject(propertyUri, RdfWriter.RDF_TYPE,
					RdfWriter.OWL_DATATYPE_PROPERTY);
		}
		this.datatypePropertyUriQueue.clear();

	}

	/**
	 * Writes OWL declarations for classes that have been added recently.
	 * Declared classes are stored so that duplicate declarations are avoided.
	 *
	 * @param rdfWriter
	 *            the writer to write the declarations to
	 * @throws RDFHandlerException
	 *             if there was a problem writing the declarations
	 */
	public void writeClassDeclarations(RdfWriter rdfWriter)
			throws RDFHandlerException {
		for (EntityIdValue entityIdValue : this.classEntityQueue) {
			if (!this.declaredClassEntities.add(entityIdValue)) {
				continue;
			}
			rdfWriter.writeTripleValueObject(entityIdValue.getIri(),
					RdfWriter.RDF_TYPE, RdfWriter.OWL_CLASS);
		}
		this.classEntityQueue.clear();
	}

}
