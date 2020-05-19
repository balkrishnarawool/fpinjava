package fpinjava.chapter2;

import static fpinjava.chapter2.FunctionUtilities.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import fpinjava.chapter1.Tuple;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

public class FunctionUtilitiesTest {

    Function<Integer, Integer> square = i -> i * i;
    Function<Integer, Integer>  triple = i -> i * 3;

    Function<Integer, Function<Integer, Integer>>  sum = n1 -> n2 -> n1 + n2;
    Function<String, Function<String, String>>  append = s1 -> s2 -> s1 + s2;

    @Test
    public void testCompose() {
        assertEquals(36, compose(square, triple).apply(2));
    }

    @Test
    public void testHigherCompose() {
        // Note that the higherCompose() method, when called requires parameter types,
        // but compose() does not need them.
        // This is because, if they are not specified then the call would be Function2.higherCompose()
        // which would give a Function<Object, Object> instance.
        // But in the second call, higherCompose() already takes Function instances so it knows the types.

        assertEquals( 36, FunctionUtilities.<Integer, Integer, Integer>
                        higherCompose().apply(square).apply(triple).apply(2)
        );
    }

    @Test
    public void testAndThen() {
        assertEquals( 12, andThen(square, triple).apply(2));
    }

    @Test
    public void testHigherAndThen() {
        assertEquals( 12, FunctionUtilities.<Integer, Integer, Integer>
                higherAndThen().apply(square).apply(triple).apply(2));
    }

    @Test
    public void testPartialA() {
     assertEquals(7,  partialA(5,  sum).apply(2));
     assertEquals( "AB", partialA("A",  append).apply("B"));
    }

    @Test
    public void testPartialB() {
        assertEquals( 7, partialB(5,  sum).apply(2));
        assertEquals( "BA", partialB("A",  append).apply("B"));

    }

    @Test
    public void testFuncCurried() {
        assertEquals( "A, B, C, D", func("A", "B", "C", "D"));
        assertEquals(  "A, B, C, D", funcCurried().apply("A").apply("B").apply("C").apply("D"));
    }

    @Test
    public void testCurry() {
        Function<Tuple<String, String>, String> f = t -> t._1 + t._2;
        assertEquals( f.apply(new Tuple<>("A", "B")), curry(f).apply("A").apply("B"));
    }

    @Test
    public void testReverseArgs() {
        Function<Integer, Function<String, String>> mapper = n -> s -> n + s;
        String str = mapper.apply(1).apply("Number");
        assertEquals( str, reverseArgs(mapper).apply("Number").apply(1));
    }

}
