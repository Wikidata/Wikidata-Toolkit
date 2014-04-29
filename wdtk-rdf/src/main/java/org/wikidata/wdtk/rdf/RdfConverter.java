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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.wikidata.wdtk.datamodel.interfaces.Claim;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.TermedDocument;

/**
 * This class provides functions to convert objects of wdtk-datamodel in a rdf
 * graph.
 * 
 * @author Michael Günther
 * 
 */
public class RdfConverter {

	// Prefixes

	final static String PREFIX_W = "http://www.wikidata.org/entity/";
	final static String PREFIX_WO = "http://www.wikidata.org/ontology#";
	final static String PREFIX_R = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	final static String PREFIX_RS = "http://www.w3.org/2000/01/rdf-schema#";
	final static String PREFIX_O = "http://www.w3.org/2002/07/owl#";
	final static String PREFIX_X = "http://www.w3.org/2001/XMLSchema#";
	final static String PREFIX_SO = "http://schema.org/";
	final static String PREFIX_SK = "http://www.w3.org/2004/02/skos/core#";
	final static String PREFIX_PV = "http://www.w3.org/ns/prov#";

	final String RDF_TYPE = PREFIX_R + "type";

	RDFWriter writer;

	final ValueFactory factory = ValueFactoryImpl.getInstance();

	final ValueRdfConverter valueRdfConverter = new ValueRdfConverter();
	final SnakRdfConverter snakRdfConverter = new SnakRdfConverter(
			this.valueRdfConverter);

	// map of prefixes (namespaces)
	Map<String, String> namespaces = new HashMap<String, String>();

	/**
	 * Fills a map with prefixes which can be added to the writer later.
	 */
	void setPrefixes() {
		namespaces.put("w", PREFIX_W);
		namespaces.put("wo", PREFIX_WO);
		namespaces.put("r", PREFIX_R);
		namespaces.put("rs", PREFIX_RS);
		namespaces.put("o", PREFIX_O);
		namespaces.put("x", PREFIX_X);
		namespaces.put("so", PREFIX_SO);
		namespaces.put("sk", PREFIX_SK);
		namespaces.put("pv", PREFIX_PV);
	}

	/**
	 * Returns a set with some triples to define instances of properties.
	 * 
	 * @return Set<org.openrdf.model.Statement>
	 */
	public Set<org.openrdf.model.Statement> getBasicDefinitions() {
		Set<org.openrdf.model.Statement> statements = new HashSet<org.openrdf.model.Statement>();

		statements.add(createStatementWithResourceValue(
				"http://www.wikidata.org/ontology#propertyType", RDF_TYPE,
				"http://www.w3.org/2002/07/owl#ObjectProperty"));
		statements.add(createStatementWithResourceValue(
				"http://www.wikidata.org/ontology#globe", RDF_TYPE,
				"http://www.w3.org/2002/07/owl#ObjectProperty"));
		statements.add(createStatementWithResourceValue(
				"http://www.wikidata.org/ontology#latitude", RDF_TYPE,
				"http://www.w3.org/2002/07/owl#DatatypeProperty"));
		statements.add(createStatementWithResourceValue(
				"http://www.wikidata.org/ontology#longitude", RDF_TYPE,
				"http://www.w3.org/2002/07/owl#DatatypeProperty"));
		statements.add(createStatementWithResourceValue(
				"http://www.wikidata.org/ontology#altitude", RDF_TYPE,
				"http://www.w3.org/2002/07/owl#DatatypeProperty"));
		statements.add(createStatementWithResourceValue(
				"http://www.wikidata.org/ontology#gcPrecision", RDF_TYPE,
				"http://www.w3.org/2002/07/owl#DatatypeProperty"));
		statements.add(createStatementWithResourceValue(
				"http://www.wikidata.org/ontology#time", RDF_TYPE,
				"http://www.w3.org/2002/07/owl#DatatypeProperty"));
		statements.add(createStatementWithResourceValue(
				"http://www.wikidata.org/ontology#timePrecision", RDF_TYPE,
				"http://www.w3.org/2002/07/owl#DatatypeProperty"));
		statements.add(createStatementWithResourceValue(
				"http://www.wikidata.org/ontology#preferredCalendar", RDF_TYPE,
				"http://www.w3.org/2002/07/owl#ObjectProperty"));
		statements.add(createStatementWithResourceValue(
				"http://www.w3.org/ns/prov#wasDerivedFrom", RDF_TYPE,
				"http://www.w3.org/2002/07/owl#ObjectProperty"));
		statements.add(createStatementWithResourceValue(
				"http://schema.org/about", RDF_TYPE,
				"http://www.w3.org/2002/07/owl#ObjectProperty"));
		statements.add(createStatementWithResourceValue(
				"http://schema.org/inLanguage", RDF_TYPE,
				"http://www.w3.org/2002/07/owl#DatatypeProperty"));

		return statements;

	}

	public RdfConverter(RDFWriter writer) {
		setPrefixes();
		this.writer = writer;
	}

	public Map<String, String> getNamespaces() {
		return this.namespaces;
	}

	org.openrdf.model.Statement createStatementWithGenericValue(String subjectURI, String predicateURI, Value value){
		URI subject = this.factory.createURI(subjectURI);
		URI predicate = this.factory.createURI(predicateURI);

		return this.factory.createStatement(subject, predicate, value);
	}

	org.openrdf.model.Statement createStatementWithStringValue(
			String subjectURI, String predicateURI, String objectValue) {
		URI subject = this.factory.createURI(subjectURI);
		URI predicate = this.factory.createURI(predicateURI);
		Literal object = this.factory.createLiteral(objectValue);

		return this.factory.createStatement(subject, predicate, object);
	}

	org.openrdf.model.Statement createStatementWithResourceValue(
			String subjectURI, String predicateURI, String objectValue) {
		URI subject = this.factory.createURI(subjectURI);
		URI predicate = this.factory.createURI(predicateURI);
		URI object = this.factory.createURI(objectValue);

		return this.factory.createStatement(subject, predicate, object);
	}

	org.openrdf.model.Statement createStatementWithLiteral(String subjectURI,
			String predicateURI, Literal literal) {

		URI subject = this.factory.createURI(subjectURI);
		URI predicate = this.factory.createURI(predicateURI);

		return this.factory.createStatement(subject, predicate, literal);
	}

	public void convertLabelsToRdf(TermedDocument document)
			throws RDFHandlerException {
		for (String key : document.getLabels().keySet()) {
			writer.handleStatement(createStatementWithGenericValue(PREFIX_W
					+ document.getEntityId().getId(), PREFIX_RS + "label",
					document.getLabels().get(key)
							.accept(this.valueRdfConverter)));
		}
	}

	public void convertAliasesToRdf(TermedDocument document)
			throws RDFHandlerException {
		for (String key : document.getAliases().keySet()) {
			for (MonolingualTextValue value : document.getAliases().get(key)) {
				writer.handleStatement(createStatementWithGenericValue(PREFIX_W
						+ document.getEntityId().getId(), PREFIX_SK
						+ "altLabel", value.accept(this.valueRdfConverter)));
			}
		}
	}

	public void convertDescriptionsToRdf(TermedDocument document)
			throws RDFHandlerException {

		for (String key : document.getDescriptions().keySet()) {
			writer.handleStatement(createStatementWithGenericValue(
					PREFIX_W + document.getEntityId().getId(),
					PREFIX_SO + "description",
					document.getDescriptions().get(key)
							.accept(this.valueRdfConverter)));
		}

	}

	void addTermedDocumentAttributes(TermedDocument document)
			throws RDFHandlerException {

		convertLabelsToRdf(document);
		convertDescriptionsToRdf(document);
		convertAliasesToRdf(document);

	}

	public Resource getRdfForItemDocument(ItemDocument document)
			throws RDFHandlerException {

		writer.handleStatement(createStatementWithResourceValue(PREFIX_W
				+ document.getItemId().getId(), RDF_TYPE, PREFIX_WO + "Item"));

		addTermedDocumentAttributes(document);

		for (org.wikidata.wdtk.datamodel.interfaces.StatementGroup statementGroup : document
				.getStatementGroups()) {
			getRdfForStatementGroup(statementGroup);
		}

		// TODO: add SiteLinks

		return this.factory.createURI(PREFIX_W + document.getEntityId());
	}

	public Resource getRdfForPropertyDocument(PropertyDocument document)
			throws RDFHandlerException {
		addTermedDocumentAttributes(document);

		return this.factory.createURI(PREFIX_W + document.getEntityId());
	}

	public void getRdfForStatementGroup(StatementGroup statementGroup)
			throws RDFHandlerException {
		for (org.wikidata.wdtk.datamodel.interfaces.Statement statement : statementGroup
				.getStatements()) {
			getRdfForStatement(statement);
		}
	}

	public void getRdfForStatement(
			org.wikidata.wdtk.datamodel.interfaces.Statement statement)
			throws RDFHandlerException {
		writer.handleStatement(createStatementWithResourceValue(PREFIX_W
				+ statement.getClaim().getSubject().getId(), PREFIX_W
				+ statement.getClaim().getMainSnak().getPropertyId() + "s",
				PREFIX_W + statement.getStatementId()));
		writer.handleStatement(createStatementWithResourceValue(PREFIX_W
				+ statement.getStatementId(), RDF_TYPE, PREFIX_WO + "Statement"));
		getRdfForClaim(statement.getClaim(), statement.getStatementId());

		// TODO: References

		// What about the RANK?

	}

	public Set<org.openrdf.model.Statement> getRdfForQualifiers(
			List<SnakGroup> qualifiers, String statementId) {
		Set<org.openrdf.model.Statement> result = new HashSet<org.openrdf.model.Statement>();

		return result;
	}

	public void getRdfForClaim(Claim claim, String statementId) {
		claim.getMainSnak().accept(this.snakRdfConverter);
		getRdfForQualifiers(claim.getQualifiers(), statementId); //TODO: irgendwas
	}
}
