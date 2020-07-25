package fpinjava.chapter9;

import fpinjava.chapter7.Result;

import java.util.function.Supplier;

//
// TODO
// What is lazy?
// Some languages are lazy and some are eager.
// Java is lot eager and some lazy.
// Why not use Java Stream-s?
public abstract class Stream<A> {

    protected abstract A head();
    protected abstract Stream<A> tail();
    public abstract boolean isEmpty();

    public abstract Result<A> headOption();

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

        @Override
        public Result<A> headOption() {
            return Result.empty();
        }
    }

    private static class Cons<A> extends Stream<A> {

        private Supplier<A> head;
        private Supplier<Stream<A>> tail;
        private A h;
        private Stream<A> t;

        private Cons(Supplier<A> head, Supplier<Stream<A>> tail) {
            this.tail = tail;
            this.head = head;
        }

        @Override
        protected A head() {
            if(h == null) {
                h = head.get()
            }
            return h;
        }

        @Override
        protected Stream<A> tail() {
            if(t == null) {
                t = tail.get();
            }
            return t;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public Result<A> headOption() {
            if(h == null) {
                h = head.get();
            }
            return Result.success(h);
        }
    }

    public static <A> Stream<A> cons(Supplier<A> head, Supplier<Stream<A>> tail){
        return new Cons<>(head, tail);
    }

    public static <A> Stream<A> cons(Supplier<A> head, Stream<A> tail){
        return new Cons<>(head, () -> tail);
    }

    @SuppressWarnings("unchecked")
    public static <A> Stream<A> empty() {
        return EMPTY;
    }

    public static Stream<Integer> from(int i) {
        return cons(() -> i, () -> from(i+1));
    }
}