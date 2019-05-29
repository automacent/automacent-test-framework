package com.automacent.fwk.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.automacent.fwk.core.BaseTest;
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
	 * Process all the {@link Pages} / {@link Steps} annotation present in th
	 * provided obj including super classes and initialize them based upon the
	 * provided annotationClass paremeter
	 * 
	 * @param obj
	 *            Instance of class in which {@link Pages} / {@link Steps}
	 *            annotations are used
	 * @param annotationClass
	 *            Type of annotation to be processed [{@link Pages} / {@link Steps}]
	 */
	public static void processAnnotation(Object obj, Class<?> annotationClass) {
		_logger.debug(String.format("Initializing @Pages for Step class %s", obj.getClass().getName()));

		Class<?> objClass = obj.getClass();
		List<Class<?>> objClassList = new ArrayList<>();
		while (objClass != null) {
			objClassList.add(objClass);
			objClass = objClass.getSuperclass();
		}
		Collections.reverse(objClassList);

		for (Class<?> Objclass : objClassList)
			for (Field field : Objclass.getClass().getDeclaredFields())
				for (Annotation annotation : field.getAnnotations())
					if (annotation.annotationType() == annotationClass)
						if (annotationClass.equals(Pages.class))
							initializePageClasses(obj, field);
						else if (annotationClass.equals(Steps.class))
							initializeStepClasses(obj, field);
	}

	/**
	 * This method initializes Page class field which are annotated with
	 * {@link Pages}
	 * 
	 * @param obj
	 *            Instance of class in which {@link Pages} annotations are used
	 * @param field
	 *            Page class field
	 */
	private static void initializePageClasses(Object obj, Field field) {
		field.setAccessible(true);
		try {
			Object newObject = PageFactory.initElements(
					BaseTest.getTestObject().getDriverManager().getActiveDriver().getWebDriver(),
					field.getType());
			field.set(obj, newObject);
		} catch (IllegalAccessException | IllegalArgumentException | SecurityException e) {
			throw new SetupFailedFatalException(String.format("Error in initializing Page class %s", field.getType().getName()),e);
		}
	}

	/**
	 * This method initializes Step class filed which are annotated with
	 * {@link Steps}
	 * 
	 * @param obj
	 *            Instance of class in which {@link Steps} annotations are used
	 * @param field
	 *            Step class field
	 */
	private static void initializeStepClasses(Object obj, Field field) {
		field.setAccessible(true);
		try {
			Object newObj = field.getType().getConstructor(WebDriver.class)
					.newInstance(BaseTest.getTestObject().getDriverManager().getActiveDriver().getWebDriver());
			processAnnotation(field.getType().cast(newObj), Pages.class);
			field.set(obj, newObj);
		} catch (NoSuchMethodException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException | SecurityException e) {
			throw new SetupFailedFatalException(
					String.format("Error in initializing Step class %s", field.getType().getName()), e);
		}
	}
}