package fpinjava.chapter7;

import fpinjava.chapter2.Function;
import fpinjava.chapter3.Effect;

import java.util.function.Supplier;

//          Result
//           /\
//     _____ |_____
//    |           |
//  Empty      Success
//   /\
//   |
// Failure
public abstract class Result<T> {

    public abstract T getOrElse(final T defaultValue); // getSuccessOrElse()
    public abstract T getOrElse(final Supplier<T> defaultValue); // getSuccessOrElse()
    public abstract <U> Result<U> map(Function<T, U> f); // mapSuccess()
    public abstract <U> Result<U> flatMap(Function<T, Result<U>> f); // flatMapSuccess()
    public abstract Result<T> mapFailure(String s);
    public abstract Result<T> mapFailure(String s, Exception e);
    public abstract Result<T> mapFailure(Exception e);
    public abstract void forEach(Effect<T> ef); // forEachSuccess()
    public abstract void forEachOrThrow(Effect<T> ef); // forEachSuccessOrThrow(). Note: throw is only possible for Failure (it has Exception).
    public abstract Result<RuntimeException> forEachOrException(Effect<T> ef); // forEachSuccessOrResultException(). Converts Failure into Success<RuntimeException>. Empty otherwise.

    @SuppressWarnings("rawtypes")
    private static Result empty = new Empty();

    static class Empty<T> extends Result<T> {
        @Override
        public T getOrElse(T defaultValue) {
            return defaultValue;
        }

        @Override
        public T getOrElse(Supplier<T> defaultValue) {
            return defaultValue.get();
        }

        @Override
        public <U> Result<U> map(Function<T, U> f) {
            return empty();
        }

        @Override
        public <U> Result<U> flatMap(Function<T, Result<U>> f) {
            return empty();
        }

        @Override
        public Result<T> mapFailure(String s) {
            return empty();
        }

        @Override
        public Result<T> mapFailure(String s, Exception e) {
            return empty();
        }

        @Override
        public Result<T> mapFailure(Exception e) {
            return empty();
        }

        @Override
        public void forEach(Effect<T> ef) {
        }

        @Override
        public void forEachOrThrow(Effect<T> ef) {
        }

        @Override
        public Result<RuntimeException> forEachOrException(Effect<T> ef) {
            return empty();
        }

        @Override
        public String toString() {
            return "Empty()";
        }
    }

    static class Failure<T> extends Empty<T> {
        private RuntimeException e;

        private Failure(String message) {
            e = new RuntimeException(message);
        }

        private Failure(Exception e) {
            this.e = new RuntimeException(e.getMessage());
        }

        private Failure(RuntimeException e) {
            this.e = e;
        }

        @Override
        public <U> Result<U> map(Function<T, U> f) {
            return failure(e);
        }

        @Override
        public <U> Result<U> flatMap(Function<T, Result<U>> f) {
            return failure(e);
        }

        @Override
        public Result<T> mapFailure(String s) {
            return failure(new IllegalStateException(s, e));
        }

        @Override
        public Result<T> mapFailure(String s, Exception e) {
            return failure(new IllegalStateException(s, e));
        }

        @Override
        public Result<T> mapFailure(Exception e) {
            return failure(e);
        }

        @Override
        public void forEachOrThrow(Effect<T> ef) {
            throw e;
        }

        @Override
        public Result<RuntimeException> forEachOrException(Effect<T> ef) {
            return success(e);
        }

        @Override
        public String toString() {
            return String.format("Failure(%s)", e.getMessage());
        }
    }

    static class Success<T> extends Result<T> {
        private T value;

        private Success(T value) {
            this.value = value;
        }

        @Override
        public T getOrElse(T defaultValue) {
            return value;
        }

        @Override
        public T getOrElse(Supplier<T> defaultValue) {
            return value;
        }

        @Override
        public <U> Result<U> map(Function<T, U> f) {
            try {                                       // Result class is the only place where we should throw Exceptions
                return new Success<>(f.apply(value));   // and nowhere else in our application.
            } catch (Exception e) {                     // Result class is the only place where we should catch Exceptions
                return failure(e);                      // and nowhere else in our application.
            }
        }

        @Override
        public <U> Result<U> flatMap(Function<T, Result<U>> f) {
            try {                       // Result class is the only place where we should throw Exceptions
                return f.apply(value);  // and nowhere else in our application.
            } catch (Exception e) {     // Result class is the only place where we should catch Exceptions
                return failure(e);      // and nowhere else in our application.
            }
        }

        @Override
        public Result<T> mapFailure(String s) {
            return this;
        }

        @Override
        public Result<T> mapFailure(String s, Exception e) {
            return this;
        }

        @Override
        public Result<T> mapFailure(Exception e) {
            return this;
        }

        @Override
        public void forEach(Effect<T> ef) {
            ef.apply(value);
        }

        @Override
        public void forEachOrThrow(Effect<T> ef) {
            ef.apply(value);
        }

        @Override
        public Result<RuntimeException> forEachOrException(Effect<T> ef) {
            ef.apply(value);
            return empty();
        }

        @Override
        public String toString() {
            return String.format("Success(%s)", value.toString());
        }
    }

    public static <T> Result<T> failure(String message) {
        return new Failure<>(message);
    }

    public static <T> Result<T> failure(Exception e) {
        return new Failure<>(e);
    }

    public static <T> Result<T> failure(RuntimeException e) {
        return new Failure<>(e);
    }

    public static <T> Result<T> success(T t) {
        return new Success<>(t);
    }

    @SuppressWarnings("unchecked")
    public static <T> Result<T> empty() {
        return empty;
    }

    public Result<T> orElse(Supplier<Result<T>> defaultValue) { // successOrElse()
        return map(t -> this).getOrElse(defaultValue);
    }

    // Exercise 7.5
    /**
     * If "this" is Empty, return Empty
     * If "this" is Failure, return Failure
     * If "this" is Success and match, return Success
     * If "this" is Success and non-match, return Failure
     */
    public Result<T> filter(Function<T, Boolean> f) {
        return flatMap(t -> f.apply(t) ? this : empty());
    }
    public Result<T> filter(Function<T, Boolean> f, String message) {
        return flatMap(t -> f.apply(t) ? this : failure(message));
    }

    // Exercise 7.6
    /** Returns true if "this" is Success and condition matches, false otherwise */
    public boolean exists(Function<T, Boolean> f) {
        //return map(t -> f.apply(t) ? true : false).getOrElse(false);
        //return map(t -> f.apply(t)).getOrElse(false);
        //return map(f::apply).getOrElse(false);
        return map(f).getOrElse(false);
    }

    // Exercise 7.8
    /** Failure if null, Success otherwise */
    public static <T> Result<T> of(T value) {
        return value == null ? failure("value is null") : success(value);
    }
    public static <T> Result<T> of(T value, String message) {
        return value == null ? failure(message) : success(value);
    }
    /** Success if predicate is true for value, Failure otherwise */
    public static <T> Result<T> of(Function<T, Boolean> predicate, T value) {
        try {
            return predicate.apply(value) ? success(value) : failure(String.format("Predicate %s is false for value %s", predicate, value));
        } catch (Exception e) {
            return failure(String.format("Exception while applying Predicate %s to value %s", predicate, value));
        }
    }
    public static <T> Result<T> of(Function<T, Boolean> predicate, T value, String message) {
        try {
            return predicate.apply(value) ? success(value) : failure(message);
        } catch (Exception e) {
            return failure(String.format("Exception while applying Predicate %s to value %s", predicate, value));
        }
    }

    // Exercise 7.12
    static <T, U> Function<Result<T>, Result<U>> lift(final Function<T, U> f) {
        // return rt -> rt.map(t -> f.apply(t)); // My solution
        return rt -> {
            try {
                return rt.map(f);
            } catch (Exception e) {
                return failure(e);
            }
        };
    }

    // TODO: The 2 things, Either, exception use case, question: why try..catch used in some implementations and not all-or-nothing?

    // Exercise 7.13
    public static <T, U, V> Function<Result<T>, Function<Result<U>, Result<V>>> lift2(Function<T, Function<U, V>> f) {
        // My solution:
        // return rt -> ru -> ru.flatMap(u -> rt.map(f).map(fuv -> fuv.apply(u)));
        // How did this solution come about?
        // -> I first did this: Result<Function<U, V>> rfuv = rt.map(t -> f.apply(t)) This gives Result<Function<U, V>>.
        // Then if I do rfuv.map(fuv -> fuv.apply(u)), it gives Result<V>. But the question is how to get u (of type U).
        // We can get u from ru.map(u -> ...).
        // so the final solution is: return rt -> ru -> ru.flatMap(u -> rfuv.map(fuv -> fuv.apply(u)));
        // but this is not exactly correct. Read below why it is different from author's solution.

        // Author's solution:
        // Some analysis:
        // If we do this rt -> ru -> rt.map(f).map(fuv -> ru.map(fuv))
        // we get Function<Result<T>, Function<Result<U>, Result<Result<V>>>>
        // i.e. fuv -> ru.map(fuv) gives object of type Result<Result<V>>
        // so we use flatMap() instead of map()
        // So we get rt -> ru -> rt.map(f).flatMap(fuv -> ru.map(fuv))
        // which is of type: Function<Result<T>, Function<Result<U>, Result<V>>>

        // Why is my solution not correct?
        // In my solution, first ru.flatMap() is done. So second argument is evaluated first.
        // Whereas in author's solution, rt.map() is done first. That means first argument is evaluated first.
        // This creates difference in output.
        // See table below for differences:
        // Argument1        Argument2       Author's output                     My output                           Same/Different?
        // Success1         Success2        f.apply(Success1).apply(Success2)   f.apply(Success1).apply(Success2)   Same
        // Success1         Failure2        Failure2                            Failure2                            Same
        // Success1         Empty           Empty                               Empty                               Same
        // Failure1         Success2        Failure1                            Failure1                            Same
        // Failure1         Failure2        Failure1                            Failure2                            Different
        // Failure1         Empty           Failure1                            Failure1                            Same
        // Empty            Success2        Empty                               Empty                               Same
        // Empty            Failure2        Empty                               Failure2                            Different
        // Empty            Empty           Empty                               Empty                               Same

        return rt -> ru -> rt.map(f).flatMap(ru::map);
    }
    public static <T, U, V, W> Function<Result<T>, Function<Result<U>, Function<Result<V>, Result<W>>>> lift3(Function<T, Function<U, Function<V, W>>> f) {
        return rt -> ru -> rv -> rt.map(f).flatMap(ru::map).flatMap(rv::map);
    }

    // Exercise 7.14
    public static <T, U, V> Result<V> map2(Result<T> rt, Result<U> ru, Function<T, Function<U, V>> f) {
        return lift2(f).apply(rt).apply(ru);
    }
}