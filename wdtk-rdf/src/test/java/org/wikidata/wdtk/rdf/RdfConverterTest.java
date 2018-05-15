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

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.StatementBuilder;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.implementation.SitesImpl;
import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

public class RdfConverterTest {

	ByteArrayOutputStream out;

	RdfWriter rdfWriter;
	RdfConverter rdfConverter;

	SitesImpl sites;

	ValueFactory rdfFactory = SimpleValueFactory.getInstance();
	Resource resource = rdfFactory.createIRI("http://test.org/");

	final TestObjectFactory objectFactory = new TestObjectFactory();
	final DataObjectFactory dataObjectFactory = new DataObjectFactoryImpl();

	@Before
	public void setUp() throws Exception {
		this.out = new ByteArrayOutputStream();
		this.rdfWriter = new RdfWriter(RDFFormat.TURTLE, out);
		this.sites = new SitesImpl();
		this.rdfConverter = new RdfConverter(this.rdfWriter, this.sites,
				new MockPropertyRegister());
		this.rdfWriter.start();
	}

	@Test
	public void testWriteItemDocument() throws RDFHandlerException,
			IOException, RDFParseException {
		ItemDocument document = this.objectFactory.createItemDocument();
		this.rdfConverter.writeItemDocument(document);
		this.rdfWriter.finish();
		Model model = RdfTestHelpers.parseRdf(out.toString());
		assertEquals(model, RdfTestHelpers.parseRdf(RdfTestHelpers
				.getResourceFromFile("ItemDocument.rdf")));
	}

	@Test
	public void testWriteItemDocumentWithNullPropertyTypes() throws RDFHandlerException,
			IOException, RDFParseException {
		this.rdfConverter = new RdfConverter(this.rdfWriter, this.sites,
				new MockPropertyRegister.WithNullPropertyTypes());

		ItemDocument document = this.objectFactory.createItemDocument();
		this.rdfConverter.writeItemDocument(document);
		this.rdfWriter.finish();
		Model model = RdfTestHelpers.parseRdf(out.toString());
		assertEquals(model, RdfTestHelpers.parseRdf(RdfTestHelpers
				.getResourceFromFile("ItemDocumentUnknownPropertyTypes.rdf")));
	}


    @Test
	public void testWritePropertyDocument() throws RDFHandlerException,
			RDFParseException, IOException {
		PropertyDocument document = this.objectFactory
				.createEmptyPropertyDocument();
		this.rdfConverter.writePropertyDocument(document);
		this.rdfWriter.finish();
		Model model = RdfTestHelpers.parseRdf(this.out.toString());
		assertEquals(model, RdfTestHelpers.parseRdf(RdfTestHelpers
				.getResourceFromFile("EmptyPropertyDocument.rdf")));
	}

	@Test
	public void testWriteStatementRankTriple() throws RDFHandlerException,
			RDFParseException, IOException {
		StatementRank rank = StatementRank.DEPRECATED;
		Resource subject = this.rdfFactory
				.createIRI("http://www.wikidata.org/Q10Snone");
		this.rdfConverter.writeStatementRankTriple(subject, rank);
		this.rdfWriter.finish();
		Model model = RdfTestHelpers.parseRdf(this.out.toString());
		assertEquals(RdfTestHelpers.parseRdf(RdfTestHelpers
				.getResourceFromFile("StatementRankTriple.rdf")), model);
	}

	@Test
	public void testStatementSimpleValue() throws RDFHandlerException,
			RDFParseException, IOException {
		Statement statement = objectFactory.createStatement("Q100", "P227");
		this.rdfConverter.writeStatement(statement);
		this.rdfWriter.finish();
		Model model = RdfTestHelpers.parseRdf(this.out.toString());
		assertEquals(model, RdfTestHelpers.parseRdf(RdfTestHelpers
				.getResourceFromFile("Statement.rdf")));
	}

	@Test
	public void testStatementComplexValue() throws RDFHandlerException,
			RDFParseException, IOException {
		GlobeCoordinatesValue value = Datamodel.makeGlobeCoordinatesValue(51,
				13, GlobeCoordinatesValue.PREC_DEGREE,
				GlobeCoordinatesValue.GLOBE_EARTH);
		Statement statement = StatementBuilder
				.forSubjectAndProperty(ItemIdValue.NULL, PropertyIdValue.NULL)
				.withValue(value).build();
		this.rdfConverter.writeStatement(statement);
		this.rdfWriter.finish();
		Model model = RdfTestHelpers.parseRdf(this.out.toString());
		assertEquals(model, RdfTestHelpers.parseRdf(RdfTestHelpers
				.getResourceFromFile("StatementCplx.rdf")));
	}

	@Test
	public void testStatementNoValue() throws RDFHandlerException,
			RDFParseException, IOException {
		PropertyIdValue pid = dataObjectFactory.getPropertyIdValue("P31", "http://www.wikidata.org/");
		Statement statement = StatementBuilder
				.forSubjectAndProperty(ItemIdValue.NULL, pid)
				.withNoValue().build();
		this.rdfConverter.writeStatement(statement);
		this.rdfWriter.finish();
		Model model = RdfTestHelpers.parseRdf(this.out.toString());
		assertEquals(model, RdfTestHelpers.parseRdf(RdfTestHelpers
				.getResourceFromFile("StatementNoValue.rdf")));
	}

	@Test
	public void testWriteBasicDeclarations() throws RDFHandlerException,
			RDFParseException, IOException {
		this.rdfConverter.writeBasicDeclarations();
		this.rdfWriter.finish();
		Model model = RdfTestHelpers.parseRdf(this.out.toString());
		assertEquals(RdfTestHelpers.parseRdf(RdfTestHelpers
				.getResourceFromFile("BasicDeclarations.rdf")), model);
	}

	@Test
	public void testWriteNamespaceDeclarations() throws RDFHandlerException,
			RDFParseException, IOException {
		this.rdfConverter.writeNamespaceDeclarations();
		this.rdfWriter.finish();
		Model model = RdfTestHelpers.parseRdf(this.out.toString());
		assertEquals(RdfTestHelpers.parseRdf(RdfTestHelpers
				.getResourceFromFile("Namespaces.rdf")), model);
	}

	@Test
	public void testWriteSiteLinks() throws RDFHandlerException, IOException,
			RDFParseException {
		this.sites.setSiteInformation("enwiki", "wikipedia", "en", "mediawiki",
				"http://en.wikipedia.org/w/$1",
				"http://en.wikipedia.org/wiki/$1");
		this.sites.setSiteInformation("dewiki", "wikipedia", "de", "mediawiki",
				"http://de.wikipedia.org/w/$1",
				"http://de.wikipedia.org/wiki/$1");
		Map<String, SiteLink> siteLinks = objectFactory.createSiteLinks();
		this.rdfConverter.writeSiteLinks(this.resource, siteLinks);
		this.rdfWriter.finish();
		Model model = RdfTestHelpers.parseRdf(out.toString());
		assertEquals(model, RdfTestHelpers.parseRdf(RdfTestHelpers
				.getResourceFromFile("SiteLinks.rdf")));

	}

	private ItemDocument createTestItemDocument() {
		ItemIdValue itemValue = dataObjectFactory.getItemIdValue("Q100",
				"http://www.wikidata.org/");
		ItemIdValue value1 = dataObjectFactory.getItemIdValue("Q10",
				"http://www.wikidata.org/");
		ItemIdValue value2 = dataObjectFactory.getItemIdValue("Q11",
				"http://www.wikidata.org/");
		PropertyIdValue propertyIdValueP31 = dataObjectFactory
				.getPropertyIdValue("P31", "http://www.wikidata.org/");
		PropertyIdValue propertyIdValueP279 = dataObjectFactory
				.getPropertyIdValue("P279", "http://www.wikidata.org/");
		// Statement InstaceOf - P31
		ValueSnak mainSnak1 = dataObjectFactory.getValueSnak(
				propertyIdValueP31, value1);
		Statement statement1 = dataObjectFactory.getStatement(itemValue, mainSnak1,
				Collections.emptyList(), Collections.emptyList(),
				StatementRank.NORMAL, "10103");
		List<Statement> statementList1 = new ArrayList<Statement>();
		statementList1.add(statement1);
		StatementGroup statementGroup1 = this.dataObjectFactory
				.getStatementGroup(statementList1);
		// Statement SubclassOf - P279
		ValueSnak mainSnak2 = dataObjectFactory.getValueSnak(
				propertyIdValueP279, value2);
		Statement statement2 = dataObjectFactory.getStatement(itemValue, mainSnak2,
				Collections.emptyList(), Collections.emptyList(),
				StatementRank.NORMAL, "10104");
		List<Statement> statementList2 = new ArrayList<Statement>();
		statementList2.add(statement2);
		StatementGroup statementGroup2 = this.dataObjectFactory
				.getStatementGroup(statementList2);

		List<StatementGroup> statementGroups = new ArrayList<StatementGroup>();
		statementGroups.add(statementGroup1);
		statementGroups.add(statementGroup2);
		return dataObjectFactory.getItemDocument(itemValue,
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				statementGroups, Collections.<String, SiteLink> emptyMap(), 0);
	}

	private PropertyDocument createTestPropertyDocument() {
		PropertyIdValue propertyIdValue = this.dataObjectFactory
				.getPropertyIdValue("P171", "http://www.wikidata.org/");
		PropertyIdValue subpropertyOf = this.dataObjectFactory
				.getPropertyIdValue("P1647", "http://www.wikidata.org/");
		PropertyIdValue subclassOf = this.dataObjectFactory.getPropertyIdValue(
				"P279", "http://www.wikidata.org/");

		List<MonolingualTextValue> labels = new ArrayList<MonolingualTextValue>();
		List<MonolingualTextValue> descriptions = new ArrayList<MonolingualTextValue>();
		List<MonolingualTextValue> aliases = new ArrayList<MonolingualTextValue>();

		List<Statement> statements = new ArrayList<Statement>();
		statements.add(this.dataObjectFactory.getStatement(propertyIdValue,
				this.dataObjectFactory.getValueSnak(subpropertyOf, subclassOf),
				Collections.emptyList(), Collections.emptyList(),
				StatementRank.NORMAL, "P171$6fb788c6-4e81-8398-3a1a-68f8b98a8943"));
		StatementGroup statementGroup = this.dataObjectFactory
				.getStatementGroup(statements);
		List<StatementGroup> statementGroups = new ArrayList<StatementGroup>();
		statementGroups.add(statementGroup);

		DatatypeIdValue datatypeId = this.dataObjectFactory
				.getDatatypeIdValue(DatatypeIdValue.DT_ITEM);

		return this.dataObjectFactory.getPropertyDocument(propertyIdValue,
				labels, descriptions, aliases, statementGroups, datatypeId, 0);
	}

	private PropertyDocument createWrongTestPropertyDocument() {
		PropertyIdValue propertyIdValue = this.dataObjectFactory
				.getPropertyIdValue("P171", "http://www.wikidata.org/");
		PropertyIdValue subpropertyOf = this.dataObjectFactory
				.getPropertyIdValue("P1647", "http://www.wikidata.org/");
		PropertyIdValue wrongProperty = this.dataObjectFactory
				.getPropertyIdValue("P90000", "http://www.wikidata.org/");

		List<MonolingualTextValue> labels = new ArrayList<MonolingualTextValue>();
		List<MonolingualTextValue> descriptions = new ArrayList<MonolingualTextValue>();
		List<MonolingualTextValue> aliases = new ArrayList<MonolingualTextValue>();

		List<Statement> statements = new ArrayList<Statement>();
		statements.add(this.dataObjectFactory.getStatement(propertyIdValue,
				this.dataObjectFactory.getValueSnak(subpropertyOf, wrongProperty),
				Collections.emptyList(), Collections.emptyList(),
				StatementRank.NORMAL, "P171$6fb788c6-4e81-8398-3a1a-68f8b98a8943"));
		StatementGroup statementGroup = this.dataObjectFactory
				.getStatementGroup(statements);
		List<StatementGroup> statementGroups = new ArrayList<StatementGroup>();
		statementGroups.add(statementGroup);

		DatatypeIdValue datatypeId = this.dataObjectFactory
				.getDatatypeIdValue(DatatypeIdValue.DT_ITEM);

		return this.dataObjectFactory.getPropertyDocument(propertyIdValue,
				labels, descriptions, aliases, statementGroups, datatypeId, 0);
	}

	@Test
	public void testWriteSimpleStatements() throws RDFHandlerException,
			RDFParseException, IOException {
		ItemDocument document = createTestItemDocument();
		this.rdfConverter.writeSimpleStatements(resource, document);
		this.rdfWriter.finish();
		Model model = RdfTestHelpers.parseRdf(this.out.toString());
		assertEquals(
				RdfTestHelpers
						.parseRdf("\n<http://test.org/> <http://www.wikidata.org/prop/direct/P31> <http://www.wikidata.org/Q10> ;\n"
								+ "<http://www.wikidata.org/prop/direct/P279> <http://www.wikidata.org/Q11> .\n"),
				model);
	}

	@Test
	public void testWriteInterPropertyLinks() throws RDFHandlerException,
			RDFParseException, IOException {
		PropertyDocument document = this.dataObjectFactory.getPropertyDocument(
				this.dataObjectFactory.getPropertyIdValue("P17",
						"http://www.wikidata.org/"), Collections
						.<MonolingualTextValue> emptyList(), Collections
						.<MonolingualTextValue> emptyList(), Collections
						.<MonolingualTextValue> emptyList(), Collections
						.<StatementGroup> emptyList(), this.dataObjectFactory
						.getDatatypeIdValue(DatatypeIdValue.DT_ITEM), 0);
		this.rdfConverter.writeInterPropertyLinks(document);
		this.rdfWriter.finish();
		Model model = RdfTestHelpers.parseRdf(out.toString());

		assertEquals(RdfTestHelpers.parseRdf(RdfTestHelpers
				.getResourceFromFile("InterPropertyLinks.rdf")), model);
	}

	@After
	public void clear() throws RDFHandlerException, IOException {
		this.out.close();
	}

}
