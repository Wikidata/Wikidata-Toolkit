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
import java.util.List;

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
	public BNode getDataIntersectionOf(Resource datatype0, Resource datatype1) {
		// FIXME
		BNode ret = factory.createBNode();

		return ret;
	}

	@Override
	public BNode getDataSomeValuesFrom(Resource datatypeProperty,
			Resource datatype) {
		BNode ret = factory.createBNode();
		try {
			this.rdfWriter.writeTripleValueObject(datatype,
					RdfUriConstant.RDF_TYPE, RdfUriConstant.RDFS_DATATYPE);
			this.rdfWriter.writeTripleValueObject(datatypeProperty,
					RdfUriConstant.RDF_TYPE,
					RdfUriConstant.OWL_DATATYPE_PROPERTY);
			this.rdfWriter.writeTripleValueObject(ret, RdfUriConstant.RDF_TYPE,
					RdfUriConstant.OWL_RESTRICTION);
			this.rdfWriter.writeTripleValueObject(ret,
					RdfUriConstant.OWL_ON_PROPERTY, datatypeProperty);
			this.rdfWriter.writeTripleValueObject(ret,
					RdfUriConstant.OWL_SOME_VALUES_FROM, datatype);
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
	public BNode getLiteral(Resource value, Resource type) {
		// FIXME

		return factory.createBNode();
	}

	@Override
	public BNode getObjectComplementOf(Resource clss) {
		BNode ret = factory.createBNode();
		try {
			this.rdfWriter.writeTripleValueObject(ret,
					RdfUriConstant.OWL_COMPLEMENT_OF, clss);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return ret;
	}

	@Override
	public BNode getObjectExactCardinality(int cardinality, Resource property) {
		BNode ret = factory.createBNode();
		try {
			this.rdfWriter
					.writeTripleValueObject(property, RdfUriConstant.RDF_TYPE,
							RdfUriConstant.OWL_OBJECT_PROPERTY);
			this.rdfWriter.writeTripleValueObject(ret, RdfUriConstant.RDF_TYPE,
					RdfUriConstant.OWL_RESTRICTION);
			this.rdfWriter.writeTripleValueObject(ret,
					RdfUriConstant.OWL_ON_PROPERTY, property);

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
	public BNode getObjectOneOf(Resource clss) {
		// FIXME
		BNode ret = factory.createBNode();

		return ret;
	}

	@Override
	public BNode getObjectOneOf(List<Resource> list) {
		// FIXME
		BNode ret = factory.createBNode();

		return ret;
	}

	@Override
	public BNode getObjectSomeValuesFrom(Resource propertyUri, Resource rangeUri) {
		BNode ret = factory.createBNode();
		try {
			this.rdfWriter.writeTripleValueObject(ret, RdfUriConstant.RDF_TYPE,
					RdfUriConstant.OWL_RESTRICTION);
			this.rdfWriter.writeTripleValueObject(ret,
					RdfUriConstant.OWL_ON_PROPERTY, propertyUri);
			this.rdfWriter.writeTripleValueObject(ret,
					RdfUriConstant.OWL_SOME_VALUES_FROM, rangeUri);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return ret;
	}

	@Override
	public BNode getObjectUnionOf(Resource class0, Resource class1) {
		// FIXME
		BNode ret = factory.createBNode();

		return ret;
	}

	@Override
	public BNode getDatatypeRestriction(Resource arg0, Resource arg1,
			Resource arg2) {
		// FIXME
		BNode ret = factory.createBNode();

		return ret;
	}

	@Override
	public boolean addAnnotationAssertion(Resource entity, String comment) {
		try {
			this.rdfWriter.writeTripleStringObject(entity,
					RdfUriConstant.RDFS_COMMENT, comment);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean addDataPropertyRange(Resource property, Resource datatype) {
		try {
			this.rdfWriter.writeTripleValueObject(datatype,
					RdfUriConstant.RDF_TYPE, RdfUriConstant.RDFS_DATATYPE);
			this.rdfWriter.writeTripleValueObject(property,
					RdfUriConstant.RDF_TYPE,
					RdfUriConstant.OWL_DATATYPE_PROPERTY);
			this.rdfWriter.writeTripleValueObject(property,
					RdfUriConstant.RDFS_RANGE, datatype);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean addDatatypeDefinition(Resource arg0, Resource arg1) {
		// FIXME

		return false;
	}

	@Override
	public boolean addDeclarationAnnotationProperty(Resource entity) {
		try {
			this.rdfWriter.writeTripleValueObject(entity,
					RdfUriConstant.RDF_TYPE,
					RdfUriConstant.OWL_ANNOTATION_PROPERTY);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean addDeclarationClass(Resource entity) {
		try {
			this.rdfWriter.writeTripleValueObject(entity,
					RdfUriConstant.RDF_TYPE, RdfUriConstant.OWL_CLASS);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean addDeclarationDatatype(Resource entity) {
		try {
			this.rdfWriter.writeTripleValueObject(entity,
					RdfUriConstant.RDF_TYPE, RdfUriConstant.RDFS_DATATYPE);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean addDeclarationDatatypeProperty(Resource entity) {
		try {
			this.rdfWriter.writeTripleValueObject(entity,
					RdfUriConstant.RDF_TYPE,
					RdfUriConstant.OWL_DATATYPE_PROPERTY);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean addDeclarationNamedIndividual(Resource entity) {
		try {
			this.rdfWriter.writeTripleValueObject(entity,
					RdfUriConstant.RDF_TYPE,
					RdfUriConstant.OWL_NAMED_INDIVIDUAL);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean addDeclarationObjectProperty(Resource entity) {
		try {
			this.rdfWriter
					.writeTripleValueObject(entity, RdfUriConstant.RDF_TYPE,
							RdfUriConstant.OWL_OBJECT_PROPERTY);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean addDisjointClasses(Resource arg0, Resource arg1) {
		// FIXME

		return false;
	}

	@Override
	public boolean addFunctionalObjectProperty(Resource property) {
		try {
			this.rdfWriter
					.writeTripleValueObject(property, RdfUriConstant.RDF_TYPE,
							RdfUriConstant.OWL_OBJECT_PROPERTY);
			this.rdfWriter.writeTripleValueObject(property,
					RdfUriConstant.RDF_TYPE,
					RdfUriConstant.OWL_FUNCTIONAL_PROPERTY);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean addHasKey(Resource arg0, Resource arg1, Resource arg2) {
		// FIXME

		return false;
	}

	@Override
	public boolean addInverseFunctionalObjectProperty(Resource property) {
		try {
			this.rdfWriter
					.writeTripleValueObject(property, RdfUriConstant.RDF_TYPE,
							RdfUriConstant.OWL_OBJECT_PROPERTY);
			this.rdfWriter.writeTripleValueObject(property,
					RdfUriConstant.RDF_TYPE,
					RdfUriConstant.OWL_INVERSE_FUNCTIONAL_PROPERTY);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean addObjectPropertyDomain(Resource property, Resource domain) {
		try {
			this.rdfWriter.writeTripleValueObject(domain,
					RdfUriConstant.RDF_TYPE, RdfUriConstant.OWL_CLASS);
			this.rdfWriter
					.writeTripleValueObject(property, RdfUriConstant.RDF_TYPE,
							RdfUriConstant.OWL_OBJECT_PROPERTY);
			this.rdfWriter.writeTripleValueObject(property,
					RdfUriConstant.RDFS_DOMAIN, domain);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean addObjectPropertyRange(Resource property, Resource range) {
		try {
			this.rdfWriter.writeTripleValueObject(range,
					RdfUriConstant.RDF_TYPE, RdfUriConstant.OWL_CLASS);
			this.rdfWriter
					.writeTripleValueObject(property, RdfUriConstant.RDF_TYPE,
							RdfUriConstant.OWL_OBJECT_PROPERTY);
			this.rdfWriter.writeTripleValueObject(property,
					RdfUriConstant.RDFS_RANGE, range);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean addSubClassOf(Resource subClass, Resource superClass) {
		try {
			this.rdfWriter.writeTripleValueObject(subClass,
					RdfUriConstant.RDFS_SUB_CLASS_OF, superClass);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public String getEnd() {
		// FIXME
		return " ";
	}

	@Override
	public String getStart() {
		// FIXME
		return " ";
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

	public boolean addClassAssertion(Resource clss, Resource individual) {
		try {
			this.rdfWriter.writeTripleValueObject(individual,
					RdfUriConstant.RDF_TYPE, clss);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	public boolean addClassNegativeAssertion(Resource clss, Resource individual) {
		try {
			BNode bnode = factory.createBNode();
			this.rdfWriter.writeTripleValueObject(bnode,
					RdfUriConstant.RDF_TYPE, RdfUriConstant.OWL_CLASS);
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
