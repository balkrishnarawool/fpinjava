package fpinjava.chapter4;

import fpinjava.chapter2.Function;

import java.math.BigInteger;
import java.util.List;

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
        return (start > end)
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
}