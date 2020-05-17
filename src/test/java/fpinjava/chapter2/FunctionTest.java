package fpinjava.chapter2;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FunctionTest {

    final Function<Integer, Long> intToLong = i -> i * 100000000000L;
    final Function<String, Integer> stringToInt = Integer::parseInt;
    final Function<Long, Integer> longToInt = i -> i.intValue();

    @Test
    public void testCompose() {
        assertEquals(intToLong.compose(stringToInt).apply("2"), 200000000000L);
    }

    @Test
    public void testAndThen() {
        assertEquals(stringToInt.andThen(intToLong).apply("2"), 200000000000L);
    }

    @Test
    public void testIdentity() {
        Function<Integer, Integer> square = i -> i * i;

        assertEquals(Function.identity().apply("2"), "2");
        assertEquals(Function.identity().apply(square), square);
        assertEquals(Function.identity().apply(Function.identity()), Function.identity());
    }

    @Test
    public void testComposeStatic1() {
        assertEquals(Function.compose(intToLong, stringToInt).apply("2"), 200000000000L);
    }

    @Test
    public void testAndThenStatic1() {
        assertEquals(Function.andThen(stringToInt, intToLong).apply("2"), 200000000000L);
    }

    @Test
    public void testComposeStatic2() {
        assertEquals(Function.<String, Integer, Long>compose().apply(stringToInt).apply(intToLong).apply("2"), 200000000000L);
    }

    @Test
    public void testAndThenStatic2() {
        assertEquals(Function.<Integer, Long, String>andThen().apply(intToLong).apply(stringToInt).apply("2"), 200000000000L);
    }

    @Test
    public void testHigherAndThen() {
        assertEquals(Function.<String, Integer, Long>higherAndThen().apply(stringToInt).apply(intToLong).apply("2"), 200000000000L);
    }

    @Test
    public void testHigherCompose() {
        assertEquals(Function.<String, Integer, Long>higherCompose().apply(intToLong).apply(stringToInt).apply("2"), 200000000000L);
    }

}
