package org.wikidata.wdtk.clt;

/*
 * #%L
 * Wikidata Toolkit Examples
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

/**
 * This class holds a configuration of parameters for a conversion.
 *
 * @author Michael Günther
 *
 */
public class ConversionConfiguration {
	String outputFormat = "none";
	String outputDestination = "";
	String dumplocation = null;
	String rdfdump = "";
	String compressionExtension = ConversionClient.COMPRESS_NONE;
	Boolean stdout = false;
	Boolean offlineMode = false;

	public String getCompressionExtension() {
		return compressionExtension;
	}

	public void setCompressionExtension(String compressionExtention) {
		this.compressionExtension = compressionExtention;
	}

	public String getRdfdump() {
		return rdfdump;
	}

	public void setRdfdump(String rdfdump) {
		this.rdfdump = rdfdump;
	}

	public String getOutputFormat() {
		return outputFormat;
	}

	public void setOutputFormat(String outputFormat) {
		this.outputFormat = outputFormat;
	}

	public String getOutputDestination() {
		return outputDestination;
	}

	public void setOutputDestination(String outputDestination) {
		this.outputDestination = outputDestination;
	}

	public String getDumplocation() {
		return dumplocation;
	}

	public void setDumplocation(String dumplocation) {
		this.dumplocation = dumplocation;
	}

	public Boolean getStdout() {
		return stdout;
	}

	public void setStdout(Boolean stdout) {
		this.stdout = stdout;
	}

	public Boolean getOfflineMode() {
		return offlineMode;
	}

	public void setOfflineMode(Boolean offlineMode) {
		this.offlineMode = offlineMode;
	}
}
