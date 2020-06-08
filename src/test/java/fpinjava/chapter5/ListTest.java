package fpinjava.chapter5;

import org.junit.jupiter.api.Test;

import static fpinjava.chapter5.List.concat;
import static fpinjava.chapter5.List.list;
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

}
