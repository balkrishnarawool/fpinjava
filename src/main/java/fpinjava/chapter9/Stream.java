package fpinjava.chapter9;

import fpinjava.chapter2.Function;
import fpinjava.chapter4.TailCall;
import fpinjava.chapter7.Result;
import fpinjava.chapter8.List;

import java.util.function.Supplier;

import static fpinjava.chapter4.TailCall.ret;
import static fpinjava.chapter4.TailCall.sus;

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

    public abstract Stream<A> take(int n);
    public abstract <B> B foldRight(Supplier<B> z, Function<A, Function<Supplier<B>, B>> f);

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

        @Override
        public Stream<A> take(int n) {
            return this;
        }

        @Override
        public <B> B foldRight(Supplier<B> z, Function<A, Function<Supplier<B>, B>> f) {
            return z.get();
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
                h = head.get();
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

        // The recursion in take() is lazy (the recursive call to take() happens in a Supplier).
        // So this makes take() function stack-safe.
        @Override
        public Stream<A> take(int n) {
            return n <= 0
                    ? empty()
                    : cons(head, () -> tail().take(n - 1));
        }

        // The recursion in foldRight() is lazy (the recursive call to foldRight() happens in a Supplier).
        // So this makes foldRight() function stack-safe.
        @Override
        public <B> B foldRight(Supplier<B> z, Function<A, Function<Supplier<B>, B>> f) {
            return f.apply(head()).apply(() -> tail().foldRight(z, f));
        }
    }

    // In TailCall was not used, the recursion in drop() would have been eager.
    // That would make drop() function stack-unsafe.
    // Therefore TailCall is added to make recursion-lazy and the function stack-safe.
    public Stream<A> drop(int n) {
        return drop_(n).eval();
    }
    private TailCall<Stream<A>> drop_(int n) {
        return n <= 0
                ? ret(this)
                : sus(() -> tail().drop_(n - 1));
    }

    public List<A> toList() {
        return toList_(List.list()).eval().reverse();
    }
    private TailCall<List<A>> toList_(List<A> acc) {
        return isEmpty() ? ret(acc) : sus(() -> tail().toList_(acc.cons(head())));
    }

    // Lazy-recursion, so stack-safe
    // It is different from book because this implementation is in Stream and in book the implementation is in Cons class.
    public Stream<A> takeWhile(Function<A, Boolean> p) {
        return isEmpty()
                ? empty()
                : p.apply(head())
                    ? cons(() -> head(), () -> tail().takeWhile(p))
                    : empty();
    }

    // With TailCall because otherwise it would have been eager-recursion and stack-unsafe.
    public Stream<A> dropWhile(Function<A, Boolean> p) {
        return dropWhile_(p).eval();
    }
    private TailCall<Stream<A>> dropWhile_(Function<A, Boolean> p) {
        return isEmpty()
                ? ret(this)
                : p.apply(head())
                    ? sus(() -> tail().dropWhile_(p))
                    : ret(this);
    }

    // TODO
    // The real difference between strictness and laziness is that strictness is about doing things,
    // and laziness is about noting things to do.
    // Lazy evaluation of data notes that data must be evaluated sometime in the future.

    // TODO
    // One huge advantage of this approach is that you could produce a description of a program producing an error,
    // and then decide not to execute it based on some condition.
    // Or you could produce an infinite expression, and then apply some means of reducing it to a finite one.

    public boolean exists(Function<A, Boolean> p) {
        return exists_(p).eval();
    }
    private TailCall<Boolean> exists_(Function<A, Boolean> p) {
        return isEmpty()
                ? ret(false)
                : p.apply(head())
                    ? ret(true)
                    : sus(() -> tail().exists_(p));
    }

    public Stream<A> takeWhileWithFoldRight(Function<A, Boolean> p) {
        return isEmpty()
                ? empty()
                : foldRight(() -> empty(), a -> sa -> p.apply(a) ? cons(() -> a, sa) : empty());
    }

    public Result<A> headOptionWithFoldRight() {
        return foldRight(() -> Result.empty(), a -> sb -> Result.success(a));
    }

    // Exercise 9.10
    public <B> Stream<B> map(Function<A, B> f) {
        return foldRight(Stream::empty, a -> ssb -> cons(() -> f.apply(a), ssb)); //ssb is Supplier<Stream<B>>
    }

    // Note that this method evaluates the stream elements until the first match is found. This means
    // - It returns the stream with head as the first matching element and then lazily fetches the other matching elements (when needed).
    // It can run into infinite loop if no matching elements are found (in unbounded Streams-s).
    public Stream<A> filter(Function<A, Boolean> p) {
        return foldRight(Stream::empty, a -> ssa -> p.apply(a) ? cons(() -> a, ssa) : ssa.get()); //ssa is Supplier<Stream<A>>
    }

    public Stream<A> append(Supplier<Stream<A>> s) {
        return foldRight(s, a -> ssa -> cons(() -> a, ssa));
    }

    public <B> Stream<B> flatMap(Function<A, Stream<B>> f) {
        return foldRight(() -> empty(), a -> sb -> f.apply(a).append(sb));
    }

    // TODO In Stream-s, traversing the elements occurs only once even when multiple filter(), map() functions are composed.

    public Result<A> find(Function<A, Boolean> p) {
        return filter(p).headOption();
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

    public static <A> Stream<A> repeat(A a) {
        return cons(() -> a, () -> repeat(a));
    }
}