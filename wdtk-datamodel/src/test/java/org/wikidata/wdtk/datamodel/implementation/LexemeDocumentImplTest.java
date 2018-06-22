package org.wikidata.wdtk.datamodel.implementation;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.DatamodelMapper;
import org.wikidata.wdtk.datamodel.interfaces.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class LexemeDocumentImplTest {

	private final ObjectMapper mapper = new DatamodelMapper("http://example.com/entity/");

	private final LexemeIdValue lid = new LexemeIdValueImpl("L42", "http://example.com/entity/");
	private final ItemIdValue lexCat = new ItemIdValueImpl("Q1", "http://example.com/entity/");
	private final ItemIdValue language = new ItemIdValueImpl("Q2", "http://example.com/entity/");
	private final Statement s = new StatementImpl("MyId", StatementRank.NORMAL,
			new SomeValueSnakImpl(new PropertyIdValueImpl("P42", "http://example.com/entity/")),
			Collections.emptyList(), Collections.emptyList(), lid);
	private final List<StatementGroup> statementGroups = Collections.singletonList(
			new StatementGroupImpl(Collections.singletonList(s))
	);
	private final MonolingualTextValue lemma = new TermImpl("en", "lemma");
	private final List<MonolingualTextValue> lemmaList = Collections.singletonList(lemma);
	private final FormDocument form = new FormDocumentImpl(
			new FormIdValueImpl("L42-F1", "http://example.com/entity/"),
			Collections.singletonList(new TermImpl("en", "foo")),
			Collections.emptyList(),
			Collections.emptyList(),
			0
	);
	private final List<FormDocument> forms = Collections.singletonList(form);
	private final SenseDocument sense = new SenseDocumentImpl(
			new SenseIdValueImpl("L42-S1", "http://example.com/entity/"),
			Collections.singletonList(new TermImpl("en", "foo meaning")),
			Collections.emptyList(),
			0
	);
	private final List<SenseDocument> senses = Collections.singletonList(sense);

	private final LexemeDocument ld1 = new LexemeDocumentImpl(lid, lexCat, language, lemmaList, statementGroups, forms, senses, 1234);
	private final LexemeDocument ld2 = new LexemeDocumentImpl(lid, lexCat, language, lemmaList, statementGroups, forms, senses, 1234);

	private final String JSON_LEXEME = "{\"type\":\"lexeme\",\"id\":\"L42\",\"lexicalCategory\":\"Q1\",\"language\":\"Q2\",\"lemmas\":{\"en\":{\"language\":\"en\",\"value\":\"lemma\"}},\"claims\":{\"P42\":[{\"rank\":\"normal\",\"id\":\"MyId\",\"mainsnak\":{\"property\":\"P42\",\"snaktype\":\"somevalue\"},\"type\":\"statement\"}]},\"forms\":[{\"type\":\"form\",\"id\":\"L42-F1\",\"representations\":{\"en\":{\"language\":\"en\",\"value\":\"foo\"}},\"grammaticalFeatures\":[],\"claims\":{}}],\"senses\":[{\"type\":\"sense\",\"id\":\"L42-S1\",\"glosses\":{\"en\":{\"language\":\"en\",\"value\":\"foo meaning\"}},\"claims\":{}}],\"lastrevid\":1234}";

	@Test
	public void fieldsAreCorrect() {
		assertEquals(ld1.getEntityId(), lid);
		assertEquals(ld1.getLanguage(), language);
		assertEquals(ld1.getLexicalCategory(), lexCat);
		assertEquals(ld1.getLemmas(), Collections.singletonMap(lemma.getLanguageCode(), lemma));
		assertEquals(ld1.getStatementGroups(), statementGroups);
		assertEquals(ld1.getForms(), forms);
	}

	@Test
	public void formGetter() {
		assertEquals(form, ld1.getForm(form.getEntityId()));
	}

	@Test(expected=IndexOutOfBoundsException.class)
	public void formGetterNotFound() {
		ld1.getForm(new FormIdValueImpl("L42-F2", "http://example.com/entity/"));
	}

	@Test
	public void senseGetter() {
		assertEquals(sense, ld1.getSense(sense.getEntityId()));
	}

	@Test(expected=IndexOutOfBoundsException.class)
	public void senseGetterNotFound() {
		ld1.getSense(new SenseIdValueImpl("L42-S2", "http://example.com/entity/"));
	}

	@Test
	public void equalityBasedOnContent() {
		LexemeDocument irDiffLexCat = new LexemeDocumentImpl(lid, language, language, lemmaList, statementGroups, forms, senses, 1234);
		LexemeDocument irDiffLanguage = new LexemeDocumentImpl(lid, lexCat, lexCat, lemmaList, statementGroups, forms, senses, 1234);
		LexemeDocument irDiffLemmas = new LexemeDocumentImpl(lid, lexCat, language, Collections.singletonList(new TermImpl("en", "bar")), statementGroups, forms, senses, 1234);
		LexemeDocument irDiffStatementGroups = new LexemeDocumentImpl(lid, lexCat, language, lemmaList, Collections.emptyList(), forms, senses, 1234);
		LexemeDocument irDiffForms = new LexemeDocumentImpl(lid, lexCat, language, lemmaList, statementGroups, Collections.emptyList(), senses, 1234);
		LexemeDocument irDiffSenses = new LexemeDocumentImpl(lid, lexCat, language, lemmaList, statementGroups, forms, Collections.emptyList(), 1234);
		LexemeDocument irDiffRevisions = new LexemeDocumentImpl(lid, lexCat, language, lemmaList, statementGroups, forms, senses, 1235);
		PropertyDocument pr = new PropertyDocumentImpl(
				new PropertyIdValueImpl("P42", "foo"),
				lemmaList, Collections.emptyList(), Collections.emptyList(),
				Collections.emptyList(),
				new DatatypeIdImpl(DatatypeIdValue.DT_STRING), 1234);
		LexemeDocument irDiffLexemeIdValue = new LexemeDocumentImpl(
				new LexemeIdValueImpl("L43", "http://example.com/entity/"),
				lexCat, language, lemmaList, Collections.emptyList(), forms, senses, 1235);

		assertEquals(ld1, ld1);
		assertEquals(ld1, ld2);
		assertNotEquals(ld1, irDiffLexCat);
		assertNotEquals(ld1, irDiffLanguage);
		assertNotEquals(ld1, irDiffLemmas);
		assertNotEquals(ld1, irDiffStatementGroups);
		assertNotEquals(ld1, irDiffForms);
		assertNotEquals(ld1, irDiffSenses);
		assertNotEquals(ld1, irDiffRevisions);
		assertNotEquals(irDiffStatementGroups, irDiffLexemeIdValue);
		assertNotEquals(ld1, pr);
		assertNotEquals(ld1, null);
		assertNotEquals(ld1, this);
	}

	@Test
	public void hashBasedOnContent() {
		assertEquals(ld1.hashCode(), ld2.hashCode());
	}

	@Test(expected = NullPointerException.class)
	public void idNotNull() {
		new LexemeDocumentImpl(null, lexCat, language, lemmaList, statementGroups, forms, senses,  1234);
	}

	@Test(expected = NullPointerException.class)
	public void lexicalCategoryNotNull() {
		new LexemeDocumentImpl(lid, null, language, lemmaList, statementGroups, forms, senses, 1234);
	}

	@Test(expected = NullPointerException.class)
	public void languageNotNull() {
		new LexemeDocumentImpl(lid, lexCat, null, lemmaList, statementGroups, forms, senses,  1234);
	}

	@Test(expected = NullPointerException.class)
	public void lemmasNotNull() {
		new LexemeDocumentImpl(lid, lexCat, language, null, statementGroups, forms, senses,  1234);
	}

	@Test(expected = IllegalArgumentException.class)
	public void lemmasNotEmpty() {
		new LexemeDocumentImpl(lid, lexCat, language, Collections.emptyList(), statementGroups, forms, senses,  1234);
	}

	@Test
	public void statementGroupsCanBeNull() {
		LexemeDocument doc = new LexemeDocumentImpl(lid, lexCat, language, lemmaList, null, forms, senses,  1234);
		assertTrue(doc.getStatementGroups().isEmpty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void statementGroupsUseSameSubject() {
		LexemeIdValue iid2 = new LexemeIdValueImpl("Q23", "http://example.org/");
		Statement s2 = new StatementImpl("MyId", StatementRank.NORMAL,
				new SomeValueSnakImpl(new PropertyIdValueImpl("P42", "http://wikibase.org/entity/")),
				Collections.emptyList(),  Collections.emptyList(), iid2);
		StatementGroup sg2 = new StatementGroupImpl(Collections.singletonList(s2));

		List<StatementGroup> statementGroups2 = new ArrayList<>();
		statementGroups2.add(statementGroups.get(0));
		statementGroups2.add(sg2);

		new LexemeDocumentImpl(lid, lexCat, language, lemmaList, statementGroups2, forms, senses,  1234);
	}

	@Test
	public void iterateOverAllStatements() {
		Iterator<Statement> statements = ld1.getAllStatements();

		assertTrue(statements.hasNext());
		assertEquals(s, statements.next());
		assertFalse(statements.hasNext());
	}

	@Test
	public void formsCanBeNull() {
		LexemeDocument doc = new LexemeDocumentImpl(lid, lexCat, language, lemmaList, statementGroups, null,  senses, 1234);
		assertTrue(doc.getForms().isEmpty());
	}

	@Test
	public void sensesCanBeNull() {
		LexemeDocument doc = new LexemeDocumentImpl(lid, lexCat, language, lemmaList, statementGroups, forms, null, 1234);
		assertTrue(doc.getSenses().isEmpty());
	}

	@Test
	public void testLexemeToJson() throws JsonProcessingException {
		JsonComparator.compareJsonStrings(JSON_LEXEME, mapper.writeValueAsString(ld1));
	}

	@Test
	public void testLexemeToJava() throws IOException {
		assertEquals(ld1, mapper.readValue(JSON_LEXEME, LexemeDocumentImpl.class));
	}
}
