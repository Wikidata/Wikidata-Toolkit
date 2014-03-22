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
 * Interface for Converters providing methods to convert Object created with
 * {@link org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory} in an
 * Output-format
 * 
 * @author Michael Günther
 * 
 * @param <T>
 */
public interface Converter<T> {

	/**
	 * Converts the attributes of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.Claim}
	 * 
	 * @param claim
	 * @return representation of an
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.Claim}
	 */
	public T visit(Claim claim);

	/**
	 * Create a representation of an
	 * {@link org.wikidata.wdtk.datamodel.interfaces.ItemDocument}
	 * 
	 * @param itemDocument
	 * @return representation of an
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.ItemDocument}
	 */
	public T visit(ItemDocument itemDocument);

	/**
	 * Create a representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.PropertyDocument}
	 * 
	 * @param propertyDocument
	 * @return representation of a
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.PropertyDocument}
	 */
	public T visit(PropertyDocument propertyDocument);

	/**
	 * Create a representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.Reference}
	 * 
	 * @param ref
	 * @return representation of a
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.Reference}
	 */
	public T visit(Reference ref);

	/**
	 * Create a representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.Statement}
	 * 
	 * @param statement
	 * @return representation of a
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.Statement}
	 */
	public T visit(Statement statement);

	/**
	 * Create a representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.ValueSnak}
	 * 
	 * @param snak
	 * @return representation of a
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.ValueSnak}
	 */
	public T visit(ValueSnak snak);

	/**
	 * Create a representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.NoValueSnak}
	 * 
	 * @param snak
	 * @return representation of a
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.NoValueSnak}
	 */
	public T visit(NoValueSnak snak);

	/**
	 * Create a representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak}
	 * 
	 * @param snak
	 * @return representation of a
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak}
	 */
	public T visit(SomeValueSnak snak);

	/**
	 * Create a representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.QuantityValue}
	 * 
	 * @param value
	 * @return representation of a
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.QuantityValue}
	 */
	public T visit(QuantityValue value);

	/**
	 * Create a representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.TimeValue}
	 * 
	 * @param value
	 * @return representation of a
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.TimeValue}
	 */
	public T visit(TimeValue value);

	/**
	 * Create a representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue}
	 * 
	 * @param value
	 * @return representation of a
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue}
	 */
	public T visit(GlobeCoordinatesValue value);

	/**
	 * Create a representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.StringValue}
	 * 
	 * @param value
	 * @return representation of a
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.StringValue}
	 */
	public T visit(StringValue value);

	/**
	 * Create a representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue}
	 * 
	 * @param value
	 * @return representation of a
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue}
	 */
	public T visit(DatatypeIdValue value);

	/**
	 * Create a representation of an
	 * {@link org.wikidata.wdtk.datamodel.interfaces.ItemIdValue}
	 * 
	 * @param value
	 * @return representation of an
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.ItemIdValue}
	 */
	public T visit(ItemIdValue value);

	/**
	 * Create a representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue}
	 * 
	 * @param value
	 * @return representation of a
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue}
	 */
	public T visit(MonolingualTextValue value);

	/**
	 * Create a representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue}
	 * 
	 * @param value
	 * @return representation of a
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue}
	 */
	public T visit(PropertyIdValue value);

	/**
	 * Create a representation of a
	 * {@link org.wikidata.wdtk.datamodel.interfaces.SiteLink}
	 * 
	 * @param link
	 * @return representation of
	 *         {@link org.wikidata.wdtk.datamodel.interfaces.SiteLink}
	 */
	public T visit(SiteLink link);

}
