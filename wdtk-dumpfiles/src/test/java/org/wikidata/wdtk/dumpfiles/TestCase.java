package org.wikidata.wdtk.dumpfiles;

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
		if(!file.exists()){
			System.err.println("File " + file.getAbsolutePath() + " not found!");
			return "";
		}
		
		Scanner scanner = new Scanner(file);
		scanner.useDelimiter("\\A");
		String entireFileText = scanner.next();
		scanner.close();
		
		return entireFileText;
	}

	private String filePath;
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
