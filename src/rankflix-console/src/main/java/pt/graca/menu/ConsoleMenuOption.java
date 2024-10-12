package pt.graca.menu;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ConsoleMenuOption {
    String value();

    boolean priority() default false;
}
