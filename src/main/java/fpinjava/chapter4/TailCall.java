package fpinjava.chapter4;

import java.util.function.Supplier;

public abstract class TailCall<T> {

    public abstract T eval();
    public abstract TailCall<T> resume();
    public abstract boolean isSuspend();

    private static class Return<T> extends TailCall<T> {
        private T value;

        private Return(T value) {
            this.value = value;
        }
        public T eval() {
            return value;
        }
        public TailCall<T> resume() {
            throw new RuntimeException("resume() called on Return");
        }
        public boolean isSuspend() {
            return false;
        }
    }

    private static class Suspend<T> extends TailCall<T> {
        private Supplier<TailCall<T>> supplier;

        private Suspend(Supplier<TailCall<T>> supplier) {
            this.supplier = supplier;
        }
        public T eval() {
            TailCall<T> tc = supplier.get();
            while(tc.isSuspend()) {
                tc = tc.resume();
            }
            return tc.eval();
        }
        public TailCall<T> resume() {
            return supplier.get();
        }
        public boolean isSuspend() {
            return true;
        }
    }

    public static <T> Return<T> ret(T value) { return new Return<>(value); }
    public static <T> Suspend<T> sus(Supplier<TailCall<T>> supplier) { return new Suspend<>(supplier); }
}

// Although it might appear that we can use this TailCall API for non-tail-recursive functions to make them stack-safe.
// For example:
//    public static <T, U> U foldRight(List<T> list, U identity, Function<T, Function<U, U>> f) {
//        return list.isEmpty()
//            ? identity
//            : f.apply(list.head()).apply(foldRight(list.tail(), identity, f));
//    }
//
//    public static <T, U> U foldRightStackSafe(List<T> list, U identity, Function<T, Function<U, U>> f) {
//        return foldRightStackSafe_(list, identity, f).eval();
//    }
//
//    public static <T, U> TailCall<U> foldRightStackSafe_(List<T> list, U identity, Function<T, Function<U, U>> f) {
//        return list.isEmpty()
//                ? ret(identity)
//                : sus(() -> ret(f.apply(list.head()).apply(foldRightStackSafe_(list.tail(), identity, f).eval())));
//    }
//
//    public static BigInteger fibo(int n) {
//        return n == 0
//                ? BigInteger.ONE
//                : n == 1
//                ? BigInteger.ONE
//                : fibo(n - 1).add(fibo(n - 2));
//    }
//
//    public static BigInteger fiboStackSafe(int n) {
//        return fiboStackSafe_(n).eval();
//    }
//
//    public static TailCall<BigInteger> fiboStackSafe_(int n) {
//        return n == 0
//                ? ret(BigInteger.ONE)
//                : n == 1
//                ? ret(BigInteger.ONE)
//                : sus(() -> ret(fiboStackSafe_(n - 1).eval().add(fiboStackSafe_(n - 2).eval())));
//    }
// Although, if you see the implementation of Suspend.eval(), you see that it requires a Supplier which can be unfolded in one-direction.
// When we call tc.resume(), we assign it to tc again. So it is important that tc.resume() returns Suspend object or Return with value.
// If Return doesn't have value but recursive call then we are back to stack-unsafe functions.