package fpinjava.chapter3;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CollectionUtilitiesTest {

    private final List<Integer> numbers = List.of(1, 2, 3, 4, 5);

    @Test
    public void testMap() {
        assertEquals( CollectionUtilities.map(numbers , n -> Integer.toString(n)), List.of("1", "2", "3", "4", "5"));
    }

    @Test
    public void testHead() {
        assertEquals( CollectionUtilities.head(numbers), 1);
    }

    @Test
    public void testTail() {
        assertEquals( CollectionUtilities.tail(numbers), List.of(2, 3, 4, 5));
    }

    @Test
    public void testFoldIntsImper() {
        assertEquals( CollectionUtilities.foldIntsImperMySol(numbers, 0, (n1, n2) -> n1 + n2), 15);
    }

    @Test
    public void testFoldIntsFunc() {
        assertEquals( CollectionUtilities.foldIntsFuncMySol(numbers, 0, (n1, n2) -> n1 + n2), 15);
    }

    @Test
    public void testFold() {
        assertEquals( CollectionUtilities.fold(numbers, 0, n1 -> n2 -> n1 + n2), 15);
    }

    @Test
    public void testFoldLeftImpr() {
        assertEquals( CollectionUtilities.foldLeftImper(numbers, "0", s -> i -> "(" + s + " + " + i + ")"), "(((((0 + 1) + 2) + 3) + 4) + 5)");
    }

    @Test
    public void testFoldLeftRecurs() {
        assertEquals( CollectionUtilities.foldLeftRecurs(numbers, "0", s -> i -> "(" + s + " + " + i + ")"), "(((((0 + 1) + 2) + 3) + 4) + 5)");
    }

    @Test
    public void testFoldRightImper() {
        assertEquals( CollectionUtilities.foldRightImper(numbers , "0", i -> s -> "(" + i + " + " + s + ")"), "(1 + (2 + (3 + (4 + (5 + 0)))))");
    }

    @Test
    public void testFoldRightRecurs() {
        assertEquals( CollectionUtilities.foldRightRecurs(numbers , "0", i -> s -> "(" + i + " + " + s + ")"), "(1 + (2 + (3 + (4 + (5 + 0)))))");
    }

    @Test
    public void testReverse() {
        assertEquals( CollectionUtilities.reverseFoldRightMySol(numbers), List.of(5, 4, 3, 2, 1));
    }

    @Test
    public void testReverseFoldLeftMySol() {
        assertEquals( CollectionUtilities.reverseFoldLeftMySol(numbers), List.of(5, 4, 3, 2, 1));
    }

    @Test
    public void testReverseFoldLeft() {
        assertEquals( CollectionUtilities.reverseFoldLeft(numbers), List.of(5, 4, 3, 2, 1));
    }

    @Test
    public void testMapFoldLeft() {
        assertEquals( CollectionUtilities.mapFoldLeft(numbers , n -> Integer.toString(n)), List.of("1", "2", "3", "4", "5"));
    }

    @Test
    public void testMapFoldRight() {
        assertEquals( CollectionUtilities.mapFoldRight(numbers , n -> Integer.toString(n)), List.of("1", "2", "3", "4", "5"));
    }

    @Test
    public void testRangeImper() {
        assertEquals( CollectionUtilities.rangeImper(1, 5), numbers);
    }

    @Test
    public void testRangeRecurs() {
        assertEquals( CollectionUtilities.rangeRecurs(1, 5), numbers);
    }

    @Test
    public void testRangeRecurs2() {
        assertEquals( CollectionUtilities.rangeRecurs2(1, 5), numbers);
    }

    @Test
    public void testUnfoldImper() {
        assertEquals( CollectionUtilities.unfoldImper(1, n -> n + 1, n -> n <= 5), numbers);
    }

    @Test
    public void testUnfoldRecurs() {
        assertEquals( CollectionUtilities.unfoldRecurs(1, n -> n + 1, n -> n <= 5), numbers);

        String s = "080019025420180301";
        s = s.replaceFirst ("^0*", "");
        System.out.println(s);
    }

}
