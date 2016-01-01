package com.mirzaakhena.swing.basetablemodel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Create a column for your header by put this annotation in field class or
 * method class
 * 
 * @author mirzaakhena
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface TableColumn {

	/**
	 * Header of table. Default header is a field name
	 * 
	 * @return header name
	 */
	String header() default "";

	/**
	 * Order of table header. Default is 2147483647
	 * 
	 * @return number of order
	 */
	int order() default Integer.MAX_VALUE;

}
