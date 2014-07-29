package org.wikidata.wdtk.datamodel.json.jackson.datavalues;

public class GlobeCoordinate {

	private long latitude;
	private long longitude;
	private long precision;
	private String globe;
	
	public GlobeCoordinate(){}
	public GlobeCoordinate(long latitude, long longitude, long precision, String globe){
		this.latitude = latitude;
		this.longitude = longitude;
		this.precision = precision;
		this.globe = globe;
	}
	
	public long getLatitude() {
		return latitude;
	}
	
	public void setLatitude(long latitude) {
		this.latitude = latitude;
	}
	
	public long getLongitude() {
		return longitude;
	}
	
	public void setLongitude(long longitude) {
		this.longitude = longitude;
	}
	
	public long getPrecision() {
		return precision;
	}
	
	public void setPrecision(long precision) {
		this.precision = precision;
	}
	
	public String getGlobe() {
		return globe;
	}
	
	public void setGlobe(String globe) {
		this.globe = globe;
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		
		if(!(o instanceof GlobeCoordinate)){
			return false;
		}
		
		GlobeCoordinate other = (GlobeCoordinate)o;
		
		return (this.globe.equals(other.globe)
				&& this.latitude == other.latitude
				&& this.longitude == other.longitude
				&& this.precision == other.precision);
	}
}
