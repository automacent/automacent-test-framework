package com.automacent.fwk.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for initializing Step class fields. This annotation can be used in
 * the Test classes for declaring and initializing required Step classes as
 * global variables.
 * 
 * <pre>
 * public class LoginTest() {
 * 	&#64;Steps 
 * 	private LoginSteps loginSteps;
 * }
 * </pre>
 * 
 * @author sighil.sivadas
 */
@Inherited
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Steps {
}