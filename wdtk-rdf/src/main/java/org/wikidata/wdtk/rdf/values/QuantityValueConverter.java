package org.wikidata.wdtk.rdf.values;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.rio.RDFHandlerException;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;
import org.wikidata.wdtk.rdf.PropertyTypes;
import org.wikidata.wdtk.rdf.OwlDeclarationBuffer;
import org.wikidata.wdtk.rdf.RdfWriter;
import org.wikidata.wdtk.rdf.Vocabulary;

public class QuantityValueConverter extends
		BufferedValueConverter<QuantityValue> {

	public QuantityValueConverter(RdfWriter rdfWriter,
			PropertyTypes propertyTypes, OwlDeclarationBuffer rdfConversionBuffer) {
		super(rdfWriter, propertyTypes, rdfConversionBuffer);
	}

	@Override
	public Value getRdfValue(QuantityValue value,
			PropertyIdValue propertyIdValue) {
		String datatype = this.propertyTypes.setPropertyTypeFromQuantityValue(
				propertyIdValue, value);

		switch (datatype) {
		case DatatypeIdValue.DT_QUANTITY:
			URI valueUri = this.rdfWriter.getUri(Vocabulary
					.getQuantityValueUri(value));

			this.rdfConversionBuffer.addObjectProperty(propertyIdValue);
			addValue(value, valueUri);

			return valueUri;
		default:
			logIncompatibleValueError(propertyIdValue, datatype, "quantity");
			return null;
		}
	}

	@Override
	public void writeValue(QuantityValue value, Resource resource)
			throws RDFHandlerException {
		this.rdfWriter.writeTripleValueObject(resource, RdfWriter.RDF_TYPE,
				RdfWriter.WB_QUANTITY_VALUE);
		this.rdfWriter.writeTripleLiteralObject(resource,
				RdfWriter.WB_NUMERIC_VALUE, value.getNumericValue().toString(),
				RdfWriter.XSD_DECIMAL);
		this.rdfWriter.writeTripleLiteralObject(resource,
				RdfWriter.WB_LOWER_BOUND, value.getLowerBound().toString(),
				RdfWriter.XSD_DECIMAL);
		this.rdfWriter.writeTripleLiteralObject(resource,
				RdfWriter.WB_UPPER_BOUND, value.getUpperBound().toString(),
				RdfWriter.XSD_DECIMAL);
	}

}
