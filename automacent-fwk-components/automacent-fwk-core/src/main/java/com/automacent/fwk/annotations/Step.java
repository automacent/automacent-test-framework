package com.automacent.fwk.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.automacent.fwk.core.WebTestSteps;
import com.automacent.fwk.core.UnitTestSteps;
import com.automacent.fwk.core.WebServiceTestSteps;

/**
 * This annotation marks a method as Step method. Step methods are methods
 * defined in Step classes (classes which inherit {@link WebTestSteps},
 * {@link UnitTestSteps} or {@link WebServiceTestSteps})
 * 
 * @author sighil.sivadas
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Step {

}
