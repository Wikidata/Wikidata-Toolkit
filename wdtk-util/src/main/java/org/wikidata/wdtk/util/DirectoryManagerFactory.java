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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Static helper class for creating {@link DirectoryManager} objects.
 *
 * @author Markus Kroetzsch
 *
 */
public class DirectoryManagerFactory {

	/**
	 * The class that will be used for accessing directories.
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
	 * @param readOnly
	 *            if false, the directory manager will attempt to create
	 *            directories when changing to a location that does not exist
	 * @return the directory manager
	 * @throws IOException
	 *             if there was an IO error constructing the directory manager
	 */
	public static DirectoryManager createDirectoryManager(Path path,
			boolean readOnly) throws IOException {
		try {
			return dmClass.getConstructor(Path.class, Boolean.class)
					.newInstance(path, readOnly);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | NoSuchMethodException
				| SecurityException e) {
			throw new RuntimeException(e.toString(), e);
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof IOException) {
				throw (IOException) e.getTargetException();
			} else {
				throw new RuntimeException(e.getTargetException().toString(),
						e.getTargetException());
			}
		}
	}

	/**
	 * Creates a new {@link DirectoryManager} for the given directory.
	 *
	 * @param directory
	 *            the directory that the directory manager points to
	 * @param readOnly
	 *            if false, the directory manager will attempt to create
	 *            directories when changing to a location that does not exist
	 * @return the directory manager
	 * @throws IOException
	 *             if there was an IO error constructing the directory manager
	 */
	public static DirectoryManager createDirectoryManager(String directory,
			boolean readOnly) throws IOException {
		return createDirectoryManager(Paths.get(directory), readOnly);
	}

}
