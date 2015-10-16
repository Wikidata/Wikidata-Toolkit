package org.wikidata.wdtk.datamodel.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;

public class StatementDocumentTest {
	static ItemIdValue Q1 = Datamodel.makeWikidataItemIdValue("Q1");
	static ItemIdValue Q2 = Datamodel.makeWikidataItemIdValue("Q2");
	static PropertyIdValue P1 = Datamodel.makeWikidataPropertyIdValue("P1");
	static PropertyIdValue P2 = Datamodel.makeWikidataPropertyIdValue("P2");
	static PropertyIdValue P3 = Datamodel.makeWikidataPropertyIdValue("P3");

	@Test
	public void testFindStatement() {
		Statement s1 = StatementBuilder.forSubjectAndProperty(Q1, P1)
				.withValue(Q1).build();
		Statement s2 = StatementBuilder.forSubjectAndProperty(Q1, P1)
				.withValue(Q2).build();
		Statement s3 = StatementBuilder.forSubjectAndProperty(Q1, P2)
				.withValue(Q1).build();
		ItemDocument id = ItemDocumentBuilder.forItemId(Q1).withStatement(s1)
				.withStatement(s2).withStatement(s3).build();

		assertTrue(id.hasStatement(P1));
		assertTrue(id.hasStatement("P1"));
		assertEquals(null, id.findStatement(P1));
		assertEquals(null, id.findStatement("P1"));
		assertTrue(id.hasStatement(P2));
		assertTrue(id.hasStatement("P2"));
		assertEquals(s3, id.findStatement(P2));
		assertEquals(s3, id.findStatement("P2"));
		assertFalse(id.hasStatement(P3));
		assertFalse(id.hasStatement("P3"));
		assertEquals(null, id.findStatement(P3));
		assertEquals(null, id.findStatement("P3"));
	}
}
