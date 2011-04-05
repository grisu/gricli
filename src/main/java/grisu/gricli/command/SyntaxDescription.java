package grisu.gricli.command;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
public @interface SyntaxDescription {
	String[] command();
}
