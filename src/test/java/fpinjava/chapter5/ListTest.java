package fpinjava.chapter5;

import org.junit.jupiter.api.Test;

import static fpinjava.chapter5.List.concat;
import static fpinjava.chapter5.List.concat2;
import static fpinjava.chapter5.List.concatViaFoldLeft;
import static fpinjava.chapter5.List.doubleToString;
import static fpinjava.chapter5.List.flatten;
import static fpinjava.chapter5.List.foldLeftViaFoldRight;
import static fpinjava.chapter5.List.foldRightViaFoldLeft;
import static fpinjava.chapter5.List.lengthViaFoldLeft;
import static fpinjava.chapter5.List.list;
import static fpinjava.chapter5.List.product;
import static fpinjava.chapter5.List.productViaFoldLeft;
import static fpinjava.chapter5.List.reverseViaFoldLeft;
import static fpinjava.chapter5.List.sum;
import static fpinjava.chapter5.List.sumViaFoldLeft;
import static fpinjava.chapter5.List.triple;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ListTest {
    private final List<Integer> l = List.list(1, 2, 3, 4, 5);

    @Test
    public void testToString() {
         assertEquals("[ 1, 2, 3, 4, 5, NIL ]", l.toString());
    }

    @Test
    public void testDrop() {
        assertEquals(List.list(3, 4, 5).toString(), l.drop(2).toString());
    }

    @Test
    public void testDropWhile() {
        assertEquals(List.list(2, 3, 4, 5).toString(), l.dropWhile(i -> i < 2).toString());
    }

    @Test
    public void testConcat() {
        final List<Integer> l2 = list(6, 7, 8, 9, 10);
        assertEquals(List.list(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).toString(), concat(l, l2).toString());
    }

    @Test
    public void testReverse() {
        assertEquals(List.list(5, 4, 3, 2, 1).toString(), l.reverse().toString());
    }

    @Test
    public void testInit() {
        assertEquals(List.list(1, 2, 3, 4).toString(), l.init().toString());
    }

    @Test
    public void testSum() {
        assertEquals(15, sum(l));
    }

    @Test
    public void testProduct() {
        final List<Double> l1 = List.list(1.0, 2.0, 3.0, 4.0, 5.0);
        assertEquals(120.0, product(l1));
    }

    @Test
    public void testFoldRight() { // Right fold traverses from right to left in the list
        assertEquals("12345", l.foldRightStackUnsafe("", n -> s -> n.toString() + s));
        assertEquals("54321", l.foldRightStackUnsafe("", n -> s -> s + n.toString()));
    }

    @Test
    public void testFoldLeft() { // Left fold traverses from left to right in the list
        assertEquals("12345", l.foldLeftStackUnsafe("", s -> n -> s + n.toString()));
        assertEquals("54321", l.foldLeftStackUnsafe("", s -> n -> n.toString() + s));
    }

    @Test
    public void testFoldLeftStackSafe() {
        assertEquals("12345", l.foldLeftStackUnsafe("", s -> n -> s + n.toString()));
        assertEquals("54321", l.foldLeftStackUnsafe("", s -> n -> n.toString() + s));
    }

    @Test
    public void testLength() {
        assertEquals(5, l.length());
    }

    @Test
    public void testSumViaFoldLeft() {
        assertEquals(15, sumViaFoldLeft(l));
    }

    @Test
    public void testProductViaFoldLeft() {
        final List<Double> l1 = List.list(1.0, 2.0, 3.0, 4.0, 5.0);
        assertEquals(120, productViaFoldLeft(l1));
    }

    @Test
    public void testLengthViaFoldLeft() {
        assertEquals(5, lengthViaFoldLeft(l));
    }

    @Test
    public void testReverseViaFoldLeft() {
        assertEquals("[ 5, 4, 3, 2, 1, NIL ]", reverseViaFoldLeft(l).toString());
    }

    @Test
    public void testFoldRightViaFoldLeft() {
        assertEquals("12345", foldRightViaFoldLeft(l, "", s -> n -> s + n.toString()));
        assertEquals("54321", foldRightViaFoldLeft(l, "", s -> n -> n.toString() + s));
    }

    @Test
    public void testFoldLeftViaFoldRight() {
        assertEquals("12345", foldLeftViaFoldRight(l, "", s -> n -> s + n.toString()));
        assertEquals("54321", foldLeftViaFoldRight(l, "", s -> n -> n.toString() + s));
    }

    @Test
    public void testFoldRightStackSafe() {
        assertEquals("12345", l.foldRight("", s -> n -> s + n.toString()));
        assertEquals("54321", l.foldRight("", s -> n -> n.toString() + s));
    }

    @Test
    public void testConcat2() {
        final List<Integer> l2 = List.list(6, 7, 8, 9, 10);
        assertEquals("[ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, NIL ]", concat2(l, l2).toString());
    }

    @Test
    public void testConcat3() {
        final List<Integer> l2 = List.list(6, 7, 8, 9, 10);
        assertEquals("[ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, NIL ]", concatViaFoldLeft(l, l2).toString());
    }

    @Test
    public void testFlatten() {
        final List<Integer> l2 = List.list(6, 7, 8, 9, 10);
        final List<Integer> l3 = List.list(11, 12, 13, 14, 15);
        assertEquals("[ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, NIL ]", flatten(list(l, l2, l3)).toString());
    }

    @Test
    public void testTriple() {
        assertEquals("[ 3, 6, 9, 12, 15, NIL ]", triple(l).toString());
    }

    @Test
    public void testDoubleToString() {
        final List<Double> l1 = List.list(1.0, 2.0, 3.0, 4.0, 5.0);
        assertEquals("[ 1.0, 2.0, 3.0, 4.0, 5.0, NIL ]", doubleToString(l1).toString());
    }

    @Test
    public void testMap() {
        assertEquals("[ 2, 4, 6, 8, 10, NIL ]", l.map(n -> n * 2).toString());
        assertEquals("[ NIL ]", list().map(o -> o.toString()).toString());
    }

    @Test
    public void testFilter() {
        assertEquals("[ 3, 4, 5, NIL ]", l.filter( n -> n > 2).toString());
    }

    @Test
    public void testFlatMap() {
        assertEquals("[ 1, 2, 3, 4, 2, 4, 6, 8, 3, 6, 9, 12, 4, 8, 12, 16, 5, 10, 15, 20, NIL ]", l.flatMap(n -> list(n * 1, n * 2, n * 3, n * 4)).toString());
        assertEquals("[ 1, 2, 3, 4, 5, NIL ]", l.flatMap(n -> list(n)).toString());
        assertEquals("[ NIL ]", List.<Integer>list().flatMap(n -> list(n * 1, n * 2, n * 3, n * 4)).toString());
        assertEquals("[ NIL ]", List.<Integer>list().flatMap(n -> list(n)).toString());
    }

    @Test
    public void testFilterViaFlatMap() {
        assertEquals("[ 3, 4, 5, NIL ]", l.filterViaFlatMap(n -> n > 2).toString());
    }

}
