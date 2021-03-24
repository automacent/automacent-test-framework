package com.automacent.fwk.execution;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import com.automacent.fwk.reporting.Logger;
import com.automacent.fwk.utils.AspectJUtils;
import com.automacent.fwk.utils.LoggingUtils;

//@Aspect
public class AssertComplier {

//	@Around("execution(* org.testng.Assert.*(..))")
	public Object toLowerCase(ProceedingJoinPoint joinPoint) throws Throwable {
		String methodName = MethodSignature.class.cast(joinPoint.getSignature()).getMethod().getName();
		Object result = joinPoint.proceed();
		Logger.getLogger(MethodSignature.class.cast(joinPoint.getSignature()).getDeclaringType())
				.infoHeading(
						String.format("    Validation Successful: %s%s",
								LoggingUtils.addGrammer(LoggingUtils.addSpaceToCamelCaseString(methodName)),
								AspectJUtils.getArguments(joinPoint)));
		return result;
	}
}