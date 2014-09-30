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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wikidata.wdtk.datamodel.helpers.DataModelConverter;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.Claim;
import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.NoValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.Value;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonInnerGlobeCoordinates;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonInnerMonolingualText;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonInnerQuantity;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonInnerTime;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonValue;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonValueGlobeCoordinates;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonValueMonolingualText;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonValueQuantity;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonValueString;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonValueTime;

/**
 * Factory implementation to create Jackson versions of the datamodel objects,
 * where available.
 *
 * @author Markus Kroetzsch
 *
 */
public class JacksonObjectFactory implements DataObjectFactory {

	private final DataModelConverter dataModelConverter = new DataModelConverter(
			this);

	@Override
	public ItemIdValue getItemIdValue(String id, String siteIri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PropertyIdValue getPropertyIdValue(String id, String siteIri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DatatypeIdValue getDatatypeIdValue(String id) {
		return Datamodel.makeDatatypeIdValue(id);
	}

	@Override
	public TimeValue getTimeValue(long year, byte month, byte day, byte hour,
			byte minute, byte second, byte precision, int beforeTolerance,
			int afterTolerance, int timezoneOffset, String calendarModel) {
		JacksonInnerTime innerTime = new JacksonInnerTime(year, month, day,
				hour, minute, second, timezoneOffset, beforeTolerance,
				afterTolerance, precision, calendarModel);
		JacksonValueTime result = new JacksonValueTime();
		result.setValue(innerTime);
		return result;
	}

	@Override
	public GlobeCoordinatesValue getGlobeCoordinatesValue(long latitude,
			long longitude, long precision, String globeIri) {
		JacksonInnerGlobeCoordinates innerCoordinates = new JacksonInnerGlobeCoordinates(
				latitude, longitude, precision, globeIri);
		JacksonValueGlobeCoordinates result = new JacksonValueGlobeCoordinates();
		result.setValue(innerCoordinates);
		return result;
	}

	@Override
	public StringValue getStringValue(String string) {
		JacksonValueString result = new JacksonValueString();
		result.setValue(string);
		return result;
	}

	@Override
	public MonolingualTextValue getMonolingualTextValue(String text,
			String languageCode) {
		JacksonInnerMonolingualText innerMtlv = new JacksonInnerMonolingualText(
				languageCode, text);
		JacksonValueMonolingualText result = new JacksonValueMonolingualText();
		result.setValue(innerMtlv);
		return result;
	}

	@Override
	public QuantityValue getQuantityValue(BigDecimal numericValue,
			BigDecimal lowerBound, BigDecimal upperBound) {
		JacksonInnerQuantity innerQuantity = new JacksonInnerQuantity(
				numericValue, upperBound, lowerBound);
		JacksonValueQuantity result = new JacksonValueQuantity();
		result.setValue(innerQuantity);
		return result;
	}

	/**
	 * Creates a {@link JacksonValueSnak}. Value snaks in JSON need to know the
	 * datatype of their property, which is not given in the parameters of this
	 * method. The snak that will be returned will use a default type based on
	 * the kind of value that is used (usually the "simplest" type for that
	 * value). This may not be desired.
	 *
	 * @see DataObjectFactory#getValueSnak(PropertyIdValue, Value)
	 */
	@Override
	public ValueSnak getValueSnak(PropertyIdValue propertyId, Value value) {
		JacksonValueSnak result = new JacksonValueSnak();
		result.setProperty(propertyId.getId());
		if (value instanceof JacksonValue) {
			return getJacksonValueSnak(propertyId.getId(),
					(JacksonValue) value,
					getDefaultJsonPropertyTypeForValueType(value));
		} else {
			return getJacksonValueSnak(propertyId.getId(),
					(JacksonValue) this.dataModelConverter.convertValue(value),
					getDefaultJsonPropertyTypeForValueType(value));
		}
	}

	@Override
	public SomeValueSnak getSomeValueSnak(PropertyIdValue propertyId) {
		JacksonSomeValueSnak result = new JacksonSomeValueSnak();
		result.setProperty(propertyId.getId());
		return result;
	}

	@Override
	public NoValueSnak getNoValueSnak(PropertyIdValue propertyId) {
		JacksonNoValueSnak result = new JacksonNoValueSnak();
		result.setProperty(propertyId.getId());
		return result;
	}

	@Override
	public SnakGroup getSnakGroup(List<? extends Snak> snaks) {
		return Datamodel.makeSnakGroup(convertSnakList(snaks));
	}

	@Override
	public Claim getClaim(EntityIdValue subject, Snak mainSnak,
			List<SnakGroup> qualifiers) {
		// Jackson claims cannot exist without a statement.
		Statement statement = getStatement(
				Datamodel.makeClaim(subject, mainSnak, qualifiers),
				Collections.<Reference> emptyList(), StatementRank.NORMAL,
				"empty id 12345");
		return statement.getClaim();
	}

	@Override
	public Reference getReference(List<SnakGroup> snakGroups) {
		JacksonReference result = new JacksonReference();
		Map<String, List<JacksonSnak>> snakMap = new HashMap<>();
		for (SnakGroup snakGroup : snakGroups) {
			snakMap.put(snakGroup.getProperty().getId(),
					convertSnakList(snakGroup.getSnaks()));
		}
		result.setSnaks(snakMap);
		return result;
	}

	@Override
	public Statement getStatement(Claim claim,
			List<? extends Reference> references, StatementRank rank,
			String statementId) {
		JacksonStatement result = new JacksonStatement();
		result.setMainsnak((JacksonSnak) dataModelConverter.convertSnak(claim
				.getMainSnak()));

		Map<String, List<JacksonSnak>> qualifiers = new HashMap<>();
		for (SnakGroup sg : claim.getQualifiers()) {
			qualifiers.put(sg.getProperty().getId(),
					convertSnakList(sg.getSnaks()));
		}
		result.setQualifiers(qualifiers);

		List<JacksonReference> jacksonReferences = new ArrayList<>(
				references.size());
		for (Reference reference : references) {
			if (reference instanceof JacksonReference) {
				jacksonReferences.add((JacksonReference) reference);
			} else {
				jacksonReferences.add((JacksonReference) dataModelConverter
						.convert(reference));
			}
		}
		result.setReferences(jacksonReferences);

		result.setRank(rank);
		result.setStatementId(statementId);

		JacksonItemDocument helperParentDocument = new JacksonItemDocument();
		helperParentDocument.setJsonId(claim.getSubject().getId());
		helperParentDocument.setSiteIri(claim.getSubject().getIri());
		result.setParentDocument(helperParentDocument);

		return result;
	}

	@Override
	public StatementGroup getStatementGroup(List<Statement> statements) {
		List<Statement> newStatements = new ArrayList<>(statements.size());
		for (Statement statement : statements) {
			if (statement instanceof JacksonStatement) {
				newStatements.add(statement);
			} else {
				newStatements.add(this.dataModelConverter.convert(statement));
			}
		}
		return Datamodel.makeStatementGroup(statements);
	}

	@Override
	public SiteLink getSiteLink(String title, String siteKey,
			List<String> badges) {
		JacksonSiteLink result = new JacksonSiteLink();
		result.setPageTitle(title);
		result.setSiteKey(siteKey);
		result.setBadges(badges);
		return result;
	}

	@Override
	public PropertyDocument getPropertyDocument(PropertyIdValue propertyId,
			List<MonolingualTextValue> labels,
			List<MonolingualTextValue> descriptions,
			List<MonolingualTextValue> aliases, DatatypeIdValue datatypeId) {
		JacksonPropertyDocument result = new JacksonPropertyDocument();
		initializeTermedDocument(result, propertyId, labels, descriptions,
				aliases);

		switch (datatypeId.getIri()) {
		case DatatypeIdValue.DT_ITEM:
			result.setJsonDatatype(JacksonDatatypeId.JSON_DT_ITEM);
			break;
		case DatatypeIdValue.DT_GLOBE_COORDINATES:
			result.setJsonDatatype(JacksonDatatypeId.JSON_DT_GLOBE_COORDINATES);
			break;
		case DatatypeIdValue.DT_URL:
			result.setJsonDatatype(JacksonDatatypeId.JSON_DT_URL);
			break;
		case DatatypeIdValue.DT_COMMONS_MEDIA:
			result.setJsonDatatype(JacksonDatatypeId.JSON_DT_COMMONS_MEDIA);
			break;
		case DatatypeIdValue.DT_TIME:
			result.setJsonDatatype(JacksonDatatypeId.JSON_DT_TIME);
			break;
		case DatatypeIdValue.DT_QUANTITY:
			result.setJsonDatatype(JacksonDatatypeId.JSON_DT_QUANTITY);
			break;
		case DatatypeIdValue.DT_STRING:
			result.setJsonDatatype(JacksonDatatypeId.JSON_DT_STRING);
			break;
		default:
			throw new IllegalArgumentException("Unknown datatype: "
					+ datatypeId.getIri());
		}

		return null;
	}

	@Override
	public ItemDocument getItemDocument(ItemIdValue itemIdValue,
			List<MonolingualTextValue> labels,
			List<MonolingualTextValue> descriptions,
			List<MonolingualTextValue> aliases,
			List<StatementGroup> statementGroups,
			Map<String, SiteLink> siteLinks) {
		JacksonItemDocument result = new JacksonItemDocument();
		initializeTermedDocument(result, itemIdValue, labels, descriptions,
				aliases);

		Map<String, List<JacksonStatement>> jacksonStatements = new HashMap<>();
		for (StatementGroup sg : statementGroups) {
			String propertyId = sg.getProperty().getId();
			List<JacksonStatement> propertyStatements = new ArrayList<>(sg
					.getStatements().size());
			jacksonStatements.put(propertyId, propertyStatements);

			for (Statement s : sg) {
				if (s instanceof JacksonStatement) {
					propertyStatements.add((JacksonStatement) s);
				} else {
					propertyStatements
							.add((JacksonStatement) this.dataModelConverter
									.convert(s));
				}
			}
		}
		result.setJsonClaims(jacksonStatements);

		Map<String, JacksonSiteLink> jacksonSiteLinks = new HashMap<>(
				siteLinks.size());
		for (SiteLink siteLink : siteLinks.values()) {
			if (siteLink instanceof JacksonSiteLink) {
				jacksonSiteLinks.put(siteLink.getSiteKey(),
						(JacksonSiteLink) siteLink);
			} else {
				jacksonSiteLinks.put(siteLink.getSiteKey(),
						(JacksonSiteLink) this.dataModelConverter
								.convert(siteLink));
			}
		}
		result.setSiteLinks(jacksonSiteLinks);

		return result;
	}

	private void initializeTermedDocument(JacksonTermedDocument document,
			EntityIdValue entityIdValue, List<MonolingualTextValue> labels,
			List<MonolingualTextValue> descriptions,
			List<MonolingualTextValue> aliases) {

		document.setJsonId(entityIdValue.getId());
		document.setSiteIri(entityIdValue.getSiteIri());

		Map<String, List<JacksonMonolingualTextValue>> aliasMap = new HashMap<>();
		for (MonolingualTextValue mltv : aliases) {
			List<JacksonMonolingualTextValue> langAliases = aliasMap.get(mltv
					.getLanguageCode());
			if (langAliases == null) {
				langAliases = new ArrayList<>();
				aliasMap.put(mltv.getLanguageCode(), langAliases);
			}
			langAliases.add(convertMltv(mltv));
		}
		document.setAliases(aliasMap);

		document.setLabels(buildTermMapFromTermList(labels));
		document.setDescriptions(buildTermMapFromTermList(descriptions));
	}

	private Map<String, JacksonMonolingualTextValue> buildTermMapFromTermList(
			List<MonolingualTextValue> terms) {
		Map<String, JacksonMonolingualTextValue> result = new HashMap<>(
				terms.size());
		for (MonolingualTextValue mltv : terms) {
			result.put(mltv.getLanguageCode(), convertMltv(mltv));
		}
		return result;
	}

	private JacksonMonolingualTextValue convertMltv(MonolingualTextValue mltv) {
		if (mltv instanceof JacksonMonolingualTextValue) {
			return (JacksonMonolingualTextValue) mltv;
		} else {
			return new JacksonMonolingualTextValue(mltv);
		}
	}

	private ValueSnak getJacksonValueSnak(String propertyId,
			JacksonValue value, String propertyDatatype) {
		JacksonValueSnak result = new JacksonValueSnak();
		result.setProperty(propertyId);
		result.setDatavalue(value);
		result.setDatatype(propertyDatatype);
		return result;
	}

	private String getDefaultJsonPropertyTypeForValueType(Value value) {
		if (value instanceof TimeValue) {
			return JacksonDatatypeId.JSON_DT_TIME;
		} else if (value instanceof ItemIdValue) {
			return JacksonDatatypeId.JSON_DT_ITEM;
		} else if (value instanceof StringValue) {
			return JacksonDatatypeId.JSON_DT_STRING;
		} else if (value instanceof GlobeCoordinatesValue) {
			return JacksonDatatypeId.JSON_DT_GLOBE_COORDINATES;
		} else if (value instanceof MonolingualTextValue) {
			return JacksonDatatypeId.JSON_DT_MONOLINGUAL_TEXT;
		} else {
			throw new UnsupportedOperationException("Unsupported value type "
					+ value.getClass());
		}
	}

	private List<JacksonSnak> convertSnakList(List<? extends Snak> snaks) {
		List<JacksonSnak> result = new ArrayList<>(snaks.size());
		for (Snak snak : snaks) {
			if (snak instanceof JacksonSnak) {
				result.add((JacksonSnak) snak);
			} else {
				result.add((JacksonSnak) dataModelConverter.convertSnak(snak));
			}
		}
		return result;
	}
}
