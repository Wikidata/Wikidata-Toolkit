package org.wikidata.wdtk.rdf.extensions;

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

import java.util.HashMap;

import org.openrdf.model.Resource;
import org.openrdf.rio.RDFHandlerException;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;
import org.wikidata.wdtk.rdf.OwlDeclarationBuffer;
import org.wikidata.wdtk.rdf.PropertyRegister;
import org.wikidata.wdtk.rdf.RdfWriter;

/**
 * Class for managing extensions that add data to the RDF export which is not
 * directly given in the data model. For example, this can be used to augment
 * property value assignments that refer to other data sets with direct links to
 * a Linked Open Data version of this data set. This is the main use case for
 * this class, though in principle, arbitrary RDF triples could be added under
 * specific conditions.
 *
 * @author Markus Kroetzsch
 *
 */
public class ExportExtensions implements ValueVisitor<Void> {

	final RdfWriter rdfWriter;
	final OwlDeclarationBuffer owlDeclarationBuffer;
	final PropertyRegister propertyRegister;
	final HashMap<String, ValueExportExtension<StringValue>> stringExportExtensions;

	Resource currentResource;
	PropertyIdValue currentPropertyIdValue;

	public ExportExtensions(RdfWriter rdfWriter,
			OwlDeclarationBuffer owlDeclarationBuffer,
			PropertyRegister propertyRegister) {
		this.rdfWriter = rdfWriter;
		this.owlDeclarationBuffer = owlDeclarationBuffer;
		this.propertyRegister = propertyRegister;
		this.stringExportExtensions = new HashMap<String, ValueExportExtension<StringValue>>();
	}

	/**
	 * Registers an export extension for string values for a certain property.
	 * At most one export extension can be registered for any property;
	 * registering another one will overwrite the previous extension. It is
	 * possible to register null for disabling previously registered extensions
	 * for a certain property.
	 *
	 * @param valueExportExtension
	 *            the export extension to register
	 * @param propertyIri
	 *            the property that the extension is registered for
	 */
	public void registerStringValueExportExtension(
			ValueExportExtension<StringValue> valueExportExtension,
			String propertyIri) {
		this.stringExportExtensions.put(propertyIri, valueExportExtension);
	}

	/**
	 * Writes any additional triples for the given value snak.
	 *
	 * @param snak
	 *            the extended snak
	 * @param resource
	 *            the subject of the snak
	 */
	public void writeValueSnakExtensions(ValueSnak snak, Resource resource) {
		this.currentResource = resource;
		this.currentPropertyIdValue = snak.getPropertyId();
		snak.getValue().accept(this);
	}

	@Override
	public Void visit(DatatypeIdValue value) {
		return null;
	}

	@Override
	public Void visit(EntityIdValue value) {
		return null;
	}

	@Override
	public Void visit(GlobeCoordinatesValue value) {
		return null;
	}

	@Override
	public Void visit(MonolingualTextValue value) {
		return null;
	}

	@Override
	public Void visit(QuantityValue value) {
		return null;
	}

	@Override
	public Void visit(StringValue value) {
		ValueExportExtension<StringValue> vee = this.stringExportExtensions
				.get(this.currentPropertyIdValue.getIri());

		try {
			if (vee != null) {
				vee.writeExtensionData(this.currentResource,
						this.currentPropertyIdValue, value, this.rdfWriter,
						this.owlDeclarationBuffer);
			} else {
				writeUriPatternData(this.currentResource,
						this.currentPropertyIdValue, value);
			}
		} catch (RDFHandlerException e) { // give up; it's probably
											// impossible to continue anyway
			throw new RuntimeException(e.toString(), e);
		}
		return null;
	}

	@Override
	public Void visit(TimeValue value) {
		return null;
	}

	protected void writeUriPatternData(Resource resource,
			PropertyIdValue propertyIdValue, StringValue value)
			throws RDFHandlerException {

		String uriPattern = this.propertyRegister
				.getPropertyUriPattern(propertyIdValue);
		if (uriPattern == null) {
			return;
		}

		this.rdfWriter.writeTripleUriObject(resource, RdfWriter.RDFS_SEE_ALSO,
				uriPattern.replace("$1", value.getString()));
	}

	/**
	 * Registers a predefined set of known export extensions for certain
	 * Wikidata properties.
	 *
	 * @param exportExtensions
	 *            the object for which to register the export extensions
	 */
	public static void registerWikidataExportExtensions(
			ExportExtensions exportExtensions) {
		exportExtensions.registerStringValueExportExtension(
				new FreebaseExportExtension(),
				"http://www.wikidata.org/entity/P646");
	}
}
