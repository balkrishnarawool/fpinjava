package fpinjava.chapter5;

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

    public static <T> List<T> setHead(List<T> list, T h) {
        if (list.isEmpty()) {
            throw new IllegalStateException("setHead called on an empty list");
        } else {
            return new Cons<>(h, list.tail());
        }
    }

    private static class Nil<T> extends List {

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