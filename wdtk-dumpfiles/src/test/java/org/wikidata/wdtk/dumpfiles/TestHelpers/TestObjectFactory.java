package org.wikidata.wdtk.dumpfiles.TestHelpers;

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
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;
import org.wikidata.wdtk.dumpfiles.StatementGroupBuilder;

/**
 * This class provides functions to create objects from
 * {@link org.wikidata.wdtk.datamodel.interfaces} with certain predefined
 * parameters.
 * 
 * @author Michael Günther, Fredo Erxleben
 * 
 */
public class TestObjectFactory {

	private static DataObjectFactory factory = new DataObjectFactoryImpl();
	private static String baseIri = "";
	private static StatementGroupBuilder sgBuilder = new StatementGroupBuilder(factory);

	/**
	 * Creates an empty {@link PropertyDocument} with the propertyID "P1" and
	 * the datatypeID <i>DT_GLOBE_COORDINATES</i>.
	 * 
	 * <p>
	 * ID = PropDoc
	 * </p>
	 * 
	 * @return empty {@link PropertyDocument}
	 */
	public PropertyDocument createEmptyPropertyDocument(String baseIri) {

		PropertyIdValue propertyId = factory.getPropertyIdValue("P1",
				baseIri);
		DatatypeIdValue datatypeId = factory
				.getDatatypeIdValue(DatatypeIdValue.DT_GLOBE_COORDINATES);

		PropertyDocument emptyPropertyDocument = factory
				.getPropertyDocument(propertyId,
						Collections.<MonolingualTextValue> emptyList(),
						Collections.<MonolingualTextValue> emptyList(),
						Collections.<MonolingualTextValue> emptyList(),
						datatypeId);
		return emptyPropertyDocument;
	}

	/**
	 * Creates an empty {@link ItemDocument} with the itemID "Q1".
	 * 
	 * @return empty {@link ItemDocument}
	 */
	public ItemDocument createEmptyItemDocument(String baseIri) {

		ItemIdValue itemId = factory.getItemIdValue("Q1",
				baseIri);

		ItemDocument emptyItemDocument = factory
				.getItemDocument(itemId, 
						Collections.<MonolingualTextValue> emptyList(), 
						Collections.<MonolingualTextValue> emptyList(), 
						Collections.<MonolingualTextValue> emptyList(), 
						Collections.<StatementGroup> emptyList(), 
						Collections.<String, SiteLink> emptyMap());
		return emptyItemDocument;
	}

	/**
	 * Creates test labels with the content 
	 * <p>
	 * "en" : "testLabel"
	 * </p>
	 * 
	 * @return list of {@link MonolingualTextValue}
	 */
	public Map<String, MonolingualTextValue> createTestLabels() {
		
		Map<String,MonolingualTextValue> result = new HashMap<>();
		result.put("en", factory.getMonolingualTextValue("testLabel", "en"));
		return result;
	}
	
	/**
	 * Creates test descriptions with the content 
	 * <p>
	 * "en" : "testDescription"
	 * </p>
	 * 
	 * @return list of {@link MonolingualTextValue}
	 */
	public Map<String, MonolingualTextValue> createTestDescriptions() {
		
		Map<String,MonolingualTextValue> result = new HashMap<>();
		result.put("en", factory.getMonolingualTextValue("testDescription", "en"));
		return result;
	}
	
	/**
	 * Creates test aliases with the content 
	 * <p>
	 * "en" : "testAlias"
	 * </p>
	 * 
	 * @return list of {@link MonolingualTextValue}
	 */
	public Map<String, List<MonolingualTextValue>> createTestAliases() {
		
		Map<String, List<MonolingualTextValue>> result = new HashMap<>();
		result.put("en", Collections.singletonList(factory.getMonolingualTextValue("testAlias", "en")));
		
		List<MonolingualTextValue> sameLanguageAliases = new ArrayList<>();
		sameLanguageAliases.add(factory.getMonolingualTextValue("testAlias2", "de"));
		sameLanguageAliases.add(factory.getMonolingualTextValue("testAlias3", "de"));
		result.put("de", sameLanguageAliases);
		return result;
	}

	/**
	 * Creates a list of aliases.
	 * 
	 * <p>
	 * ID = Aliases
	 * </p>
	 * 
	 * <p>
	 * <b>Default values</b>
	 * </p>
	 * <ul>
	 * <li>MonolingualTextValue: "foo" (alias for the certain language), "lc"
	 * (LanguageCode)</li>
	 * <li>MonolingualTextValue: "bar" (label in the certain language), "lc"
	 * (LanguageCode)</li>
	 * 
	 * @return List of {@link MonolingualTextValue}
	 */
	public List<MonolingualTextValue> createAliases() {
		List<MonolingualTextValue> result = new LinkedList<>();
		result.add(factory.getMonolingualTextValue("foo", "lc"));
		result.add(factory.getMonolingualTextValue("bar", "lc"));
		return result;
	}

	/**
	 * Creates a list of descriptions.
	 * 
	 * <p>
	 * ID = Descs
	 * </p>
	 * 
	 * <p>
	 * <b>Default values</b>
	 * </p>
	 * <ul>
	 * <li>MonolingualTextValue: "it's foo" (description in the certain
	 * language), "lc" (LanguageCode)</li>
	 * <li>MonolingualTextValue: "it's bar" (description in the certain
	 * language), "lc2" (LanguageCode)</li>
	 * </ul>
	 * 
	 * @return List of {@link MonolingualTextValue}
	 */
	public List<MonolingualTextValue> createDescriptions() {
		List<MonolingualTextValue> result = new LinkedList<>();
		result.add(factory.getMonolingualTextValue("it's foo", "lc"));
		result.add(factory.getMonolingualTextValue("it's bar", "lc2"));
		return result;
	}

	/**
	 * Creates a map of {@link SiteLink}s with empty badges.
	 * 
	 * <p>
	 * ID = SLs
	 * </p>
	 * 
	 * <p>
	 * <b>Default values</b>
	 * </p>
	 * <ul>
	 * <li>SiteLink: name = "enwiki" title = "title_en", siteKey = "siteKey",
	 * baseIri = "test"</li>
	 * <li>SiteLink: name = "auwiki" title = "title_au", siteKey = "siteKey" ,
	 * baseIri = "test"</li>
	 * </ul>
	 * 
	 * @return Map for {@link SiteLink}s and there titles
	 */
	public Map<String, SiteLink> createSiteLinks() {
		Map<String, SiteLink> result = new HashMap<String, SiteLink>();
		result.put("enwiki", factory.getSiteLink("title_en", "siteKey",
				baseIri, new LinkedList<String>()));
		result.put("auwiki", factory.getSiteLink("title_au", "siteKey",
				baseIri, new LinkedList<String>()));
		return result;
	}

	/**
	 * Creates a list of qualifiers.
	 * 
	 * <p>
	 * ID = Quals
	 * </p>
	 * 
	 * <p>
	 * <b>Default values</b>
	 * </p>
	 * <ul>
	 * <li>ValSnakTime (
	 * <li>
	 * </ul>
	 * 
	 * @return List of {@link Snak}
	 */
	public List<? extends Snak> createQualifiers() {
		List<Snak> result = new ArrayList<Snak>();
		result.add(createValueSnakTimeValue("P15"));
		return result;
	}

	/**
	 * Create a list of {@link Reference}s (containing only one reference).
	 * 
	 * <p>
	 * ID = Refs
	 * </p>
	 * 
	 * <p>
	 * <b>Default values</b>
	 * </p>
	 * <ul>
	 * <li>reference: snaks = {@link #createValueSnakTimeValue(String)
	 * ValSnakTime}</li>
	 * </ul>
	 * 
	 * @return List of {@link Reference}
	 */
	public List<? extends Reference> createReferences() {
		List<ValueSnak> snaks = new ArrayList<ValueSnak>();
		List<Reference> refs = new ArrayList<>();
		snaks.add(createValueSnakTimeValue("P112"));
		SnakGroup snakGroup = factory.getSnakGroup(snaks);
		
		refs.add(factory.getReference(Collections.singletonList(snakGroup)));
		return refs;
	}

	/**
	 * Creates a reference containing a value snak (string)
	 * @return {@link Reference}
	 */
	public Reference createReference() {
		List<ValueSnak> snaks = new ArrayList<ValueSnak>();

		snaks.add(createValueSnakStringValue("P1"));
		SnakGroup snakGroup = factory.getSnakGroup(snaks);

		return factory.getReference(Collections.singletonList(snakGroup));
	}

	/**
	 * Creates a {@link Claim}.
	 * 
	 * <p>
	 * ID = Claim
	 * </p>
	 * 
	 * @param id
	 *            id of the subject of the {@link Claim}
	 * @param snak
	 *            mainsnak for the {@link Claim}
	 * 
	 * @return {@link Claim} with the given parameters
	 */
	public Claim createClaim(String id, Snak snak) {
		return factory.getClaim(
				factory.getItemIdValue(id, baseIri), 
				snak,
				Collections.<SnakGroup> emptyList());
	}

	/**
	 * Creates a {@link SomeValueSnak} with pId.
	 * 
	 * <p>
	 * ID = SomeValSnak
	 * </p>
	 * 
	 * <p>
	 * <b>Default values</b>
	 * </p>
	 * <ul>
	 * <li>baseIri: "test"</li>
	 * </ul>
	 * 
	 * @param pId
	 *            property-id
	 * @return {@link SomeValueSnak}
	 */
	public SomeValueSnak createSomeValueSnak(String pId) {
		return factory.getSomeValueSnak(factory
				.getPropertyIdValue(pId, baseIri));
	}

	/**
	 * Creates a {@link ValueSnak} with an {@link ItemIdValue} in it.
	 * 
	 * <p>
	 * ID = ValSnakItem
	 * </p>
	 * 
	 * <p>
	 * <b>Default values</b>
	 * </p>
	 * <ul>
	 * <li>baseIri: "test"</li>
	 * </ul>
	 * 
	 * @param pId
	 *            property-id
	 * @param qId
	 *            item-id of the containing value
	 * 
	 * @return {@link ValueSnak}
	 */
	public ValueSnak createValueSnakItemIdValue(String pId, String qId) {
		return factory.getValueSnak(factory.getPropertyIdValue(pId, baseIri),
				factory.getItemIdValue(qId, baseIri));
	}

	/**
	 * Creates a {@link ValueSnak} with an
	 * {@link org.wikidata.wdtk.datamodel.interfaces.StringValue} in it.
	 * 
	 * <p>
	 * ID = ValSnakStr
	 * </p>
	 * 
	 * <p>
	 * <b>Default values</b>
	 * </p>
	 * <ul>
	 * <li>baseIri: "test"</li>
	 * <li>String: "TestString"</li>
	 * </ul>
	 * 
	 * @param pId
	 *            property-id
	 * 
	 * @return {@link ValueSnak}
	 */
	public ValueSnak createValueSnakStringValue(String pId) {
		return factory.getValueSnak(factory.getPropertyIdValue(pId, baseIri),
				factory.getStringValue("TestString"));
	}

	/**
	 * Creates a {@link ValueSnak} with an {@link GlobeCoordinatesValue} in it.
	 * 
	 * <p>
	 * ID = ValSnakGlCo
	 * </p>
	 * 
	 * <p>
	 * <b>Default values</b>
	 * </p>
	 * <ul>
	 * <li>latitude: 213124</li>
	 * <li>longitude: 21314</li>
	 * <li>precision: 16666667</li>
	 * </ul>
	 * 
	 * @param pId
	 *            property-id
	 * 
	 * @return {@link ValueSnak}
	 */
	public ValueSnak createValueSnakGlobeCoordinatesValue(String pId) {
		return factory.getValueSnak(factory.getPropertyIdValue(pId, baseIri),
				factory.getGlobeCoordinatesValue(213124, 21314,
						GlobeCoordinatesValue.PREC_ARCMINUTE,
						"http://www.wikidata.org/entity/Q2"));
	}

	/**
	 * Creates a {@link ValueSnak} with an
	 * {@link org.wikidata.wdtk.datamodel.interfaces.QuantityValue} in it.
	 * 
	 * <p>
	 * ID = ValSnakQuant
	 * </p>
	 * 
	 * <p>
	 * <b>Default values</b>
	 * </p>
	 * <ul>
	 * <li>baseIri: "test"</li>
	 * <li>numericValue: 3</li>
	 * <li>lowerBound: 3</li>
	 * <li>upperBound: 3</li>
	 * </ul>
	 * 
	 * @param pId
	 *            property-id
	 * 
	 * @return {@link ValueSnak}
	 */
	public ValueSnak createValueSnakQuantityValue(String pId) {
		return factory.getValueSnak(factory.getPropertyIdValue(pId, baseIri),
				factory.getQuantityValue(new BigDecimal(3), new BigDecimal(3),
						new BigDecimal(3)));
	}

	/**
	 * Creates a {@link ValueSnak} with an
	 * {@link org.wikidata.wdtk.datamodel.interfaces.TimeValue} in it.
	 * 
	 * <p>
	 * ID = ValSnakTime
	 * </p>
	 * 
	 * <p>
	 * <b>Default values</b>
	 * </p>
	 * <ul>
	 * <li>baseIri: "test"</li>
	 * <li>year: 306</li>
	 * <li>month: 11</li>
	 * <li>day: 3</li>
	 * <li>hour: 13</li>
	 * <li>minute: 7</li>
	 * <li>second: 6</li>
	 * <li>precision: 32</li>
	 * <li>beforeTolerance: 17</li>
	 * <li>afterTolerance: 43</li>
	 * <li>timezoneOffset: 0</li>
	 * <li>calendarModel: "http://www.wikidata.org/entity/Q1985727"</li>
	 * </ul>
	 * 
	 * @param pId
	 *            property-id
	 * 
	 * @return {@link ValueSnak}
	 */
	public ValueSnak createValueSnakTimeValue(String pId) {
		return factory.getValueSnak(factory.getPropertyIdValue(pId, baseIri),
				factory.getTimeValue(306, (byte) 11, (byte) 3, (byte) 13,
						(byte) 7, (byte) 6, (byte) 32, 17, 43, 0,
						"http://www.wikidata.org/entity/Q1985727"));
	}

	/**
	 * Creates a {@link PropertyIdValue}.
	 * 
	 * <p>
	 * ID = PropVal
	 * </p>
	 * 
	 * <p>
	 * <b>Default values</b>
	 * </p>
	 * <ul>
	 * <li>baseIri: "test"</li>
	 * </ul>
	 * 
	 * @param id
	 *            property-id
	 * 
	 * @return {@link PropertyIdValue}
	 */
	public PropertyIdValue createPropertyIdValue(String id) {
		return factory.getPropertyIdValue(id, baseIri);
	}

	/**
	 * Creates an {@link ItemIdValue}.
	 * 
	 * <p>
	 * ID = ItemVal
	 * </p>
	 * 
	 * <p>
	 * <b>Default values</b>
	 * </p>
	 * <ul>
	 * <li>baseIri: "test"</li>
	 * </ul>
	 * 
	 * @param id
	 *            item-id
	 * 
	 * @return {@link ItemIdValue}
	 */
	public ItemIdValue createItemIdValue(String id) {
		return factory.getItemIdValue(id, baseIri);
	}

	public Map<String, SiteLink> createTestLinks() {
		
		Map<String, SiteLink> result = new HashMap<>();
		result.put("enwiki", factory.getSiteLink("test", "enwiki", baseIri, Collections.<String>singletonList("testBadge")));
		result.put("dewiki", factory.getSiteLink("TEST", "dewiki", baseIri, Collections.<String>emptyList()));
		
		return result;
	}

	public List<StatementGroup> createTestStatementGroups() {
		// NOTE: for evey subtest, the items should have a new property id
		// to avoid messed up statement groups
		
		// NOTE: In case of test bugs 
		// make sure to be consistent with the test JSON first.
		
		List<Statement> statements = new ArrayList<>();
		statements.add(this.createTestDefaultStatement());
		statements.add(this.createTestRankedStatement(StatementRank.DEPRECATED));
		statements.add(this.createTestRankedStatement(StatementRank.PREFERRED));
		statements.add(this.createTestSomevalueStatement());
		statements.add(this.createTestReferencedStatement());
		
		List<StatementGroup> result = sgBuilder.buildFromStatementList(statements);	
		
		return result;
	}

	private Statement createTestReferencedStatement() {
		Statement result = factory.getStatement(
				this.createDefaultClaim("P4"), 
				Collections.<Reference>singletonList(this.createReference()), 
				StatementRank.NORMAL, 
				"referencedTestStatement");
		return result;
	}

	private Statement createTestSomevalueStatement() {
		Statement result = factory.getStatement(
				this.createSomevalueClaim("P3"), 
				Collections.<Reference>emptyList(), 
				StatementRank.NORMAL, 
				"somevalueTestStatement");
		return result;
	}

	private Claim createSomevalueClaim(String pId) {
		return this.createClaim("Q1", this.createSomeValueSnak(pId));
	}

	private Statement createTestDefaultStatement() {
		Statement result = factory.getStatement(
				this.createDefaultClaim(), 
				Collections.<Reference>emptyList(), 
				StatementRank.NORMAL, 
				"defaultTestStatement");
		return result;
	}
	
	private Statement createTestRankedStatement(StatementRank rank){
		Statement result = factory.getStatement(
				this.createDefaultClaim("P2"), 
				Collections.<Reference>emptyList(), 
				rank, 
				"rankTestStatement");
		return result;
	}

	/**
	 * Creates just some claim with item id Q1 and a no-value-snak of property P1.
	 * @return
	 */
	private Claim createDefaultClaim(){
		return this.createClaim("Q1", this.createNoValueSnak("P1"));
	}
	
	private Claim createDefaultClaim(String snakId){
		return this.createClaim("Q1", this.createNoValueSnak(snakId));
	}

	private Snak createNoValueSnak(String id) {
		PropertyIdValue propertyId = factory.getPropertyIdValue(id, baseIri);
		NoValueSnak result = factory.getNoValueSnak(propertyId);
		return result;
	}
	
	

}
