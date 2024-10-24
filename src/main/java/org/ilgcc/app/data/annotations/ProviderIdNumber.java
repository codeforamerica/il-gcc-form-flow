package org.ilgcc.app.data.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.ilgcc.app.data.validators.ProviderIdNumberValidator;

/**
 * Custom annotation for validating Provider Id Numbers in form submissions. This annotation ensures that the annotated field
 * contains a valid Provider Id Number, if the database has any data, otherwise it checks the length. It uses
 * {@link ProviderIdNumberValidator} for the validation logic.
 */
@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = ProviderIdNumberValidator.class)
@Documented
public @interface ProviderIdNumber {

    /**
     * Default message that will be used when the provider id number validation fails.
     *
     * @return The default error message.
     */
    String message() default "Please make sure you enter a valid provider id number";

    /**
     * Optional groups for categorizing validation constraints.
     *
     * @return An array of group classes.
     */
    Class<?>[] groups() default {};

    /**
     * Can be used by clients of the Bean Validation API to assign custom payload objects to a constraint.
     *
     * @return An array of payload classes.
     */
    Class<? extends Payload>[] payload() default {};
}


