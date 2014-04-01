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
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

/**
 * This class provides functions to create objects from
 * {@link org.wikidata.wdtk.datamodel.interfaces} with certain predefined
 * parameters.
 * 
 * @author Michael Günther, Fredo Erxleben
 * 
 */
public class TestObjectFactory {

	private DataObjectFactory factory = new DataObjectFactoryImpl();
	private static String baseIri = "test";

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

		PropertyIdValue propertyId = this.factory.getPropertyIdValue("P1",
				baseIri);
		DatatypeIdValue datatypeId = this.factory
				.getDatatypeIdValue(DatatypeIdValue.DT_GLOBE_COORDINATES);

		PropertyDocument emptyPropertyDocument = this.factory
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

		ItemIdValue itemId = this.factory.getItemIdValue("Q1",
				baseIri);

		ItemDocument emptyItemDocument = this.factory
				.getItemDocument(itemId, 
						Collections.<MonolingualTextValue> emptyList(), 
						Collections.<MonolingualTextValue> emptyList(), 
						Collections.<MonolingualTextValue> emptyList(), 
						Collections.<StatementGroup> emptyList(), 
						Collections.<String, SiteLink> emptyMap());
		return emptyItemDocument;
	}
	
	/**
	 * Creates a {@link Statement} with entity-id qId, property-id pId
	 * 
	 * <p>
	 * ID = Stat
	 * </p>
	 * 
	 * <p>
	 * <b>Default values</b>
	 * </p>
	 * <ul>
	 * <li>Rank: "normal"
	 * <li>MainSnak: {@link #createValueSnakStringValue ValSnakStr}
	 * <li>StatementId: "id111"
	 * <li>References: {@link #createReferences() Refs}
	 * </ul>
	 * 
	 * @param qId
	 * @param pId
	 * @return {@link Statement}
	 */
	public Statement createStatement(String qId, String pId) {
		return factory.getStatement(
				createClaim(qId, createValueSnakStringValue(pId)),
				createReferences(), StatementRank.NORMAL, "id111");
	}

	/**
	 * Creates a {@link StatementGroup}
	 * 
	 * <p>
	 * ID = StatGr
	 * </p>
	 * 
	 * <p>
	 * <b>Default values</b>
	 * </p>
	 * <ul>
	 * <li>Statement1: {@link #createStatement(String, String) Stat} (qId = Q10,
	 * pId= P122)</li>
	 * <li>Statement2: Statement with Rank = "normal", Mainsnak =
	 * {@link #createValueSnakQuantityValue(String) ValSnakQuant}, StatementId =
	 * "id112"</li>
	 * </ul>
	 * 
	 * @return {@link StatementGroup}
	 */
	public StatementGroup createStatementGroup() {
		final String pId = "P122";
		final String qId = "Q10";
		List<Statement> statements = new ArrayList<Statement>();
		statements.add(createStatement(qId, pId));
		statements.add(factory.getStatement(
				createClaim(qId, createValueSnakQuantityValue(pId)),
				Collections.<Reference> emptyList(), StatementRank.NORMAL,
				"id112"));
		return factory.getStatementGroup(statements);
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
	public Map<String, MonolingualTextValue> createTestAliases() {
		
		Map<String,MonolingualTextValue> result = new HashMap<>();
		result.put("en", factory.getMonolingualTextValue("testAlias", "en"));
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
		refs.add(factory.getReference(snaks));
		return refs;
	}

	/**
	 * Creates a {@link Reference}.
	 * 
	 * <p>
	 * ID = Ref
	 * </p>
	 * 
	 * <p>
	 * <b>DefualtValues</b>
	 * </p>
	 * <ul>
	 * <li>Snak1: {@link #createValueSnakGlobeCoordinatesValue(String)
	 * ValSnakGlCo (pId = P232)}</li>
	 * <li>Snak2: {@link #createValueSnakQuantityValue(String) ValSnakQuant (pId
	 * = 211)}</li>
	 * </ul>
	 * 
	 * @return {@link Reference}
	 */
	public Reference createReference() {
		List<ValueSnak> snaks = new ArrayList<ValueSnak>();

		snaks.add(createValueSnakGlobeCoordinatesValue("P232"));
		snaks.add(createValueSnakQuantityValue("P211"));

		return factory.getReference(snaks);
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
		return factory.getClaim(factory.getItemIdValue(id, baseIri), snak,
				Collections.<Snak> emptyList());
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

}
