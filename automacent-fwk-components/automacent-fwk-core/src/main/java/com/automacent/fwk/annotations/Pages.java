package com.automacent.fwk.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for initializing Page class fields. This annotation can be used in
 * the Step classes for declaring and initializing required Page/View classes as
 * global variables.
 * 
 * <pre>
 * public class LoginSteps() {
 * 	&#64;Pages 
 * 	private LoginPage onLoginPage;
 * 
 * 	&#64;Pages 
 * 	private HomePage onHomePage;
 * }
 * </pre>
 * 
 * @author sighil.sivadas
 */
@Inherited
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Pages {
}