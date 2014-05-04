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

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
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

	final RdfWriter writer;
	final ValueRdfConverter valueRdfConverter;
	final SnakRdfConverter snakRdfConverter;
	final RdfConversionBuffer rdfConversionBuffer;
	final PropertyTypes propertyTypes;
	final Sites sites;

	public RdfConverter(RdfWriter writer, Sites sites) {
		this.sites = sites;
		this.writer = writer;
		this.propertyTypes = new PropertyTypes(
				"http://www.wikidata.org/w/api.php");
		this.rdfConversionBuffer = new RdfConversionBuffer();
		this.valueRdfConverter = new ValueRdfConverter(writer,
				this.rdfConversionBuffer, this.propertyTypes);
		this.snakRdfConverter = new SnakRdfConverter(writer,
				this.rdfConversionBuffer, this.propertyTypes,
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
					RdfWriter.RDF_TYPE, uriType.getValue());
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
		Resource subject = this.writer.getUri(subjectUri);

		this.writer.writeTripleValueObject(subject, RdfWriter.RDF_TYPE,
				RdfWriter.WB_ITEM);

		writeDocumentTerms(subject, document);

		for (StatementGroup statementGroup : document.getStatementGroups()) {
			for (Statement statement : statementGroup.getStatements()) {
				URI property = this.writer.getUri(Vocabulary.getPropertyUri(
						statement.getClaim().getMainSnak().getPropertyId(),
						PropertyContext.STATEMENT));
				this.writer.writeTripleUriObject(subject, property,
						Vocabulary.getStatementUri(statement));
			}
		}

		for (StatementGroup statementGroup : document.getStatementGroups()) {
			for (Statement statement : statementGroup.getStatements()) {
				writeStatement(statement);
			}
		}

		writeSiteLinks(subject, document.getSiteLinks());

		this.rdfConversionBuffer.writeValues(this.valueRdfConverter);
		this.rdfConversionBuffer.writePropertyDeclarations(this.writer);
		this.rdfConversionBuffer
				.writePropertyRestrictions(this.snakRdfConverter);
		this.rdfConversionBuffer.writeReferences(this);
	}

	public void writePropertyDocument(PropertyDocument document)
			throws RDFHandlerException {

		String propertyUri = document.getEntityId().getIri();
		Resource subject = this.writer.getUri(propertyUri);

		this.writer.writeTripleValueObject(subject, RdfWriter.RDF_TYPE,
				RdfWriter.WB_PROPERTY);

		writeDocumentTerms(subject, document);

		this.writer.writeTripleValueObject(subject, RdfWriter.WB_PROPERTY_TYPE,
				this.valueRdfConverter.getDatatypeIdValueLiteral(document
						.getDatatype()));
		this.propertyTypes.setPropertyType(document.getPropertyId(), document
				.getDatatype().getIri());

		// Most of these should do nothing for properties, but this might change
		// in the future:
		this.rdfConversionBuffer.writeValues(this.valueRdfConverter);
		this.rdfConversionBuffer.writePropertyDeclarations(this.writer);
		this.rdfConversionBuffer.writeReferences(this);
	}

	void writeDocumentTerms(Resource subject, TermedDocument document)
			throws RDFHandlerException {

		writeTermTriples(subject, RdfWriter.RDFS_LABEL, document.getLabels()
				.values());
		writeTermTriples(subject, RdfWriter.SCHEMA_DESCRIPTION, document
				.getDescriptions().values());
		for (List<MonolingualTextValue> aliases : document.getAliases()
				.values()) {
			writeTermTriples(subject, RdfWriter.SKOS_ALT_LABEL, aliases);
		}
	}

	void writeTermTriples(Resource subject, URI predicate,
			Collection<MonolingualTextValue> terms) throws RDFHandlerException {
		for (MonolingualTextValue mtv : terms) {
			this.writer.writeTripleValueObject(subject, predicate,
					this.valueRdfConverter.getMonolingualTextValueLiteral(mtv));
		}
	}

	void writeStatement(Statement statement) throws RDFHandlerException {
		String statementUri = Vocabulary.getStatementUri(statement);
		Resource statementResource = this.writer.getUri(statementUri);

		this.writer.writeTripleValueObject(statementResource,
				RdfWriter.RDF_TYPE, RdfWriter.WB_STATEMENT);
		writeClaim(statementResource, statement.getClaim());

		writeReferences(statementResource, statement.getReferences());
		// TODO What about the RANK?

	}

	void writeReference(Reference reference, Resource resource)
			throws RDFHandlerException {

		this.writer.writeTripleValueObject(resource, RdfWriter.RDF_TYPE,
				RdfWriter.WB_REFERENCE);
		for (SnakGroup snakGroup : reference.getSnakGroups()) {
			this.snakRdfConverter.setSnakContext(resource,
					PropertyContext.REFERENCE);
			for (Snak snak : snakGroup.getSnaks()) {
				snak.accept(this.snakRdfConverter);
			}
		}
	}

	void writeReferences(Resource statementResource,
			List<? extends Reference> references) throws RDFHandlerException {
		for (Reference reference : references) {
			String referenceUri = Vocabulary.getReferenceUri(reference);
			Resource resource = this.writer.getUri(referenceUri);
			this.rdfConversionBuffer.addReference(reference, resource);
			this.writer.writeTripleValueObject(statementResource,
					RdfWriter.PROV_WAS_DERIVED_FROM, resource);
		}
	}

	void writeClaim(Resource claimResource, Claim claim) {
		this.snakRdfConverter.setSnakContext(claimResource,
				PropertyContext.VALUE);
		claim.getMainSnak().accept(this.snakRdfConverter);

		this.snakRdfConverter.setSnakContext(claimResource,
				PropertyContext.QUALIFIER);
		for (SnakGroup snakGroup : claim.getQualifiers()) {
			for (Snak snak : snakGroup.getSnaks()) {
				snak.accept(this.snakRdfConverter);
			}
		}
	}

	void writeSiteLinks(Resource subject, Map<String, SiteLink> siteLinks)
			throws RDFHandlerException {
		for (String key : siteLinks.keySet()) {
			SiteLink siteLink = siteLinks.get(key);
			String siteLinkUrl = this.sites.getSiteLinkUrl(siteLink);
			URI siteLinkUri = this.writer.getUri(siteLinkUrl);
			if (siteLinkUrl != null) {
				this.writer.writeTripleValueObject(siteLinkUri,
						RdfWriter.RDF_TYPE, RdfWriter.WB_ARTICLE);
				this.writer.writeTripleValueObject(siteLinkUri,
						RdfWriter.SCHEMA_ABOUT, subject);
				// Commons has no uniform language; don't export
				if (!"commonswiki".equals(siteLink.getSiteKey())) {
					String siteLanguageCode = this.sites
							.getLanguageCode(siteLink.getSiteKey());
					String languageCode;
					try {
						languageCode = WikimediaLanguageCodes
								.getLanguageCode(siteLanguageCode);
					} catch (IllegalArgumentException e) {
						languageCode = siteLanguageCode;
						logger.warn("Unknown Wikimedia language code \""
								+ languageCode
								+ "\". Using this code in RDF now, but this might be wrong.");
					}

					this.writer.writeTripleStringObject(siteLinkUri,
							RdfWriter.SCHEMA_IN_LANGUAGE, languageCode);
				}
			} else {
				logger.warn("Failed to find URL for page \""
						+ siteLink.getPageTitle() + "\" on site \""
						+ siteLink.getSiteKey() + "\"");
			}
		}
	}

}
