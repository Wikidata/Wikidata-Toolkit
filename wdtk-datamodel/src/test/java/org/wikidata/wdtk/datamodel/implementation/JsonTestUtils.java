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
package org.wikidata.wdtk.datamodel.implementation;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.TreeMap;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

class JsonTestUtils {

	private static final ObjectMapper mapper = new ObjectMapper();

	static {
		/*
		 * Support for Optional properties.
		 */
		mapper.registerModule(new Jdk8Module());
		/*
		 * Sort fields by name to produce mostly canonical JSON.
		 * https://cowtowncoder.medium.com/
		 * jackson-tips-sorting-json-using-jsonnode-ce4476e37aee
		 */
		mapper.setNodeFactory(new JsonNodeFactory() {

			private static final long serialVersionUID = 1L;

			@Override
			public ObjectNode objectNode() {
				return new ObjectNode(this, new TreeMap<String, JsonNode>());
			}

		});
	}

	static String toJson(Object value) {
		try {
			String json = mapper.writeValueAsString(value);
			/*
			 * Canonical form.
			 */
			JsonNode tree = mapper.readTree(json);
			return mapper.writeValueAsString(tree);
		} catch (IOException ex) {
			fail("JSON serialization failed.");
			return null;
		}
	}

	private static class JsonMatcher<T> extends BaseMatcher<T> {

		private final String expected;

		JsonMatcher(String expected) {
			this.expected = expected.replace('\'', '"');
		}

		@Override
		public boolean matches(Object actual) {
			return toJson(actual).equals(expected);
		}

		@Override
		public void describeTo(Description description) {
			description.appendText(expected);
		}

		@Override
		public void describeMismatch(Object item, Description description) {
			description.appendText("was ").appendText(toJson(item));
		}

	}

	static <T> Matcher<T> producesJson(String json) {
		return new JsonMatcher<>(json);
	}

}
