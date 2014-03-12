package org.wikidata.wdtk.datamodel.jsonconverter;

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

import org.json.JSONException;
import org.wikidata.wdtk.datamodel.interfaces.Claim;
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
import org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

/**
 * Interface for Converters providing methods to convert Object from
 * {@link org.wikidata.wdtk.datamodel.interfaces} in json}
 * 
 * @author Michael Günther
 * 
 * @param <Output>
 * @param <OutputArray>
 */
public interface Converter<Output, OutputArray> {

	/**
	 * Create a json-representation of a attribute of an
	 * {@link org.wikidata.wdtk.datamodel.interfaces.Claim}
	 * 
	 * @param claim
	 * @return Json representation of an
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.Claim}
	 * @throws JSONException
	 */
	public Output convertClaimToJson(Claim claim) throws JSONException;

	/**
	 * Create a json-representation of an
	 * {@link org.wikidata.wdtk.datamodel.interfaces.ItemDocument}
	 * 
	 * @param itemDocument
	 * @return Json representation of an
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.ItemDocument}
	 * @throws JSONException
	 */
	public Output convertItemDocumentToJson(ItemDocument itemDocument)
			throws JSONException;

	/**
	 * Create a json-representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.PropertyDocument}
	 * 
	 * @param propertyDocument
	 * @return Json representation of a
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.PropertyDocument}
	 * @throws JSONException
	 */
	public Output convertPropertyDocumentToJson(
			PropertyDocument propertyDocument) throws JSONException;

	/**
	 * Create a json-representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.Reference}
	 * 
	 * @param ref
	 * @return Json representation of a
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.Reference}
	 * @throws JSONException
	 */
	public Output convertReferenceToJson(Reference ref) throws JSONException;

	/**
	 * Create a json-representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.Statement}
	 * 
	 * @param statement
	 * @return Json representation of a
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.Statement}
	 * @throws JSONException
	 */
	public Output convertStatementToJson(Statement statement)
			throws JSONException;

	/**
	 * Create a json-representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.StatementGroup} (something
	 * like an array of statements,)
	 * 
	 * @param statementGroup
	 * @return Json representation of a
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.StatementGroup}
	 * @throws JSONException
	 */
	public OutputArray convertStatementGroupToJson(StatementGroup statementGroup)
			throws JSONException;

	/**
	 * Create a json-representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.ValueSnak}
	 * 
	 * @param snak
	 * @return Json representation of a
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.ValueSnak}
	 * @throws JSONException
	 */
	public Output convertValueSnakToJson(ValueSnak snak) throws JSONException;

	/**
	 * Create a json-representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.NoValueSnak}
	 * 
	 * @param snak
	 * @return Json representation of a
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.NoValueSnak}
	 * @throws JSONException
	 */
	public Output convertNoValueSnakToJson(NoValueSnak snak)
			throws JSONException;

	/**
	 * Create a json-representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak}
	 * 
	 * @param snak
	 * @return Json representation of a
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak}
	 * @throws JSONException
	 */
	public Output convertSomeValueSnakToJson(SomeValueSnak snak)
			throws JSONException;

	/**
	 * Create a json-representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.QuantityValue}
	 * 
	 * @param value
	 * @return Json representation of a
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.QuantityValue}
	 * @throws JSONException
	 */
	public Output convertQuantityValueToJson(QuantityValue value)
			throws JSONException;

	/**
	 * Create a json-representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.TimeValue}
	 * 
	 * @param value
	 * @return Json representation of a
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.TimeValue}
	 * @throws JSONException
	 */
	public Output convertTimeValueToJson(TimeValue value) throws JSONException;

	/**
	 * Create a json-representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue}
	 * 
	 * @param value
	 * @return Json representation of a
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue}
	 * @throws JSONException
	 */
	public Output convertGlobeCoordinatesValueToJson(GlobeCoordinatesValue value)
			throws JSONException;

	/**
	 * Create a json-representation of an
	 * {@link org.wikidata.wdtk.datamodel.interfaces.EntityIdValue}
	 * 
	 * @param value
	 * @return Json representation of an
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.EntityIdValue}
	 * @throws JSONException
	 */
	public Output convertEntityIdValueToJson(EntityIdValue value)
			throws JSONException;

	/**
	 * Create a json-representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.StringValue}
	 * 
	 * @param value
	 * @return Json representation of a
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.StringValue}
	 * @throws JSONException
	 */
	public Output convertStringValueToJson(StringValue value)
			throws JSONException;

	/**
	 * Create a json-representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue}
	 * 
	 * @param  value
	 * @return Json representation of a
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue}
	 * @throws JSONException
	 */
	public Output convertDatatypeIdValueToJson(DatatypeIdValue value)
			throws JSONException;

	/**
	 * Create a json-representation of an
	 * {@link org.wikidata.wdtk.datamodel.interfaces.ItemIdValue}
	 * 
	 * @param value
	 * @return Json representation of an
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.ItemIdValue}
	 * @throws JSONException
	 */
	public Output convertItemIdValueToJson(ItemIdValue value)
			throws JSONException;

	/**
	 * Create a json-representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue}
	 * 
	 * @param value
	 * @return Json representation of a
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue}
	 * @throws JSONException
	 */
	public Output convertMonolingualTextValueToJson(MonolingualTextValue value)
			throws JSONException;

	/**
	 * Create a json-representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue}
	 * 
	 * @param value
	 * @return Json representation of a
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue}
	 * @throws JSONException
	 */
	public Output convertPropertyIdValueToJson(PropertyIdValue value)
			throws JSONException;

	public Output convertSiteLinkToJson(SiteLink link) throws JSONException;

}
