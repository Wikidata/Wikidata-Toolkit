package org.wikidata.wdtk.benchmarks.memory;

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

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;

/**
 * This class implements a memory measurer. It needs to be initialized by
 * executing the {@link #premain(String, Instrumentation)} method. It is a
 * singleton.
 * 
 * @see Instrumentation
 * 
 * @author Julian Mendez
 * 
 */
public class MemoryMeasurer {

	/** Only instance of this class. */
	static MemoryMeasurer instance = null;

	/** Only instance of the instrumentation. */
	static Instrumentation instrumentation;

	/**
	 * Returns the memory measurer.
	 * 
	 * @return the memory measurer
	 */
	public static MemoryMeasurer getInstance() {
		if (instrumentation == null) {
			throw new IllegalStateException(
					"Instrumentation not initialized. The premain method must be called before the main method.");
		}
		if (instance == null) {
			instance = new MemoryMeasurer();
		}
		return instance;
	}

	/**
	 * Stores the reference to the instrumentation that is used to measure
	 * objects.
	 * 
	 * @see Instrumentation
	 * 
	 * @param args
	 *            argument
	 * @param instr
	 *            instrumentation
	 */
	public static void premain(String args, Instrumentation instr) {
		instrumentation = instr;
	}

	/**
	 * Constructs a new memory measurer.
	 */
	public MemoryMeasurer() {
	}

	/**
	 * Returns a set containing the specified class, all its interfaces, and all
	 * its super classes.
	 * 
	 * @param cls
	 *            a class
	 * @return a set containing the specified class, all its interfaces, and all
	 *         its super classes
	 */
	private Set<Class<?>> getClasses(Class<?> cls) {
		Set<Class<?>> ret = new HashSet<Class<?>>();
		ret.add(cls);
		ret.addAll(Arrays.asList(cls.getInterfaces()));
		Class<?> superClass = cls.getSuperclass();
		while (superClass != null && !ret.contains(superClass)) {
			ret.add(superClass);
			superClass = superClass.getSuperclass();
		}
		return ret;
	}

	/**
	 * Returns an approximation of the amount of storage used by the specified
	 * object, including recursively all the objects that compose it. This
	 * method is equivalent to {@link #getDeepSizeWithClass(Object, Class)},
	 * with Class=<t>Object.class</t>.
	 * 
	 * @param obj
	 *            object to measure
	 * @return an approximation of the amount of storage used by the specified
	 *         object, including recursively all the objects that compose it
	 */
	public long getDeepSize(Object obj) {
		IdentityHashMap<Object, Boolean> visited = new IdentityHashMap<Object, Boolean>();
		return getDeepSizeRecursive(obj, visited, Object.class);
	}

	/**
	 * Returns an approximation of the amount of storage used by the specified
	 * object, including recursively all the objects that compose it that belong
	 * to a specified class.
	 * 
	 * @param obj
	 *            object
	 * @param visited
	 *            all objects already measured
	 * @param referenceClass
	 *            class used as reference
	 * @return an approximation of the amount of storage used by the specified
	 *         object, including recursively all the objects that compose it
	 *         that belong to a specified class
	 */
	private long getDeepSizeRecursive(Object obj,
			IdentityHashMap<Object, Boolean> visited, Class<?> referenceClass) {
		if (obj == null || visited.containsKey(obj)) {
			return 0;
		}
		visited.put(obj, true);
		long size = hasClass(obj, referenceClass) ? getSize(obj) : 0;
		if (obj instanceof Object[]) {
			size += measureArray((Object[]) obj, visited, referenceClass);
		} else {
			size += measureFields(obj, visited, referenceClass);
		}
		return size;
	}

	/**
	 * Returns an approximation of the amount of storage used by the specified
	 * object, including recursively the objects that compose it that belong to
	 * a specified class.
	 * 
	 * @param obj
	 *            object to measure
	 * @return an approximation of the amount of storage used by the specified
	 *         object, including recursively the objects that compose it that
	 *         belong to a specified class
	 */
	public long getDeepSizeWithClass(Object obj, Class<?> referenceClass) {
		IdentityHashMap<Object, Boolean> visited = new IdentityHashMap<Object, Boolean>();
		return getDeepSizeRecursive(obj, visited, referenceClass);
	}

	/**
	 * Returns an approximation of the amount of storage used by the specified
	 * object. This method does not count the storage used by the components of
	 * the specified object.
	 * 
	 * @see Instrumentation#getObjectSize
	 * 
	 * @param obj
	 *            object to measure
	 * @return an approximation of the amount of storage used by the specified
	 *         object
	 */
	public long getSize(Object obj) {
		return (obj == null || hasSharedReference(obj)) ? 0 : instrumentation
				.getObjectSize(obj);
	}

	/**
	 * Tells whether a specified object belongs to a specified class.
	 * 
	 * @param obj
	 *            object
	 * @param cls
	 *            class
	 * @return <code>true</code> if and only if a specified object belongs to a
	 *         specified class
	 */
	private boolean hasClass(Object obj, Class<?> cls) {
		return getClasses(obj.getClass()).contains(cls);
	}

	/**
	 * Tells whether a specified object uses a shared reference. This method is
	 * used to detect some objects stored using the flyweight design pattern.
	 * Interned strings are not detected, though. For more information about
	 * interned strings, see {@link String#intern()}.
	 * 
	 * @param obj
	 *            object
	 * @return <code>true</code> if and only if a shared reference has been
	 *         detected
	 */
	private boolean hasSharedReference(Object obj) {
		if (obj instanceof Byte) {
			return (obj == Byte.valueOf((Byte) obj));
		} else if (obj instanceof Short) {
			return (obj == Short.valueOf((Short) obj));
		} else if (obj instanceof Integer) {
			return (obj == Integer.valueOf((Integer) obj));
		} else if (obj instanceof Long) {
			return (obj == Long.valueOf((Long) obj));
		} else if (obj instanceof Float) {
			return (obj == Float.valueOf((Float) obj));
		} else if (obj instanceof Double) {
			return (obj == Double.valueOf((Double) obj));
		} else if (obj instanceof Boolean) {
			return (obj == Boolean.valueOf((Boolean) obj));
		} else if (obj instanceof Character) {
			return (obj == Character.valueOf((Character) obj));
		} else if (obj instanceof Enum) {
			return true;
		}
		return false;
	}

	/**
	 * Returns an approximation of the amount of storage used by the specified
	 * array of objects, including recursively all the objects that compose it
	 * that belong to a specified class.
	 * 
	 * @param array
	 *            array of objects
	 * @param visited
	 *            objects already measured
	 * @param referenceClass
	 *            class used as a reference
	 * @return an approximation of the amount of storage used by the specified
	 *         array of objects, including recursively all the objects that
	 *         compose it that belong to a specified class
	 */
	private long measureArray(Object[] array,
			IdentityHashMap<Object, Boolean> visited, Class<?> referenceClass) {
		long size = 0;
		for (Object obj : array) {
			size += getDeepSizeRecursive(obj, visited, referenceClass);
		}
		return size;
	}

	/**
	 * Returns an approximation of the amount of storage used by all the objects
	 * that compose a specified object.
	 * 
	 * @param obj
	 *            object
	 * @param visited
	 *            objects already measured
	 * @param referenceClass
	 *            class used as a reference
	 * @return an approximation of the amount of storage used by all the objects
	 *         that compose a specified object.
	 */
	private long measureFields(Object obj,
			IdentityHashMap<Object, Boolean> visited, Class<?> referenceClass) {
		long size = 0;
		Field[] fields = obj.getClass().getDeclaredFields();
		for (Field f : fields) {
			if (!f.getType().isPrimitive()
					&& !Modifier.isStatic(f.getModifiers())) {
				f.setAccessible(true);
				Object content = null;
				try {
					content = f.get(obj);
				} catch (IllegalAccessException e) {
					throw new IllegalStateException(e);
				}
				size += getDeepSizeRecursive(content, visited, referenceClass);
			}
		}
		return size;
	}

}
