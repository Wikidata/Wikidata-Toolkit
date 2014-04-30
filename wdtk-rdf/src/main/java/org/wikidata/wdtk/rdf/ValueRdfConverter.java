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
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;


public class ValueRdfConverter implements ValueVisitor<Value> {

	final ValueFactory factory = ValueFactoryImpl.getInstance();
	
	@Override
	public Value visit(DatatypeIdValue value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Value visit(EntityIdValue value) {
		return this.factory.createURI(Vocabulary.PREFIX_WIKIDATA + value.getId());
	}

	@Override
	public Value visit(GlobeCoordinatesValue value) {
		URI valueURI = this.factory.createURI(Vocabulary.PREFIX_WIKIDATA + "VC" + value.hashCode());
		// TODO add attributes
		return valueURI;
	}

	@Override
	public Value visit(MonolingualTextValue value) {
		
		return factory.createLiteral(value.getText(), value.getLanguageCode());
	}

	@Override
	public Value visit(QuantityValue value) {
		// TODO Auto-generated method stub
		return this.factory.createLiteral(value.getNumericValue().doubleValue());
	}

	@Override
	public Value visit(StringValue value) {
		// TODO Auto-generated method stub
		return factory.createLiteral(value.getString());
	}

	@Override
	public Value visit(TimeValue value) {
		URI valueURI = this.factory.createURI(Vocabulary.PREFIX_WIKIDATA + "VT" + value.hashCode());
		// TODO add attributes
		return valueURI;
	}

}
