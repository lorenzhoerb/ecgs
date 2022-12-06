package at.ac.tuwien.sepm.groupphase.backend.validation.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = DateBeforeOrEqualsValidator.class)
@Repeatable(DateBeforeOrEquals.List.class)
@Documented
public @interface DateBeforeOrEquals {
    String message() default "{constraints.dateBefore}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String first();

    String second();

    @Target({TYPE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        DateBeforeOrEquals[] value();
    }

}
