package fpinjava.chapter2;

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
        assertEquals( FunctionUtilities.compose(square, triple).apply(2), 36);
    }

    @Test
    public void testHigherCompose() {
        // Note that the higherCompose() method, when called requires parameter types,
        // but compose() does not need them.
        // This is because, if they are not specified then the call would be Function2.higherCompose()
        // which would give a Function<Object, Object> instance.
        // But in the second call, higherCompose() already takes Function instances so it knows the types.

        assertEquals( FunctionUtilities.<Integer, Integer, Integer>
                        higherCompose().apply(square).apply(triple).apply(2),
               36);
    }

    @Test
    public void testAndThen() {
        assertEquals( FunctionUtilities.andThen(square, triple).apply(2), 12);
    }

    @Test
    public void testHigherAndThen() {
        assertEquals( FunctionUtilities.<Integer, Integer, Integer>
                higherAndThen().apply(square).apply(triple).apply(2),
               12);
    }

    @Test
    public void testPartialA() {
     assertEquals( FunctionUtilities.partialA(5,  sum).apply(2), 7);
     assertEquals( FunctionUtilities.partialA("A",  append).apply("B"), "AB");
    }

    @Test
    public void testPartialB() {
        assertEquals( FunctionUtilities.partialB(5,  sum).apply(2), 7);
        assertEquals( FunctionUtilities.partialB("A",  append).apply("B"), "BA");

    }

    @Test
    public void testFuncCurried() {
        assertEquals( FunctionUtilities.func("A", "B", "C", "D"), "A, B, C, D");
        assertEquals( FunctionUtilities.funcCurried().apply("A").apply("B").apply("C").apply("D"), "A, B, C, D");
    }

    @Test
    public void testCurry() {
        Function<Tuple<String, String>, String> f = t -> t._1 + t._2;
        assertEquals( FunctionUtilities.curry(f).apply("A").apply("B"), f.apply(new Tuple<>("A", "B")));
    }

    @Test
    public void testReverseArgs() {
        Function<Integer, Function<String, String>> mapper = n -> s -> n + s;
        String str = mapper.apply(1).apply("Number");
        assertEquals( FunctionUtilities.reverseArgs(mapper).apply("Number").apply(1), str);
    }

}
