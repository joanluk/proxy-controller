package org.emaginalabs.beanproxy.core.beans.proxy;

import org.emaginalabs.beanproxy.core.web.bind.annotation.ArgumentIndex;
import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * 
 * @author jose
 *
 */
public abstract class AbstractArgumentsWrapper implements ArgumentsWrapper {

	private static final ConcurrentMap<Class<?>, List<Field>> ARGS_FIELDS_CACHE = new ConcurrentHashMap<Class<?>, List<Field>>();

	/**
	 * 
	 * @return
	 */
	public final Object[] toArray() {

		List<Field> fields = getFields(this.getClass());
		Object[] args = new Object[fields.size()];
		
		try {
			int idx = 0;
			for (Field field : fields) {
				args[idx++] = PropertyUtils.getSimpleProperty(this, field.getName());
			}
		} catch (Throwable th) {
			throw new RuntimeException(th.getMessage(), th);
		}

		return args;
	}

	private List<Field> getFields(Class<?> clazz) {

		if (!ARGS_FIELDS_CACHE.containsKey(clazz)) {

			List<Field> foundFields = new ArrayList<Field>();
			Class<?> searchType = clazz;
			while (!AbstractArgumentsWrapper.class.equals(searchType)) {
				Field[] fields = searchType.getDeclaredFields();

				for (Field field : fields) {
					if (field.isAnnotationPresent(ArgumentIndex.class)) {
						foundFields.add(field);
					}
				}

				searchType = searchType.getSuperclass();
			}

			Collections.sort(foundFields, MethodArgumentComparator.INSTANCE);
			
			ARGS_FIELDS_CACHE.putIfAbsent(clazz, foundFields);
		}

		return ARGS_FIELDS_CACHE.get(clazz);
	}


	private static class MethodArgumentComparator implements Comparator<Field> {

		private static final Comparator<Field> INSTANCE = new MethodArgumentComparator(); 

		public int compare(Field field1, Field field2) {

			ArgumentIndex ann1 = field1.getAnnotation(ArgumentIndex.class);
			ArgumentIndex ann2 = field2.getAnnotation(ArgumentIndex.class);

			return ann1.value() - ann2.value();
		}

	}

}
