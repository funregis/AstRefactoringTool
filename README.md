# AstRefactoringTool
I had a special need , do e refactoring with logic behind. I found this api AST Parser https://code.google.com/p/javaparser/ , with this you can develop smart refactoring with logic.


My custom annotation before modification :


@Target({ PARAMETER, ANNOTATION_TYPE ,FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = FieldValidator.class)
@Documented
public @interface FieldValidation {

    String message() default "";
    Class<?>[] groups() default {};
    String required() default "";
    FieldTypeValidator[] check() default {};
    Class<? extends Payload>[] payload() default {};
}


after modification


@Target({ PARAMETER, ANNOTATION_TYPE ,FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = FieldValidator.class)
@Documented
public @interface FieldValidation {

    String message() default "";
    Class<?>[] groups() default {};
    boolean required() default false;
    String name();
    FieldTypeValidator[] check() default {};
    Clas<? extends Payload>[] payload() default {};
}


The logic for the refactoring , in all my class who use my annotation I needed to change the field "required" from String to Boolean . And add a String field "name" which have the original value from field "required":


Before :

@FieldValidation(required = "id")


After :

@FieldValidation(name = "id", required = true)
