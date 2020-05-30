package fpinjava.chapter4;

import fpinjava.chapter2.Function;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.List;

import static fpinjava.chapter3.CollectionUtilities.list;
import static fpinjava.chapter3.CollectionUtilities.map;

import static fpinjava.chapter4.TailCallExamples.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TailCallExamplesTest {

    private final int[] fibs = {0, 1, 1, 2, 3, 5, 8, 13, 21, 34};
    private final List<Integer> numbers = List.of(1, 2, 3, 4, 5);

    @Test
    public void testFib1() {
        for (int i = 0; i < 10; i++) {
            assertEquals(fibs[i], fib1(BigInteger.valueOf(i)).intValue());
        }
    }

    @Test
    public void testFib2() {
        for (int i = 0; i < 10; i++) {
            assertEquals(fibs[i], fib2(BigInteger.valueOf(i)).intValue());
        }
    }

    @Test
    public void testFoldLeft() {
        assertEquals("(((((0 + 1) + 2) + 3) + 4) + 5)", foldLeft(numbers, "0", s -> i -> "(" + s + " + " + i + ")"));
    }

    @Test
    public void testFoldRight() {
        assertEquals("(1 + (2 + (3 + (4 + (5 + 0)))))", foldRight(numbers , "0", i -> s -> "(" + i + " + " + s + ")"));
    }

    @Test
    public void testRange() {
        assertEquals(numbers, range(1, 6));
    }

    @Test
    public void testComposeAllAndThenAll() {
        final Function<Integer, Integer> add2 = n -> n + 2;
        final Function<Integer, Integer> square = n -> n * n;
        final Function<Integer, Integer> cube = n -> n * n * n;

        final List<Function<Integer, Integer>> list = list(add2, square, cube);

        assertEquals(66, composeAllNaive(list).apply(2));
        assertEquals(66, composeAllStackSafe(list).apply(2));
        assertEquals(66, composeAll(list).apply(2));

        Function<Integer, Integer> add = n -> n + 1;
        assertEquals(500, composeAll(map(range(0, 500), x -> add)).apply(0));
        // This will give StackOverflowError for recursive implementation of foldRight.
        // And will take long time when foldRight is stack-safe. So it is commented.
        // assertEquals(10000, composeAll(map(range(0, 10000), x -> add)).apply(0));

        assertEquals(500, composeAllViaFoldLeft(map(range(0, 500), x -> add)).apply(0));
        assertEquals(500, composeAllViaFoldRight(map(range(0, 500), x -> add)).apply(0));
        assertEquals(500, andThenAllViaFoldLeft(map(range(0, 500), x -> add)).apply(0));
        assertEquals(500, andThenAllViaFoldRight(map(range(0, 500), x -> add)).apply(0));
    }

    @Test
    public void testFibo() {
        assertEquals("0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55", fibo(10));
        assertEquals("0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55", fiboCorecursive(10));
    }
}
