package fpinjava.chapter3;

import org.junit.jupiter.api.Test;

import java.util.List;

import static fpinjava.chapter3.CollectionUtilities.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CollectionUtilitiesTest {

    private final List<Integer> numbers = List.of(1, 2, 3, 4, 5);

    @Test
    public void testMap() {
        assertEquals(List.of("1", "2", "3", "4", "5"), map(numbers , n -> Integer.toString(n)));
    }

    @Test
    public void testHead() {
        assertEquals(1, head(numbers));
    }

    @Test
    public void testTail() {
        assertEquals(List.of(2, 3, 4, 5), tail(numbers));
    }

    @Test
    public void testFoldIntsImper() {
        assertEquals(15, foldIntsImperMySol(numbers, 0, (n1, n2) -> n1 + n2));
    }

    @Test
    public void testFoldIntsFunc() {
        assertEquals(15, foldIntsFuncMySol(numbers, 0, (n1, n2) -> n1 + n2));
    }

    @Test
    public void testFold() {
        assertEquals(15, fold(numbers, 0, n1 -> n2 -> n1 + n2));
    }

    @Test
    public void testFoldLeftImpr() {
        assertEquals("(((((0 + 1) + 2) + 3) + 4) + 5)", foldLeftImper(numbers, "0", s -> i -> "(" + s + " + " + i + ")"));
    }

    @Test
    public void testFoldLeftRecurs() {
        assertEquals("(((((0 + 1) + 2) + 3) + 4) + 5)", foldLeftRecurs(numbers, "0", s -> i -> "(" + s + " + " + i + ")"));
    }

    @Test
    public void testFoldRightImper() {
        assertEquals("(1 + (2 + (3 + (4 + (5 + 0)))))", foldRightImper(numbers , "0", i -> s -> "(" + i + " + " + s + ")"));
    }

    @Test
    public void testFoldRightRecurs() {
        assertEquals("(1 + (2 + (3 + (4 + (5 + 0)))))", foldRightRecurs(numbers , "0", i -> s -> "(" + i + " + " + s + ")"));
    }

    @Test
    public void testReverse() {
        assertEquals(List.of(5, 4, 3, 2, 1), reverseFoldRightMySol(numbers));
    }

    @Test
    public void testReverseFoldLeftMySol() {
        assertEquals(List.of(5, 4, 3, 2, 1),  reverseFoldLeftMySol(numbers));
    }

    @Test
    public void testReverseFoldLeft() {
        assertEquals(List.of(5, 4, 3, 2, 1), reverseFoldLeft(numbers));
    }

    @Test
    public void testMapFoldLeft() {
        assertEquals(List.of("1", "2", "3", "4", "5"),  mapFoldLeft(numbers , n -> Integer.toString(n)));
    }

    @Test
    public void testMapFoldRight() {
        assertEquals(List.of("1", "2", "3", "4", "5"),  mapFoldRight(numbers , n -> Integer.toString(n)));
    }

    @Test
    public void testRangeImper() {
        assertEquals(numbers, rangeImper(1, 5));
    }

    @Test
    public void testRangeRecurs() {
        assertEquals(numbers, rangeRecurs(1, 5));
    }

    @Test
    public void testRangeRecurs2() {
        assertEquals(numbers, rangeRecurs2(1, 5));
    }

    @Test
    public void testUnfoldImper() {
        assertEquals(numbers, unfoldImper(1, n -> n + 1, n -> n <= 5));
    }

    @Test
    public void testUnfoldRecurs() {
        assertEquals(numbers, unfoldRecurs(1, n -> n + 1, n -> n <= 5));
    }

}
