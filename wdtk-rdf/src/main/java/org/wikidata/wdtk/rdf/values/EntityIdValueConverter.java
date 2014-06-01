package org.wikidata.wdtk.rdf.values;

import org.openrdf.model.Value;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.rdf.PropertyTypes;
import org.wikidata.wdtk.rdf.OwlDeclarationBuffer;
import org.wikidata.wdtk.rdf.RdfWriter;

public class EntityIdValueConverter extends
		AbstractValueConverter<EntityIdValue> {

	public EntityIdValueConverter(RdfWriter rdfWriter,
			PropertyTypes propertyTypes, OwlDeclarationBuffer rdfConversionBuffer) {
		super(rdfWriter, propertyTypes, rdfConversionBuffer);
	}

	@Override
	public Value getRdfValue(EntityIdValue value,
			PropertyIdValue propertyIdValue) {
		String datatype = this.propertyTypes.setPropertyTypeFromEntityIdValue(
				propertyIdValue, value);

		switch (datatype) {
		case DatatypeIdValue.DT_ITEM:
			this.rdfConversionBuffer.addObjectProperty(propertyIdValue);
			return this.rdfWriter.getUri(value.getIri());
		default:
			logIncompatibleValueError(propertyIdValue, datatype, "entity");
			return null;
		}
	}

}
