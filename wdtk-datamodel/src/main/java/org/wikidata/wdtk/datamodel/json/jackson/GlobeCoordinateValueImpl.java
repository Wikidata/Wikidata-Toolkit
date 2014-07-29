package org.wikidata.wdtk.datamodel.json.jackson;

import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.GlobeCoordinate;

import com.fasterxml.jackson.annotation.JsonIgnore;


// TODO test
public class GlobeCoordinateValueImpl extends ValueImpl implements GlobeCoordinatesValue {

	private GlobeCoordinate value;
	
	public GlobeCoordinateValueImpl(){
		super(typeCoordinate);
	}
	public GlobeCoordinateValueImpl(GlobeCoordinate value){
		super(typeCoordinate);
		this.value = value;
	}
	
	public GlobeCoordinate getValue() {
		return value;
	}

	public void GlobeCoordinate(GlobeCoordinate value) {
		this.value = value;
	}
	
	@JsonIgnore
	@Override
	public long getLatitude() {
		return this.value.getLatitude();
	}
	
	@JsonIgnore
	@Override
	public long getLongitude() {
		return this.value.getLongitude();
	}
	
	@JsonIgnore
	@Override
	public long getPrecision() {
		return this.value.getPrecision();
	}
	
	@JsonIgnore
	@Override
	public String getGlobe() {
		return this.value.getGlobe();
	}
}
