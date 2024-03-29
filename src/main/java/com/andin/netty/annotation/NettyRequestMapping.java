package com.andin.netty.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;


@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface NettyRequestMapping {
	String value() default "";
}
