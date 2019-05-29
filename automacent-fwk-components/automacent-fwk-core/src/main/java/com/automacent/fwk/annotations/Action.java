package com.automacent.fwk.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.automacent.fwk.core.PageObject;

/**
 * This annotation marks a method as Action method. Action methods are methods
 * defined in Page/View classes (classes which inherit {@link PageObject })
 * 
 * @author sighil.sivadas
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Action {

}
