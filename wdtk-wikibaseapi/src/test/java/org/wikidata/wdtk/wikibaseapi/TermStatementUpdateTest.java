package org.wikidata.wdtk.wikibaseapi;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.ItemDocumentBuilder;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import com.fasterxml.jackson.core.JsonProcessingException;

/*
 * #%L
 * Wikidata Toolkit Wikibase API
 * %%
 * Copyright (C) 2014 - 2015 Wikidata Toolkit Developers
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

public class TermStatementUpdateTest {
	final static ItemIdValue Q1 = Datamodel.makeWikidataItemIdValue("Q1");
	
	protected TermStatementUpdate makeUpdate(ItemDocument doc, List<MonolingualTextValue> labels,
			List<MonolingualTextValue> descriptions,
			List<MonolingualTextValue> addedLabels,
			List<MonolingualTextValue> deletedLabels) {
		return new TermStatementUpdate(
				doc,
				Collections.<Statement> emptyList(),
				Collections.<Statement> emptyList(),
				labels, descriptions, addedLabels, deletedLabels);
	}
	
	/**
	 * Adding a label on an empty item.
	 */
	@Test
	public void testAddLabel() throws JsonProcessingException {
		ItemDocument currentDocument = ItemDocumentBuilder.forItemId(Q1).build();
		
		MonolingualTextValue label = Datamodel.makeMonolingualTextValue("Apfelstrudel", "de");
		
		TermStatementUpdate su = makeUpdate(currentDocument,
				Collections.singletonList(label),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList());
		
		// Check model
		
		assertEquals(Collections.singleton("de"), su.getLabelUpdates().keySet());
		assertEquals(label.getText(), su.getLabelUpdates().get("de").getText());
		assertTrue(su.getAliasUpdates().isEmpty());
		assertTrue(su.getDescriptionUpdates().isEmpty());
		
		// Check JSON output
		assertEquals("{\"labels\":{\"de\":{\"language\":\"de\",\"text\":\"Apfelstrudel\"}}}",
				su.getJsonUpdateString());
	}
	
	/**
	 * When trying to add an alias for a language that does not have
	 * any label yet, add as label instead.
	 */
	@Test
	public void testAddAliasWithoutLabel() {
		ItemDocument currentDocument = ItemDocumentBuilder.forItemId(Q1).build();
		
		MonolingualTextValue alias = Datamodel.makeMonolingualTextValue("Apfelstrudel", "de");
		TermStatementUpdate su = makeUpdate(currentDocument,
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.singletonList(alias),
				Collections.<MonolingualTextValue> emptyList());
		
		
		assertEquals(su.getLabelUpdates().keySet(), Collections.singleton("de"));
		assertEquals(su.getLabelUpdates().get("de").getText(), alias.getText());
		assertTrue(su.getAliasUpdates().isEmpty());
		assertTrue(su.getDescriptionUpdates().isEmpty());
	}
	
	/**
	 * Adding a label and an alias at the same time.
	 */
	@Test
	public void testAddLabelAndAlias() throws JsonProcessingException {
		ItemDocument currentDocument = ItemDocumentBuilder.forItemId(Q1).build();
		MonolingualTextValue label = Datamodel.makeMonolingualTextValue("strudel aux pommes", "fr");
		MonolingualTextValue alias = Datamodel.makeMonolingualTextValue("Apfelstrudel", "fr");
		TermStatementUpdate su = makeUpdate(currentDocument,
				Collections.singletonList(label),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.singletonList(alias),
				Collections.<MonolingualTextValue> emptyList());
		
		assertEquals(Collections.singleton("fr"), su.getLabelUpdates().keySet());
		assertEquals(label.getText(), su.getLabelUpdates().get("fr").getText());
		assertEquals(Collections.singleton("fr"), su.getAliasUpdates().keySet());
		assertEquals(alias.getText(), su.getAliasUpdates().get("fr").get(0).getText());
		assertTrue(su.getDescriptionUpdates().isEmpty());
	}
	
	/**
	 * Adding the same alias twice.
	 */
	@Test
	public void testAliasTwice() throws JsonProcessingException {
		MonolingualTextValue label = Datamodel.makeMonolingualTextValue("strudel aux pommes", "fr");
		ItemDocument currentDocument = ItemDocumentBuilder.forItemId(Q1).withLabel(label).build();
		MonolingualTextValue alias = Datamodel.makeMonolingualTextValue("Apfelstrudel", "fr");
		List<MonolingualTextValue> newAliases = new ArrayList<>();
		newAliases.add(alias);
		newAliases.add(alias);
		TermStatementUpdate su = makeUpdate(currentDocument,
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				newAliases,
				Collections.<MonolingualTextValue> emptyList());
		
		assertTrue(su.getLabelUpdates().isEmpty());
		assertEquals(su.getAliasUpdates().size(), 1);
		assertEquals("{\"aliases\":{\"fr\":[{\"language\":\"fr\",\"text\":\"Apfelstrudel\"}]}}",
				su.getJsonUpdateString());
	}
	
	/**
	 * Adding an alias on an item that has already got one
	 */
	@Test
	public void testAliasMerge() throws JsonProcessingException {
		MonolingualTextValue label = Datamodel.makeMonolingualTextValue("strudel aux pommes", "fr");
		MonolingualTextValue alias = Datamodel.makeMonolingualTextValue("Apfelstrudel", "fr");
		ItemDocument currentDocument = ItemDocumentBuilder.forItemId(Q1).withLabel(label).withAlias(alias).build();
		

		MonolingualTextValue newAlias = Datamodel.makeMonolingualTextValue("Apfelstrudeln", "fr");
		TermStatementUpdate su = makeUpdate(currentDocument,
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.singletonList(newAlias),
				Collections.<MonolingualTextValue> emptyList());
		
		assertTrue(su.getLabelUpdates().isEmpty());
		assertEquals(1, su.getAliasUpdates().size());
		assertEquals(2, su.getAliasUpdates().get("fr").size());
		assertEquals("{\"aliases\":{\"fr\":[{\"language\":\"fr\",\"text\":\"Apfelstrudel\"},{\"language\":\"fr\",\"text\":\"Apfelstrudeln\"}]}}",
				su.getJsonUpdateString());
	}
	
	/**
	 * Adding an alias identical to the label in the same language does not do anything
	 */
	@Test
	public void testAddLabelAsAlias() {
		MonolingualTextValue label = Datamodel.makeMonolingualTextValue("Apfelstrudel", "de");
		ItemDocument currentDocument = ItemDocumentBuilder.forItemId(Q1).withLabel(label).build();
		
		TermStatementUpdate su = makeUpdate(currentDocument,
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.singletonList(label),
				Collections.<MonolingualTextValue> emptyList()
				);
		
		
		assertTrue(su.getLabelUpdates().isEmpty());
		assertTrue(su.getAliasUpdates().isEmpty());
		assertTrue(su.getDescriptionUpdates().isEmpty());
	}
	
	/**
	 * Adding a label identical to an alias updates the label and deletes the alias
	 */
	@Test
	public void testAddAliasAsLabel() {
		MonolingualTextValue label = Datamodel.makeMonolingualTextValue("strudel aux pommes", "fr");
		MonolingualTextValue alias = Datamodel.makeMonolingualTextValue("Apfelstrudel", "fr");
		ItemDocument currentDocument = ItemDocumentBuilder.forItemId(Q1).withLabel(label).withAlias(alias).build();
		
		TermStatementUpdate su = makeUpdate(currentDocument,
				Collections.singletonList(alias),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList()
				);
		
		assertEquals(Collections.singleton("fr"), su.getAliasUpdates().keySet());
		assertTrue(su.getAliasUpdates().get("fr").isEmpty());
		assertEquals(Collections.singleton("fr"), su.getLabelUpdates().keySet());
		assertEquals(su.getLabelUpdates().get("fr").getText(), alias.getText());
		assertTrue(su.getDescriptionUpdates().isEmpty());
	}
	
	/**
	 * Deleting an alias
	 */
	@Test
	public void testDeleteAlias() throws JsonProcessingException {
		MonolingualTextValue label = Datamodel.makeMonolingualTextValue("strudel aux pommes", "fr");
		MonolingualTextValue alias = Datamodel.makeMonolingualTextValue("Apfelstrudel", "fr");
		ItemDocument currentDocument = ItemDocumentBuilder.forItemId(Q1).withLabel(label).withAlias(alias).build();
		
		TermStatementUpdate su = makeUpdate(currentDocument,
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.singletonList(alias)
				);
		
		assertTrue(su.getLabelUpdates().isEmpty());
		assertEquals(su.getAliasUpdates().size(), 1);
		assertEquals(su.getAliasUpdates().get("fr").size(), 0);
		assertEquals("{\"aliases\":{\"fr\":[]}}",
				su.getJsonUpdateString());
	}
	
	/**
	 * Adding a description, for the sake of coverage…
	 */
	@Test
	public void testDescription() throws JsonProcessingException {
		MonolingualTextValue label = Datamodel.makeMonolingualTextValue("strudel aux pommes", "fr");
		MonolingualTextValue alias = Datamodel.makeMonolingualTextValue("Apfelstrudel", "fr");
		ItemDocument currentDocument = ItemDocumentBuilder.forItemId(Q1).withLabel(label).withAlias(alias).build();

		MonolingualTextValue description = Datamodel.makeMonolingualTextValue("délicieuse pâtisserie aux pommes", "fr");
		TermStatementUpdate su = makeUpdate(currentDocument,
				Collections.<MonolingualTextValue> emptyList(),
				Collections.singletonList(description),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList());
		
		assertTrue(su.getLabelUpdates().isEmpty());
		assertTrue(su.getAliasUpdates().isEmpty());
		assertEquals(Collections.singleton("fr"), su.getDescriptionUpdates().keySet());
		assertEquals("délicieuse pâtisserie aux pommes", su.getDescriptionUpdates().get("fr").getText());
		assertEquals("{\"descriptions\":{\"fr\":{\"language\":\"fr\",\"text\":\"délicieuse pâtisserie aux pommes\"}}}",
				su.getJsonUpdateString());
	}
}
