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

import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.interfaces.*;
import org.wikidata.wdtk.rdf.values.AnyValueConverter;

/**
 * This class provides functions to convert objects of wdtk-datamodel in a rdf
 * graph.
 *
 * @author Michael Günther
 *
 */
abstract public class AbstractRdfConverter {

	static final Logger logger = LoggerFactory.getLogger(AbstractRdfConverter.class);

	final RdfWriter rdfWriter;
	final AnyValueConverter valueRdfConverter;
	final SnakRdfConverter snakRdfConverter;
	final OwlDeclarationBuffer owlDeclarationBuffer = new OwlDeclarationBuffer();
	final ReferenceRdfConverter referenceRdfConverter;
	final PropertyRegister propertyRegister;
	final Sites sites;

	public enum TermKind {
		LABEL,
		DESCRIPTION,
		ALIAS
	}

	public AbstractRdfConverter(RdfWriter rdfWriter, Sites sites,
			PropertyRegister propertyRegister) {
		this.sites = sites;
		this.rdfWriter = rdfWriter;
		this.propertyRegister = propertyRegister;

		this.valueRdfConverter = new AnyValueConverter(rdfWriter,
				this.owlDeclarationBuffer, this.propertyRegister);
		this.snakRdfConverter = new SnakRdfConverter(rdfWriter,
				this.owlDeclarationBuffer, this.propertyRegister,
				this.valueRdfConverter);
		this.referenceRdfConverter = new ReferenceRdfConverter(rdfWriter,
				this.snakRdfConverter, this.propertyRegister.siteUri);
	}

	/**
	 * Writes OWL declarations for all basic vocabulary elements used in the
	 * dump.
	 *
	 * Example of the triples written by this method:
	 * {@code wikibase:propertyType rdf:type owl:ObjectProperty}
	 */
	public void writeBasicDeclarations() throws RDFHandlerException {
		for (Map.Entry<String, String> uriType : Vocabulary
				.getKnownVocabularyTypes().entrySet()) {
			this.rdfWriter.writeTripleUriObject(uriType.getKey(),
					RdfWriter.RDF_TYPE, uriType.getValue());
		}
	}

	/**
	 * Writes all namespace declarations used in the dump, for example {@code wikibase:} or {@code schema:}.
	 */
	public void writeNamespaceDeclarations() throws RDFHandlerException {
		this.rdfWriter.writeNamespaceDeclaration("wd",
				this.propertyRegister.getUriPrefix());
		this.rdfWriter
				.writeNamespaceDeclaration("wikibase", Vocabulary.PREFIX_WBONTO);
		this.rdfWriter.writeNamespaceDeclaration("rdf", Vocabulary.PREFIX_RDF);
		this.rdfWriter
				.writeNamespaceDeclaration("rdfs", Vocabulary.PREFIX_RDFS);
		this.rdfWriter.writeNamespaceDeclaration("owl", Vocabulary.PREFIX_OWL);
		this.rdfWriter.writeNamespaceDeclaration("xsd", Vocabulary.PREFIX_XSD);
		this.rdfWriter.writeNamespaceDeclaration("schema",
				Vocabulary.PREFIX_SCHEMA);
		this.rdfWriter
				.writeNamespaceDeclaration("skos", Vocabulary.PREFIX_SKOS);
		this.rdfWriter
				.writeNamespaceDeclaration("prov", Vocabulary.PREFIX_PROV);
	}

	/**
	 * Writes all buffered triples and finishes writing a document.
	 *
	 * This will take care of writing auxiliary triples that got buffered during serialization,
	 * such as OWL declarations, references and auxiliary triples for complex values.
	 */
	public void finishDocument() throws RDFHandlerException {
		this.snakRdfConverter.writeAuxiliaryTriples();
		this.writeOWLDeclarations();
		this.referenceRdfConverter.writeReferences();
	}

	public void writeOWLDeclarations() {
		this.owlDeclarationBuffer.writePropertyDeclarations(this.rdfWriter, true, true);
	}

	public void writeDocumentType(Resource subject, IRI type) {
		this.rdfWriter.writeTripleUriObject(subject, RdfWriter.RDF_TYPE, type.toString());
	}

	public void writeItemDocument(ItemDocument document)
			throws RDFHandlerException {
		final String subjectUri = document.getEntityId().getIri();
		final Resource subject = this.rdfWriter.getUri(subjectUri);

		writeDocumentType(subject, RdfWriter.WB_ITEM);
		writeDocumentTerms(document);
		writeStatements(document);
		writeSiteLinks(subject, document.getSiteLinks());

		finishDocument();
	}

	public void writePropertyDatatype(PropertyDocument document) {
		this.rdfWriter.writeTripleValueObject(
				this.rdfWriter.getUri(document.getEntityId().getIri()),
				RdfWriter.WB_PROPERTY_TYPE,
				this.rdfWriter.getUri(document.getDatatype().getIri()));
	}

	public void writePropertyDocument(PropertyDocument document)
			throws RDFHandlerException {

		propertyRegister.setPropertyType(document.getEntityId(), document
				.getDatatype().getIri());

		final String subjectUri = document.getEntityId().getIri();
		final Resource subject = this.rdfWriter.getUri(subjectUri);

		writeDocumentType(subject, RdfWriter.WB_PROPERTY);
		writePropertyDatatype(document);
		writeDocumentTerms(document);
		writeStatements(document);
		writeInterPropertyLinks(document);

		finishDocument();
	}

	/**
	 * Writes triples which connect properties with their corresponding rdf
	 * properties for statements, simple statements, qualifiers, reference
	 * attributes and values.
	 */
	public void writeInterPropertyLinks(PropertyDocument document)
			throws RDFHandlerException {
		Resource subject = this.rdfWriter.getUri(document.getEntityId()
				.getIri());
		this.rdfWriter.writeTripleUriObject(subject, this.rdfWriter
				.getUri(Vocabulary.WB_DIRECT_CLAIM_PROP), Vocabulary
				.getPropertyUri(document.getEntityId(),
						PropertyContext.DIRECT));

		this.rdfWriter.writeTripleUriObject(subject, this.rdfWriter
				.getUri(Vocabulary.WB_CLAIM_PROP), Vocabulary.getPropertyUri(
				document.getEntityId(), PropertyContext.STATEMENT));

		this.rdfWriter.writeTripleUriObject(subject, this.rdfWriter
				.getUri(Vocabulary.WB_STATEMENT_PROP), Vocabulary
				.getPropertyUri(document.getEntityId(),
						PropertyContext.VALUE_SIMPLE));

		this.rdfWriter.writeTripleUriObject(subject, this.rdfWriter
				.getUri(Vocabulary.WB_STATEMENT_VALUE_PROP),
				Vocabulary.getPropertyUri(document.getEntityId(),
						PropertyContext.VALUE));

		this.rdfWriter.writeTripleUriObject(subject, this.rdfWriter
				.getUri(Vocabulary.WB_QUALIFIER_PROP), Vocabulary
				.getPropertyUri(document.getEntityId(),
						PropertyContext.QUALIFIER_SIMPLE));

		this.rdfWriter.writeTripleUriObject(subject, this.rdfWriter
				.getUri(Vocabulary.WB_QUALIFIER_VALUE_PROP), Vocabulary
				.getPropertyUri(document.getEntityId(),
						PropertyContext.QUALIFIER));

		this.rdfWriter.writeTripleUriObject(subject, this.rdfWriter
				.getUri(Vocabulary.WB_REFERENCE_PROP), Vocabulary
				.getPropertyUri(document.getEntityId(),
						PropertyContext.REFERENCE_SIMPLE));

		this.rdfWriter.writeTripleUriObject(subject, this.rdfWriter
				.getUri(Vocabulary.WB_REFERENCE_VALUE_PROP), Vocabulary
				.getPropertyUri(document.getEntityId(),
						PropertyContext.REFERENCE));

		this.rdfWriter.writeTripleUriObject(subject, this.rdfWriter
				.getUri(Vocabulary.WB_NO_VALUE_PROP), Vocabulary
				.getPropertyUri(document.getEntityId(),
						PropertyContext.NO_VALUE));
		// TODO something more with NO_VALUE
	}

	public void writeDocumentTerms(TermedDocument document)
			throws RDFHandlerException {
		final Resource subject = this.rdfWriter.getUri(document.getEntityId().getIri());
		writeTermTriples(subject, TermKind.LABEL, document.getLabels().values());
		writeTermTriples(subject, TermKind.DESCRIPTION, document.getDescriptions().values());
		for (List<MonolingualTextValue> aliases : document.getAliases().values()) {
			writeTermTriples(subject, TermKind.ALIAS, aliases);
		}
	}

	public void writeTermTriples(Resource subject, TermKind kind,
						  Collection<MonolingualTextValue> terms) throws RDFHandlerException {
		final IRI predicate;
		switch (kind) {
			case LABEL:
				predicate = RdfWriter.RDFS_LABEL;
				break;
			case DESCRIPTION:
				predicate = RdfWriter.SCHEMA_DESCRIPTION;
				break;
			case ALIAS:
				predicate = RdfWriter.SKOS_ALT_LABEL;
				break;
			default:
				throw new IllegalArgumentException();
		}
		for (MonolingualTextValue mtv : terms) {
			this.rdfWriter.writeTripleValueObject(subject, predicate,
					AbstractRdfConverter.getMonolingualTextValueLiteral(mtv,
							this.rdfWriter));
		}
	}

	public void writeStatements(StatementDocument statementDocument)
			throws RDFHandlerException {
		for (StatementGroup statementGroup : statementDocument.getStatementGroups()) {
			// determine the rank of the best statement
			final StatementGroup bestStatements = statementGroup.getBestStatements();
			final StatementRank bestRank;
			if (statementGroup.getBestStatements() != null) {
				bestRank = bestStatements.iterator().next().getRank();
			} else {
				bestRank = null;
			}

			for (Statement statement : statementGroup) {
				writeStatement(statement, statement.getRank() == bestRank);
			}
		}
	}

	public void writeStatement(Statement statement, boolean best) throws RDFHandlerException {
		if (best) {
			writeSimpleStatement(statement);
		}
		writeFullStatement(statement, best);
	}

	public void writeFullStatement(Statement statement, boolean best) throws RDFHandlerException {
		final Resource subject = this.rdfWriter.getUri(statement.getSubject().getIri());

		String statementUri = Vocabulary.getStatementUri(statement);
		Resource statementResource = this.rdfWriter.getUri(statementUri);
		final IRI propertyIri = this.rdfWriter.getUri(
				Vocabulary.getPropertyUri(statement.getMainSnak().getPropertyId(), PropertyContext.STATEMENT));

		this.rdfWriter.writeTripleUriObject(subject, propertyIri, statementUri);
		this.rdfWriter.writeTripleValueObject(statementResource,
				RdfWriter.RDF_TYPE, RdfWriter.WB_STATEMENT);
		writeClaim(statementResource, statement.getClaim());
		writeReferences(statementResource, statement.getReferences());
		writeStatementRankTriple(statementResource, statement.getRank(), best);
	}

	public void writeSimpleStatement(Statement statement) {
		final Resource subject = this.rdfWriter.getUri(statement.getSubject().getIri());

		this.snakRdfConverter.setSnakContext(subject, PropertyContext.DIRECT);
		statement.getMainSnak().accept(this.snakRdfConverter);
	}

	/**
	 * Writes a triple for the {@link StatementRank} of a {@link Statement} to
	 * the dump. If this is a best-rank statement, also writes a best rank triple.
	 *
	 * @param subject The IRI of the statement
	 * @param rank The rank of the statement
	 * @param best True if this statement is a best-rank statement
	 */
	public void writeStatementRankTriple(Resource subject, StatementRank rank, boolean best) {
		try {
			this.rdfWriter.writeTripleUriObject(subject, RdfWriter.WB_RANK,
					Vocabulary.getStatementRankUri(rank));
			if (best) {
				this.rdfWriter.writeTripleUriObject(subject, RdfWriter.RDF_TYPE, Vocabulary.WB_BEST_RANK);
			}
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public void writeReferences(Resource statementResource,
			List<? extends Reference> references) throws RDFHandlerException {
		for (Reference reference : references) {
			Resource resource = this.referenceRdfConverter
					.addReference(reference);
			this.rdfWriter.writeTripleValueObject(statementResource,
					RdfWriter.PROV_WAS_DERIVED_FROM, resource);
		}
	}

	public void writeClaim(Resource claimResource, Claim claim) {
		// write main snak
		this.snakRdfConverter.setSnakContext(claimResource,
				PropertyContext.VALUE);
		claim.getMainSnak().accept(this.snakRdfConverter);
		this.snakRdfConverter.setSnakContext(claimResource,
				PropertyContext.VALUE_SIMPLE);
		claim.getMainSnak().accept(this.snakRdfConverter);
		// write qualifier
		this.snakRdfConverter.setSnakContext(claimResource,
				PropertyContext.QUALIFIER);
		for (SnakGroup snakGroup : claim.getQualifiers()) {
			for (Snak snak : snakGroup) {
				snak.accept(this.snakRdfConverter);
			}
		}
		this.snakRdfConverter.setSnakContext(claimResource,
				PropertyContext.QUALIFIER_SIMPLE);
		for (SnakGroup snakGroup : claim.getQualifiers()) {
			for (Snak snak : snakGroup) {
				snak.accept(this.snakRdfConverter);
			}
		}
	}

	public void writeSiteLinks(Resource subject, Map<String, SiteLink> siteLinks)
			throws RDFHandlerException {

		for (String key : siteLinks.keySet()) {
			SiteLink siteLink = siteLinks.get(key);
			String siteLinkUrl = this.sites.getSiteLinkUrl(siteLink);
			if (siteLinkUrl != null) {
				IRI siteLinkUri = this.rdfWriter.getUri(siteLinkUrl);

				this.rdfWriter.writeTripleValueObject(siteLinkUri,
						RdfWriter.RDF_TYPE, RdfWriter.SCHEMA_ARTICLE);
				this.rdfWriter.writeTripleValueObject(siteLinkUri,
						RdfWriter.SCHEMA_ABOUT, subject);

				String siteLanguageCode = this.sites.getLanguageCode(siteLink.getSiteKey());
				this.rdfWriter.writeTripleStringObject(siteLinkUri,
						RdfWriter.SCHEMA_IN_LANGUAGE, convertSiteLanguageCode(siteLanguageCode));

				for(ItemIdValue badge : siteLink.getBadges()) {
					this.rdfWriter.writeTripleUriObject(siteLinkUri,
							RdfWriter.WB_BADGE, badge.getIri());
				}
			} else {
				logger.warn("Failed to find URL for page \""
						+ siteLink.getPageTitle() + "\" on site \""
						+ siteLink.getSiteKey() + "\"");
			}
		}
	}

	private String convertSiteLanguageCode(String languageCode) {
		try {
			return WikimediaLanguageCodes.getLanguageCode(languageCode);
		} catch (IllegalArgumentException e) {
			logger.warn("Unknown Wikimedia language code \""
					+ languageCode
					+ "\". Using this code in RDF now, but this might be wrong.");
			return languageCode;
		}
	}
	
	public static Value getMonolingualTextValueLiteral(
			MonolingualTextValue value, RdfWriter rdfWriter) {
		String languageCode;
		try {
			languageCode = WikimediaLanguageCodes.getLanguageCode(value
					.getLanguageCode());
		} catch (IllegalArgumentException e) {
			languageCode = value.getLanguageCode();
			logger.warn("Unknown Wikimedia language code \""
					+ languageCode
					+ "\". Using this code in RDF now, but this might be wrong.");
		}
		return rdfWriter.getLiteral(value.getText(), languageCode);
	}
}
