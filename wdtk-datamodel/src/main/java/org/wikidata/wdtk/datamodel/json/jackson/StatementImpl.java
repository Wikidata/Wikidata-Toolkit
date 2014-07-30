package org.wikidata.wdtk.datamodel.json.jackson;

import java.util.List;
import java.util.Map;

import org.wikidata.wdtk.datamodel.interfaces.Claim;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;
import org.wikidata.wdtk.datamodel.json.jackson.serializers.StatementRankDeserializer;
import org.wikidata.wdtk.datamodel.json.jackson.serializers.StatementRankSerializer;
import org.wikidata.wdtk.datamodel.json.jackson.snaks.SnakImpl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class StatementImpl implements Statement {

	private String id;

	@JsonSerialize(using = StatementRankSerializer.class)
	@JsonDeserialize(using = StatementRankDeserializer.class)
	private StatementRank rank;
	
	private List<ReferenceImpl> references;

	/**
	 * While this is called "claim" in the wdtk-datamodel, it is called
	 * "mainsnak" in the external JSON. There a claim is the entirety of
	 * statements in their respective groups.
	 */
	private SnakImpl mainsnak; 
	
	private Map<String, List<SnakImpl>> qualifiers;

	/**
	 * Only needed for correct Json serialization
	 * 
	 * @return "statement"
	 */
	@JsonProperty("type")
	public String getTypeAsString() {
		return "statement";
	}

	@JsonIgnore // not in the JSON, just to satisfy the interface
	@Override
	public Claim getClaim() {
		return new ClaimImpl(this);
	}

	@Override
	public StatementRank getRank() {
		return this.rank;
	}

	public void setRank(StatementRank rank) {
		this.rank = rank;
	}
	
	@Override
	public List<? extends Reference> getReferences() {
		return this.references;
	}
	
	public void setReferences(List<ReferenceImpl> references){
		this.references = references;
	}

	@JsonProperty("id")
	@Override
	public String getStatementId() {
		return this.id;
	}

	@JsonProperty("id")
	public void setStatementId(String id) {
		this.id = id;
	}

	public SnakImpl getMainsnak(){
		return this.mainsnak;
	}
	
	public void setMainsnak(SnakImpl mainsnak){
		this.mainsnak = mainsnak;
	}
	
	public void setQualifiers(Map<String, List<SnakImpl>> qualifiers){
		this.qualifiers = qualifiers;
	}
	
	public Map<String, List<SnakImpl>> getQualifiers(){
		return this.qualifiers;
	}
}
