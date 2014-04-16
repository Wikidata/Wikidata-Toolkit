package org.wikidata.wdtk.datamodel.json;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;

import org.junit.Before;
import org.junit.Test;

public class JsonSerializerTest {

	final String START_DOCUMENT = "{\"entities\": {";
	final String END_DOCUMENT = "}}";

	ByteArrayOutputStream out;
	JsonProcessor processor;
	JsonSerializer serializer;

	@Before
	public void setUp() throws Exception {
		out = new ByteArrayOutputStream();
		processor = new JsonProcessor(out);
		serializer = new JsonSerializer(processor);
	}

	@Test
	public void testStartSerialisation() {
		serializer.startSerialisation();
		assertEquals(out.toString(), START_DOCUMENT);
	}

	@Test
	public void testFinishSerialisation() {
		serializer.finishSerialisation();
		assertEquals(out.toString(), END_DOCUMENT);
	}

}
