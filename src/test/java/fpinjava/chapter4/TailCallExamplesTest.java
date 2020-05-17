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
            assertEquals(fib1(BigInteger.valueOf(i)).intValue(), fibs[i]);
        }
    }

    @Test
    public void testFib2() {
        for (int i = 0; i < 10; i++) {
            assertEquals(fib2(BigInteger.valueOf(i)).intValue(), fibs[i]);
        }
    }

    @Test
    public void testFoldLeft() {
        assertEquals( foldLeft(numbers, "0", s -> i -> "(" + s + " + " + i + ")"), "(((((0 + 1) + 2) + 3) + 4) + 5)");
    }

    @Test
    public void testRangeImper() {
        assertEquals( range(1, 5), numbers);
    }

}
