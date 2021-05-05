package no.fishapp.store.model.validators;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * The annotated field must have a valid id field.
 */
@Constraint(validatedBy = {HasValidIdCommodityValidator.class})
@Target({FIELD})
@Retention(RUNTIME)
@Documented
public @interface HasValidId {

    String message() default "Object by id ref cannot be null or negative";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

