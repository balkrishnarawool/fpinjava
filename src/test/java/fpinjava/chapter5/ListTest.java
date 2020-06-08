package fpinjava.chapter5;

import org.junit.jupiter.api.Test;

import static fpinjava.chapter5.List.concat;
import static fpinjava.chapter5.List.foldLeft;
import static fpinjava.chapter5.List.foldRight;
import static fpinjava.chapter5.List.list;
import static fpinjava.chapter5.List.product;
import static fpinjava.chapter5.List.sum;
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
        assertEquals("12345", foldRight(l, "", n -> s -> n.toString() + s));
        assertEquals("54321", foldRight(l, "", n -> s -> s + n.toString()));
    }

    @Test
    public void testFoldLeft() { // Left fold traverses from left to right in the list
        assertEquals("12345", foldLeft(l, "", s -> n -> s + n.toString()));
        assertEquals("54321", foldLeft(l, "", s -> n -> n.toString() + s));
    }

    @Test
    public void testFoldLeftStackSafe() {
        assertEquals("12345", foldLeft(l, "", s -> n -> s + n.toString()));
        assertEquals("54321", foldLeft(l, "", s -> n -> n.toString() + s));
    }

    @Test
    public void testLength() {
        assertEquals(5, l.length());
    }

}
