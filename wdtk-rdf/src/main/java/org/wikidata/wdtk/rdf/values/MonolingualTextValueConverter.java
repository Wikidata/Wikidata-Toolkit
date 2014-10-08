package org.wikidata.wdtk.rdf.values;

import org.openrdf.model.Value;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.rdf.OwlDeclarationBuffer;
import org.wikidata.wdtk.rdf.PropertyTypes;
import org.wikidata.wdtk.rdf.RdfWriter;

public class MonolingualTextValueConverter extends
		AbstractValueConverter<MonolingualTextValue> {

	public MonolingualTextValueConverter(RdfWriter rdfWriter,
			PropertyTypes propertyTypes,
			OwlDeclarationBuffer rdfConversionBuffer) {
		super(rdfWriter, propertyTypes, rdfConversionBuffer);
	}

	@Override
	public Value getRdfValue(MonolingualTextValue value,
			PropertyIdValue propertyIdValue, boolean simple) {
		String datatype = this.propertyTypes
				.setPropertyTypeFromMonolingualTextValue(propertyIdValue, value);

		switch (datatype) {
		case DatatypeIdValue.DT_MONOLINGUAL_TEXT:
			this.rdfConversionBuffer.addObjectProperty(propertyIdValue);
			return this.rdfWriter.getLiteral(value.getText(),
					value.getLanguageCode());
		default:
			logIncompatibleValueError(propertyIdValue, datatype, "entity");
			return null;
		}
	}

}
