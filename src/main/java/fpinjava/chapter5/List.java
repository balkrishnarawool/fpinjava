package fpinjava.chapter5;

import fpinjava.chapter2.Function;
import fpinjava.chapter4.TailCall;

import java.util.Arrays;

import static fpinjava.chapter4.TailCall.ret;
import static fpinjava.chapter4.TailCall.sus;

public abstract class List<T> {

    public abstract T head();
    public abstract List<T> tail();
    public abstract boolean isEmpty();

    @SuppressWarnings("rawtypes")
    public static final List NIL = new Nil();

    private List() {}

    public List<T> cons(T t) {
        return new Cons<>(t, this);
    }

//    Below is static version of the method setHead().
//    But it could be implemented as instance method as well,
//    so instance version is being used.
//    public static <T> List<T> setHead(List<T> list, T h) {
//        if (list.isEmpty()) {
//            throw new IllegalStateException("setHead called on an empty list");
//        } else {
//            return new Cons<>(h, list.tail());
//        }
//    }
    public abstract List<T> setHead(List<T> list, T h);

    public String toString() {
        return "[ " + toStringInternal() + " ]";
    }
    protected abstract String toStringInternal();

    public abstract List<T> drop(int n);

    public abstract List<T> dropWhile(Function<T, Boolean> f);

    public static <T> List<T> concat(List<T> list1, List<T> list2) {
        return concat_(list1.reverse(), list2).eval();
    }

    private static <T> TailCall<List<T>> concat_(List<T> list1, List<T> list2) {
        return list1.isEmpty()
                ? ret(list2)
                : sus(() -> concat_(list1.tail(), new Cons<>(list1.head(), list2)));
    }

    public List<T> reverse() {
        return reverse_(this, list()).eval();
    }

    private static <T> TailCall<List<T>> reverse_(List<T> list, List<T> acc) {
        return list.isEmpty()
                ? ret(acc)
                : sus(() -> reverse_(list.tail(), new Cons<>(list.head(), acc)));
    }

    public List<T> init() {
        return reverse().tail().reverse();
    }

    // Exercise 5.7
    public static Integer sum(List<Integer> ints) {
        return ints.isEmpty()
                ? 0
                : ints.head() + sum(ints.tail());
    }

    // Exercise 5.8
    public static Double product(List<Double> ds) {
        return ds.isEmpty()
                ? 1.0
                : ds.head() * product(ds.tail());
    }

    public static <T, U> U foldRight(List<T> list, U identity, Function<T, Function<U, U>> f) {
        return list.isEmpty()
                ? identity
                : f.apply(list.head()).apply(foldRight(list.tail(), identity, f));
    }

    public static <T, U> U foldLeft(List<T> list, U identity, Function<U, Function<T, U>> f) {
        return list.isEmpty()
                ? identity
                : foldLeft(list.tail(), f.apply(identity).apply(list.head()), f);
    }

    public static <T, U> U foldLeftStackSafe(List<T> list, U identity, Function<U, Function<T, U>> f) {
        return foldLeftStackSafe_(list, identity, f).eval();
    }

    public static <T, U> TailCall<U> foldLeftStackSafe_(List<T> list, U identity, Function<U, Function<T, U>> f) {
        return list.isEmpty()
                ? ret(identity)
                : sus(() -> foldLeftStackSafe_(list.tail(), f.apply(identity).apply(list.head()), f));
    }

    // Exercise 5.9
    public int length() {
        return foldRight(this, 0, e -> n -> n + 1);
    }

    private static class Nil<T> extends List<T> {

        private Nil() {}

        @Override
        public T head() {
            throw new IllegalStateException("head() called on Nil");
        }

        @Override
        public List<T> tail() {
            throw new IllegalStateException("tail() called on Nil");
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public List<T> setHead(List<T> list, T h) {
            throw new IllegalStateException("setHead called on an empty list");
        }

        @Override
        protected String toStringInternal() {
            return "NIL";
        }

        @Override
        public List<T> drop(int n) {
            return this;
        }

        @Override
        public List<T> dropWhile(Function<T, Boolean> f) {
            return this;
        }
    }

    private static class Cons<T> extends List<T> {

        private List<T> tail;
        private T head;

        private Cons(T head, List<T> tail) {
            this.head = head;
            this.tail = tail;
        }

        @Override
        public T head() {
            return head;
        }

        @Override
        public List<T> tail() {
            return tail;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public List<T> setHead(List<T> list, T h) {
            return new Cons<>(h, list.tail());
        }

        @Override
//      public String toStringInternal() {
//          return head + ", " + tail._toString();
//      }
//      Above implementation works but it is not stack-safe.
//      So we make it stack0safe below.
        protected String toStringInternal() {
          return toStringInternal_(this, new StringBuilder()).eval().toString();
        }
        private static <T> TailCall<StringBuilder> toStringInternal_(List<T> list, StringBuilder acc) {
            return list.isEmpty()
                    ? ret(acc.append("NIL"))
                    : sus(() -> toStringInternal_(list.tail(), acc.append(list.head()).append(", ")));
        }

        @Override
        public List<T> drop(int n) {
            return drop_(this, n).eval();
        }

        private static <T> TailCall<List<T>> drop_(List<T> list, int n) {
            return (list.isEmpty() || n == 0)
                    ? ret(list)
                    : sus(() -> drop_(list.tail(), n - 1));
        }

        @Override
        public List<T> dropWhile(Function<T, Boolean> f) {
            return dropWhile_(this, f).eval();
        }
        private static <T> TailCall<List<T>> dropWhile_(List<T> list, Function<T, Boolean> f) {
            return (list.isEmpty() || !f.apply(list.head()))
                    ? ret(list)
                    : sus(() -> dropWhile_(list.tail(), f));
        }
    }

    public static <T> List<T> list() {
        return NIL;
    }

    @SafeVarargs
    public static <T> List<T> list(T... ts) {
        List<T> list = list();
        for (int i = ts.length - 1; i >= 0; i--) {
            list = new Cons<>(ts[i], list);
        }
        return list;
    }
    // Above method implementation is imperative.
    // It's functional equivalent would be this:
    @SafeVarargs
    public static <T> List<T> listFunc(T... ts) {
        return list_(list(), ts).eval();
    }
    public static <T> TailCall<List<T>> list_(List<T> acc, T[] ts) {
        return ts.length == 0
                ? ret(acc)
                : sus(() -> list_(new Cons<>(ts[ts.length -1], acc),
                Arrays.copyOfRange(ts, 0, ts.length - 1)));
    }
    // But this would be thousands of times worse in performance.
    // So we use imperative implementation.
    // Don't always use functional-style implementation.
    // Always think of imperative counterpart.
    // Sometimes imperative is better.
}