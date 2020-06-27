package fpinjava.chapter3;

import fpinjava.chapter1.Tuple;

import java.util.function.Supplier;

public class Case<T> extends Tuple<Supplier<Boolean>, Supplier<Result<T>>> {
    private Case(Supplier<Boolean> condition, Supplier<Result<T>> value) {
        super(condition, value);
    }

    // static methods
    public static <T> Case<T> mcase(Supplier<Boolean> condition, Supplier<Result<T>> value) {
        return new Case<>(condition, value);
    }

    public static <T> DefaultCase<T> mcase(Supplier<Result<T>> value) {
        return new DefaultCase<>(value);
    }

    public static <T> Result<T> match(DefaultCase<T> defaultCase, Case<T>... matchers) {
        for (Case<T> mcase: matchers) {
            if(mcase._1.get()) return mcase._2.get();
        }
        return defaultCase._2.get();
    }

    // subclass(es)
    private static class DefaultCase<T> extends Case<T>{
        private DefaultCase(Supplier<Result<T>> value) {
            super(() -> true, value);
        }
    }
}