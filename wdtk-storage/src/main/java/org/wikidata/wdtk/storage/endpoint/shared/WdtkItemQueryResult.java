package org.wikidata.wdtk.storage.endpoint.shared;

public class WdtkItemQueryResult implements WdtkQueryResult {

	private static final long serialVersionUID = -4607039781655308145L;

	// TODO HACK: since ItemDocuments are not serializable they get transported
	// as
	// Json Strings
	// private ItemDocument resultDocument;
	private String jsonRepresentation = "";

	public WdtkItemQueryResult(String resultDocument) {
		this.jsonRepresentation = resultDocument;
	}

	public String getResultDocument() {
		return this.jsonRepresentation;
	}
}
