package fpinjava.chapter9;

import java.util.function.Supplier;

//
// TODO
// What is lazy?
// Some languages are lazy and some are eager.
// Java is lot eager and some lazy.
// Why not use Java Stream-s?
public abstract class Stream<T> {

    protected abstract T head();
    protected abstract Stream<T> tail();
    public abstract boolean isEmpty();

    private Stream() {}

    private static final Empty EMPTY = new Empty();

    private static class Empty<A> extends Stream<A> {

        @Override
        protected A head() {
            throw new IllegalStateException("head called on Empty");
        }

        @Override
        protected Stream<A> tail() {
            throw new IllegalStateException("tail called on Empty");
        }

        @Override
        public boolean isEmpty() {
            return true;
        }
    }

    private static class Cons<A> extends Stream<A> {

        private Supplier<A> head;
        private Supplier<Stream<A>> tail;

        private Cons(Supplier<A> head, Supplier<Stream<A>> tail) {
            this.tail = tail;
            this.head = head;
        }

        @Override
        protected A head() {
            return head.get();
        }

        @Override
        protected Stream<A> tail() {
            return tail.get();
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }

    public static <A> Stream<A> cons(Supplier<A> head, Supplier<Stream<A>> tail){
        return new Cons<>(head, tail);
    }

    @SuppressWarnings("unchecked")
    public static <A> Stream<A> empty() {
        return EMPTY;
    }

    public static Stream<Integer> from(int i) {
        return cons(() -> i, () -> from(i+1));
    }
}