package fpinjava.chapter3;

import fpinjava.chapter2.Function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;


/**
 * A set of utilities to deal with collections.
 * It uses java.util.List
 */
public class CollectionUtilities {

    public static <T, U> List<U> map(List<T> list, Function<T, U> f) {
        List<U> newList = new ArrayList<>();
        for (T t: list) {
            newList.add(f.apply(t));
        }
        return Collections.unmodifiableList(newList);
    }

    public static <T> List<T > list() {
        return Collections.emptyList();
    }

    public static <T> List<T > list(T t) {
        return Collections.singletonList(t);
    }

    // We do 'new ArrayList' because we want to create a copy.
    // Otherwise changing elements in the input List might affect the elements from the output List.
    public static <T> List<T > list(List<T> ts) {
        return Collections.unmodifiableList(new ArrayList<>(ts));
    }

    // Note that this method may be called with an array as its argument. In such a case, the resulting list is backed by the original array.
    // As a consequence, changing an element of the array would change the corresponding element of the resulting list.
    // That's why a copy is necessary.
    @SafeVarargs
    public static <T> List<T > list(T... t) {
        return Collections.unmodifiableList(Arrays.asList(Arrays.copyOf(t, t.length)));
    }

    // This is a private method. Only for convenience. The list is kept modifiable on purpose.
    private static <T> List<T> copy(List<T> ts) {
        return new ArrayList<>(ts);
    }

    public static <T> T head(List<T> ts) {
        if(ts.isEmpty()) { throw new RuntimeException("List is empty"); }
        return ts.get(0);
    }

    // We could 'return list(newList)' but that would unnecessarily create at extra copy.
    // And the original List is only accessible from this method. So there is no question of modifying it outside.
    public static <T> List<T> tail(List<T> ts) {
        if(ts.isEmpty()) { throw new RuntimeException("List is empty"); }
        List<T> newList = copy(ts);
        newList.remove(0);
        return Collections.unmodifiableList(newList);
    }

    public static <T> List<T> append(List<T> list, T t) {
        List<T> newList = copy(list);
        newList.add(t);
        return Collections.unmodifiableList(newList);
    }

    // Exercise 3.5
    // My solution  1
    // Instead of BiFunction<Integer, Integer, Integer>, BinaryOperator<Integer> could be used as well.
    // Imperative
    public static int foldIntsImperMySol(List<Integer> list, int startingValue, BiFunction<Integer, Integer, Integer> f) {
        for (int n: list) {
            startingValue = f.apply(startingValue, n);
        }
        return startingValue;
    }
    // My solution 2
    // Functional 1
    public static int foldIntsFuncMySol(List<Integer> list, int startingValue, BiFunction<Integer, Integer, Integer> f) {
        return list.isEmpty()
                ? startingValue
                : foldIntsFuncMySol(tail(list), f.apply(head(list), startingValue), f);
    }

    public static Integer fold(List<Integer> is, Integer identity, Function<Integer, Function<Integer, Integer>> f) {
        int result = identity;
        for (Integer i : is) {
            result = f.apply(result).apply(i);
        }
        return result;
    }

    // Exercise 3.6
    public static <T, U> U foldLeftImper(List<T> ts, U identity, Function<U, Function<T, U>> f) {
        U result = identity;
        for (T t : ts) {
            result = f.apply(result).apply(t);
        }
        return result;
    }
    public static <T, U> U foldLeftRecurs(List<T> ts, U identity, Function<U, Function<T, U>> f) {
        return ts.isEmpty()
                ? identity
                : foldLeftRecurs(tail(ts), f.apply(identity).apply(head(ts)), f);
    }

    // Exercise 3.7
    public static <T, U> U foldRightImper(List<T> ts, U identity, Function<T, Function<U, U>> f) {
        U result = identity;
        for (int i = ts.size()-1; i >= 0 ; i--) {
            result = f.apply(ts.get(i)).apply(result);
        }
        return result;
    }

    public static <T, U> U foldRightRecurs1(List<T> ts, U identity, Function<T, Function<U, U>> f) {
        return foldRightRecurs2(ts, 0, identity, f);
    }

    public static <T, U> U foldRightRecurs2(List<T> ts, int i, U accumulator, Function<T, Function<U, U>> f) {
        return i == ts.size()
        ? accumulator
        : f.apply(ts.get(i)).apply(foldRightRecurs2(ts, i + 1, accumulator, f));
    }

    // Exercise 3.8
    public static <T, U> U foldRightRecurs(List<T> ts, U identity, Function<T, Function<U, U>> f) {
        return ts.isEmpty()
                ? identity
                : f.apply(head(ts)).apply(foldRightRecurs(tail(ts), identity, f));
    }

    // foldLeftRecurs() and foldRightRecurs():
    // To convert foldLeftImper() into foldLeftRecurs():
    //     left-fold first applies the function to identity/intermediate-result and then head of the list.
    //     Note that intermediate result is obtained by applying left-fold to partial list.
    //     Therefore, we do foldLeftRecurs(tail(ts), f.apply(identity).apply(head(ts)), f);
    //     Which means apply foldLeft() on value obtained by applying f.
    // To convert foldRightImper() into foldRightRecurs():
    //     right-fold first applies the function to last-element of list and then identity/intermediate-result.
    //     Note that intermediate result is obtained by applying left-fold to partial list.
    //     Therefore, we do f.apply(head(ts)).apply(foldRightRecurs(tail(ts), identity, f));
    //     Which means apply f to value obtained by applying foldRight().

    // Method references:
    //     x::y means (input) -> x.y(input) where x is object and input is set of arguments.
    //     X::y means (input) -> X.y(input) where X is class and input is set of arguments OR
    //                (x) -> x.y() where x is an object which is available as input for the lambda.

    // Exercise 3.9
    // My solution 1
    public static <T> List<T> reverseFoldRightMySol(List<T> ts) {
        return foldRightRecurs(ts, new ArrayList<T>(), t -> listT -> { listT.add(t); return listT; });
    }
    // My solution 2
    public static <T> List<T> reverseFoldLeftMySol(List<T> ts) {
        return foldLeftImper(ts, new ArrayList<T>(), list -> t -> { list.add(0, t); return list; });
    }
    // My solution 3
    public static <T> List<T> reverseFoldRightMySol2(List<T> ts) {
        return foldRightRecurs(ts, list(), list -> t -> append(t, list));
    }
    public static <T> List<T> reverseFoldLeft(List<T> ts) {
        return foldLeftImper(ts, list(), list -> t -> prepend(t, list));
        // or simply
        // return foldLeftImper(ts, list(), list -> t ->
        //                                      foldLeftImper(list, list(t), list1 -> list2 -> append(list1, list2)));
    }
    public static <T> List<T> prepend(T t, List<T> list) {
        return foldLeftImper(list, list(t), list1 -> list2 -> append(list1, list2));
    }

    public static <T, U> List<U> mapFoldLeft(List<T> list, Function<T, U> f) {
        return foldLeftImper(list, list(), newList -> t -> append(newList, f.apply(t)));
    }

    public static <T, U> List<U> mapFoldRight(List<T> list, Function<T, U> f) {
        return foldRightRecurs(list, list(), t -> newList -> prepend(f.apply(t), newList));
    }

    // Exercise 3.12
    public static List<Integer> rangeImper(int start, int end) {
        List<Integer> list = list();
        int i = start;
        while (i <= end) {
            list = append(list, i);
            i = i + 1;
        }
        return list;
    }

    public static List<Integer> rangeRecurs(int start, int end) {
        if(start > end) throw new RuntimeException("start > end");
        return rangeRecursSub(start, end, list(), n -> n + 1);
    }

    private static List<Integer> rangeRecursSub(int start, int end, List<Integer> list, Function<Integer, Integer> f) {
        return start > end
                ? list
                : rangeRecursSub(start + 1, end, append(list, start), f);
    }

    // Exercise 3.14
    public static List<Integer> rangeRecurs2(int start, int end) {
        return start > end
                ? list()
                : prepend(start, rangeRecurs2(start + 1, end));
    }

    public static <T> List<T> unfoldImper(T seed, Function<T, T> f, Function<T, Boolean> p) {
        List<T> list = list();
        T i = seed;
        while (p.apply(i)) {
            list = append(list, i);
            i = f.apply(i);
        }
        return list;
    }

    public static <T> List<T> unfoldRecurs(T seed, Function<T, T> f, Function<T, Boolean> p) {
        return p.apply(seed)
                ? prepend(seed, unfoldRecurs(f.apply(seed), f, p))
                : list();
    }

    // Returns List of n T instances
    // stack-safe version is present in TailCallExamples
    public static <T> List<T> iterate(T seed, Function<T, T> f, int n) {
        return (n == 0)
                ? list()
                : prepend(f.apply(seed), iterate(f.apply(seed), f, n - 1));
    }
}

