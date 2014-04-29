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
import org.wikidata.wdtk.datamodel.interfaces.NoValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.SnakVisitor;
import org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

public class SnakRdfConverter implements
		SnakVisitor<Value> {

	final ValueRdfConverter valueRdfConverter;
	
	final ValueFactory factory = ValueFactoryImpl.getInstance();

	public SnakRdfConverter(ValueRdfConverter valueRdfConverter) {
		this.valueRdfConverter = valueRdfConverter;
	}

	@Override
	public Value visit(ValueSnak snak) {
		return snak.getValue().accept(valueRdfConverter);
	}

	@Override
	public Value visit(SomeValueSnak snak) {
		return factory.createURI("unknownValue"); // see for real representation
	}

	@Override
	public Value visit(NoValueSnak snak) {																
		return factory.createURI("noValue"); // see for real representation
	}

}
