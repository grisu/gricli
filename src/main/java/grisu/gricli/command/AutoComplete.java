package grisu.gricli.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jline.Completor;

@Retention(RetentionPolicy.RUNTIME)
public @interface AutoComplete {
	Class<? extends Completor>[] completors();
}
