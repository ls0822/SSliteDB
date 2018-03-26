package org.sslite.plugin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.sslite.plugin.db.model.DataType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
	String name() default "";

	int length() default 100;

//	DataType type() default DataType.UNKOWN;
}
