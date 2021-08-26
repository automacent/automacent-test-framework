package com.automacent.fwk.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.automacent.fwk.exceptions.SetupFailedFatalException;
import com.automacent.fwk.reporting.Logger;

/**
 * This class is used for initializing View/Page class fields annotated with
 * {@link Pages} and Step class fields annotated with {@link Steps}.
 * 
 * @author sighil.sivadas
 */
public class StepsAndPagesProcessor {

	private static final Logger _logger = Logger.getLogger(StepsAndPagesProcessor.class);

	private StepsAndPagesProcessor() {
	}

	/**
	 * Process all the {@link Pages} / {@link Steps} annotation present in the
	 * provided obj including super classes and initialize them. This method will
	 * recursievly process all the annotations
	 * 
	 * @param obj
	 *            Instance of class in which {@link Pages} / {@link Steps}
	 *            annotations are used
	 */
	public static void processAnnotation(Object obj) {
		_logger.debug(
				String.format("Initializing %s", obj.getClass().getName()));

		Class<?> objClass = obj.getClass();
		List<Class<?>> objClassList = new ArrayList<>();
		while (objClass != null) {
			objClassList.add(objClass);
			objClass = objClass.getSuperclass();
		}
		Collections.reverse(objClassList);

		for (Class<?> clazz : objClassList)
			for (Field field : clazz.getDeclaredFields())
				for (Annotation annotation : field.getAnnotations())
					if (annotation.annotationType().equals(Steps.class)
							|| annotation.annotationType().equals(Pages.class)) {
						field.setAccessible(true);
						try {
							Object newObject = field.getType().getConstructor().newInstance();
							processAnnotation(field.getType().cast(newObject));
							field.set(obj, newObject);
						} catch (NoSuchMethodException | InstantiationException | IllegalAccessException
								| IllegalArgumentException | InvocationTargetException | SecurityException e) {
							throw new SetupFailedFatalException(
									String.format("Error in initializing Page class %s", field.getType().getName()), e);
						}
					}

	}
}