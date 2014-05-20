package org.wikidata.wdtk.rdf;

import java.io.IOException;
import java.io.OutputStream;

import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;

public interface PropertyTypes {

	public String getPropertyType(PropertyIdValue propertyIdValue);
	
	public void setPropertyType(PropertyIdValue propertyIdValue,
			String datatypeIri);

	public String setPropertyTypeFromEntityIdValue(
			PropertyIdValue propertyIdValue, EntityIdValue value);

	public String setPropertyTypeFromGlobeCoordinatesValue(
			PropertyIdValue propertyIdValue, GlobeCoordinatesValue value);
	
	public String setPropertyTypeFromQuantityValue(
			PropertyIdValue propertyIdValue, QuantityValue value);
	
	public String setPropertyTypeFromStringValue(
			PropertyIdValue propertyIdValue, StringValue value);
	
	public String setPropertyTypeFromTimeValue(PropertyIdValue propertyIdValue,
			TimeValue value);
	
	public void getPropertyList(OutputStream out) throws IOException;
	
}
