package org.wikidata.wdtk.rdf.values;

import org.openrdf.model.Value;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.rdf.LinkedDataProperties;
import org.wikidata.wdtk.rdf.PropertyTypes;
import org.wikidata.wdtk.rdf.OwlDeclarationBuffer;
import org.wikidata.wdtk.rdf.RdfWriter;

public class StringValueConverter extends AbstractValueConverter<StringValue> {

	public StringValueConverter(RdfWriter rdfWriter,
			PropertyTypes propertyTypes, OwlDeclarationBuffer rdfConversionBuffer) {
		super(rdfWriter, propertyTypes, rdfConversionBuffer);
	}

	@Override
	public Value getRdfValue(StringValue value, PropertyIdValue propertyIdValue) {
		String datatype = this.propertyTypes.setPropertyTypeFromStringValue(
				propertyIdValue, value);

		String valueUriString;
		switch (datatype) {
		case DatatypeIdValue.DT_STRING:
			valueUriString = LinkedDataProperties.getUriForPropertyValue(
					propertyIdValue, value.getString());
			break;
		case DatatypeIdValue.DT_COMMONS_MEDIA:
			valueUriString = LinkedDataProperties.getCommonsUrl(value
					.getString());
			break;
		case DatatypeIdValue.DT_URL:
			valueUriString = value.getString();
			break;
		default:
			logIncompatibleValueError(propertyIdValue, datatype, "string");
			return null;
		}

		if (valueUriString == null) {
			this.rdfConversionBuffer.addDatatypeProperty(propertyIdValue);
			return this.rdfWriter.getLiteral(value.getString());
		} else {
			this.rdfConversionBuffer.addObjectProperty(propertyIdValue);
			try {
				return this.rdfWriter.getUri(valueUriString);
			} catch (IllegalArgumentException e) {
				logger.error("Invalid URI \"" + valueUriString
						+ "\". Not serializing value.");
				return null;
			}
		}
	}

}
