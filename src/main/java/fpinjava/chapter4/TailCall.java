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