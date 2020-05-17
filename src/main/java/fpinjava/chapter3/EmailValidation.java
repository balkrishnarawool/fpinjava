package fpinjava.chapter3;

import fpinjava.chapter1.Tuple;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import static fpinjava.chapter3.Case.*;

public class EmailValidation {

    static Pattern emailPattern =
            Pattern.compile("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$");

// Implementation of emailChecker before using Case class
//    static Function<String, Result<String>> emailChecker =
//        s ->
//            s == null
//                ? new Result.Failure("email must not be null")
//                : s.length() == 0
//                    ? new Result.Failure("email must not be empty")
//                    : emailPattern.matcher(s).matches()
//                        ? new Result.Success(s)
//                        : new Result.Failure("email " + s + " is invalid.");

    static Function<String, Result<String>> emailChecker =
        s -> match(
                mcase(() -> new Result.Success(s)),
                mcase(() -> s == null, () -> new Result.Failure("email must not be null")),
                mcase(() -> s.length() == 0, () -> new Result.Failure("email must not be empty")),
                mcase(() -> !emailPattern.matcher(s).matches(), () -> new Result.Failure("email " + s + " is invalid."))
        );

//  My attempt for Exercise 3.1
//    public static void main(String... args) {
//        Function<String, Executable> onSuccess = s -> () -> sendVerificationMail(s);
//        Function<Result.Failure, Executable> onFailure = failure -> () -> logError(failure.getMessage());
//
//        validate("this.is@my.email", onSuccess, onFailure).exec();
//        validate(null, onSuccess, onFailure).exec();
//        validate("", onSuccess, onFailure).exec();
//        validate("john.doe@acme.com", onSuccess, onFailure).exec();
//    }
//    static Executable validate(String s, Function<String, Executable> onSuccess, Function<Result.Failure, Executable> onFailure) {
//        Result result = emailChecker.apply(s);
//        return result instanceof Result.Success
//                ? onSuccess.apply(s)
//                : onFailure.apply((Result.Failure)result);
//    }
//    interface Executable {
//        void exec();
//    }


//    Solution
//    public static void main(String[] args) {
//        Effect<String> success = s -> sendVerificationMail(s);
//        Effect<String> failure = error -> logError(error);
//
//        emailChecker.apply("this.is@my.email").bind(success, failure);
//        emailChecker.apply(null).bind(success, failure);
//        emailChecker.apply("").bind(success, failure);
//        emailChecker.apply("john.doe@acme.com").bind(success, failure);
//    }

    private static void logError(String s) {
        System.err.println("Error message logged: " + s);
    }

    private static void sendVerificationMail(String s) {
        System.out.println("Mail sent to " + s);
    }

//    Question: In functional programs, we create a template of the logic of the program and then apply it to the input at the end.
//    Here we created the template 4 times (for 4 different inputs) and then applied it to the input.
//    Could we not create one template and apply it to those 4 inputs?
//    My attempt at addressing this:
    public static void main(String[] args) {
        Effect<String> success = s -> sendVerificationMail(s);
        Effect<String> failure = error -> logError(error);

//        Function<Effect<String>, Function<Effect<String>, Consumer<String>>> emailValidator = successEffect -> failureEffect -> s -> emailChecker.apply(s).bind(successEffect, failureEffect);
        Consumer<String> emailValidator = s -> emailChecker.apply(s).bind(success, failure);

//        emailValidator.accept("this.is@my.email");
//        emailValidator.accept(null);
//        emailValidator.accept("");
//        emailValidator.accept("john.doe@acme.com");

//        Another way of doing the same thing. Here one improvement is you don't bind the effects again and again.
//        You bind them once and use partially-applied-function that "remembers" them.
        Function<Tuple<Effect<String>, Effect<String>>, Consumer<String>> emailValidator2 = t -> s -> emailChecker.apply(s).bind(t._1, t._2);
        Tuple<Effect<String>, Effect<String>> effects = new Tuple<>(success, failure);// Shared data, but we only read it so it's fine.
        Consumer<String> emailValidatorWithEffectsBound = emailValidator2.apply(effects);
        
        emailValidatorWithEffectsBound.accept("this.is@my.email");
        emailValidatorWithEffectsBound.accept(null);
        emailValidatorWithEffectsBound.accept("");
        emailValidatorWithEffectsBound.accept("john.doe@acme.com");

    }

}