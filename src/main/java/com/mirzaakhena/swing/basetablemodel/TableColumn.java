package com.mirzaakhena.swing.basetablemodel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface TableColumn {

	String header() default "";

	int order() default Integer.MAX_VALUE;

}
