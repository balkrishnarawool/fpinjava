package fpinjava.chapter4;

import fpinjava.chapter3.CollectionUtilities;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.List;

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
    public void testRangeImper() {
        assertEquals(numbers, range(1, 5));
    }

}
