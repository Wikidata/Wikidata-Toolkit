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
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

public class ValueRdfConverter implements ValueVisitor<Value> {

	static final String VALUE_PREFIX_GLOBECOORDS = "VC";
	static final String VALUE_PREFIX_QUANTITY = "VQ";
	static final String VALUE_PREFIX_TIME = "VT";

	final ValueFactory factory = ValueFactoryImpl.getInstance();
	final MessageDigest md;

	public ValueRdfConverter() {
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(
					"You Java does not support MD5 hashes. You should be concerned.");
		}
	}

	@Override
	public Value visit(DatatypeIdValue value) {
		return this.factory.createURI(value.getIri());
	}

	@Override
	public Value visit(EntityIdValue value) {
		return this.factory.createURI(Vocabulary.getEntityUri(value));
	}

	@Override
	public Value visit(GlobeCoordinatesValue value) {
		md.reset();
		updateMessageDigestWithString(md, value.getGlobe());
		updateMessageDigestWithLong(md, value.getLatitude());
		updateMessageDigestWithLong(md, value.getLongitude());
		updateMessageDigestWithLong(md, value.getPrecision());

		URI valueUri = this.factory.createURI(Vocabulary.PREFIX_WIKIDATA
				+ VALUE_PREFIX_GLOBECOORDS + bytesToHex(md.digest()));

		// TODO add attributes
		return valueUri;
	}

	@Override
	public Value visit(MonolingualTextValue value) {
		// TODO this is not the correct language code
		return factory.createLiteral(value.getText(), value.getLanguageCode());
	}

	@Override
	public Value visit(QuantityValue value) {
		md.reset();
		updateMessageDigestWithInt(md, value.getNumericValue().hashCode());
		updateMessageDigestWithInt(md, value.getLowerBound().hashCode());
		updateMessageDigestWithInt(md, value.getUpperBound().hashCode());

		URI valueUri = this.factory.createURI(Vocabulary.PREFIX_WIKIDATA
				+ VALUE_PREFIX_QUANTITY + bytesToHex(md.digest()));

		// TODO add attributes
		return valueUri;
	}

	@Override
	public Value visit(StringValue value) {
		return factory.createLiteral(value.getString());
	}

	@Override
	public Value visit(TimeValue value) {
		md.reset();
		updateMessageDigestWithLong(md, value.getYear());
		md.update(value.getMonth());
		md.update(value.getDay());
		md.update(value.getHour());
		md.update(value.getMinute());
		md.update(value.getSecond());
		updateMessageDigestWithString(md, value.getPreferredCalendarModel());
		updateMessageDigestWithInt(md, value.getBeforeTolerance());
		updateMessageDigestWithInt(md, value.getAfterTolerance());
		updateMessageDigestWithInt(md, value.getTimezoneOffset());

		URI valueUri = this.factory.createURI(Vocabulary.PREFIX_WIKIDATA
				+ VALUE_PREFIX_TIME + bytesToHex(md.digest()));

		// TODO add attributes
		return valueUri;
	}

	ByteBuffer longByteBuffer = ByteBuffer.allocate(Long.SIZE);

	void updateMessageDigestWithLong(MessageDigest md, long x) {
		this.longByteBuffer.putLong(0, x);
		md.update(this.longByteBuffer);
	}

	ByteBuffer intByteBuffer = ByteBuffer.allocate(Integer.SIZE);

	void updateMessageDigestWithInt(MessageDigest md, int x) {
		this.intByteBuffer.putInt(0, x);
		md.update(this.intByteBuffer);
	}

	void updateMessageDigestWithString(MessageDigest md, String s) {
		md.update(s.getBytes(StandardCharsets.UTF_8));
	}

	final protected static char[] hexArray = "0123456789abcdef".toCharArray();

	static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

}
