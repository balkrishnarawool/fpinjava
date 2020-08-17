package fpinjava.chapter9;

import fpinjava.chapter1.Tuple;
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
// Why not use Java List-s?
public abstract class Stream<A> {

    protected abstract Tuple<A, Stream<A>> head();
    protected abstract Stream<A> tail();
    public abstract boolean isEmpty();

    public abstract Tuple<Result<A>, Stream<A>> headOption();

    public abstract Stream<A> take(int n);
    public abstract <B> B foldRight(Supplier<B> z, Function<A, Function<Supplier<B>, B>> f);

    private Stream() {}

    private static final Empty EMPTY = new Empty();

    private static class Empty<A> extends Stream<A> {

        @Override
        protected Tuple<A, Stream<A>> head() {
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
        public Tuple<Result<A>, Stream<A>> headOption() {
            return new Tuple<>(Result.empty(), this);
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

        private final Supplier<A> head;
        private final Supplier<Stream<A>> tail;
        private final Result<A> h;
        private Stream<A> t;

        private Cons(Supplier<A> head, Supplier<Stream<A>> tail) {
            this.tail = tail;
            this.head = head;
            this.h = Result.empty();
        }

        private Cons(A h, Supplier<Stream<A>> t) {
            head = () -> h;
            tail = t;
            this.h = Result.success(h);
        }

        @Override
        protected Tuple<A, Stream<A>> head() {
            A a = h.getOrElse(head.get());
            return h.isEmpty()
                    ? new Tuple<>(a, new Cons<>(a, tail))
                    : new Tuple<>(a, this);
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
        public Tuple<Result<A>, Stream<A>> headOption() {
            Tuple<A, Stream<A>> t = head();
            return new Tuple<>(Result.success(t._1), t._2);
        }

        // The recursion in take() is lazy (the recursive call to take() happens in a Supplier).
        @Override
        public Stream<A> take(int n) {
            return n <= 0
                    ? empty()
                    : cons(head, () -> tail().take(n - 1));
        }

        // The recursion in foldRight() is lazy (the recursive call to foldRight() happens in a Supplier).
        // Lazy but stack-unsafe. See StreamTest.testFoldRight().
        @Override
        public <B> B foldRight(Supplier<B> z, Function<A, Function<Supplier<B>, B>> f) {
            return f.apply(head()._1).apply(() -> tail().foldRight(z, f));
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
        return isEmpty() ? ret(acc) : sus(() -> tail().toList_(acc.cons(head()._1)));
    }

    // Lazy
    // It is different from book because this implementation is in Stream and in book the implementation is in Cons class.
    // TODO Question: why is this function() stack-safe?
    public Stream<A> takeWhile(Function<A, Boolean> p) {
        return isEmpty()
                ? empty()
                : p.apply(head()._1)
                    ? cons(() -> head()._1, () -> tail().takeWhile(p))
                    : empty();
    }

    // With TailCall because otherwise it would have been eager-recursion and stack-unsafe.
    public Stream<A> dropWhile(Function<A, Boolean> p) {
        return dropWhile_(p).eval();
    }
    private TailCall<Stream<A>> dropWhile_(Function<A, Boolean> p) {
        return isEmpty()
                ? ret(this)
                : p.apply(head()._1)
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

    // Question: why are take() and takeWhile() functions stack-safe?
    // Because they return a Stream with "the condition" built into it which is lazily evaluated.
    // Why is foldRight() not stack-safe? This is also the same reason as above.
    // foldRight() returns a single object (and not Stream) and the recursion there is eager which evaluates the whole Stream.
    // Can we implement filter(), map(), drop() and dropWhile() like this so as they are also stack-safe?
    // It is not suitable for filter(), drop() and dropWhile() because it has to skip non-matching elements.
    // So it anyways has to evaluate Stream until it finds a matching element.
    public <B> Stream<B> map1(Function<A, B> f) { // This is example of implementing the function map()
        return isEmpty()
                ? empty()
                : cons(() -> f.apply(head()._1), () -> tail().map1(f));
    }

    public boolean exists(Function<A, Boolean> p) {
        return exists_(p).eval();
    }
    private TailCall<Boolean> exists_(Function<A, Boolean> p) {
        return isEmpty()
                ? ret(false)
                : p.apply(head()._1)
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

    // This implementation of filter() is lazy but stack-unsafe.
    // Lazy because it returns the stream with head as the first matching element and then lazily fetches the other matching elements (when needed).
    // It can run into StackOverflowError if no matching elements are found (in sufficiently long Streams-s).
    // public Stream<A> filter(Function<A, Boolean> p) {
    //     return foldRight(Stream::empty, a -> ssa -> p.apply(a) ? cons(() -> a, ssa) : ssa.get()); //ssa is Supplier<Stream<A>>
    // }
    // Below is stack-safe implementation - using head()
    // public Stream<A> filter(Function<A, Boolean> p) {
    //     Stream<A> stream = this.dropWhile(x -> !p.apply(x));
    //     return stream.isEmpty()
    //             ? stream
    //             : cons(() -> stream.head()._1,
    //             () -> stream.tail().filter(p));
    // }
    public Stream<A> filter(Function<A, Boolean> p) {
        Stream<A> stream = this.dropWhile(x -> !p.apply(x));
        return stream.headOption()._1.map(a -> cons(() -> a, () -> stream.tail().filter(p)))
                                    .getOrElse(empty());
    }

    public Stream<A> append(Supplier<Stream<A>> s) {
        return foldRight(s, a -> ssa -> cons(() -> a, ssa));
    }

    public <B> Stream<B> flatMap(Function<A, Stream<B>> f) {
        return foldRight(() -> empty(), a -> sb -> f.apply(a).append(sb));
    }

    // TODO In Stream-s, traversing the elements occurs only once even when multiple filter(), map() functions are composed.

    public Result<A> find(Function<A, Boolean> p) {
        return filter(p).headOption()._1;
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

    public static <A> Stream<A> iterate(A seed, Function<A, A> f) {
        return cons(() -> seed, () -> iterate(f.apply(seed), f));
    }

    public static <A> Stream<A> iterate(Supplier<A> s, Function<A, A> f) {
        return cons(s, () -> iterate(f.apply(s.get()), f));
    }

//  My implementation without using iterate.
//    public static Stream<Integer> fibs(int i1, int i2) {
//        return cons(() -> i1, () -> fibs(i2, i1 + i2));
//    }
    public static Stream<Integer> fibs() {
        return iterate(new Tuple<>(0, 1), t -> new Tuple<>(t._2, t._1 + t._2)).map(t -> t._1);
    }

    public static <A, S> Stream<A> unfold(S z, Function<S, Result<Tuple<A, S>>> f) {
        return f.apply(z).map(x -> cons(() -> x._1, () -> unfold(x._2, f))).getOrElse(empty());
    }
//    public Stream<A> repeat(A a) {
//        return unfold(a, a1 -> Result.success(new Tuple<>(a1, a1)));
//    }
//    public static Stream<Integer> from(int n) {
//        return unfold(n, x -> Result.success(new Tuple<>(x, x + 1)));
//    }
//    public static Stream<Integer> fibs() {
//        return unfold(new Tuple<>(1, 1),
//                x -> Result.success(new Tuple<>(x._1, new Tuple<>(x._2, x._1 + x._2))));
//    }
}