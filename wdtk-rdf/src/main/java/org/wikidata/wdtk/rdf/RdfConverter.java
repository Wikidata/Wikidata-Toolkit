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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.RDFHandlerException;
import org.wikidata.wdtk.datamodel.interfaces.Claim;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
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

	final ValueFactory factory = ValueFactoryImpl.getInstance();
	final RdfWriter writer;
	final ValueRdfConverter valueRdfConverter;
	final SnakRdfConverter snakRdfConverter;

	public RdfConverter(RdfWriter writer) {
		this.writer = writer;
		this.valueRdfConverter = new ValueRdfConverter();
		this.snakRdfConverter = new SnakRdfConverter(writer,
				this.valueRdfConverter);
	}

	/**
	 * Writes OWL declarations for all basic vocabulary elements used in the
	 * dump.
	 * 
	 * @throws RDFHandlerException
	 */
	public void writeBasicDeclarations() throws RDFHandlerException {
		for (Map.Entry<String, String> uriType : Vocabulary
				.getKnownVocabularyTypes().entrySet()) {
			this.writer.writeTripleUriObject(uriType.getKey(),
					Vocabulary.RDF_TYPE, uriType.getValue());
		}
	}

	public void writeNamespaceDeclarations() throws RDFHandlerException {
		this.writer.writeNamespaceDeclaration("id", Vocabulary.PREFIX_WIKIDATA);
		this.writer.writeNamespaceDeclaration("wo", Vocabulary.PREFIX_WBONTO);
		this.writer.writeNamespaceDeclaration("rdf", Vocabulary.PREFIX_RDF);
		this.writer.writeNamespaceDeclaration("rdfs", Vocabulary.PREFIX_RDFS);
		this.writer.writeNamespaceDeclaration("owl", Vocabulary.PREFIX_OWL);
		this.writer.writeNamespaceDeclaration("xsd", Vocabulary.PREFIX_XSD);
		this.writer.writeNamespaceDeclaration("schema",
				Vocabulary.PREFIX_SCHEMA);
		this.writer.writeNamespaceDeclaration("skos", Vocabulary.PREFIX_SKOS);
		this.writer.writeNamespaceDeclaration("prov", Vocabulary.PREFIX_PROV);
	}

	public void writeItemDocument(ItemDocument document)
			throws RDFHandlerException {

		String subjectUri = Vocabulary.getEntityUri(document.getEntityId());

		this.writer.writeTripleUriObject(subjectUri, Vocabulary.RDF_TYPE,
				Vocabulary.WB_ITEM);

		writeDocumentTerms(document);

		for (StatementGroup statementGroup : document.getStatementGroups()) {
			for (Statement statement : statementGroup.getStatements()) {
				this.writer.writeTripleUriObject(subjectUri, Vocabulary
						.getPropertyUri(statement.getClaim().getMainSnak()
								.getPropertyId(), PropertyContext.STATEMENT),
						Vocabulary.getStatementUri(statement));
			}
		}

		for (StatementGroup statementGroup : document.getStatementGroups()) {
			for (Statement statement : statementGroup.getStatements()) {
				writeStatement(statement);
			}
		}

		// TODO: add SiteLinks

	}

	public void writePropertyDocument(PropertyDocument document)
			throws RDFHandlerException {

		this.writer.writeTripleUriObject(Vocabulary.PREFIX_WIKIDATA
				+ document.getEntityId().getId(), Vocabulary.RDF_TYPE,
				Vocabulary.WB_PROPERTY);

		writeDocumentTerms(document);

		// TODO add datatype
	}

	void writeDocumentTerms(TermedDocument document) throws RDFHandlerException {
		String subjectUri = Vocabulary.getEntityUri(document.getEntityId());

		writeTermTriples(subjectUri, Vocabulary.RDFS_LABEL, document
				.getLabels().values());
		writeTermTriples(subjectUri, Vocabulary.SCHEMA_DESCRIPTION, document
				.getDescriptions().values());
		for (List<MonolingualTextValue> aliases : document.getAliases()
				.values()) {
			writeTermTriples(subjectUri, Vocabulary.SKOS_ALT_LABEL, aliases);
		}
	}

	void writeTermTriples(String subjectUri, String predicateUri,
			Collection<MonolingualTextValue> terms) throws RDFHandlerException {
		for (MonolingualTextValue mtv : terms) {
			this.writer.writeTripleValueObject(subjectUri, predicateUri,
					mtv.accept(this.valueRdfConverter));
		}
	}

	void writeStatement(Statement statement) throws RDFHandlerException {
		String statementUri = Vocabulary.getStatementUri(statement);

		this.writer.writeTripleUriObject(statementUri, Vocabulary.RDF_TYPE,
				Vocabulary.WB_STATEMENT);
		writeClaim(statementUri, statement.getClaim());

		// TODO: References

		// What about the RANK?

	}

	void writeClaim(String statementUri, Claim claim) {
		this.snakRdfConverter.setSnakContext(statementUri,
				PropertyContext.VALUE);
		claim.getMainSnak().accept(this.snakRdfConverter);

		this.snakRdfConverter.setSnakContext(statementUri,
				PropertyContext.QUALIFIER);
		for (SnakGroup snakGroup : claim.getQualifiers()) {
			for (Snak snak : snakGroup.getSnaks()) {
				snak.accept(this.snakRdfConverter);
			}
		}
	}

}
