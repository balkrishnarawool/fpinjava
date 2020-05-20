package fpinjava.chapter4;

import fpinjava.chapter2.Function;
import fpinjava.chapter3.CollectionUtilities;

import java.math.BigInteger;
import java.util.List;

import static fpinjava.chapter2.Function.compose;
import static fpinjava.chapter2.Function.identity;
import static fpinjava.chapter3.CollectionUtilities.*;
import static fpinjava.chapter4.TailCall.ret;
import static fpinjava.chapter4.TailCall.sus;

public abstract class TailCallExamples<T> {

    public static BigInteger fib1(BigInteger n) {
        return fib1_(n, BigInteger.ZERO, BigInteger.ONE);
    }

    // Not stack-safe
    public static BigInteger fib1_(BigInteger n, BigInteger n0, BigInteger n1) {
        if (n.equals(BigInteger.ZERO)) {
            return BigInteger.ZERO;
        }
        if (n.equals(BigInteger.ONE)) {
            return n1;
        }
        return fib1_(n.subtract(BigInteger.ONE), n1, n0.add(n1));
    }

    public static BigInteger fib2(BigInteger n) {
        return fib2_(n, BigInteger.ZERO, BigInteger.ONE).eval();
    }

    // Stack-safe
    private static TailCall<BigInteger> fib2_(BigInteger n, BigInteger n0, BigInteger n1) {
        if (n.equals(BigInteger.ZERO)) {
            return ret(BigInteger.ZERO);
        }
        if (n.equals(BigInteger.ONE)) {
            return ret(n1);
        }
        return sus(() -> fib2_(n.subtract(BigInteger.ONE), n1, n0.add(n1)));
    }

    public static <T, U> U foldLeft(List<T> ts, U identity,
                                    Function<U, Function<T, U>> f) {
        return foldLeft_(ts, identity, f).eval();
    }

    public static <T, U> TailCall<U> foldLeft_(List<T> ts, U identity,
                                    Function<U, Function<T, U>> f) {
        return (ts.isEmpty())
                ? ret(identity)
                : sus(() -> foldLeft_(tail(ts), f.apply(identity).apply(head(ts)), f));
    }

    public static List<Integer> range(Integer start, Integer end) {
        return range_(start, end, list()).eval();
    }

    public static TailCall<List<Integer>> range_(Integer start, Integer end, List<Integer> list) {
        return (start.equals(end))
                ? ret(list)
                : sus( () -> range_(start + 1, end, append(list, start)));
    }

    public static <T, U> U foldRight(List<T> ts, U identity, Function<T, Function<U, U>> f) {
        return foldRight_(reverseFoldLeft(ts), identity, f).eval(); // reverseFoldLeft() is simply reverse() using left-folding
    }

    public static <T, U> TailCall<U> foldRight_(List<T> ts, U accumulator, Function<T, Function<U, U>> f) {
        return (ts.isEmpty())
                ? ret(accumulator)
                : sus( () ->  foldRight_(tail(ts), f.apply(head(ts)).apply(accumulator), f));
    }

    // Exercise 4.6
    // This is not tail-recursive: My solution 1
    public static <T> Function<T, T> composeAllNaive(List<Function<T, T>> list) {
        return list.isEmpty()
                ? identity()
                : compose(head(list), composeAllNaive(tail(list)));
    }

    // Stack safe : My solution 2
    public static <T> Function<T, T> composeAllStackSafe(List<Function<T, T>> list) {
        return composeAllStackSafe_(reverseFoldLeft(list), identity()).eval();
    }

    public static <T> TailCall<Function<T, T>> composeAllStackSafe_(List<Function<T, T>> list, Function<T, T> accumulator) {
        return list.isEmpty()
                ? ret(accumulator)
                : sus(() -> composeAllStackSafe_(tail(list), compose(head(list), accumulator)));
    }

    // From above two solutions it is clear that composeAll requires right folding
    public static <T> Function<T, T> composeAll(List<Function<T, T>> list) {
//        return CollectionUtilities.foldRightRecurs1(list, identity(), f1 -> f2 -> f1.compose(f2));// For lists with around 8500 elements, this will throw StackOverflowError
        return foldRight(list, identity(), f1 -> f2 -> f1.compose(f2));// So use this.
    }

    public static <T> Function<T, T> composeAllViaFoldLeft(List<Function<T, T>> list) {
        return t -> foldLeft(reverseFoldLeft(list), t, t1 -> f -> f.apply(t1));
        // Question: Why is 't' used here for identity?
        // That is because when the list is empty we just want to give the same input back.
    }

    public static <T> Function<T, T> composeAllViaFoldRight(List<Function<T, T>> list) {
        return t -> foldRight(list, t, f -> f::apply); // f::apply = t1 -> f.apply(t1)
    }

    public static <T> Function<T, T> andThenAllViaFoldLeft(List<Function<T, T>> list) {
        return t -> foldLeft(list, t, t1 -> f -> f.apply(t1));
    }

    public static <T> Function<T, T> andThenAllViaFoldRight(List<Function<T, T>> list) {
        return t -> foldRight(reverseFoldLeft(list), t, f -> f::apply);
    }

}