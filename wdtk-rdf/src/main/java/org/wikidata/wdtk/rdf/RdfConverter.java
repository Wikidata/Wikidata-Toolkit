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
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.Claim;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
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

	final String PREFIX_W = "http://www.wikidata.org/entity/";
	final String PREFIX_WO = "http://www.wikidata.org/ontology#";
	final String PREFIX_R = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	final String PREFIX_RS = "http://www.w3.org/2000/01/rdf-schema#";
	final String PREFIX_O = "http://www.w3.org/2002/07/owl#";
	final String PREFIX_X = "http://www.w3.org/2001/XMLSchema#";
	final String PREFIX_SO = "http://schema.org/";
	final String PREFIX_SK = "http://www.w3.org/2004/02/skos/core#";
	final String PREFIX_PV = "http://www.w3.org/ns/prov#";

	final String RDF_TYPE = PREFIX_R + "type";

	final ValueFactory factory = ValueFactoryImpl.getInstance();

	final ValueRdfConverter valueRdfConverter = new ValueRdfConverter();
	final SnakRdfConverter snakRdfConverter = new SnakRdfConverter(
			this.valueRdfConverter);

	// map of prefixes (namespaces)
	Map<String, String> namespaces = new HashMap<String, String>();

	/**
	 * Fills a map with prefixes which can be added to the writer later.
	 */
	public void setPrefixes() {
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

	org.openrdf.model.Statement createStatementWithStringValue(
			String subjectURI, String predicateURI, String objectValue) {
		URI subject = factory.createURI(subjectURI);
		URI predicate = factory.createURI(predicateURI);
		Literal object = factory.createLiteral(objectValue);

		return factory.createStatement(subject, predicate, object);
	}

	org.openrdf.model.Statement createStatementWithResourceValue(
			String subjectURI, String predicateURI, String objectValue) {
		URI subject = factory.createURI(subjectURI);
		URI predicate = factory.createURI(predicateURI);
		URI object = factory.createURI(objectValue);

		return factory.createStatement(subject, predicate, object);
	}

	org.openrdf.model.Statement createStatementWithLiteral(String subjectURI,
			String predicateURI, Literal literal) {

		URI subject = factory.createURI(subjectURI);
		URI predicate = factory.createURI(predicateURI);

		return factory.createStatement(subject, predicate, literal);
	}

	public Set<org.openrdf.model.Statement> convertLabelsToRdf(
			TermedDocument document) {
		Set<org.openrdf.model.Statement> result = new HashSet<org.openrdf.model.Statement>();
		for (String key : document.getLabels().keySet()) {
			result.add(createStatementWithLiteral(PREFIX_W
					+ document.getEntityId().getId(), PREFIX_RS + "label",
					document.getLabels().get(key)
							.accept(this.valueRdfConverter)));
		}
		return result;
	}

	public Set<org.openrdf.model.Statement> convertAliasesToRdf(
			TermedDocument document) {
		Set<org.openrdf.model.Statement> result = new HashSet<org.openrdf.model.Statement>();
		for (String key : document.getAliases().keySet()) {
			for (MonolingualTextValue value : document.getAliases().get(key)) {
				result.add(createStatementWithLiteral(PREFIX_W
						+ document.getEntityId().getId(), PREFIX_SK
						+ "altLabel", value.accept(this.valueRdfConverter)));
			}
		}
		return result;
	}

	public Set<org.openrdf.model.Statement> convertDescriptionsToRdf(
			TermedDocument document) {
		Set<org.openrdf.model.Statement> result = new HashSet<org.openrdf.model.Statement>();

		for (String key : document.getDescriptions().keySet()) {
			result.add(createStatementWithLiteral(
					PREFIX_W + document.getEntityId().getId(),
					PREFIX_SO + "description",
					document.getDescriptions().get(key)
							.accept(this.valueRdfConverter)));
		}

		return result;
	}

	Set<org.openrdf.model.Statement> addTermedDocumentAttributes(
			TermedDocument document, Set<org.openrdf.model.Statement> result) {

		result.addAll(convertLabelsToRdf(document));
		result.addAll(convertDescriptionsToRdf(document));
		result.addAll(convertAliasesToRdf(document));

		return result;

	}

	public Set<org.openrdf.model.Statement> getRdfForItemDocument(
			ItemDocument document) {
		Set<org.openrdf.model.Statement> result = new HashSet<org.openrdf.model.Statement>();

		result.add(createStatementWithResourceValue(PREFIX_W
				+ document.getItemId().getId(), RDF_TYPE, PREFIX_WO + "Item"));

		result = addTermedDocumentAttributes(document, result);

		for (org.wikidata.wdtk.datamodel.interfaces.StatementGroup statementGroup : document
				.getStatementGroups()) {
			result.addAll(getRdfForStatementGroup(statementGroup));
		}

		return result;
	}

	public Set<org.openrdf.model.Statement> getRdfForStatementGroup(
			StatementGroup statementGroup) {

		Set<org.openrdf.model.Statement> result = new HashSet<org.openrdf.model.Statement>();

		for (org.wikidata.wdtk.datamodel.interfaces.Statement statement : statementGroup
				.getStatements()) {
			result.addAll(getRdfForStatement(statement));
		}

		return result;
	}

	public Set<org.openrdf.model.Statement> getRdfForStatement(
			org.wikidata.wdtk.datamodel.interfaces.Statement statement) {
		Set<org.openrdf.model.Statement> result = new HashSet<org.openrdf.model.Statement>();
		result.add(createStatementWithResourceValue(PREFIX_W
				+ statement.getClaim().getSubject().getId(), PREFIX_W
				+ statement.getClaim().getMainSnak().getPropertyId() + "s",
				PREFIX_W + statement.getStatementId()));
		result.add(createStatementWithResourceValue(
				PREFIX_W + statement.getStatementId(), RDF_TYPE, PREFIX_WO
						+ "Statement"));
		result.addAll(getRdfForClaim(statement.getClaim()));
		return result;
	}

	public Set<org.openrdf.model.Statement> getRdfForClaim(Claim claim) {
		Set<org.openrdf.model.Statement> result = new HashSet<org.openrdf.model.Statement>();
		// result.addAll(claim.getMainSnak().accept(snakRdfConverter));
		return result;
	}
}
