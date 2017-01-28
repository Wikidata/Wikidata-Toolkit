package org.wikidata.wdtk.datamodel.json.jackson;

public class JacksonRedirect {
	String entity;
	String redirect;
	public String getEntity(){
		return this.entity;
	}
	public String getRedirect(){
		return this.redirect;
	}
	public void setRedirect(String redirect){
		this.redirect = redirect;
	}
	public void setId(String entity){
		this.entity = entity;
	}
}
