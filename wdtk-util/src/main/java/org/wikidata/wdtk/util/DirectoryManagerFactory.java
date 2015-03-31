package org.wikidata.wdtk.util;

/*
 * #%L
 * Wikidata Toolkit Utilities
 * %%
 * Copyright (C) 2014 - 2015 Wikidata Toolkit Developers
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

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;

/**
 * Static helper class for creating {@link DirectoryManager} objects.
 * 
 * @author Markus Kroetzsch
 *
 */
public class DirectoryManagerFactory {

	/**
	 * The class that will be used for accessing directories. Package-private so
	 * that it can be overwritten in tests in order to mock file access.
	 */
	static Class<? extends DirectoryManager> dmClass = DirectoryManagerImpl.class;

	/**
	 * Sets the class of {@link DirectoryManager} that should be used when
	 * creating instances here. This class should provide constructors for
	 * {@link Path} and {@link String} versions of the directory.
	 *
	 * @param clazz
	 *            the class to use
	 */
	public static void setDirectoryManagerClass(
			Class<? extends DirectoryManager> clazz) {
		dmClass = clazz;
	}

	/**
	 * Creates a new {@link DirectoryManager} for the given directory path.
	 *
	 * @param path
	 *            the directory that the directory manager points to
	 * @return the directory manager
	 */
	public static DirectoryManager createDirectoryManager(Path path) {
		try {
			return dmClass.getConstructor(Path.class).newInstance(path);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e.toString(), e);
		}
	}

	/**
	 * Creates a new {@link DirectoryManager} for the given directory.
	 *
	 * @param directory
	 *            the directory that the directory manager points to
	 * @return the directory manager
	 */
	public static DirectoryManager createDirectoryManager(String directory) {
		try {
			return dmClass.getConstructor(String.class).newInstance(directory);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e.toString(), e);
		}
	}
}
