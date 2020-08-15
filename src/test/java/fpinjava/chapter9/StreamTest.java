package fpinjava.chapter9;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StreamTest {

    Stream<Integer> numbers = Stream.from(1);

    @Test
    public void testTake() {
        assertEquals("[ 1, 2, 3, 4, 5, NIL ]", numbers.take(5).toList().toString());
    }

    @Test public void testDrop() {
        assertEquals("[ 6, 7, 8, 9, 10, NIL ]", numbers.drop(5).take(5).toList().toString());
    }

    @Test
    public void testTakeWhile() {
        assertEquals("[ 1, 2, 3, 4, NIL ]", numbers.takeWhile(n -> n < 5).toList().toString());
        assertEquals("[ 1, 2, 3, 4, NIL ]", numbers.takeWhileWithFoldRight(n -> n < 5).toList().toString());
    }

    @Test
    public void testDropWhile() {
        assertEquals("[ 4, 5, NIL ]", numbers.takeWhile(n -> n <= 5).dropWhile(n -> n < 4).toList().toString());
    }

    @Test
    public void testExists() {
        assertTrue(numbers.take(5).exists(n -> n == 1));
        assertTrue(numbers.take(5).exists(n -> n == 5));
        assertFalse(numbers.take(5).exists(n -> n == 0));
    }

    @Test
    public void testFoldRight() {
        assertEquals(15, numbers.take(5).foldRight(() -> 0, n -> s -> n + s.get()));
    }

    @Test
    public void testFilter() {
        Stream<Integer> evenNumbers = numbers.filter(n -> n % 2 == 0);
        assertEquals(2, evenNumbers.head());
        assertEquals(4, evenNumbers.tail().head());
    }

    @Test
    public void testAppend() {
        assertEquals("[ 2, 4, 6, 8, 10, 1, 3, 5, 7, 9, NIL ]", numbers.filter(n -> n % 2 == 0).take(5).append(() -> numbers.filter(n -> n % 2 != 0).take(5)).toList().toString());
    }

    @Test
    public void testFlatMap() {
        assertEquals("[ 1, 2, 3, 4, 5, 2, 3, 4, 5, 6, 3, 4, 5, 6, 7, 4, 5, 6, 7, 8, 5, 6, 7, 8, 9, NIL ]", numbers.take(5).flatMap(n -> Stream.from(n).take(5)).toList().toString());
    }

    @Test
    public void testFind() {
        assertEquals("Success(5)", numbers.find(n -> n * 2 == 10).toString());
    }

}
