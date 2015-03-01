package org.wikidata.wdtk.util;

/*
 * #%L
 * Wikidata Toolkit utilities
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

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A wrapper around an {@link InputStream} that tracks its progress using a
 * {@link Logger}.
 *
 */
public class ProgressLoggingInputStream extends InputStream {

	static final Logger logger = LoggerFactory
			.getLogger(ProgressLoggingInputStream.class);

	private final InputStream inputStream;
	private final String prefix;
	private final String postfix;
	private final long size;

	private long percentLogged = 0;
	private long progress = 0;

	/**
	 * @param inputStream
	 *            the {@link InputStream} to be tracked
	 * @param prefix
	 *            the portion of each log message preceding the progress
	 * @param postfix
	 *            the portion of each log message proceeding the progress
	 * @param size
	 *            the total size of the wrapped {@link InputStream}
	 */
	public ProgressLoggingInputStream(InputStream inputStream, String prefix,
			String postfix, long size) throws IOException {
		this.inputStream = inputStream;
		this.prefix = prefix;
		this.postfix = postfix;
		this.size = size;
	}

	@Override
	public int read() throws IOException {
		int bite = inputStream.read();

		progress = progress + 1;
		if (size > 0) {
			long percent = progress * 100 / size;
			if (percent > percentLogged) {
				logger.info(prefix + percent + postfix);
				percentLogged = percent;
			}
		}

		return bite;
	}

}
