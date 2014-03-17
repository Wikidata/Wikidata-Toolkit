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

import org.wikidata.wdtk.datamodel.interfaces.Claim;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
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
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

/**
 * Interface for Converters providing methods to convert Object from
 * {@link org.wikidata.wdtk.datamodel.interfaces} in an Output-format}
 * 
 * @author Michael Günther
 * 
 * @param <Output>
 */
public interface Converter<Output> {

	/**
	 * Converts the attributes of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.Claim}
	 * 
	 * @param claim
	 * @return representation of an
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.Claim}
	 * @throws Exception
	 */
	public Output visit(Claim claim) throws Exception;

	/**
	 * Create a representation of an
	 * {@link org.wikidata.wdtk.datamodel.interfaces.ItemDocument}
	 * 
	 * @param itemDocument
	 * @return representation of an
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.ItemDocument}
	 * @throws Exception
	 */
	public Output visit(ItemDocument itemDocument) throws Exception;

	/**
	 * Create a representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.PropertyDocument}
	 * 
	 * @param propertyDocument
	 * @return representation of a
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.PropertyDocument}
	 * @throws Exception
	 */
	public Output visit(PropertyDocument propertyDocument) throws Exception;

	/**
	 * Create a representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.Reference}
	 * 
	 * @param ref
	 * @return representation of a
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.Reference}
	 * @throws Exception
	 */
	public Output visit(Reference ref) throws Exception;

	/**
	 * Create a representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.Statement}
	 * 
	 * @param statement
	 * @return representation of a
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.Statement}
	 * @throws Exception
	 */
	public Output visit(Statement statement) throws Exception;

	/**
	 * Create a representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.ValueSnak}
	 * 
	 * @param snak
	 * @return representation of a
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.ValueSnak}
	 * @throws Exception
	 */
	public Output visit(ValueSnak snak) throws Exception;

	/**
	 * Create a representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.NoValueSnak}
	 * 
	 * @param snak
	 * @return representation of a
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.NoValueSnak}
	 * @throws Exception
	 */
	public Output visit(NoValueSnak snak) throws Exception;

	/**
	 * Create a representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak}
	 * 
	 * @param snak
	 * @return representation of a
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak}
	 * @throws Exception
	 */
	public Output visit(SomeValueSnak snak) throws Exception;

	/**
	 * Create a representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.QuantityValue}
	 * 
	 * @param value
	 * @return representation of a
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.QuantityValue}
	 * @throws Exception
	 */
	public Output visit(QuantityValue value) throws Exception;

	/**
	 * Create a representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.TimeValue}
	 * 
	 * @param value
	 * @return representation of a
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.TimeValue}
	 * @throws Exception
	 */
	public Output visit(TimeValue value) throws Exception;

	/**
	 * Create a representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue}
	 * 
	 * @param value
	 * @return representation of a
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue}
	 * @throws Exception
	 */
	public Output visit(GlobeCoordinatesValue value) throws Exception;

	/**
	 * Create a representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.StringValue}
	 * 
	 * @param value
	 * @return representation of a
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.StringValue}
	 * @throws Exception
	 */
	public Output visit(StringValue value) throws Exception;

	/**
	 * Create a representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue}
	 * 
	 * @param value
	 * @return representation of a
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue}
	 * @throws Exception
	 */
	public Output visit(DatatypeIdValue value) throws Exception;

	/**
	 * Create a representation of an
	 * {@link org.wikidata.wdtk.datamodel.interfaces.ItemIdValue}
	 * 
	 * @param value
	 * @return representation of an
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.ItemIdValue}
	 * @throws Exception
	 */
	public Output visit(ItemIdValue value) throws Exception;

	/**
	 * Create a representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue}
	 * 
	 * @param value
	 * @return representation of a
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue}
	 * @throws Exception
	 */
	public Output visit(MonolingualTextValue value) throws Exception;

	/**
	 * Create a representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue}
	 * 
	 * @param value
	 * @return representation of a
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue}
	 * @throws Exception
	 */
	public Output visit(PropertyIdValue value) throws Exception;

	/**
	 * Create a representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.SiteLink}
	 * 
	 * @param link
	 * @return representation of
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.SiteLink}
	 * @throws Exception
	 */
	public Output visit(SiteLink link) throws Exception;

}
