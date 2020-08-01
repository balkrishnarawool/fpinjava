package fpinjava.chapter9;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    }

    @Test
    public void testDropWhile() {
        assertEquals("[ 4, 5, NIL ]", numbers.takeWhile(n -> n <= 5).dropWhile(n -> n < 4).toList().toString());
    }
}
