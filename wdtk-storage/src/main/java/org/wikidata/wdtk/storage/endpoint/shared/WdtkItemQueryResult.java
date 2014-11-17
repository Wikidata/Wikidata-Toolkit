package org.wikidata.wdtk.storage.endpoint.shared;

import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;

public class WdtkItemQueryResult implements WdtkQueryResult {

	private static final long serialVersionUID = -4607039781655308145L;
	
	private EntityDocument resultDocument;
	
	public WdtkItemQueryResult(EntityDocument resultDocument){
		this.resultDocument = resultDocument;
	}
	
	public EntityDocument getResultDocument(){
		return this.resultDocument;
	}
}
