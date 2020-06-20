package fpinjava.chapter6;

import fpinjava.chapter2.Function;
import fpinjava.chapter5.List;

import java.util.Objects;
import java.util.function.Supplier;

public abstract class Option<T> {

    public abstract T getOrThrow();
    public abstract T getOrElse(T defaultValue);
    public abstract T getOrElse(Supplier<T> defaultValue);
    public abstract <U> Option<U> map(Function<T, U> f);

    public abstract boolean isSome();

    @SuppressWarnings("rawtypes")
    private static None none = new None();

    public static class None<T> extends Option<T> {
        private None() { }

        @Override
        public T getOrThrow() {
            throw new IllegalStateException("get called on None");
        }

        @Override
        public T getOrElse(T defaultValue) {
            return defaultValue;
        }

        @Override
        public String toString() {
            return "None";
        }

        @Override
        public T getOrElse(Supplier<T> defaultValue) {
            return defaultValue.get();
        }

        @Override
        public <U> Option<U> map(Function<T, U> f) {
            return none();
        }

        @Override
        public <U> Option<U> flatMap(Function<T, Option<U>> f) {
            return none();
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof None;
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean isSome() {
            return false;
        }
    }

    public static class Some<T> extends Option<T> {
        private T value;

        private Some(T value) {
            this.value = value;
        }

        @Override
        public T getOrThrow() {
            return value;
        }

        @Override
        public T getOrElse(T defaultValue) {
            return value;
        }

        @Override
        public String toString() {
            return String.format("Some(%s)", value);
        }

        @Override
        public T getOrElse(Supplier<T> defaultValue) {
            return value;
        }

        @Override
        public <U> Option<U> map(Function<T, U> f) {
            return some(f.apply(value));
        }

        @Override
        public <U> Option<U> flatMap(Function<T, Option<U>> f) {
            return f.apply(value);
        }

        @Override
        public boolean equals(Object o) {
            return o == this || (o instanceof Some && value.equals(((Some) o).value));
            // I think null checks are not added for value because if we use Option class properly
            // in the application, null checks won't be necessary.
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }

        @Override
        public boolean isSome() {
            return true;
        }
    }

    public static <T> Option<T> some(T t) {
        return new Some(t);
    }

    @SuppressWarnings("unchecked")
    public static <T> Option<T> none() {
        return none;
    }

    // Exercise 6.4
    public <U> Option<U> flatMap(Function<T, Option<U>> f) {
        return map(f).getOrElse(none());
    }

    // Exercise 6.5
    public Option<T> orElse(Supplier<Option<T>> defaultValue) {
        return map(t -> this).getOrElse(defaultValue);
    }

    // Exercise 6.6
    public Option<T> filter(Function<T, Boolean> f) {
//        return map(f).getOrElse(false) ? this : none(); // This is my implementation.
        return flatMap(t -> f.apply(t) ? this : none());
    }

    // Exercise 6.7
    public Function<List<Double>, Double> sum = l -> l.foldLeftStackUnsafe(0.0D, acc -> n -> acc + n);
    public Function<List<Double>, Option<Double>> mean = l -> l.isEmpty() ? none() : some(sum.apply(l) / l.length());
    public Function<List<Double>, Option<Double>> variance =  // l -> mean.apply(l.map(n -> Math.pow(n - mean.apply(l).getOrElse(0.0D), 2))); // My solution
                                                            l -> mean.apply(l).flatMap(m -> mean.apply(l.map(n -> Math.pow(n - m, 2))));

    // Exercise 6.8
    public static <T, U> Function<Option<T>, Option<U>> lift(Function<T, U> f) {
        return ot -> ot.map(f);
    }

    // Exercise 6.10
    // Think of Option.map2() as a folding of Option. Folding takes original object, brings in another object
    // and maps them to a third kind of object. That is what map2() does.
    // map2() is same as both left-fold and right-fold, because Option contains only one object, if at all
    // and map2() takes two Option objects.
    public static <T, U, V> Option<V> map2(Option<T> ot, Option<U> ou, Function<T, Function<U, V>> f) {
        return ot.flatMap(t -> ou.map(u-> f.apply(t).apply(u)));
    }
    public static <T, U, V, W> Option<W> map3(Option<T> ot, Option<U> ou, Option<V> ov,
                                           Function<T, Function<U, Function<V, W>>> f) {
        return ot.flatMap(t -> ou.flatMap(u -> ov.map(v ->f.apply(t).apply(u).apply(v))));
    }

    // Exercise 6.11
    public static <T> Option<List<T>> sequence(List<Option<T>> list) {
        return list.foldRight(some(List.list()), ot -> ol -> map2(ot, ol, ft -> fl -> fl.cons(ft)));
        // Why did we use foldRight() here and not foldLeft()?
        // -> That's because at the end we are using cons() and cons prepends to the list. So we used foldRight().
        // How does this work when (at least) one element in List<Option<T>> list is None?
        // -> The way map2() works is it returns None when one of the input objects is None.
        // Because of foldRight() the whole list is traversed. At the point when None element is passed
        // (as first input object) to map2(), it returns None. For the subsequent "iterations" None is passed as
        // second input object and it always returns None.
        // Interesting thing to note: the traversing is not stopped when first None object is encountered which is less efficient.
    }

    // Exercise 6.12
    public static <T, U>Option<List<U>> traverse(List<T> list, Function<T, Option<U>> f) {
        // return list.map(f).foldRight(some(List.list()), ou -> olu -> map2(ou, olu, fu -> flu -> flu.cons(fu)));
        // This implementation is same as above (sequence()) with list replaced by list.map(f).
        // It traverses the list twice.
        return list.foldRight(some(List.list()), t -> olu -> map2(f.apply(t), olu, fu -> flu -> flu.cons(fu)));
        // In this implementation, we traverse once (more efficient than above).
        // This is possible because we pass f.apply(t) [which gives Option<U>] to map2.
    }
    // traverse() using sequence()
    public static <T, U> Option<List<U>> traverse1(List<T> list, Function<T, Option<U>> f) {
        return sequence(list.map(f));
    }
    // sequence() using traverse()
    public static <T> Option<List<T>> sequence1(List<Option<T>> list) {
        return traverse(list, ot -> ot);
    }
}