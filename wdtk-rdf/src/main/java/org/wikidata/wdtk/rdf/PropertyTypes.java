package org.wikidata.wdtk.rdf;

/*
 * #%L
 * Wikidata Toolkit RDF
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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.Value;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;
import org.wikidata.wdtk.util.WebResourceFetcher;
import org.wikidata.wdtk.util.WebResourceFetcherImpl;

/**
 * This class helps to manage the exact datatype of properties used in an RDF
 * dump. It caches known types and fetches type information from the Web if
 * needed.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class PropertyTypes implements ValueVisitor<String> {

	static final Logger logger = LoggerFactory.getLogger(PropertyTypes.class);

	final Map<String, String> propertyTypes;

	PropertyIdValue propertyRegister = null;

	final static String WIKIBASE = "http://www.wikidata.org/ontology#";

	public PropertyTypes() {
		this.propertyTypes = new HashMap<String, String>();
		this.propertyTypes.putAll(PropertyTypes.KNOWN_PROPERTY_TYPES);
	}

	public String getPropertyType(PropertyIdValue propertyIdValue) {
		if (!propertyTypes.containsKey(propertyIdValue.getId())) {
			try {
				propertyTypes.put(propertyIdValue.getId(),
						fetchPropertyType(propertyIdValue));
			} catch (IOException e) {
				
				logger.error(e.toString());
			}
		}
		return propertyTypes.get(propertyIdValue.getId());
	}

	public void setPropertyType(PropertyIdValue propertyIdValue,
			String datatypeIri) {
		propertyTypes.put(propertyIdValue.getId(), datatypeIri);
	}

	public String setPropertyTypeFromValue(PropertyIdValue propertyIdValue,
			Value value) {
		if (!propertyTypes.containsKey(propertyIdValue.getId())) {
			value.accept(this);
		}
		return propertyTypes.get(propertyIdValue);
	}

	/**
	 * Find the datatype of a property online.
	 * 
	 * @param propertyIdValue
	 * @return
	 * @throws IOException
	 */
	String fetchPropertyType(PropertyIdValue propertyIdValue)
			throws IOException {
		WebResourceFetcher webResourceFetcher = new WebResourceFetcherImpl();
		URIBuilder uriBuilder = new URIBuilder();
		uriBuilder.setScheme("http");
		uriBuilder.setHost("www.wikidata.org");
		uriBuilder.setPath("/w/api.php");
		uriBuilder.setParameter("action", "wbgetentities");
		uriBuilder.setParameter("ids", propertyIdValue.getId());
		uriBuilder.setParameter("format", "json");
		uriBuilder.setParameter("props", "datatype");
		System.out.println(uriBuilder.toString());
		InputStream inStream = webResourceFetcher
				.getInputStreamForUrl(uriBuilder.toString());
		JSONObject jsonResult = new JSONObject(IOUtils.toString(inStream));
		return jsonResult.getJSONObject("entities")
				.getJSONObject(propertyIdValue.getId()).getString("datatype");
	}

	public void registerProperty(PropertyIdValue propertyIdValue) {
		propertyRegister = propertyIdValue;
	}

	@Override
	public String visit(DatatypeIdValue value) {
		// No property datatype currently uses this
		return null;
	}

	@Override
	public String visit(EntityIdValue value) {
		// Only Items can be used as entity values so far
		return DatatypeIdValue.DT_ITEM;
	}

	@Override
	public String visit(GlobeCoordinatesValue value) {
		return DatatypeIdValue.DT_GLOBE_COORDINATES;
	}

	@Override
	public String visit(MonolingualTextValue value) {
		// No property datatype currently uses this
		return null;
	}

	@Override
	public String visit(QuantityValue value) {
		return DatatypeIdValue.DT_QUANTITY;
	}

	@Override
	public String visit(StringValue value) {
		if (propertyRegister != null) {
			switch (getPropertyType(propertyRegister)) {
			case "string":
				return DatatypeIdValue.DT_STRING;
			case "commonsMedia":
				return DatatypeIdValue.DT_COMMONS_MEDIA;
			default:
				logger.error("Unknown string type"
						+ getPropertyType(propertyRegister));
			}
		}
		return null;
	}

	@Override
	public String visit(TimeValue value) {
		return DatatypeIdValue.DT_TIME;
	}

	static Map<String, String> KNOWN_PROPERTY_TYPES = new HashMap<String, String>();
	static {
		KNOWN_PROPERTY_TYPES.put("P10", "commonsMedia");
		KNOWN_PROPERTY_TYPES.put("P1001", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P1002", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P1003", "string");
		KNOWN_PROPERTY_TYPES.put("P1004", "string");
		KNOWN_PROPERTY_TYPES.put("P1005", "string");
		KNOWN_PROPERTY_TYPES.put("P1006", "string");
		KNOWN_PROPERTY_TYPES.put("P101", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P1013", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P1014", "string");
		KNOWN_PROPERTY_TYPES.put("P1015", "string");
		KNOWN_PROPERTY_TYPES.put("P1016", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P1017", "string");
		KNOWN_PROPERTY_TYPES.put("P1019", "url");
		KNOWN_PROPERTY_TYPES.put("P102", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P1025", "string");
		KNOWN_PROPERTY_TYPES.put("P1027", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P103", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P1030", "string");
		KNOWN_PROPERTY_TYPES.put("P1031", "string");
		KNOWN_PROPERTY_TYPES.put("P1033", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P1034", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P1036", "string");
		KNOWN_PROPERTY_TYPES.put("P1037", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P1038", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P1039", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P1040", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P1042", "string");
		KNOWN_PROPERTY_TYPES.put("P1044", "string");
		KNOWN_PROPERTY_TYPES.put("P1047", "string");
		KNOWN_PROPERTY_TYPES.put("P1048", "string");
		KNOWN_PROPERTY_TYPES.put("P105", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P1054", "string");
		KNOWN_PROPERTY_TYPES.put("P1055", "string");
		KNOWN_PROPERTY_TYPES.put("P1056", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P1058", "string");
		KNOWN_PROPERTY_TYPES.put("P1059", "string");
		KNOWN_PROPERTY_TYPES.put("P106", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P1065", "url");
		KNOWN_PROPERTY_TYPES.put("P1066", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P1067", "string");
		KNOWN_PROPERTY_TYPES.put("P1069", "string");
		KNOWN_PROPERTY_TYPES.put("P107", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P1070", "string");
		KNOWN_PROPERTY_TYPES.put("P1074", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P1075", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P1076", "string");
		KNOWN_PROPERTY_TYPES.put("P1077", "string");
		KNOWN_PROPERTY_TYPES.put("P108", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P1080", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P1081", "quantity");
		KNOWN_PROPERTY_TYPES.put("P1082", "quantity");
		KNOWN_PROPERTY_TYPES.put("P1085", "string");
		KNOWN_PROPERTY_TYPES.put("P1086", "quantity");
		KNOWN_PROPERTY_TYPES.put("P109", "commonsMedia");
		KNOWN_PROPERTY_TYPES.put("P1092", "quantity");
		KNOWN_PROPERTY_TYPES.put("P110", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P1100", "quantity");
		KNOWN_PROPERTY_TYPES.put("P1101", "quantity");
		KNOWN_PROPERTY_TYPES.put("P1103", "quantity");
		KNOWN_PROPERTY_TYPES.put("P1104", "quantity");
		KNOWN_PROPERTY_TYPES.put("P1107", "quantity");
		KNOWN_PROPERTY_TYPES.put("P1108", "quantity");
		KNOWN_PROPERTY_TYPES.put("P1110", "quantity");
		KNOWN_PROPERTY_TYPES.put("P1113", "quantity");
		KNOWN_PROPERTY_TYPES.put("P1114", "quantity");
		KNOWN_PROPERTY_TYPES.put("P1115", "string");
		KNOWN_PROPERTY_TYPES.put("P1118", "quantity");
		KNOWN_PROPERTY_TYPES.put("P1119", "quantity");
		KNOWN_PROPERTY_TYPES.put("P112", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P1120", "quantity");
		KNOWN_PROPERTY_TYPES.put("P1121", "quantity");
		KNOWN_PROPERTY_TYPES.put("P1128", "quantity");
		KNOWN_PROPERTY_TYPES.put("P113", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P1130", "quantity");
		KNOWN_PROPERTY_TYPES.put("P1132", "quantity");
		KNOWN_PROPERTY_TYPES.put("P1134", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P114", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P1142", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P1144", "string");
		KNOWN_PROPERTY_TYPES.put("P1146", "string");
		KNOWN_PROPERTY_TYPES.put("P1148", "quantity");
		KNOWN_PROPERTY_TYPES.put("P1149", "string");
		KNOWN_PROPERTY_TYPES.put("P115", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P1150", "string");
		KNOWN_PROPERTY_TYPES.put("P117", "commonsMedia");
		KNOWN_PROPERTY_TYPES.put("P118", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P119", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P121", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P122", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P123", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P126", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P127", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P131", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P132", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P133", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P134", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P135", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P136", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P137", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P138", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P14", "commonsMedia");
		KNOWN_PROPERTY_TYPES.put("P140", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P141", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P143", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P144", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P149", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P15", "commonsMedia");
		KNOWN_PROPERTY_TYPES.put("P150", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P154", "commonsMedia");
		KNOWN_PROPERTY_TYPES.put("P155", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P156", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P157", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P158", "commonsMedia");
		KNOWN_PROPERTY_TYPES.put("P159", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P16", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P161", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P162", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P163", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P166", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P167", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P168", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P169", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P17", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P170", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P171", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P172", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P173", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P175", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P176", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P177", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P178", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P179", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P18", "commonsMedia");
		KNOWN_PROPERTY_TYPES.put("P180", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P181", "commonsMedia");
		KNOWN_PROPERTY_TYPES.put("P183", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P184", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P185", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P186", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P189", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P19", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P190", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P193", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P194", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P195", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P196", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P197", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P198", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P199", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P20", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P200", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P201", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P202", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P205", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P206", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P208", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P209", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P21", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P210", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P212", "string");
		KNOWN_PROPERTY_TYPES.put("P213", "string");
		KNOWN_PROPERTY_TYPES.put("P214", "string");
		KNOWN_PROPERTY_TYPES.put("P215", "string");
		KNOWN_PROPERTY_TYPES.put("P217", "string");
		KNOWN_PROPERTY_TYPES.put("P218", "string");
		KNOWN_PROPERTY_TYPES.put("P219", "string");
		KNOWN_PROPERTY_TYPES.put("P22", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P220", "string");
		KNOWN_PROPERTY_TYPES.put("P223", "string");
		KNOWN_PROPERTY_TYPES.put("P225", "string");
		KNOWN_PROPERTY_TYPES.put("P227", "string");
		KNOWN_PROPERTY_TYPES.put("P229", "string");
		KNOWN_PROPERTY_TYPES.put("P230", "string");
		KNOWN_PROPERTY_TYPES.put("P231", "string");
		KNOWN_PROPERTY_TYPES.put("P232", "string");
		KNOWN_PROPERTY_TYPES.put("P233", "string");
		KNOWN_PROPERTY_TYPES.put("P234", "string");
		KNOWN_PROPERTY_TYPES.put("P235", "string");
		KNOWN_PROPERTY_TYPES.put("P236", "string");
		KNOWN_PROPERTY_TYPES.put("P237", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P238", "string");
		KNOWN_PROPERTY_TYPES.put("P239", "string");
		KNOWN_PROPERTY_TYPES.put("P240", "string");
		KNOWN_PROPERTY_TYPES.put("P241", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P242", "commonsMedia");
		KNOWN_PROPERTY_TYPES.put("P243", "string");
		KNOWN_PROPERTY_TYPES.put("P244", "string");
		KNOWN_PROPERTY_TYPES.put("P245", "string");
		KNOWN_PROPERTY_TYPES.put("P246", "string");
		KNOWN_PROPERTY_TYPES.put("P247", "string");
		KNOWN_PROPERTY_TYPES.put("P248", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P249", "string");
		KNOWN_PROPERTY_TYPES.put("P25", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P26", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P263", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P264", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P268", "string");
		KNOWN_PROPERTY_TYPES.put("P269", "string");
		KNOWN_PROPERTY_TYPES.put("P27", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P270", "string");
		KNOWN_PROPERTY_TYPES.put("P271", "string");
		KNOWN_PROPERTY_TYPES.put("P272", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P274", "string");
		KNOWN_PROPERTY_TYPES.put("P275", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P276", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P277", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P279", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P281", "string");
		KNOWN_PROPERTY_TYPES.put("P282", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P286", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P287", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P289", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P291", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P295", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P296", "string");
		KNOWN_PROPERTY_TYPES.put("P297", "string");
		KNOWN_PROPERTY_TYPES.put("P298", "string");
		KNOWN_PROPERTY_TYPES.put("P299", "string");
		KNOWN_PROPERTY_TYPES.put("P30", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P300", "string");
		KNOWN_PROPERTY_TYPES.put("P301", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P304", "string");
		KNOWN_PROPERTY_TYPES.put("P306", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P31", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P344", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P345", "string");
		KNOWN_PROPERTY_TYPES.put("P347", "string");
		KNOWN_PROPERTY_TYPES.put("P348", "string");
		KNOWN_PROPERTY_TYPES.put("P349", "string");
		KNOWN_PROPERTY_TYPES.put("P35", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P350", "string");
		KNOWN_PROPERTY_TYPES.put("P352", "string");
		KNOWN_PROPERTY_TYPES.put("P355", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P356", "string");
		KNOWN_PROPERTY_TYPES.put("P357", "string");
		KNOWN_PROPERTY_TYPES.put("P358", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P359", "string");
		KNOWN_PROPERTY_TYPES.put("P36", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P360", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P361", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P364", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P366", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P367", "commonsMedia");
		KNOWN_PROPERTY_TYPES.put("P37", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P370", "string");
		KNOWN_PROPERTY_TYPES.put("P371", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P373", "string");
		KNOWN_PROPERTY_TYPES.put("P374", "string");
		KNOWN_PROPERTY_TYPES.put("P375", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P376", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P377", "string");
		KNOWN_PROPERTY_TYPES.put("P38", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P380", "string");
		KNOWN_PROPERTY_TYPES.put("P381", "string");
		KNOWN_PROPERTY_TYPES.put("P382", "string");
		KNOWN_PROPERTY_TYPES.put("P387", "string");
		KNOWN_PROPERTY_TYPES.put("P39", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P392", "string");
		KNOWN_PROPERTY_TYPES.put("P393", "string");
		KNOWN_PROPERTY_TYPES.put("P395", "string");
		KNOWN_PROPERTY_TYPES.put("P396", "string");
		KNOWN_PROPERTY_TYPES.put("P397", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P40", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P400", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P402", "string");
		KNOWN_PROPERTY_TYPES.put("P403", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P404", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P405", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P406", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P407", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P408", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P409", "string");
		KNOWN_PROPERTY_TYPES.put("P41", "commonsMedia");
		KNOWN_PROPERTY_TYPES.put("P410", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P412", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P413", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P414", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P417", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P418", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P421", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P424", "string");
		KNOWN_PROPERTY_TYPES.put("P425", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P426", "string");
		KNOWN_PROPERTY_TYPES.put("P427", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P428", "string");
		KNOWN_PROPERTY_TYPES.put("P429", "string");
		KNOWN_PROPERTY_TYPES.put("P43", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P432", "string");
		KNOWN_PROPERTY_TYPES.put("P433", "string");
		KNOWN_PROPERTY_TYPES.put("P434", "string");
		KNOWN_PROPERTY_TYPES.put("P435", "string");
		KNOWN_PROPERTY_TYPES.put("P436", "string");
		KNOWN_PROPERTY_TYPES.put("P437", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P438", "string");
		KNOWN_PROPERTY_TYPES.put("P439", "string");
		KNOWN_PROPERTY_TYPES.put("P44", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P440", "string");
		KNOWN_PROPERTY_TYPES.put("P442", "string");
		KNOWN_PROPERTY_TYPES.put("P443", "commonsMedia");
		KNOWN_PROPERTY_TYPES.put("P444", "string");
		KNOWN_PROPERTY_TYPES.put("P447", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P448", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P449", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P45", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P450", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P451", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P452", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P453", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P454", "string");
		KNOWN_PROPERTY_TYPES.put("P455", "string");
		KNOWN_PROPERTY_TYPES.put("P457", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P458", "string");
		KNOWN_PROPERTY_TYPES.put("P459", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P460", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P461", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P462", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P463", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P465", "string");
		KNOWN_PROPERTY_TYPES.put("P466", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P467", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P47", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P473", "string");
		KNOWN_PROPERTY_TYPES.put("P474", "string");
		KNOWN_PROPERTY_TYPES.put("P477", "string");
		KNOWN_PROPERTY_TYPES.put("P478", "string");
		KNOWN_PROPERTY_TYPES.put("P480", "string");
		KNOWN_PROPERTY_TYPES.put("P484", "string");
		KNOWN_PROPERTY_TYPES.put("P485", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P486", "string");
		KNOWN_PROPERTY_TYPES.put("P487", "string");
		KNOWN_PROPERTY_TYPES.put("P488", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P489", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P490", "string");
		KNOWN_PROPERTY_TYPES.put("P492", "string");
		KNOWN_PROPERTY_TYPES.put("P493", "string");
		KNOWN_PROPERTY_TYPES.put("P494", "string");
		KNOWN_PROPERTY_TYPES.put("P495", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P497", "string");
		KNOWN_PROPERTY_TYPES.put("P498", "string");
		KNOWN_PROPERTY_TYPES.put("P50", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P500", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P501", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P504", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P506", "string");
		KNOWN_PROPERTY_TYPES.put("P508", "string");
		KNOWN_PROPERTY_TYPES.put("P509", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P51", "commonsMedia");
		KNOWN_PROPERTY_TYPES.put("P511", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P512", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P513", "string");
		KNOWN_PROPERTY_TYPES.put("P516", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P518", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P520", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P521", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P522", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P523", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P524", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P525", "string");
		KNOWN_PROPERTY_TYPES.put("P527", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P528", "string");
		KNOWN_PROPERTY_TYPES.put("P529", "string");
		KNOWN_PROPERTY_TYPES.put("P53", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P530", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P531", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P532", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P535", "string");
		KNOWN_PROPERTY_TYPES.put("P536", "string");
		KNOWN_PROPERTY_TYPES.put("P539", "string");
		KNOWN_PROPERTY_TYPES.put("P54", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P542", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P543", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P545", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P547", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P549", "string");
		KNOWN_PROPERTY_TYPES.put("P551", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P552", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P553", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P554", "string");
		KNOWN_PROPERTY_TYPES.put("P555", "string");
		KNOWN_PROPERTY_TYPES.put("P557", "string");
		KNOWN_PROPERTY_TYPES.put("P558", "string");
		KNOWN_PROPERTY_TYPES.put("P559", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P560", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P561", "string");
		KNOWN_PROPERTY_TYPES.put("P562", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P563", "string");
		KNOWN_PROPERTY_TYPES.put("P564", "string");
		KNOWN_PROPERTY_TYPES.put("P566", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P569", "time");
		KNOWN_PROPERTY_TYPES.put("P57", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P570", "time");
		KNOWN_PROPERTY_TYPES.put("P571", "time");
		KNOWN_PROPERTY_TYPES.put("P574", "time");
		KNOWN_PROPERTY_TYPES.put("P575", "time");
		KNOWN_PROPERTY_TYPES.put("P576", "time");
		KNOWN_PROPERTY_TYPES.put("P577", "time");
		KNOWN_PROPERTY_TYPES.put("P579", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P58", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P580", "time");
		KNOWN_PROPERTY_TYPES.put("P582", "time");
		KNOWN_PROPERTY_TYPES.put("P585", "time");
		KNOWN_PROPERTY_TYPES.put("P586", "string");
		KNOWN_PROPERTY_TYPES.put("P587", "string");
		KNOWN_PROPERTY_TYPES.put("P59", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P590", "string");
		KNOWN_PROPERTY_TYPES.put("P592", "string");
		KNOWN_PROPERTY_TYPES.put("P597", "string");
		KNOWN_PROPERTY_TYPES.put("P599", "string");
		KNOWN_PROPERTY_TYPES.put("P6", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P60", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P600", "string");
		KNOWN_PROPERTY_TYPES.put("P604", "string");
		KNOWN_PROPERTY_TYPES.put("P605", "string");
		KNOWN_PROPERTY_TYPES.put("P606", "time");
		KNOWN_PROPERTY_TYPES.put("P607", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P608", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P609", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P61", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P610", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P611", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P612", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P613", "string");
		KNOWN_PROPERTY_TYPES.put("P618", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P619", "time");
		KNOWN_PROPERTY_TYPES.put("P624", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P625", "globe-coordinate");
		KNOWN_PROPERTY_TYPES.put("P627", "string");
		KNOWN_PROPERTY_TYPES.put("P629", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P630", "string");
		KNOWN_PROPERTY_TYPES.put("P631", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P633", "string");
		KNOWN_PROPERTY_TYPES.put("P634", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P635", "string");
		KNOWN_PROPERTY_TYPES.put("P637", "string");
		KNOWN_PROPERTY_TYPES.put("P638", "string");
		KNOWN_PROPERTY_TYPES.put("P640", "string");
		KNOWN_PROPERTY_TYPES.put("P641", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P642", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P646", "string");
		KNOWN_PROPERTY_TYPES.put("P648", "string");
		KNOWN_PROPERTY_TYPES.put("P649", "string");
		KNOWN_PROPERTY_TYPES.put("P65", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P653", "string");
		KNOWN_PROPERTY_TYPES.put("P655", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P657", "string");
		KNOWN_PROPERTY_TYPES.put("P658", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P66", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P661", "string");
		KNOWN_PROPERTY_TYPES.put("P662", "string");
		KNOWN_PROPERTY_TYPES.put("P664", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P665", "string");
		KNOWN_PROPERTY_TYPES.put("P668", "string");
		KNOWN_PROPERTY_TYPES.put("P669", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P670", "string");
		KNOWN_PROPERTY_TYPES.put("P672", "string");
		KNOWN_PROPERTY_TYPES.put("P673", "string");
		KNOWN_PROPERTY_TYPES.put("P674", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P676", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P677", "string");
		KNOWN_PROPERTY_TYPES.put("P680", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P681", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P682", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P683", "string");
		KNOWN_PROPERTY_TYPES.put("P685", "string");
		KNOWN_PROPERTY_TYPES.put("P686", "string");
		KNOWN_PROPERTY_TYPES.put("P687", "string");
		KNOWN_PROPERTY_TYPES.put("P69", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P691", "string");
		KNOWN_PROPERTY_TYPES.put("P694", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P695", "string");
		KNOWN_PROPERTY_TYPES.put("P697", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P7", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P70", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P702", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P703", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P705", "string");
		KNOWN_PROPERTY_TYPES.put("P706", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P708", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P709", "string");
		KNOWN_PROPERTY_TYPES.put("P71", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P710", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P711", "string");
		KNOWN_PROPERTY_TYPES.put("P712", "string");
		KNOWN_PROPERTY_TYPES.put("P713", "string");
		KNOWN_PROPERTY_TYPES.put("P714", "string");
		KNOWN_PROPERTY_TYPES.put("P715", "string");
		KNOWN_PROPERTY_TYPES.put("P716", "string");
		KNOWN_PROPERTY_TYPES.put("P718", "string");
		KNOWN_PROPERTY_TYPES.put("P720", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P721", "string");
		KNOWN_PROPERTY_TYPES.put("P722", "string");
		KNOWN_PROPERTY_TYPES.put("P725", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P729", "time");
		KNOWN_PROPERTY_TYPES.put("P734", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P735", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P736", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P737", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P74", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P740", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P741", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P742", "string");
		KNOWN_PROPERTY_TYPES.put("P743", "string");
		KNOWN_PROPERTY_TYPES.put("P744", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P747", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P749", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P750", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P757", "string");
		KNOWN_PROPERTY_TYPES.put("P758", "string");
		KNOWN_PROPERTY_TYPES.put("P759", "string");
		KNOWN_PROPERTY_TYPES.put("P76", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P761", "string");
		KNOWN_PROPERTY_TYPES.put("P762", "string");
		KNOWN_PROPERTY_TYPES.put("P763", "string");
		KNOWN_PROPERTY_TYPES.put("P764", "string");
		KNOWN_PROPERTY_TYPES.put("P765", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P766", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P767", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P768", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P77", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P770", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P771", "string");
		KNOWN_PROPERTY_TYPES.put("P772", "string");
		KNOWN_PROPERTY_TYPES.put("P773", "string");
		KNOWN_PROPERTY_TYPES.put("P774", "string");
		KNOWN_PROPERTY_TYPES.put("P775", "string");
		KNOWN_PROPERTY_TYPES.put("P78", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P780", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P782", "string");
		KNOWN_PROPERTY_TYPES.put("P790", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P791", "string");
		KNOWN_PROPERTY_TYPES.put("P792", "string");
		KNOWN_PROPERTY_TYPES.put("P793", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P794", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P799", "string");
		KNOWN_PROPERTY_TYPES.put("P800", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P802", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P803", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P804", "string");
		KNOWN_PROPERTY_TYPES.put("P805", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P806", "string");
		KNOWN_PROPERTY_TYPES.put("P808", "string");
		KNOWN_PROPERTY_TYPES.put("P809", "string");
		KNOWN_PROPERTY_TYPES.put("P81", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P813", "time");
		KNOWN_PROPERTY_TYPES.put("P814", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P815", "string");
		KNOWN_PROPERTY_TYPES.put("P816", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P817", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P827", "string");
		KNOWN_PROPERTY_TYPES.put("P828", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P829", "string");
		KNOWN_PROPERTY_TYPES.put("P830", "string");
		KNOWN_PROPERTY_TYPES.put("P831", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P832", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P833", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P836", "string");
		KNOWN_PROPERTY_TYPES.put("P837", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P838", "string");
		KNOWN_PROPERTY_TYPES.put("P84", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P840", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P841", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P842", "string");
		KNOWN_PROPERTY_TYPES.put("P846", "string");
		KNOWN_PROPERTY_TYPES.put("P849", "string");
		KNOWN_PROPERTY_TYPES.put("P85", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P850", "string");
		KNOWN_PROPERTY_TYPES.put("P853", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P854", "url");
		KNOWN_PROPERTY_TYPES.put("P856", "url");
		KNOWN_PROPERTY_TYPES.put("P858", "string");
		KNOWN_PROPERTY_TYPES.put("P86", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P862", "string");
		KNOWN_PROPERTY_TYPES.put("P865", "string");
		KNOWN_PROPERTY_TYPES.put("P866", "string");
		KNOWN_PROPERTY_TYPES.put("P867", "string");
		KNOWN_PROPERTY_TYPES.put("P868", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P87", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P872", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P878", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P879", "string");
		KNOWN_PROPERTY_TYPES.put("P88", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P882", "string");
		KNOWN_PROPERTY_TYPES.put("P883", "string");
		KNOWN_PROPERTY_TYPES.put("P884", "string");
		KNOWN_PROPERTY_TYPES.put("P888", "string");
		KNOWN_PROPERTY_TYPES.put("P898", "string");
		KNOWN_PROPERTY_TYPES.put("P9", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P901", "string");
		KNOWN_PROPERTY_TYPES.put("P902", "string");
		KNOWN_PROPERTY_TYPES.put("P905", "string");
		KNOWN_PROPERTY_TYPES.put("P906", "string");
		KNOWN_PROPERTY_TYPES.put("P908", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P909", "string");
		KNOWN_PROPERTY_TYPES.put("P91", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P910", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P912", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P913", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P914", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P915", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P916", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P92", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P921", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P931", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P933", "string");
		KNOWN_PROPERTY_TYPES.put("P935", "string");
		KNOWN_PROPERTY_TYPES.put("P937", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P94", "commonsMedia");
		KNOWN_PROPERTY_TYPES.put("P941", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P944", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P945", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P946", "string");
		KNOWN_PROPERTY_TYPES.put("P947", "string");
		KNOWN_PROPERTY_TYPES.put("P948", "commonsMedia");
		KNOWN_PROPERTY_TYPES.put("P949", "string");
		KNOWN_PROPERTY_TYPES.put("P950", "string");
		KNOWN_PROPERTY_TYPES.put("P951", "string");
		KNOWN_PROPERTY_TYPES.put("P954", "string");
		KNOWN_PROPERTY_TYPES.put("P957", "string");
		KNOWN_PROPERTY_TYPES.put("P958", "string");
		KNOWN_PROPERTY_TYPES.put("P959", "string");
		KNOWN_PROPERTY_TYPES.put("P960", "string");
		KNOWN_PROPERTY_TYPES.put("P961", "string");
		KNOWN_PROPERTY_TYPES.put("P963", "url");
		KNOWN_PROPERTY_TYPES.put("P964", "string");
		KNOWN_PROPERTY_TYPES.put("P965", "string");
		KNOWN_PROPERTY_TYPES.put("P966", "string");
		KNOWN_PROPERTY_TYPES.put("P969", "string");
		KNOWN_PROPERTY_TYPES.put("P97", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P971", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P972", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P973", "url");
		KNOWN_PROPERTY_TYPES.put("P98", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P982", "string");
		KNOWN_PROPERTY_TYPES.put("P984", "string");
		KNOWN_PROPERTY_TYPES.put("P990", "commonsMedia");
		KNOWN_PROPERTY_TYPES.put("P991", "wikibase-item");
		KNOWN_PROPERTY_TYPES.put("P996", "commonsMedia");
		KNOWN_PROPERTY_TYPES.put("P998", "string");
	}

}
