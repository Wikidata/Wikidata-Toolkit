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
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.RDFHandlerException;
import org.wikidata.wdtk.datamodel.interfaces.NoValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.SnakVisitor;
import org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

public class SnakRdfConverter implements SnakVisitor<Void> {

	final ValueRdfConverter valueRdfConverter;

	final ValueFactory factory = ValueFactoryImpl.getInstance();
	final RdfWriter rdfWriter;

	String subjectUri;
	PropertyContext propertyContext;

	public SnakRdfConverter(RdfWriter rdfWriter,
			ValueRdfConverter valueRdfConverter) {
		this.rdfWriter = rdfWriter;
		this.valueRdfConverter = valueRdfConverter;
	}

	public void setSnakContext(String subjectUri,
			PropertyContext propertyContext) {
		this.subjectUri = subjectUri;
		this.propertyContext = propertyContext;
	}

	@Override
	public Void visit(ValueSnak snak) {
		try {
			String propertyUri = Vocabulary.getPropertyUri(
					snak.getPropertyId(), this.propertyContext);
			Value value = snak.getValue().accept(valueRdfConverter);
			this.rdfWriter.writeTripleValueObject(this.subjectUri, propertyUri,
					value);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e.toString(), e);
		}
		return null;
	}

	@Override
	public Void visit(SomeValueSnak snak) {
		// TODO
		return null;
	}

	@Override
	public Void visit(NoValueSnak snak) {
		// TODO
		return null;
	}

}
