package org.wikidata.wdtk.dumpfiles;

/*
 * #%L
 * Wikidata Toolkit Dump File Handling
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

abstract class TestCase {

	TestCase(String filePath, JsonConverter converter) {
		this.filePath = filePath;
		String contents;
		try {
			contents = readFile(filePath);
			this.json = new JSONObject(contents);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.converter = converter;
	}

	private String readFile(String filePath) throws FileNotFoundException {
		File file = new File(filePath);
		if (!file.exists()) {
			System.err
					.println("File " + file.getAbsolutePath() + " not found!");
			return "";
		}

		Scanner scanner = new Scanner(file);
		scanner.useDelimiter("\\A");
		String entireFileText = scanner.next();
		scanner.close();

		return entireFileText;
	}

	private final String filePath;
	protected JSONObject json;
	protected JsonConverter converter;

	abstract void convert() throws JSONException;

	@Override
	public String toString() {
		return this.filePath + "\n" + this.json.toString();
	}

	public JSONObject getJson() {
		return this.json;
	}
}
