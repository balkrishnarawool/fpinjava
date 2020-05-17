package fpinjava.chapter2;

import fpinjava.chapter1.Tuple;

import java.util.function.Function;

/**
 * A set of utilities to deal with functions.
 * It uses java.util.function.Function
 */
public class FunctionUtilities {

    // Exercise 2.1, 2.2
    // Order of composition is: f2 and then f1.
    // The method is called higherCompose() because it creates a higher-order-function by composing functions.
    public static <T, U, V> Function<T, U> compose(Function<V, U> f1, Function<T, V> f2) {
        return t -> f1.apply(f2.apply(t));
    }

// Exercise  2.5
// We could do this (a Function instance that composes two give functions)
// but then the parameterized types need to be added to the class i.e. Test<T,U,V>
//    final Function<
//            Function<V, U>, // --> f1
//              Function<
//                Function<T, V>, // --> f2
//                  Function< T, U>>> // --> result/ composed-function
//    higherCompose = f1 -> f2 -> t -> f1.apply(f2.apply(t));

    // Exercise  2.5
    // The method is called higherCompose() because it creates a higher-order-function
    // to which you have to pass other functions.
    public static final <T, U, V>
      Function<
              Function<V, U>, // --> f1
              Function<
                      Function<T, V>, // --> f2
                      Function<T, U>>> // --> result of applying both (composing) function
        higherCompose() { return f1 -> f2 -> t -> f1.apply(f2.apply(t)); }

    // Exercise 2.6
    // Order of composition is: f1 and then f2.
    public static <T, U, V> Function<T, U> andThen(Function<T, V> f1, Function<V, U> f2) {
        return t -> f2.apply(f1.apply(t));
    }

    // Exercise 2.6
    // The method is called higherAndThen() because it creates a higher-order-function
    // to which we have to pass other functions.
    public static final <T, U, V>
    Function<
            Function<T, V>, // --> f1
            Function<
                    Function<V, U>, // --> f2
                    Function<T, U>>> // --> result of applying both (composing) function
        higherAndThen() { return f1 -> f2 -> t -> f2.apply(f1.apply(t)); }

    // Exercise 2.7
    public static <A, B, C> Function<B, C> partialA(A a, Function<A, Function<B, C>> f) {
        return f.apply(a);
    }

    // Exercise 2.8
    public static <A, B, C> Function<A, C> partialB(B b, Function<A, Function<B, C>> f) {
        return a -> f.apply(a).apply(b);
    }

    // Exercise 2.9
    public static <A, B, C, D> String func(A a, B b, C c, D d) {
        return String.format("%s, %s, %s, %s", a, b, c, d);
    }

    public static <A, B, C, D> Function<A, Function<B, Function<C, Function<D, String>>>> funcCurried() {
        return a -> b -> c -> d -> String.format("%s, %s, %s, %s", a, b, c, d);
    }

    // Exercise 2.10
    public static <A, B, C> Function<A, Function<B, C>> curry(Function<Tuple<A, B>, C> f) {
        return a -> b -> f.apply(new Tuple<>(a, b));
    }

    // Exercise 2.11
    public static <T, U, V> Function<U, Function<T, V>> reverseArgs(Function<T, Function<U, V>> f) {
        return u -> t -> f.apply(t).apply(u);
    }


}
