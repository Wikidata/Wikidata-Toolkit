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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openrdf.model.BNode;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.rdf.PropertyContext;
import org.wikidata.wdtk.rdf.RdfWriter;
import org.wikidata.wdtk.rdf.Vocabulary;

public class RdfRendererFormat implements RendererFormat {

	final Set<Resource> declaredEntities = new HashSet<Resource>();

	final RdfWriter rdfWriter;

	public RdfRendererFormat(OutputStream outputStream) {
		this.rdfWriter = new RdfWriter(RDFFormat.RDFXML, outputStream);
	}

	@Override
	public URI getItem(ItemIdValue item) {
		return this.rdfWriter.getUri(item.getIri());
	}

	@Override
	public URI getProperty(PropertyIdValue property) {
		return this.rdfWriter.getUri(property.getIri());
	}

	@Override
	public URI getPs(PropertyIdValue property) {
		return this.rdfWriter.getUri(Vocabulary.getPropertyUri(property,
				PropertyContext.STATEMENT));
	}

	@Override
	public URI getPv(PropertyIdValue property) {
		return this.rdfWriter.getUri(Vocabulary.getPropertyUri(property,
				PropertyContext.VALUE));
	}

	@Override
	public URI getRp(PropertyIdValue property) {
		return this.rdfWriter.getUri(property.getIri() + RdfStringConstant.AUX);
	}

	@Override
	public URI rdfsComment() {
		return RdfUriConstant.RDFS_COMMENT;
	}

	@Override
	public URI wbTimeValue() {
		return RdfUriConstant.WB_TIME_VALUE;
	}

	@Override
	public URI wbQuantityValue() {
		return RdfUriConstant.WB_QUANTITY_VALUE;
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
		BNode ret = this.rdfWriter.getFreshBNode();

		try {
			BNode bnode1 = this.rdfWriter.getFreshBNode();
			BNode bnode2 = this.rdfWriter.getFreshBNode();

			this.rdfWriter.writeTripleValueObject(ret, RdfUriConstant.RDF_TYPE,
					RdfUriConstant.RDFS_DATATYPE);
			this.rdfWriter.writeTripleValueObject(ret,
					RdfUriConstant.OWL_INTERSECTION_OF, bnode1);
			this.rdfWriter.writeTripleValueObject(bnode1,
					RdfUriConstant.RDF_FIRST, dataRange0);
			this.rdfWriter.writeTripleValueObject(bnode1,
					RdfUriConstant.RDF_REST, bnode2);
			this.rdfWriter.writeTripleValueObject(bnode2,
					RdfUriConstant.RDF_FIRST, dataRange1);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return ret;
	}

	@Override
	public BNode getDataSomeValuesFrom(URI datatypePropertyExpression,
			Resource dataRange) {
		BNode ret = this.rdfWriter.getFreshBNode();
		try {
			// addDeclarationDatatypeProperty(datatypePropertyExpression);
			// addDeclarationDatatype(dataRange);

			this.rdfWriter.writeTripleValueObject(ret, RdfUriConstant.RDF_TYPE,
					RdfUriConstant.OWL_RESTRICTION);
			this.rdfWriter.writeTripleValueObject(ret,
					RdfUriConstant.OWL_ON_PROPERTY, datatypePropertyExpression);
			this.rdfWriter.writeTripleValueObject(ret,
					RdfUriConstant.OWL_SOME_VALUES_FROM, dataRange);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return ret;
	}

	@Override
	public BNode getDatatypeRestriction(URI datatype, URI constrainingFacet,
			Resource restrictionValue) {
		BNode ret = this.rdfWriter.getFreshBNode();
		try {
			// addDeclarationDatatype(datatype);

			BNode bnode1 = this.rdfWriter.getFreshBNode();
			BNode bnode2 = this.rdfWriter.getFreshBNode();

			this.rdfWriter.writeTripleValueObject(ret,
					RdfUriConstant.OWL_ON_DATATYPE, datatype);
			this.rdfWriter.writeTripleValueObject(ret,
					RdfUriConstant.OWL_WITH_RESTRICTIONS, bnode1);
			this.rdfWriter.writeTripleValueObject(bnode1,
					RdfUriConstant.RDF_FIRST, bnode2);
			this.rdfWriter.writeTripleStringObject(bnode2, constrainingFacet,
					restrictionValue.stringValue());
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}

		return ret;
	}

	@Override
	public BNode getObjectComplementOf(Resource classExpression) {
		BNode ret = this.rdfWriter.getFreshBNode();
		try {
			// addDeclarationClass(classExpression);

			this.rdfWriter.writeTripleValueObject(ret,
					RdfUriConstant.OWL_COMPLEMENT_OF, classExpression);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return ret;
	}

	@Override
	public BNode getObjectExactCardinality(int nonNegativeInteger,
			Resource objectPropertyExpression) {
		BNode ret = this.rdfWriter.getFreshBNode();
		try {
			// addDeclarationObjectProperty(objectPropertyExpression);

			this.rdfWriter.writeTripleValueObject(ret, RdfUriConstant.RDF_TYPE,
					RdfUriConstant.OWL_RESTRICTION);
			this.rdfWriter.writeTripleValueObject(ret,
					RdfUriConstant.OWL_ON_PROPERTY, objectPropertyExpression);

			this.rdfWriter.writeTripleValueObject(ret,
					RdfUriConstant.OWL_ON_CLASS, RdfUriConstant.OWL_THING);
			// this is not necessary

			this.rdfWriter.writeTripleIntegerObject(ret,
					RdfUriConstant.OWL_QUALIFIED_CARDINALITY,
					nonNegativeInteger);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return ret;
	}

	@Override
	public BNode getObjectOneOf(Resource individual) {
		BNode ret = this.rdfWriter.getFreshBNode();

		try {
			BNode bnode1 = this.rdfWriter.getFreshBNode();

			this.rdfWriter.writeTripleValueObject(ret, RdfUriConstant.RDF_TYPE,
					RdfUriConstant.OWL_CLASS);
			this.rdfWriter.writeTripleValueObject(ret,
					RdfUriConstant.OWL_ONE_OF, bnode1);
			this.rdfWriter.writeTripleValueObject(bnode1,
					RdfUriConstant.RDF_FIRST, individual);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return ret;
	}

	@Override
	public BNode getObjectOneOf(List<Resource> listOfIndividuals) {
		BNode ret = this.rdfWriter.getFreshBNode();

		try {
			BNode currentBnode = this.rdfWriter.getFreshBNode();

			this.rdfWriter.writeTripleValueObject(ret, RdfUriConstant.RDF_TYPE,
					RdfUriConstant.OWL_CLASS);
			this.rdfWriter.writeTripleValueObject(ret,
					RdfUriConstant.OWL_ONE_OF, currentBnode);

			Iterator<Resource> it = listOfIndividuals.iterator();
			while (it.hasNext()) {
				Resource currentIndividual = it.next();
				this.rdfWriter.writeTripleValueObject(currentBnode,
						RdfUriConstant.RDF_FIRST, currentIndividual);
				if (it.hasNext()) {
					BNode nextBnode = this.rdfWriter.getFreshBNode();
					this.rdfWriter.writeTripleValueObject(currentBnode,
							RdfUriConstant.RDF_REST, nextBnode);
					currentBnode = nextBnode;
				}
			}
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return ret;
	}

	@Override
	public BNode getObjectSomeValuesFrom(Resource objectPropertyExpression,
			Resource classExpression) {
		BNode ret = this.rdfWriter.getFreshBNode();
		try {
			// addDeclarationObjectProperty(objectPropertyExpression);
			// addDeclarationClass(classExpression);

			this.rdfWriter.writeTripleValueObject(ret, RdfUriConstant.RDF_TYPE,
					RdfUriConstant.OWL_RESTRICTION);
			this.rdfWriter.writeTripleValueObject(ret,
					RdfUriConstant.OWL_ON_PROPERTY, objectPropertyExpression);
			this.rdfWriter.writeTripleValueObject(ret,
					RdfUriConstant.OWL_SOME_VALUES_FROM, classExpression);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return ret;
	}

	@Override
	public BNode getObjectUnionOf(Resource classExpression0,
			Resource classExpression1) {
		BNode ret = this.rdfWriter.getFreshBNode();

		try {
			// addDeclarationClass(classExpression0);
			// addDeclarationClass(classExpression1);

			BNode bnode1 = this.rdfWriter.getFreshBNode();
			BNode bnode2 = this.rdfWriter.getFreshBNode();

			this.rdfWriter.writeTripleValueObject(ret, RdfUriConstant.RDF_TYPE,
					RdfUriConstant.OWL_CLASS);
			this.rdfWriter.writeTripleValueObject(ret,
					RdfUriConstant.OWL_UNION_OF, bnode1);
			this.rdfWriter.writeTripleValueObject(bnode1,
					RdfUriConstant.RDF_FIRST, classExpression0);
			this.rdfWriter.writeTripleValueObject(bnode1,
					RdfUriConstant.RDF_REST, bnode2);
			this.rdfWriter.writeTripleValueObject(bnode2,
					RdfUriConstant.RDF_FIRST, classExpression1);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return ret;
	}

	@Override
	public boolean addAnnotationAssertion(URI annotationProperty,
			URI annotationSubject, String annotationValue) {
		try {
			this.rdfWriter.writeTripleStringObject(annotationSubject,
					annotationProperty, annotationValue);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean addDataPropertyRange(URI dataPropertyExpression,
			Resource dataRange) {
		try {
			// addDeclarationDatatypeProperty(dataPropertyExpression);
			// addDeclarationDatatype(dataRange);

			this.rdfWriter.writeTripleValueObject(dataPropertyExpression,
					RdfUriConstant.RDFS_RANGE, dataRange);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean addDatatypeDefinition(URI datatype, Resource dataRange) {
		try {
			// addDeclarationDatatype(datatype);

			this.rdfWriter.writeTripleValueObject(datatype,
					RdfUriConstant.OWL_EQUIVALENT_CLASS, dataRange);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean addDeclarationAnnotationProperty(URI annotationProperty) {
		if (this.declaredEntities.contains(annotationProperty)) {
			return false;
		}
		try {
			this.rdfWriter.writeTripleValueObject(annotationProperty,
					RdfUriConstant.RDF_TYPE,
					RdfUriConstant.OWL_ANNOTATION_PROPERTY);
			this.declaredEntities.add(annotationProperty);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean addDeclarationClass(URI clss) {
		if (this.declaredEntities.contains(clss)) {
			return false;
		}
		try {
			this.rdfWriter.writeTripleValueObject(clss,
					RdfUriConstant.RDF_TYPE, RdfUriConstant.OWL_CLASS);
			this.declaredEntities.add(clss);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean addDeclarationDatatype(URI datatype) {
		if (this.declaredEntities.contains(datatype)) {
			return false;
		}
		try {
			this.rdfWriter.writeTripleValueObject(datatype,
					RdfUriConstant.RDF_TYPE, RdfUriConstant.RDFS_DATATYPE);
			this.declaredEntities.add(datatype);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean addDeclarationDatatypeProperty(URI datatypeProperty) {
		if (this.declaredEntities.contains(datatypeProperty)) {
			return false;
		}
		try {
			this.rdfWriter.writeTripleValueObject(datatypeProperty,
					RdfUriConstant.RDF_TYPE,
					RdfUriConstant.OWL_DATATYPE_PROPERTY);
			this.declaredEntities.add(datatypeProperty);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean addDeclarationNamedIndividual(URI namedIndividual) {
		if (this.declaredEntities.contains(namedIndividual)) {
			return false;
		}
		try {
			this.rdfWriter.writeTripleValueObject(namedIndividual,
					RdfUriConstant.RDF_TYPE,
					RdfUriConstant.OWL_NAMED_INDIVIDUAL);
			this.declaredEntities.add(namedIndividual);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean addDeclarationObjectProperty(URI objectProperty) {
		if (this.declaredEntities.contains(objectProperty)) {
			return false;
		}
		try {
			this.rdfWriter
					.writeTripleValueObject(objectProperty,
							RdfUriConstant.RDF_TYPE,
							RdfUriConstant.OWL_OBJECT_PROPERTY);
			this.declaredEntities.add(objectProperty);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean addDisjointClasses(Resource classExpression0,
			Resource classExpression1) {
		try {
			// addDeclarationClass(classExpression0);
			// addDeclarationClass(classExpression1);

			this.rdfWriter.writeTripleValueObject(classExpression0,
					RdfUriConstant.OWL_DISJOINT_WITH, classExpression1);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean addFunctionalObjectProperty(Resource objectPropertyExpression) {
		try {
			// addDeclarationObjectProperty(objectProperty);

			this.rdfWriter.writeTripleValueObject(objectPropertyExpression,
					RdfUriConstant.RDF_TYPE,
					RdfUriConstant.OWL_FUNCTIONAL_PROPERTY);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean addHasKey(Resource classExpression,
			Resource objectPropertyExpression) {
		try {
			// addDeclarationClass(classExpression);
			// addDeclarationObjectProperty(objectPropertyExpression);
			// addDeclarationDatatypeProperty(dataProperty);

			BNode bnode0 = this.rdfWriter.getFreshBNode();

			this.rdfWriter.writeTripleValueObject(objectPropertyExpression,
					RdfUriConstant.OWL_HAS_KEY, bnode0);
			this.rdfWriter.writeTripleValueObject(bnode0,
					RdfUriConstant.RDF_FIRST, objectPropertyExpression);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean addInverseFunctionalObjectProperty(
			Resource objectPropertyExpression) {
		try {
			// addDeclarationObjectProperty(objectPropertyExpression);

			this.rdfWriter.writeTripleValueObject(objectPropertyExpression,
					RdfUriConstant.RDF_TYPE,
					RdfUriConstant.OWL_INVERSE_FUNCTIONAL_PROPERTY);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean addObjectPropertyDomain(Resource objectPropertyExpression,
			Resource classExpression) {
		try {
			// addDeclarationObjectProperty(objectPropertyExpression);
			// addDeclarationClass(classExpression);

			this.rdfWriter.writeTripleValueObject(objectPropertyExpression,
					RdfUriConstant.RDFS_DOMAIN, classExpression);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean addObjectPropertyRange(Resource objectPropertyExpression,
			Resource classExpression) {
		try {
			// addDeclarationObjectProperty(objectPropertyExpression);
			// addDeclarationClass(classExpression);

			this.rdfWriter.writeTripleValueObject(objectPropertyExpression,
					RdfUriConstant.RDFS_RANGE, classExpression);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean addSubClassOf(Resource subClassExpression,
			Resource superClassExpression) {
		try {
			// addDeclarationClass(subClassExpression);
			// addDeclarationClass(superClassExpression);

			this.rdfWriter.writeTripleValueObject(subClassExpression,
					RdfUriConstant.RDFS_SUB_CLASS_OF, superClassExpression);
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

	public boolean addClassAssertion(Resource classExpression,
			Resource individual) {
		try {
			// addDeclarationClass(classExpression);
			// addDeclarationNamedIndividual(individual);

			this.rdfWriter.writeTripleValueObject(individual,
					RdfUriConstant.RDF_TYPE, classExpression);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	public boolean addClassNegativeAssertion(Resource classExpression,
			Resource individual) {
		try {
			// addDeclarationClass(classExpression);
			// addDeclarationNamedIndividual(individual);

			BNode bnode = this.rdfWriter.getFreshBNode();

			this.rdfWriter.writeTripleValueObject(bnode,
					RdfUriConstant.RDF_TYPE, RdfUriConstant.OWL_CLASS);
			this.rdfWriter.writeTripleValueObject(bnode,
					RdfUriConstant.OWL_COMPLEMENT_OF, classExpression);
			this.rdfWriter.writeTripleValueObject(individual,
					RdfUriConstant.RDF_TYPE, bnode);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

}
