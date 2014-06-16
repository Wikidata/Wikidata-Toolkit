package org.wikidata.wdtk.datamodel.externalJsonImplementation;

import java.util.LinkedList;
import java.util.List;

import org.wikidata.wdtk.datamodel.interfaces.SiteLink;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SiteLinkImpl implements SiteLink {

	String title;
	String site;
	List<String> badges = new LinkedList<>();
	
	SiteLinkImpl(){}
	SiteLinkImpl(String site, String title){
		this.site = site;
		this.title = title;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	@JsonProperty("title")
	@Override
	public String getPageTitle() {
		return this.title;
	}

	public void setSite(String site){
		this.site = site;
	}
	
	@JsonProperty("site")
	@Override
	public String getSiteKey() {
		return this.site;
	}

	public void setBadges(List<String> badges){
		this.badges = badges;
	}
	
	@Override
	public List<String> getBadges() {
		return this.badges;
	}

}
