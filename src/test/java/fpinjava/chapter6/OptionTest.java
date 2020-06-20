package fpinjava.chapter6;

import fpinjava.chapter2.Function;
import org.junit.jupiter.api.Test;

import static fpinjava.chapter6.Option.lift;
import static fpinjava.chapter6.Option.none;
import static fpinjava.chapter6.Option.some;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OptionTest {

    @Test
    public void testGetOrElse() {
        assertEquals(0, none().getOrElse(0));
        assertEquals(1, some(1).getOrElse(0));
        assertEquals(0, none().getOrElse(() -> 0));
        assertEquals(1, some(1).getOrElse(() -> 0));
    }

    @Test
    public void testMap() {
        assertEquals(none(), none().map(Object::toString));
        assertEquals(some("1").toString(), some(1).map(Object::toString).toString());
    }

    @Test
    public void testFlatMap() {
        assertEquals(none(), none().flatMap(n -> some(n)));
        assertEquals(some(1).toString(), some(1).flatMap(n -> some(n)).toString());
    }

    @Test
    public void testOrElse() {
        assertEquals(some(0).toString(), none().orElse(() -> some(0)).toString());
        assertEquals(some(1).toString(), some(1).orElse(() -> some(0)).toString());
    }

    @Test
    public void testFilter() {
        assertEquals(none(), Option.<Integer>none().filter(n -> n < 2));
        assertEquals(some(1).toString(), some(1).filter(n -> n < 2).toString());
    }

    @Test
    public void testLift() {
        Function<Integer, Integer> square = n -> n * n;
        assertEquals(some(4).toString(), lift(square).apply(some(2)).toString());
        assertEquals(none(), lift(square).apply(none()));
    }
}
