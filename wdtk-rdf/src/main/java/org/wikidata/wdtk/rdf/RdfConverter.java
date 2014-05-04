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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.interfaces.Claim;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Sites;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.TermedDocument;
import org.wikidata.wdtk.datamodel.interfaces.WikimediaLanguageCodes;

/**
 * This class provides functions to convert objects of wdtk-datamodel in a rdf
 * graph.
 * 
 * @author Michael Günther
 * 
 */
public class RdfConverter {

	static final Logger logger = LoggerFactory.getLogger(RdfConverter.class);

	final ValueFactory factory = ValueFactoryImpl.getInstance();
	final RdfWriter writer;
	final ValueRdfConverter valueRdfConverter;
	final SnakRdfConverter snakRdfConverter;
	final RdfConversionBuffer rdfConversionBuffer;
	final Sites sites;

	public RdfConverter(RdfWriter writer, Sites sites) {
		this.sites = sites;
		this.writer = writer;
		this.rdfConversionBuffer = new RdfConversionBuffer();
		this.valueRdfConverter = new ValueRdfConverter(writer,
				this.rdfConversionBuffer);
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
		// TODO The prefix for wiki entities should depend on the data
		this.writer.writeNamespaceDeclaration("id",
				"http://www.wikidata.org/entity/");
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

		String subjectUri = document.getEntityId().getIri();

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

		writeSiteLinks(subjectUri, document.getSiteLinks());

		this.rdfConversionBuffer.writeValues(this.valueRdfConverter);
		this.rdfConversionBuffer.writePropertyDeclarations(this.writer);
	}

	public void writePropertyDocument(PropertyDocument document)
			throws RDFHandlerException {

		String propertyUri = document.getEntityId().getIri();

		this.writer.writeTripleUriObject(propertyUri, Vocabulary.RDF_TYPE,
				Vocabulary.WB_PROPERTY);

		writeDocumentTerms(document);

		this.writer.writeTripleUriObject(propertyUri,
				Vocabulary.WB_PROPERTY_TYPE, document.getDatatype().getIri());

		this.rdfConversionBuffer.writeValues(this.valueRdfConverter);
		this.rdfConversionBuffer.writePropertyDeclarations(this.writer);
	}

	void writeDocumentTerms(TermedDocument document) throws RDFHandlerException {
		String subjectUri = document.getEntityId().getIri();

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
					this.valueRdfConverter.getMonolingualTextValueLiteral(mtv));
		}
	}

	void writeStatement(Statement statement) throws RDFHandlerException {
		String statementUri = Vocabulary.getStatementUri(statement);

		this.writer.writeTripleUriObject(statementUri, Vocabulary.RDF_TYPE,
				Vocabulary.WB_STATEMENT);
		writeClaim(statementUri, statement.getClaim());

		writeReferences(statementUri, statement.getReferences());
		// What about the RANK?

	}

	String writeReference(Reference reference) throws RDFHandlerException {
		String referenceUri = Vocabulary.getReferenceUri(reference);
		this.writer.writeTripleUriObject(referenceUri, Vocabulary.RDF_TYPE,
				Vocabulary.WB_REFERENCE);
		for (SnakGroup snakGroup : reference.getSnakGroups()) {
			this.snakRdfConverter.setSnakContext(referenceUri,
					PropertyContext.REFERENCE);
			for (Snak snak : snakGroup.getSnaks()) {
				snak.accept(this.snakRdfConverter);
			}
		}
		return referenceUri;
	}

	void writeReferences(String statementUri,
			List<? extends Reference> references) throws RDFHandlerException {
		for (Reference ref : references) {
			this.writer.writeTripleUriObject(statementUri,
					Vocabulary.PROV_WAS_DERIVED_FROM, writeReference(ref));
		}
	}

	void writeClaim(String claimUri, Claim claim) {
		this.snakRdfConverter.setSnakContext(claimUri, PropertyContext.VALUE);
		claim.getMainSnak().accept(this.snakRdfConverter);

		this.snakRdfConverter.setSnakContext(claimUri,
				PropertyContext.QUALIFIER);
		for (SnakGroup snakGroup : claim.getQualifiers()) {
			for (Snak snak : snakGroup.getSnaks()) {
				snak.accept(this.snakRdfConverter);
			}
		}
	}

	void writeSiteLinks(String subjectUri, Map<String, SiteLink> siteLinks)
			throws RDFHandlerException {
		for (String key : siteLinks.keySet()) {
			SiteLink siteLink = siteLinks.get(key);
			String siteLinkUrl = this.sites.getSiteLinkUrl(siteLink);
			if (siteLinkUrl != null) {
				this.writer.writeTripleUriObject(siteLinkUrl,
						Vocabulary.RDF_TYPE, Vocabulary.WB_ARTICLE);
				this.writer.writeTripleUriObject(siteLinkUrl,
						Vocabulary.SCHEMA_ABOUT, subjectUri);
				// Commons has no uniform language; don't export
				if (!"commonswiki".equals(siteLink.getSiteKey())) {
					String siteLanguageCode = this.sites
							.getLanguageCode(siteLink.getSiteKey());
					String languageCode = WikimediaLanguageCodes
							.getLanguageCode(siteLanguageCode);
					this.writer.writeTripleStringObject(siteLinkUrl,
							Vocabulary.SCHEMA_IN_LANGUAGE, languageCode);
				}
			} else {
				logger.warn("Failed to find URL for page \""
						+ siteLink.getPageTitle() + "\" on site \""
						+ siteLink.getSiteKey() + "\"");
			}
		}
	}

}
