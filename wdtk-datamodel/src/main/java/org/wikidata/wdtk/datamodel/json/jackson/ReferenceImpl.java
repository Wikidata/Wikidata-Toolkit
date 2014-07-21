package org.wikidata.wdtk.datamodel.json.jackson;

import java.util.List;
import java.util.Map;

import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ReferenceImpl implements Reference {

	private Map<String, List<SnakImpl>> snaks;
	
	@JsonIgnore // not in the actual JSON, just to satisfy the interface
	@Override
	public List<SnakGroup> getSnakGroups() {
		Helper.buildSnakGroups(this.snaks);
		return null;
	}
	
	public void setSnaks(Map<String, List<SnakImpl>> snaks){
		this.snaks = snaks;
	}

	public Map<String, List<SnakImpl>> getSanks(){
		return this.snaks;
	}
}
