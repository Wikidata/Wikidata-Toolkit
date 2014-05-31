package org.wikidata.wdtk.dumpfiles.renderer.format;

/*
 * #%L
 * Wikidata Toolkit Dump File Handling
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

import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openrdf.model.BNode;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.rdf.RdfWriter;

public class RdfRendererFormat implements RendererFormat {

	static final ValueFactory factory = ValueFactoryImpl.getInstance();

	final Set<Resource> declaredEntities = new HashSet<Resource>();

	final RdfWriter rdfWriter;

	public RdfRendererFormat(OutputStream outputStream) {
		this.rdfWriter = new RdfWriter(RDFFormat.RDFXML, outputStream);
	}

	@Override
	public URI a_s(PropertyIdValue property) {
		return factory.createURI(property.getIri());
	}

	@Override
	public URI a_v(PropertyIdValue property) {
		return factory.createURI(property.getIri());
	}

	@Override
	public URI aItem(ItemIdValue item) {
		return factory.createURI(item.getIri());
	}

	@Override
	public URI aRp(PropertyIdValue property) {
		return factory.createURI(property.getIri());
	}

	@Override
	public URI owlThing() {
		return RdfUriConstant.OWL_THING;
	}

	@Override
	public URI xsdDateTime() {
		return RdfUriConstant.XSD_DATE_TIME;
	}

	@Override
	public URI xsdDecimal() {
		return RdfUriConstant.XSD_DECIMAL;
	}

	@Override
	public URI xsdMaxInclusive() {
		return RdfUriConstant.XSD_MAX_INCLUSIVE;
	}

	@Override
	public URI xsdMinInclusive() {
		return RdfUriConstant.XSD_MIN_INCLUSIVE;
	}

	@Override
	public URI xsdPattern() {
		return RdfUriConstant.XSD_PATTERN;
	}

	@Override
	public URI xsdString() {
		return RdfUriConstant.XSD_STRING;
	}

	@Override
	public BNode getDataIntersectionOf(Resource dataRange0, Resource dataRange1) {
		// FIXME
		BNode ret = factory.createBNode();

		return ret;
	}

	@Override
	public BNode getDataSomeValuesFrom(Resource datatypeProperty,
			Resource dataRange) {
		BNode ret = factory.createBNode();
		try {
			addDeclarationDatatypeProperty(datatypeProperty);
			addDeclarationDatatype(dataRange);

			this.rdfWriter.writeTripleValueObject(ret, RdfUriConstant.RDF_TYPE,
					RdfUriConstant.OWL_RESTRICTION);
			this.rdfWriter.writeTripleValueObject(ret,
					RdfUriConstant.OWL_ON_PROPERTY, datatypeProperty);
			this.rdfWriter.writeTripleValueObject(ret,
					RdfUriConstant.OWL_SOME_VALUES_FROM, dataRange);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return ret;
	}

	@Override
	public BNode getDatatype(Resource arg) {
		// FIXME
		BNode ret = factory.createBNode();

		return ret;
	}

	@Override
	public BNode getDatatypeRestriction(Resource dataType, Resource facet,
			Resource value) {
		// FIXME
		BNode ret = factory.createBNode();

		return ret;
	}

	@Override
	public BNode getLiteral(Resource value, Resource type) {
		// FIXME

		return factory.createBNode();
	}

	@Override
	public BNode getObjectComplementOf(Resource clss) {
		BNode ret = factory.createBNode();
		try {
			addDeclarationClass(clss);

			this.rdfWriter.writeTripleValueObject(ret,
					RdfUriConstant.OWL_COMPLEMENT_OF, clss);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return ret;
	}

	@Override
	public BNode getObjectExactCardinality(int cardinality,
			Resource objectProperty) {
		BNode ret = factory.createBNode();
		try {
			addDeclarationObjectProperty(objectProperty);

			this.rdfWriter.writeTripleValueObject(ret, RdfUriConstant.RDF_TYPE,
					RdfUriConstant.OWL_RESTRICTION);
			this.rdfWriter.writeTripleValueObject(ret,
					RdfUriConstant.OWL_ON_PROPERTY, objectProperty);

			this.rdfWriter.writeTripleValueObject(ret,
					RdfUriConstant.OWL_ON_CLASS, RdfUriConstant.OWL_THING);
			// this is not necessary

			this.rdfWriter.writeTripleIntegerObject(ret,
					RdfUriConstant.OWL_QUALIFIED_CARDINALITY, cardinality);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return ret;
	}

	@Override
	public BNode getObjectOneOf(Resource individual) {
		// FIXME
		BNode ret = factory.createBNode();

		return ret;
	}

	@Override
	public BNode getObjectOneOf(List<Resource> listOfIndividuals) {
		// FIXME
		BNode ret = factory.createBNode();

		return ret;
	}

	@Override
	public BNode getObjectSomeValuesFrom(Resource property, Resource clss) {
		BNode ret = factory.createBNode();
		try {
			addDeclarationObjectProperty(property);
			addDeclarationClass(clss);

			this.rdfWriter.writeTripleValueObject(ret, RdfUriConstant.RDF_TYPE,
					RdfUriConstant.OWL_RESTRICTION);
			this.rdfWriter.writeTripleValueObject(ret,
					RdfUriConstant.OWL_ON_PROPERTY, property);
			this.rdfWriter.writeTripleValueObject(ret,
					RdfUriConstant.OWL_SOME_VALUES_FROM, clss);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return ret;
	}

	@Override
	public BNode getObjectUnionOf(Resource class0, Resource class1) {
		BNode ret = factory.createBNode();

		try {
			addDeclarationClass(class0);
			addDeclarationClass(class1);

			BNode bnode1 = factory.createBNode();
			BNode bnode2 = factory.createBNode();

			this.rdfWriter.writeTripleValueObject(ret, RdfUriConstant.RDF_TYPE,
					RdfUriConstant.OWL_CLASS);
			this.rdfWriter.writeTripleValueObject(ret,
					RdfUriConstant.OWL_UNION_OF, bnode1);
			this.rdfWriter.writeTripleValueObject(bnode1,
					RdfUriConstant.RDF_FIRST, class0);
			this.rdfWriter.writeTripleValueObject(bnode1,
					RdfUriConstant.RDF_REST, bnode2);
			this.rdfWriter.writeTripleValueObject(bnode2,
					RdfUriConstant.RDF_FIRST, class1);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return ret;
	}

	@Override
	public boolean addAnnotationAssertionComment(Resource subject, String value) {
		try {
			this.rdfWriter.writeTripleStringObject(subject,
					RdfUriConstant.RDFS_COMMENT, value);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean addDataPropertyRange(Resource dataProperty,
			Resource dataRange) {
		try {
			addDeclarationDatatypeProperty(dataProperty);
			addDeclarationDatatype(dataRange);

			this.rdfWriter.writeTripleValueObject(dataProperty,
					RdfUriConstant.RDFS_RANGE, dataRange);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean addDatatypeDefinition(Resource datatype, Resource dataRange) {
		// FIXME

		return false;
	}

	@Override
	public boolean addDeclarationAnnotationProperty(Resource entity) {
		if (this.declaredEntities.contains(entity)) {
			return false;
		}
		try {
			this.rdfWriter.writeTripleValueObject(entity,
					RdfUriConstant.RDF_TYPE,
					RdfUriConstant.OWL_ANNOTATION_PROPERTY);
			this.declaredEntities.add(entity);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean addDeclarationClass(Resource entity) {
		if (this.declaredEntities.contains(entity)) {
			return false;
		}
		try {
			this.rdfWriter.writeTripleValueObject(entity,
					RdfUriConstant.RDF_TYPE, RdfUriConstant.OWL_CLASS);
			this.declaredEntities.add(entity);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean addDeclarationDatatype(Resource entity) {
		if (this.declaredEntities.contains(entity)) {
			return false;
		}
		try {
			this.rdfWriter.writeTripleValueObject(entity,
					RdfUriConstant.RDF_TYPE, RdfUriConstant.RDFS_DATATYPE);
			this.declaredEntities.add(entity);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean addDeclarationDatatypeProperty(Resource entity) {
		if (this.declaredEntities.contains(entity)) {
			return false;
		}
		try {
			this.rdfWriter.writeTripleValueObject(entity,
					RdfUriConstant.RDF_TYPE,
					RdfUriConstant.OWL_DATATYPE_PROPERTY);
			this.declaredEntities.add(entity);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean addDeclarationNamedIndividual(Resource entity) {
		if (this.declaredEntities.contains(entity)) {
			return false;
		}
		try {
			this.rdfWriter.writeTripleValueObject(entity,
					RdfUriConstant.RDF_TYPE,
					RdfUriConstant.OWL_NAMED_INDIVIDUAL);
			this.declaredEntities.add(entity);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean addDeclarationObjectProperty(Resource entity) {
		if (this.declaredEntities.contains(entity)) {
			return false;
		}
		try {
			this.rdfWriter
					.writeTripleValueObject(entity, RdfUriConstant.RDF_TYPE,
							RdfUriConstant.OWL_OBJECT_PROPERTY);
			this.declaredEntities.add(entity);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean addDisjointClasses(Resource class0, Resource class1) {
		// FIXME

		return false;
	}

	@Override
	public boolean addFunctionalObjectProperty(Resource objectProperty) {
		try {
			addDeclarationObjectProperty(objectProperty);

			this.rdfWriter.writeTripleValueObject(objectProperty,
					RdfUriConstant.RDF_TYPE,
					RdfUriConstant.OWL_FUNCTIONAL_PROPERTY);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean addHasKey(Resource clss, Resource objectProperty,
			Resource dataProperty) {
		// FIXME

		return false;
	}

	@Override
	public boolean addInverseFunctionalObjectProperty(Resource objectProperty) {
		try {
			addDeclarationObjectProperty(objectProperty);

			this.rdfWriter.writeTripleValueObject(objectProperty,
					RdfUriConstant.RDF_TYPE,
					RdfUriConstant.OWL_INVERSE_FUNCTIONAL_PROPERTY);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean addObjectPropertyDomain(Resource objectProperty,
			Resource clss) {
		try {
			addDeclarationObjectProperty(objectProperty);
			addDeclarationClass(clss);

			this.rdfWriter.writeTripleValueObject(objectProperty,
					RdfUriConstant.RDFS_DOMAIN, clss);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean addObjectPropertyRange(Resource objectProperty, Resource clss) {
		try {
			addDeclarationObjectProperty(objectProperty);
			addDeclarationClass(clss);

			this.rdfWriter.writeTripleValueObject(objectProperty,
					RdfUriConstant.RDFS_RANGE, clss);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean addSubClassOf(Resource subClass, Resource superClass) {
		try {
			addDeclarationClass(subClass);
			addDeclarationClass(superClass);

			this.rdfWriter.writeTripleValueObject(subClass,
					RdfUriConstant.RDFS_SUB_CLASS_OF, superClass);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public void start() {
		try {
			this.rdfWriter.start();
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void finish() {
		try {
			this.rdfWriter.finish();
			this.declaredEntities.clear();
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean addClassAssertion(Resource clss, Resource individual) {
		try {
			addDeclarationClass(clss);
			addDeclarationNamedIndividual(individual);

			this.rdfWriter.writeTripleValueObject(individual,
					RdfUriConstant.RDF_TYPE, clss);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	public boolean addClassNegativeAssertion(Resource clss, Resource individual) {
		try {
			addDeclarationClass(clss);
			addDeclarationNamedIndividual(individual);

			BNode bnode = factory.createBNode();

			addDeclarationClass(bnode);

			this.rdfWriter.writeTripleValueObject(bnode,
					RdfUriConstant.OWL_COMPLEMENT_OF, clss);

			this.rdfWriter.writeTripleValueObject(individual,
					RdfUriConstant.RDF_TYPE, bnode);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

}
