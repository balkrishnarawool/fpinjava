package fpinjava.chapter4;

import fpinjava.chapter2.Function;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Memoizer<T, U> {
    private Map<T, U> map = new ConcurrentHashMap<>();

    private Memoizer() { }

    public static <T, U> Function<T, U> memoize(Function<T, U> f) {
        return new Memoizer<T, U>().doMemoize(f);
    }

    private Function<T, U> doMemoize(Function<T, U> f) {
        return t -> map.computeIfAbsent(t, t1 -> f.apply(t1));
    }
}