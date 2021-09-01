package com.automacent.fwk.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.automacent.fwk.enums.RepeatMode;

/**
 * Aannotation to be used on methods in Test classes to implement looping logic
 * (iterating over the same test). Based on the parameter in testng XML file
 * <i>&lt;parameter name="repeat" value=""&gt;</i>, it can loop the enclosed
 * test method with the options specified in {@link RepeatMode}
 * 
 * @author sighil.sivadas
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Repeat {

}
