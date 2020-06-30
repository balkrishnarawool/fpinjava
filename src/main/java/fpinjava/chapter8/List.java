package fpinjava.chapter8;

import fpinjava.chapter2.Function;
import fpinjava.chapter4.TailCall;
import fpinjava.chapter7.Result;

import java.util.Arrays;

import static fpinjava.chapter4.TailCall.ret;
import static fpinjava.chapter4.TailCall.sus;

public abstract class List<T> {

    protected abstract T head();
    protected abstract List<T> tail();
    public abstract boolean isEmpty();
    public abstract List<T> setHead(List<T> list, T h);
    public abstract List<T> drop(int n);
    public abstract List<T> dropWhile(Function<T, Boolean> f);

    // Exercise 8.1
    public abstract int lengthMemoized();
    // Exercise 8.2
    public abstract Result<T> headOption();

    public String toString() {
        return "[ " + toStringInternal() + " ]";
    }
    protected abstract String toStringInternal();

    @SuppressWarnings("rawtypes")
    public static final List NIL = new Nil();

    private List() {}

    public List<T> cons(T t) {
        return new Cons<>(t, this);
    }

    private static class Nil<T> extends List<T> {

        private Nil() {}

        @Override
        protected T head() {
            throw new IllegalStateException("head() called on Nil");
        }

        @Override
        protected List<T> tail() {
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

        @Override
        public int lengthMemoized() {
            return 0;
        }

        @Override
        public Result<T> headOption() {
            return Result.empty();
        }
    }

    private static class Cons<T> extends List<T> {

        private List<T> tail;
        private T head;
        private final int length;

        private Cons(T head, List<T> tail) {
            this.head = head;
            this.tail = tail;
            this.length = tail.length() + 1;
        }

        @Override
        protected T head() {
            return head;
        }

        @Override
        protected List<T> tail() {
            return tail;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public List<T> setHead(List<T> list, T h) {
            return new List.Cons<>(h, list.tail());
        }

        // @Override
        // public String toStringInternal() {
        //     return head + ", " + tail._toString();
        // }
        // Above implementation works but it is not stack-safe.
        // So we make it stack safe below.
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

        @Override
        public int lengthMemoized() {
            return length;
        }

        @Override
        public Result<T> headOption() {
            return Result.success(head);
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
    // Below is static version of the method setHead().
    // But it could be implemented as instance method as well,
    // so instance version is being used.
    // public static <T> List<T> setHead(List<T> list, T h) {
    //     if (list.isEmpty()) {
    //         throw new IllegalStateException("setHead called on an empty list");
    //     } else {
    //         return new Cons<>(h, list.tail());
    //     }
    // }

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

    // Exercise 5.9
    public <U> U foldRightStackUnsafe(U identity, Function<T, Function<U, U>> f) {
        return isEmpty()
                ? identity
                : f.apply(head()).apply(tail().foldRightStackUnsafe(identity, f));
    }
    public int length() {
        return foldRightStackUnsafe(0, e -> n -> n + 1);
    }

    // Exercise 5.10
    public <U> U foldLeftStackUnsafe(U identity, Function<U, Function<T, U>> f) {
        return isEmpty()
                ? identity
                : tail().foldLeftStackUnsafe(f.apply(identity).apply(head()), f);
    }

    // stack-safe
    public static <T, U> U foldLeft(List<T> list, U identity, Function<U, Function<T, U>> f) {
        return foldLeftStackSafe_(list, identity, f).eval();
    }
    public static <T, U> TailCall<U> foldLeftStackSafe_(List<T> list, U identity, Function<U, Function<T, U>> f) {
        return list.isEmpty()
                ? ret(identity)
                : sus(() -> foldLeftStackSafe_(list.tail(), f.apply(identity).apply(list.head()), f));
    }

    // Exercise 5.11
    public static Integer sumViaFoldLeft(List<Integer> list) {
        return list.foldLeftStackUnsafe(0, acc -> num -> acc + num);
    }
    public static Double productViaFoldLeft(List<Double> list) {
        return list.foldLeftStackUnsafe(1.0, acc -> num -> acc * num);
    }
    public static Integer lengthViaFoldLeft(List<Integer> list) {
        return list.foldLeftStackUnsafe(0, acc -> num -> acc + 1);
    }

    // Exercise 5.12
    public static <T> List<T> reverseViaFoldLeft(List<T> list) {
        return list.foldLeftStackUnsafe(list(), l -> l::cons);
    }

    // Exercise 5.13
    public static <T, U> U foldRightViaFoldLeft(List<T> list, U identity, Function<T, Function<U, U>> f) {
        return list.reverse().foldLeftStackUnsafe(identity, u -> t -> f.apply(t).apply(u));
    }
    public static <T, U> U foldLeftViaFoldRight(List<T> list, U identity, Function<U, Function<T, U>> f) {
        return list.reverse().foldRightStackUnsafe(identity, t -> u -> f.apply(u).apply(t));
    }

    // Exercise 5.14
    // stack-safe
    public <U> U foldRight(U identity, Function<T, Function<U, U>> f) {
        return reverse().foldRightStackSafe_(f, identity).eval();
    }
    private <U> TailCall<U> foldRightStackSafe_(Function<T, Function<U, U>> f, U accumulator) {
        return isEmpty()
                ? ret(accumulator)
                : sus(() -> tail().foldRightStackSafe_(f, f.apply(head()).apply(accumulator)));
    }

    // Exercise 5.15
    public static <T> List<T> concat2(List<T> list1, List<T> list2) {
        return list1.foldRight(list2, t -> list -> list.cons(t));
    }
    public static <T> List<T> concatViaFoldLeft(List<T> list1, List<T> list2) {
        return list1.reverse().foldLeftStackUnsafe(list2, list -> t -> list.cons(t));
    }

    // Exercise 5.16
    public static <T> List<T> flatten(List<List<T>> list) {
        return list.foldRight(list(), l -> acc -> concat(l, acc));
    }

    // Exercise 5.17
    public static List<Integer> triple(List<Integer> list) {
        return list.foldRight(list(), n -> l -> l.cons(n * 3));
    }

    // Exercise 5.18
    public static List<String> doubleToString(List<Double> list) {
        return list.foldRight(list(), n -> l -> l.cons(n.toString()));
    }

    // Exercise 5.19
    public <U> List<U> map(Function<T, U> f) {
        return foldRight(list(), t -> list -> list.cons(f.apply(t)));
    }

    // Exercise 5.20
    public List<T> filter(Function<T, Boolean> f) {
        return foldRight(list(), t -> list -> f.apply(t) ? list.cons(t) : list);
    }

    // Exercise 5.21
    public <U> List<U> flatMap(Function<T, List<U>> f) {
        return flatten(map(f));
    }

    // Exercise 5.22
    public List<T> filterViaFlatMap(Function<T, Boolean> f) {
        return flatMap(t -> f.apply(t) ? list(t) : list());
    }

    //Exercise 8.3
    public Result<T> lastOption() {
        return foldLeft(this, Result.empty(), x -> Result::success);
    }

    //Exercise 8.4
    // This is functional equivalent of the headOption() function.
    // But performance wise it is much worse as it traverses the entire list. So don't use it.
    public Result<T> headOptionFunc() {
        return foldRight(Result.empty(), x -> y -> Result.success(x));
    }

    // Exercise 8.5
    public static <T> List<T> flattenResult(List<Result<T>> list) {
        // My solution
        // Problem with this solution:
        // We have to specify a value to be used when Result<T> is empty or Failure.
        // return list.foldRight(list(), rt -> lt -> lt.cons(rt.getOrElse(() -> null /* This is default value when Result<T> is empty or Failure. What could be a good default? */)));

        // Logic with author's solution:
        // Map each element in List<Result<A>> (i.e. Result<A>) to List<A>: if it is Empty or Failure then empty-list, otherwise list with one element.
        // Right-fold the list and then flatten the list.
        return flatten(list.foldRight(list(), rt -> llt -> rt.map(t -> llt.cons(list(t))).getOrElse(list())));
    }
}