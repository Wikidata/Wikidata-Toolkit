package org.wikidata.wdtk.datamodel.helpers;

/*
 * #%L
 * Wikidata Toolkit Data Model
 * %%
 * Copyright (C) 2014 - 2018 Wikidata Toolkit Developers
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
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementDocument;
import org.wikidata.wdtk.datamodel.interfaces.TermedDocument;

public class EntityDocumentBuilderFactoryTest {
	@Test
	public void testBuildFromItem() {
		ItemDocument initialDocument = ItemDocumentBuilder.forItemId(ItemIdValue.NULL).build();
		
		EntityDocumentBuilder<?, ? extends TermedDocument> builder = EntityDocumentBuilderFactory.builderForDocument(initialDocument);
		TermedDocument copy = builder.build();
		assertEquals(initialDocument, copy);
	}
	
	@Test
	public void testAddLabelOnItemDocument() {
		ItemDocument initialDocument = ItemDocumentBuilder.forItemId(ItemIdValue.NULL).build();
		TermedDocument termed = initialDocument;
		
		MonolingualTextValue label = Datamodel.makeMonolingualTextValue("My label", "en");
		TermedStatementDocumentBuilder<?,? extends TermedDocument> builder = EntityDocumentBuilderFactory.builderForDocument(termed);
		TermedDocument copy = builder.withLabel(label).build();
		
		assertTrue(ItemDocument.class.isInstance(copy));
		assertEquals(copy.getLabels().get("en"), label);
	}
	
	@Test
	public void testAddStatementOnPropertyDocument() {
		PropertyDocument initialDocument = PropertyDocumentBuilder.forPropertyIdAndDatatype(PropertyIdValue.NULL, DatatypeIdValue.DT_TIME).build();
		StatementDocument termed = initialDocument;
		
		PropertyIdValue pid = Datamodel.makeWikidataPropertyIdValue("P31");
		Statement statement = StatementBuilder.forSubjectAndProperty(PropertyIdValue.NULL, pid).withNoValue().build();
		TermedStatementDocumentBuilder<?,? extends StatementDocument> builder = EntityDocumentBuilderFactory.builderForDocument(termed);
		StatementDocument copy = builder.withStatement(statement).build();
		
		assertTrue(PropertyDocument.class.isInstance(copy));
		assertEquals(copy.findStatement("P31"), statement);
	}
}
