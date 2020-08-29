package fpinjava.chapter8;

import fpinjava.chapter1.Tuple;
import fpinjava.chapter2.Function;
import fpinjava.chapter4.TailCall;
import fpinjava.chapter4.Tuple3;
import fpinjava.chapter7.Result;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import static fpinjava.chapter4.TailCall.ret;
import static fpinjava.chapter4.TailCall.sus;
import static fpinjava.chapter7.Result.empty;
import static fpinjava.chapter7.Result.failure;
import static fpinjava.chapter7.Result.success;

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
            return empty();
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

    public <T> List<T> concat(List<T> list) {
        return concat_(reverse(), list).eval();
    }
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

    // stack-safe instance method
    public <U> U foldLeft(U identity, Function<U, Function<T, U>> f) {
        return foldLeft(this, identity, f);
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
        return foldLeft(this, empty(), x -> Result::success);
    }

    //Exercise 8.4
    // This is functional equivalent of the headOption() function.
    // But performance wise it is much worse as it traverses the entire list. So don't use it.
    public Result<T> headOptionFunc() {
        return foldRight(empty(), x -> y -> Result.success(x));
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

    // Exercise 8.6
    public static <T> Result<List<T>> sequence(List<Result<T>> list) {
        return list.filter(rt -> rt.isSuccess() || rt.isFailure())
                   .foldRight(Result.success(list()), rt -> rlt -> Result.map2(rt, rlt, t -> lt -> lt.cons(t)));
    }

    // Exercise 8.7
    // Unlike sequence() in exercise 8.6, Empty results are considered as Failure-s here and are not trated specially.
    public static <T, U> Result<List<U>> traverse(List<T> list, Function<T, Result<U>> f) {
        return list.foldRight(Result.success(list()), t -> rlt -> Result.map2(f.apply(t), rlt, t2 -> lt -> lt.cons(t2)));
    }
    public static <T> Result<List<T>> sequenceFromTraverse(List<Result<T>> list) {
        return traverse(list, rt -> rt);
    }

    // Exercise 8.8
    public static <T, U, V> List<V> zipWith(List<T> list1, List<U> list2, Function<T, Function<U, V>> f) {
        return zipWith_(list1, list2, f, list()).eval().reverse();
    }
    private static <T, U, V> TailCall<List<V>> zipWith_(List<T> list1, List<U> list2, Function<T, Function<U, V>> f, List<V> acc) {
        return list1.isEmpty() || list2.isEmpty()
                ? ret(acc)
                : sus(() -> zipWith_(list1.tail(), list2.tail(), f, acc.cons(f.apply(list1.head()).apply(list2.head()))));
    }

    // Exercise 8.9
    public static <T, U, V> List<V> product(List<T> list1, List<U> list2, Function<T, Function<U, V>> f) {
        // My solution
        // return list1.foldLeft(list(), lv -> t -> list2.foldLeft(lv, lv2 -> u -> lv.cons(f.apply(t).apply(u))));
        // Author's solution
        return list1.flatMap(t -> list2.map(u -> f.apply(t).apply(u)));
    }

    // Exercise 8.10
    public static <T, U> Tuple<List<T>, List<U>> unzip(List<Tuple<T, U>> list) {
        return list.foldRight(new Tuple<>(list(), list()), ttu -> tltlu -> new Tuple<>(tltlu._1.cons(ttu._1), tltlu._2.cons(ttu._2)));
    }

    // Exercise 8.11
    public <T1, T2> Tuple<List<T1>, List<T2>> unzip(Function<T, Tuple<T1, T2>> f) {
        return foldRight(new Tuple<>(list(), list()), t -> tlt1lt2 ->  {
            Tuple<T1, T2> tt1t2 = f.apply(t);
            return new Tuple<>(tlt1lt2._1.cons(tt1t2._1), tlt1lt2._2.cons(tt1t2._2));
        });
    }

    // Exercise 8.12
    public Result<T> getAt(int index) {
        return index < 0 || index >= length()
                ? failure("Index out of bounds, index: "+index)
                : getAt_(index).eval();
    }
    public TailCall<Result<T>> getAt_(int index) {
        return index == 0
                ? ret(success(head()))
                : sus(() -> tail().getAt_(index - 1));
    }
    public Result<T> getAtUsingFoldLeft(int index) {
        Tuple<Result<T>, Integer> identity = new Tuple<>(failure("Index out of bounds, index: "+index), index);
        Tuple<Result<T>, Integer> tuple =
                index < 0 || index >= length()
                ? identity
                : foldLeft( identity, trti -> t -> trti._2 < 0 ? trti : new Tuple<>(success(t), trti._2-1));
        return tuple._1;

        // return (index < 0 || index >= length()
        //         ? identity
        //         : foldLeft( identity, trti -> t -> trti._2 < 0 ? trti : new Tuple<>(success(t), trti._2-1)))._1;
    }

    // Exercise 8.13
    public static <T, U> U foldLeft(List<T> list, U identity, U zero, Function<U, Function<T, U>> f) {
        return foldLeft_(list, identity, zero, f).eval();
    }
    private static <T, U> TailCall<U> foldLeft_(List<T> list, U acc, U zero, Function<U, Function<T, U>> f) {
        return list.isEmpty() || acc.equals(zero)
                ? ret(acc)
                : sus(() -> foldLeft_(list.tail(), f.apply(acc).apply(list.head()), zero, f));
    }
    public static <T> Result<T> getAtUsingFoldLeftEfficient(List<T> list, int index) {
        class Tuple<T, U> {

            public final T _1;
            public final U _2;

            public Tuple(T t, U u) {
                this._1 = Objects.requireNonNull(t);
                this._2 = Objects.requireNonNull(u);
            }

            @Override
            public boolean equals(Object o) {
                if (!(o.getClass() == this.getClass()))
                    return false;
                else {
                    @SuppressWarnings("rawtypes")
                    Tuple that = (Tuple) o;
                    return _2.equals(that._2);
                }
            }
        }

        Tuple<Result<T>, Integer> identity = new Tuple<>(failure("Index out of bounds, index: "+index), index);
        Tuple<Result<T>, Integer> zero = new Tuple<>(Result.failure("Index out of bound"), -1);
        Tuple<Result<T>, Integer> tuple =
                index < 0 || index >= list.length()
                        ? identity
                        : foldLeft( list, identity, zero, trti -> t -> trti._2 < 0 ? trti : new Tuple<>(success(t), trti._2-1));
        return tuple._1;
    }

    // Exercise 8.14
    // My solution
    public Tuple<List<T>, List<T>> splitAtMySol(int index) {
        return index < 0
                ? new Tuple<>(list(), this)
                : index >= length()
                    ? new Tuple<>(this, list())
                    : splitAtMySol_(new Tuple<>(list(), this), index).eval();
    }
    private static <T> TailCall<Tuple<List<T>, List<T>>> splitAtMySol_(Tuple<List<T>, List<T>> acc, int index) {
        return index < 0 || acc._2.isEmpty()
                ? ret(new Tuple<>(acc._1.reverse(), acc._2))
                : sus(() -> splitAtMySol_(new Tuple<>(acc._1.cons(acc._2.head()), acc._2.tail()), index - 1));

    }
    // Author's solution
    public Tuple<List<T>, List<T>> splitAt(int index) {
        return index < 0
                ? splitAt(0)
                : index > length()
                ? splitAt(length())
                : splitAt(list(), this.reverse(), this.length() - index).eval();
    }
    private TailCall<Tuple<List<T>, List<T>>> splitAt(List<T> acc, List<T> list, int i) {
        return i == 0 || list.isEmpty()
                ? ret(new Tuple<>(list.reverse(), acc)) // reverse() is done for the remainder of the list and it is added first to the Tuple
                : sus(() -> splitAt(acc.cons(list.head()), list.tail(), i - 1));
    }
    // Differences:
    // Author did not use Tuple as param, makes it more efficient
    // Author used split(0) and splitAt(length()) which are more logical given the problem statement
    // Author used reversed list and then reversed one of the Tuple objects which could have been avoided and only do reverse() once like my solution

    // Exercise 8.15: Part 1
    public Tuple<List<T>, List<T>> splitAtUsingFoldLeftMySol(Integer index) {
        return index < 0
                ? new Tuple<>(list(), this)
                : index >= length()
                ? new Tuple<>(this, list())
                : foldLeft(new Tuple<Tuple<List<T>, List<T>>, Integer>(new Tuple<>(list(), this), index),
                    ttltlti -> t -> index >= 0 ? new Tuple<>(new Tuple<>(List.<T>list().cons(t), tail()), index - 1) : ttltlti
                )._1;
    }
    // Author's solution
    public Tuple<List<T>, List<T>> splitAtUsingFoldLeft(int index) {
        int ii = index < 0 ? 0 : index >= length() ? length() : index;
        Tuple3<List<T>, List<T>, Integer> identity =
                new Tuple3<>(List.list(), List.list(), ii);
        Tuple3<List<T>, List<T>, Integer> rt =
                foldLeft(identity, ta -> a -> ta._3 == 0
                        ? new Tuple3<>(ta._1, ta._2.cons(a), ta._3)
                        : new Tuple3<>(ta._1.cons(a), ta._2, ta._3 - 1));
        return new Tuple<>(rt._1.reverse(), rt._2.reverse());
    }
    // Differences:
    // Author used Tuple3, much better choice than Tuple<Tuple<>, <>>
    // He starts with empty lists then fills them up depending on which side of index we are. this is less efficient.
    // He adjusts index, I ignore it if it is out of bounds.

    // Exercise 8.15: Part 2
    // Here, there is only one implementation, but the book uses abstract method with implementation in both subclasses.
    public static <T, U> Tuple<U, List<T>> foldLeftTuple(List<T> list, U identity, U zero, Function<U, Function<T, U>> f) {
        return foldLeftTuple_(list, identity, zero, f).eval();
    }
    private static <T, U> TailCall<Tuple<U, List<T>>> foldLeftTuple_(List<T> list, U acc, U zero, Function<U, Function<T, U>> f) {
        return list.isEmpty() || acc.equals(zero)
                ? ret(new Tuple<>(acc, list))
                : sus(() -> foldLeftTuple_(list.tail(), f.apply(acc).apply(list.head()), zero, f));
    }
    public static <T> Tuple<List<T>, List<T>> splitAtUsingFoldLeftEfficient(List<T> list, int index) {
        class Tuple3<T, U, V> {

            public final T _1;
            public final U _2;
            public final V _3;

            public Tuple3(T t, U u, V v) {
                this._1 = Objects.requireNonNull(t);
                this._2 = Objects.requireNonNull(u);
                this._3 = Objects.requireNonNull(v);
            }

            @Override
            public boolean equals(Object o) {
                if (!(o.getClass() == this.getClass()))
                    return false;
                else {
                    @SuppressWarnings("rawtypes")
                    Tuple3 that = (Tuple3) o;
                    return _3.equals(that._3);
                }
            }
        }

        Tuple3<List<T>, List<T>, Integer> identity = new Tuple3<>(list(), list(), index);
        Tuple3<List<T>, List<T>, Integer> zero = new Tuple3<>(list(), list(), 0);
        Tuple<Tuple3<List<T>, List<T>, Integer>, List<T>> tuple =
                index <= 0
                        ? new Tuple<>(identity, list)
                        : foldLeftTuple( list, identity, zero, t3ltlti -> t -> t3ltlti._3 < 0 ? t3ltlti : new Tuple3<>(t3ltlti._1.cons(t), t3ltlti._2, t3ltlti._3 - 1));
        return new Tuple<>(tuple._1._1.reverse(), tuple._2);
    }
    // It looks like the second param in the Tuple3 is not being used at all, so we can even do this with a Tuple (with 2 params).
    // Let's implement it below, write test and see if it works. It is true. See fpinjava.chapter8.ListTest.testSplitAtUsingFoldLeftEfficient()
    public static <T> Tuple<List<T>, List<T>> splitAtUsingFoldLeftEfficientWithTuple(List<T> list, int index) {
        class MyTuple<T, U> {

            public final T _1;
            public final U _2;

            public MyTuple(T t, U u) {
                this._1 = Objects.requireNonNull(t);
                this._2 = Objects.requireNonNull(u);
            }

            @Override
            public boolean equals(Object o) {
                if (!(o.getClass() == this.getClass()))
                    return false;
                else {
                    @SuppressWarnings("rawtypes")
                    MyTuple that = (MyTuple) o;
                    return _2.equals(that._2);
                }
            }
        }

        MyTuple<List<T>, Integer> identity = new MyTuple<>(list(), index);
        MyTuple<List<T>, Integer> zero = new MyTuple<>(list(), 0);
        Tuple<MyTuple<List<T>, Integer>, List<T>> tuple =
                index <= 0
                        ? new Tuple<>(identity, list)
                        : foldLeftTuple( list, identity, zero, tlti -> t -> tlti._2 < 0 ? tlti : new MyTuple<>(tlti._1.cons(t), tlti._2 - 1));
        return new Tuple<>(tuple._1._1.reverse(), tuple._2);
    }

    // Exercise 8.16
    public static <T> boolean hasSubList(List<T> list, List<T> sub) {
        return hasSubList_(list, sub).eval();
    }
    private static <T> TailCall<Boolean> hasSubList_(List<T> list, List<T> sub) {
        return list.isEmpty()
                ? ret(sub.isEmpty())
                : startsWith(list, sub)
                    ? ret(true)
                    : sus(() -> hasSubList_(list.tail(), sub));
    }
    private static <T> boolean startsWith(List<T> list, List<T> sub) {
        return startsWith_(list, sub).eval();
    }
    private static <T> TailCall<Boolean> startsWith_(List<T> list, List<T> sub) {
        return sub.isEmpty()
                ? ret(true)
                : list.isEmpty()
                    ? ret(false)
                    : list.head().equals(sub.head())
                        ? sus(() -> startsWith_(list.tail(), sub.tail()))
                        : ret(false);
    }

    // Exercise 8.17
    // The book mentions about a (functional) Map from previous chapters, but we never created one.
    // So I have used Map from jdk.
    public <U> Map<U, List<T>> groupBy(Function<T, U> f) {
        return foldLeft(Map.of(), mult -> t -> {
            if (mult.get(f.apply(t)) == null) { mult.put(f.apply(t), list(t)); }
            else { mult.put(f.apply(t), mult.get(f.apply(t)).cons(t)); }
            return mult;
        });
    }

    // Exercise 8.18
    // Attempt 1
    // Problems:
    //      1. Use of new Tuple<>(null, null)
    //      2. Recursive but non-stack-safe
    public static <T, U> List<U> unfold1(T t, Function<T, Result<Tuple<U, T>>> f) {
        return f.apply(t).isSuccess()
                ? unfold1(f.apply(t).getOrElse(new Tuple<>(null, null))._2, f).cons(f.apply(t).getOrElse(new Tuple<>(null, null))._1)
                : list();
    }
    // Attempt 2
    // Same as attempt 1 but with accumulator
    // Problems:
    //      1. Use of new Tuple<>(null, null)
    //      2. Recursive but non-stack-safe
    public static <T, U> List<U> unfold2(T t, Function<T, Result<Tuple<U, T>>> f) {
        return unfold_(list(), t, f).reverse();
    }
    public static <T, U> List<U> unfold_(List<U> lu, T t, Function<T, Result<Tuple<U, T>>> f) {
        return f.apply(t).isSuccess()
                ? unfold_(lu.cons(f.apply(t).getOrElse(new Tuple<>(null, null))._1), f.apply(t).getOrElse(new Tuple<>(null, null))._2, f)
                : lu;
    }
    // Attempt 3
    // Same as attempt 2 but without new Tuple<>(null, null) and with wrapper types so map()/flatMap() can be used easily.
    // Problems:
    //      1. Recursive but non-stack-safe
    //      2. isSuccess() is redundant because getOrElse() is done. -> see attempt 4
    public static <T, U> List<U> unfold3(T t, Function<T, Result<Tuple<U, T>>> f) {
        return unfold3_(success(list()), f.apply(t), f).getOrElse(list()).reverse();
    }
    public static <T, U> Result<List<U>> unfold3_(Result<List<U>> rlu, Result<Tuple<U, T>> rtut, Function<T, Result<Tuple<U, T>>> f) {
        return rtut.isSuccess()
                ? unfold3_(rtut.flatMap(tut -> rlu.map(lu -> lu.cons(tut._1))), rtut.flatMap(tut -> f.apply(tut._2)), f)
                : rlu;
    }
    // Solution 1 from author
    // Notice use of recursive call from within map(). I didn't think of that.
    // This could be applied to attempt 3, to remove isSuccess() but
    // that would make it infinite-loopy because rwcursive call is outside map()/ flatMap().
    // Problems:
    //      1. Recursive but non-stack-safe
    public static <U, T> List<U> unfoldAuthor1(T t, Function<T, Result<Tuple<U, T>>> f) {
        return f.apply(t).map(rtut -> unfoldAuthor1(rtut._2, f).cons(rtut._1)).getOrElse(list());
    }
    // One interesting thing from above function:
    // For implementing non-stack-safe recursive function, the recursive call can be anywhere in the implementation.

    // Solution 2 from author
    // Details about this function, see below
    public static <U, T> List<U> unfold(T t, Function<T, Result<Tuple<U, T>>> f) {
        return unfold(list(), t, f).eval().reverse();
    }
    private static <U, T> TailCall<List<U>> unfold(List<U> acc, T t, Function<T, Result<Tuple<U, T>>> f) {
        Result<TailCall<List<U>>> result = f.apply(t).map(rt -> sus(() -> unfold(acc.cons(rt._1), rt._2, f)));
        return result.getOrElse(ret(acc));
    }
    // Question 1: This looks like a new pattern of using TailCall.
    // Normally we do: return <expression> ? sus(() -> <recursive-call>) : ret(<value>);
    // Is this equivalent to that?
    // -> Yes, it is. Mainly because there is Result and we do getOrElse() as the last thing.
    // This means when result is success return what's inside result, otherwise return ret(acc).
    // This is equivalent to: return result.isSuccess() ? result.get() : ret(acc)
    // Notice that result.get() is not possible and it actually means that return what's inside result which is sus(() -> unfold(..)) i.e. a recursive call.
    // This makes it same as familiar TailCall pattern.
    // Question 2: There seems to be too much happening after sus() or ret().
    // Is this really tail-recursive?
    // -> If you see the answer to above question, result.getOrElse() is equivalent to result.isSuccess() ? result.get() : ret(acc)
    // That makes it tail-recursive.
    // One interesting thing from above implementation:
    // The TailCall pattern for implementing stack-safe recursive functions can be differently written when dealing with Result<TailCall>-type.
    // The last call should be result.getOrElse(ret(..)) and the result can be achieved in any way as long as TailCall inside is sus(() -> <recursive-call>)

    // Exercise 8.19
    public static List<Integer> range(int start, int end) {
        return unfold(start, i -> i < end ? success(new Tuple<>(i, i + 1)) : empty());
    }

    // Exercise 8.20
    // Question: why is there _1 in Author's solution
    public boolean exists(Function<T, Boolean> f) {
        return foldLeft(this, false, true, b -> t -> b || f.apply(t));
    }

    public boolean forAll(Function<T, Boolean> p) {
        return foldLeft(this,true, false, x -> y -> x && p.apply(y));
    }
    public boolean forAll2(Function<T, Boolean> p) {
        return !exists(x -> !p.apply(x));
    }

    // Exercise 8.22
    // My first solution
    // Problems with this implementation: recursive, stack-unsafe
    public List<List<T>> divideMySol1(int depth) {
        if (depth == 0) {
            return list(this);
        } else {
            Tuple<List<T>, List<T>> tu = splitAt(length() / 2);
            return concat(tu._1.divideMySol1(depth - 1), tu._2.divideMySol1(depth - 1));
        }
    }
    // My solution 2
    // Stack-safe
    public List<List<T>> divideMySol2(int depth) {
        return divideMySol2_(list(this), depth).eval();
    }
    private static <T> TailCall<List<List<T>>> divideMySol2_(List<List<T>> acc, int depth) {
        if (depth == 0) {
            return ret(acc);
        } else {
            Function<List<T>, Function<List<List<T>>, List<List<T>>>> f =
                    l -> ll -> {
                        Tuple<List<T>, List<T>> tu = l.splitAt(l.length() / 2);
                        return ll.cons(tu._2).cons(tu._1);
                    };
            return sus(() -> divideMySol2_(acc.foldRight(list(), f ), depth -1));
        }
    }
    // Author's solution
    public List<List<T>> divide(int depth) {
        return this.isEmpty()
                ? list(this)
                : divide(list(this), depth);
    }
    private List<List<T>> divide(List<List<T>> list, int depth) {
        return list.head().length() < depth || depth < 2
                ? list
                : divide(list.flatMap(x -> x.splitListAt(x.length() / 2)), depth / 2);
    }
    public List<List<T>> splitListAt(int i) {
        return splitListAt(list(), this.reverse(), i).eval();
    }
    private TailCall<List<List<T>>> splitListAt(List<T> acc, List<T> list, int i) {
        return i == 0 || list.isEmpty()
                ? ret(List.list(list.reverse(), acc))
                : sus(() -> splitListAt(acc.cons(list.head()), list.tail(), i - 1));
    }
    // 3 differences in my solution and author's solution:
    //  1: Understanding 'depth'
    //  -> I thought this is number of times the list is to be split.
    //  But in fact this is number of sub-lists to create.
    //  2: Use of flatMap()
    //  -> I used splitAt() with foldRight() instead I could use flatMap()
    //  map() and flatMap() on list could have been used to transform each element into a List
    //  3: No need of stack-safe
    //  -> Note that you don’t need to make this method stack-safe because
    //  the number of recursion steps will only be log(length).
    //  In other words, you’ll never have enough heap memory
    //  to hold a list long enoughto cause a stack overflow.

    public<U> Result<U> parFoldLeft(ExecutorService es, U identity, Function<U, Function<T, U>> fn, Function<U, Function<U, U>> m) {
        List<List<T>> lists = divide(1024);
        try {
            List<U> list = lists.map(l -> es.submit(() -> l.foldLeft(identity, fn))).map(f -> { // f is Future
                try {
                    return f.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            });
            return success(list.foldLeft(identity, m));
        } catch(Exception e) {
            return failure(e);
        }
    }

    public <U> Result<List<U>> parMap(ExecutorService es, Function<T, U> g) {
        try {
            List<U> list = map(t -> es.submit(() -> g.apply(t))).map(f -> { // f is Future
                try {
                    return f.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            });
            return success(list);
        } catch(Exception e) {
            return failure(e);
        }
    }
}