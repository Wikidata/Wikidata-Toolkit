package org.wikidata.wdtk.datamodel.jsonconverter;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

public class DatatypeConvertersTest {

	final DataObjectFactory factory = new DataObjectFactoryImpl();
	final TestObjectFactory testObjectFactory = new TestObjectFactory();

	@Test
	public void testFormatTimeISO8601() {
		TimeValue time = (TimeValue) testObjectFactory.createValueSnakTime(3,
				"P17").getValue();
		assertEquals(DatatypeConverters.formatTimeISO8601(time),
				"+00000000054-10-10T06:21:18Z");

	}

	@Test
	public void testBigDecimals() {
		BigDecimal test = new BigDecimal(3638);
		assertEquals(DatatypeConverters.formatBigDecimal(test), "+3638");
	}

}
