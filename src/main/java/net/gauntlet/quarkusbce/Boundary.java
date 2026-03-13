package net.gauntlet.quarkusbce;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Stereotype;
import jakarta.inject.Named;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Stereotype
@ApplicationScoped
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Named
public @interface Boundary {
}
