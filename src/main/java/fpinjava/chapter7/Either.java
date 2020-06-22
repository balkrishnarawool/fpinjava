package fpinjava.chapter7;

import fpinjava.chapter2.Function;

import java.util.function.Supplier;

public abstract class Either<T, U> {
    // A thing to note about Either class:
    // The function map(), maps Right and passes-through Left.
    // The function orElse(), maps Left (to given value) and passes through Right.
    // Similar is true for Option and Result classes.

    public abstract <V> Either<T, V> map(Function<U, V> f); // This is more like mapRight() because it maps only Right
    public abstract <V> Either<T, V> flatMap(Function<U, Either<T, V>> f); // This is more like flatMapRight()
    // getOrElse() returns value (which is U) when "this" Either is Right.
    // Otherwise it returns given defaultValue (which is  U).
    public abstract U getOrElse(Supplier<U> defaultValue); // This is more like getRightOrElse()

    static class Left<T, U> extends Either<T, U> {
        private T value;

        private Left(T value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.format("Left(%s)", value);
        }

        @Override
        public <V> Either<T, V> map(Function<U, V> f) {
            return new Left<>(value);
        }

        @Override
        public <V> Either<T, V> flatMap(Function<U, Either<T, V>> f) {
            return new Left<>(value);
        }

        @Override
        public U getOrElse(Supplier<U> defaultValue) {
            return defaultValue.get();
        }
    }

    static class Right<T, U> extends Either<T, U> {
        private U value;

        private Right(U value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.format("Right(%s)", value);
        }

        @Override
        public <V> Either<T, V> map(Function<U, V> f) {
            return new Right<>(f.apply(value));
        }

        @Override
        public <V> Either<T, V> flatMap(Function<U, Either<T, V>> f) {
            return f.apply(value);
        }

        @Override
        public U getOrElse(Supplier<U> defaultValue) {
            return value;
        }
    }

    // Exercise 7.3
    // orElse() returns "this" if it is Right.
    // Otherwise it returns defaultValue.get() (which is Either).
    public Either<T, U> orElse(Supplier<Either<T, U>> defaultValue) { // this is more like rightOrElse()
        return map(u -> this).getOrElse(defaultValue);
    }

    public static <T, U> Either<T, U> left(T value) {
        return new Left<>(value);
    }

    public static <T, U> Either<T, U> right(U value) {
        return new Right<>(value);
    }

}