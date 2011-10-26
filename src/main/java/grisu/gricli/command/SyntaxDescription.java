package grisu.gricli.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SyntaxDescription {
	String[] arguments() default {};

	String[] command();

	String help() default "";
}
