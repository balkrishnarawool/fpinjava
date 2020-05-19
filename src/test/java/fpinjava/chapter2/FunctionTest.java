package fpinjava.chapter2;

import org.junit.jupiter.api.Test;

import static fpinjava.chapter2.Function.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FunctionTest {

    final Function<Integer, Long> intToLong = i -> i * 100000000000L;
    final Function<String, Integer> stringToInt = Integer::parseInt;
    final Function<Long, Integer> longToInt = i -> i.intValue();

    @Test
    public void testCompose() {
        assertEquals(200000000000L, intToLong.compose(stringToInt).apply("2"));
    }

    @Test
    public void testAndThen() {
        assertEquals(200000000000L, stringToInt.andThen(intToLong).apply("2"));
    }

    @Test
    public void testIdentity() {
        Function<Integer, Integer> square = i -> i * i;

        assertEquals("2", identity().apply("2"));
        assertEquals(square, identity().apply(square));
        assertEquals(identity(), identity().apply(identity()));
    }

    @Test
    public void testComposeStatic1() {
        assertEquals(200000000000L, compose(intToLong, stringToInt).apply("2"));
    }

    @Test
    public void testAndThenStatic1() {
        assertEquals(200000000000L, andThen(stringToInt, intToLong).apply("2"));
    }

    @Test
    public void testComposeStatic2() {
        assertEquals(200000000000L, Function.<String, Integer, Long>compose().apply(stringToInt).apply(intToLong).apply("2"));
    }

    @Test
    public void testAndThenStatic2() {
        assertEquals(200000000000L, Function.<Integer, Long, String>andThen().apply(intToLong).apply(stringToInt).apply("2"));
    }

    @Test
    public void testHigherAndThen() {
        assertEquals(200000000000L, Function.<String, Integer, Long>higherAndThen().apply(stringToInt).apply(intToLong).apply("2"));
    }

    @Test
    public void testHigherCompose() {
        assertEquals(200000000000L, Function.<String, Integer, Long>higherCompose().apply(intToLong).apply(stringToInt).apply("2"));
    }

}
