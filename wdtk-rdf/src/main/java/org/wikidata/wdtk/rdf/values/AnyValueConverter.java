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
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.UnsupportedValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;
import org.wikidata.wdtk.rdf.OwlDeclarationBuffer;
import org.wikidata.wdtk.rdf.PropertyRegister;
import org.wikidata.wdtk.rdf.RdfWriter;

/**
 * Class to convert Wikibase data values to RDF. The class is a visitor that
 * that computes an RDF value (URI or literal) to represent any kind of Wikibase
 * data value. Some values are complex and require further RDF triples to be
 * written. In such cases, the class stores the values to a buffer. Methods for
 * writing additional triples for these buffered values can be called later.
 *
 * @author Markus Kroetzsch
 *
 */
public class AnyValueConverter implements
		ValueConverter<org.wikidata.wdtk.datamodel.interfaces.Value>,
		ValueVisitor<Value> {

	final private RdfWriter rdfWriter;
	final EntityIdValueConverter entityIdValueConverter;
	final StringValueConverter stringValueConverter;
	final TimeValueConverter timeValueConverter;
	final GlobeCoordinatesValueConverter globeCoordinatesValueConverter;
	final QuantityValueConverter quantityValueConverter;
	final MonolingualTextValueConverter monolingualTextValueConverter;

	PropertyIdValue currentPropertyIdValue;
	boolean simple;

	static final Logger logger = LoggerFactory
			.getLogger(AnyValueConverter.class);

	public AnyValueConverter(RdfWriter rdfWriter,
			OwlDeclarationBuffer rdfConversionBuffer,
			PropertyRegister propertyRegister) {

		this.rdfWriter = rdfWriter;
		this.entityIdValueConverter = new EntityIdValueConverter(rdfWriter,
				propertyRegister, rdfConversionBuffer);
		this.stringValueConverter = new StringValueConverter(rdfWriter,
				propertyRegister, rdfConversionBuffer);
		this.timeValueConverter = new TimeValueConverter(rdfWriter,
				propertyRegister, rdfConversionBuffer);
		this.globeCoordinatesValueConverter = new GlobeCoordinatesValueConverter(
				rdfWriter, propertyRegister, rdfConversionBuffer);
		this.quantityValueConverter = new QuantityValueConverter(rdfWriter,
				propertyRegister, rdfConversionBuffer);
		this.monolingualTextValueConverter = new MonolingualTextValueConverter(
				rdfWriter, propertyRegister, rdfConversionBuffer);
	}

	@Override
	public Value getRdfValue(
			org.wikidata.wdtk.datamodel.interfaces.Value value,
			PropertyIdValue propertyIdValue, boolean simple) {
		this.currentPropertyIdValue = propertyIdValue;
		this.simple = simple;
		return value.accept(this);
	}

	@Override
	public Value visit(EntityIdValue value) {
		return this.entityIdValueConverter.getRdfValue(value,
				this.currentPropertyIdValue, this.simple);
	}

	@Override
	public Value visit(GlobeCoordinatesValue value) {
		return this.globeCoordinatesValueConverter.getRdfValue(value,
				this.currentPropertyIdValue, this.simple);
	}

	@Override
	public Value visit(MonolingualTextValue value) {
		return this.monolingualTextValueConverter.getRdfValue(value,
				this.currentPropertyIdValue, this.simple);
	}

	@Override
	public Value visit(QuantityValue value) {
		return this.quantityValueConverter.getRdfValue(value,
				this.currentPropertyIdValue, this.simple);
	}

	@Override
	public Value visit(StringValue value) {
		return this.stringValueConverter.getRdfValue(value,
				this.currentPropertyIdValue, this.simple);
	}

	@Override
	public Value visit(TimeValue value) {
		return this.timeValueConverter.getRdfValue(value,
				this.currentPropertyIdValue, this.simple);
	}

	@Override
	public void writeAuxiliaryTriples() throws RDFHandlerException {
		this.entityIdValueConverter.writeAuxiliaryTriples();
		this.stringValueConverter.writeAuxiliaryTriples();
		this.globeCoordinatesValueConverter.writeAuxiliaryTriples();
		this.timeValueConverter.writeAuxiliaryTriples();
		this.quantityValueConverter.writeAuxiliaryTriples();
	}

	@Override
	public Value visit(UnsupportedValue value) {
		return this.rdfWriter.getFreshBNode();
	}

}
