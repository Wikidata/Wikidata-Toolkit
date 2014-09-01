package org.wikidata.wdtk.datamodel.json.jackson;

/*
 * #%L
 * Wikidata Toolkit Data Model
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
	
	public SiteLinkImpl(SiteLink value) {
		this(value.getSiteKey(), value.getPageTitle());
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
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		
		if(!(o instanceof SiteLinkImpl)){
			return false;
		}
		SiteLinkImpl other = (SiteLinkImpl)o;
		return this.badges.equals(other.badges)
				&& this.site.equals(other.site)
				&& this.title.equals(other.title);
	}

}
