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
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.rdf.OwlDeclarationBuffer;
import org.wikidata.wdtk.rdf.PropertyRegister;
import org.wikidata.wdtk.rdf.RdfConverter;
import org.wikidata.wdtk.rdf.RdfWriter;

public class MonolingualTextValueConverter extends
		AbstractValueConverter<MonolingualTextValue> {

	public MonolingualTextValueConverter(RdfWriter rdfWriter,
			PropertyRegister propertyRegister,
			OwlDeclarationBuffer rdfConversionBuffer) {
		super(rdfWriter, propertyRegister, rdfConversionBuffer);
	}

	@Override
	public Value getRdfValue(MonolingualTextValue value,
			PropertyIdValue propertyIdValue, boolean simple) {
		String datatype = this.propertyRegister
				.setPropertyTypeFromMonolingualTextValue(propertyIdValue, value);

		switch (datatype) {
		case DatatypeIdValue.DT_MONOLINGUAL_TEXT:
			if (simple) {
				this.rdfConversionBuffer.addObjectProperty(propertyIdValue);
				return RdfConverter.getMonolingualTextValueLiteral(value,
						this.rdfWriter);
			} else {
				return null; // or blank node
			}
		default:
			logIncompatibleValueError(propertyIdValue, datatype, "entity");
			return null;
		}
	}

}
