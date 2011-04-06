package grisu.gricli.command;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import jline.Completor;

@Retention(RetentionPolicy.RUNTIME)
public @interface AutoComplete {
	Class<? extends Completor>[] completors();
}
