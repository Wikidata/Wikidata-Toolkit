package org.wikidata.wdtk.datamodel.jsonconverter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.Claim;
import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.NoValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

/**
 * 
 * @author michael, fredo
 *
 */

public class TestObjectFactory {

	private DataObjectFactory factory = new DataObjectFactoryImpl();
	private static String baseIri = "test";

	PropertyDocument createEmptyPropertyDocument() {

		PropertyIdValue propertyId = this.factory.getPropertyIdValue("P1",
				baseIri);
		List<MonolingualTextValue> labels = new LinkedList<>();
		List<MonolingualTextValue> descriptions = new LinkedList<>();
		List<MonolingualTextValue> aliases = new LinkedList<>();
		DatatypeIdValue datatypeId = this.factory
				.getDatatypeIdValue("globe-coordinate");
		PropertyDocument document = this.factory.getPropertyDocument(
				propertyId, labels, descriptions, aliases, datatypeId);
		return document;
	}

	ItemDocument createEmptyItemDocument() {

		ItemIdValue itemIdValue = this.factory.getItemIdValue("Q1", baseIri);
		List<MonolingualTextValue> labels = new LinkedList<>();
		List<MonolingualTextValue> descriptions = new LinkedList<>();
		List<MonolingualTextValue> aliases = new LinkedList<>();
		List<StatementGroup> statementGroups = new LinkedList<>();
		Map<String, SiteLink> siteLinks = new HashMap<>();
		ItemDocument document = this.factory.getItemDocument(itemIdValue,
				labels, descriptions, aliases, statementGroups, siteLinks);
		return document;
	}

	List<MonolingualTextValue> createLabels() {
		List<MonolingualTextValue> result = new LinkedList<>();
		result.add(factory.getMonolingualTextValue("foo", "lc"));
		result.add(factory.getMonolingualTextValue("bar", "lc2"));
		return result;
	}

	List<MonolingualTextValue> createAliases() {
		List<MonolingualTextValue> result = new LinkedList<>();
		result.add(factory.getMonolingualTextValue("foo", "lc"));
		result.add(factory.getMonolingualTextValue("bar", "lc"));
		return result;
	}

	List<MonolingualTextValue> createDescriptions() {
		List<MonolingualTextValue> result = new LinkedList<>();
		result.add(factory.getMonolingualTextValue("it's foo", "lc"));
		result.add(factory.getMonolingualTextValue("it's bar", "lc2"));
		return result;
	}

	Map<String, SiteLink> createSiteLinks() {
		Map<String, SiteLink> result = new HashMap<String, SiteLink>();
		result.put("enwiki", factory.getSiteLink("title_en", "siteKey",
				baseIri, new LinkedList<String>()));
		result.put("auwiki", factory.getSiteLink("title_au", "siteKey",
				baseIri, new LinkedList<String>()));
		return result;
	}

	/*
	 * ValueSnak createValueSnak(){ ValueSnak result =
	 * factory.getValueSnak(factory.getPropertyIdValue("P100", baseIri),
	 * factory.getItemIdValue("Q101", baseIri)); return result; }
	 */
	ValueSnak createValueSnakTime(int random, String pId) {
		return factory.getValueSnak(factory.getPropertyIdValue(pId, baseIri),
				factory.getTimeValue((random * 18) % 25500,
						(byte) ((random * 15) % 12 + 1),
						(byte) ((random * 3) % 28 + 1),
						(byte) ((random * 2) % 24), (byte) ((random * 7) % 61),
						(byte) ((random * 6) % 61),
						(byte) ((random * 3) % 25500), (random * 17) % 25500,
						(random * 43) % 25500, (random * 390) % 25500,
						"http://www.wikidata.org/entity/Q1985727"));
	}

	List<? extends Snak> createQualifiers() {
		List<Snak> result = new ArrayList<Snak>();
		result.add(createValueSnakTime(14, "P15"));
		return result;
	}

	List<? extends Reference> createReferences(int random) {
		List<ValueSnak> snaks = new ArrayList<ValueSnak>();
		List<Reference> refs = new ArrayList<>();
		snaks.add(createValueSnakTime(122, "P112"));
		refs.add(factory.getReference(snaks));
		return refs;
	}

	Claim createClaim(String id, Snak snak) {
		return factory.getClaim(factory.getItemIdValue(id, baseIri), snak,
				Collections.<Snak> emptyList());
	}

	SomeValueSnak createSomeValueSnak(String pId){
		return factory.getSomeValueSnak(factory.getPropertyIdValue(pId, baseIri));
	}
	
	ValueSnak createValueSnakItemIdValue(String pId, String qId) {
		return factory.getValueSnak(factory.getPropertyIdValue(pId, baseIri),
				factory.getItemIdValue(qId, baseIri));
	}
	
	ValueSnak createValueSnakStringValue(String pId){
		return factory.getValueSnak(factory.getPropertyIdValue(pId, baseIri),
				factory.getStringValue("TestString"));
	}
	
	ValueSnak createValueSnakCoordinatesValue(String pId){
		return factory.getValueSnak(factory.getPropertyIdValue(pId, baseIri), factory.getGlobeCoordinatesValue(213124, 21314, GlobeCoordinatesValue.PREC_ARCMINUTE, "http://www.wikidata.org/entity/Q2"));
	}
	ValueSnak createValueSnakQuantityValue(String pId){
		return factory.getValueSnak(factory.getPropertyIdValue(pId, baseIri), factory.getQuantityValue(new BigDecimal(3), new BigDecimal(3), new BigDecimal(3)));
	}
	
	PropertyIdValue createPropertyIdValue(String id){
		return factory.getPropertyIdValue(id, baseIri);
	}
	ItemIdValue createItemIdValue(String id){
		return factory.getItemIdValue(id, baseIri);
	}
	/*
	 * Statement createSatement(){ Claim claim =
	 * factory.getClaim(factory.getItemIdValue("Q10", "base/"),
	 * factory.getNoValueSnak(factory.getPropertyIdValue("P11", "base/")),
	 * Collections.<Snak> emptyList()); return factory.getStatement(claim,
	 * references, rank, statementId) }
	 */
}
