package fpinjava.chapter7;

import fpinjava.chapter2.Function;
import org.junit.jupiter.api.Test;

import static fpinjava.chapter7.Result.empty;
import static fpinjava.chapter7.Result.failure;
import static fpinjava.chapter7.Result.lift2;
import static fpinjava.chapter7.Result.success;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResultTest {

    @Test
    public void testGetOrElse() {
        assertEquals("Default", failure("F").getOrElse(() -> "Default"));
        assertEquals("Default", empty().getOrElse(() -> "Default"));
        assertEquals("S", success("S").getOrElse(() -> "Default"));
    }

    @Test
    public void testMap() {
        assertEquals("Failure(F)", Result.<String>failure("F").map(String::length).toString());
        assertEquals("Empty()", Result.<String>empty().map(String::length).toString());
        assertEquals("Success(1)", Result.<String>success("S").map(String::length).toString());
    }

    @Test
    public void testFlatMap() {
        assertEquals("Failure(F)", Result.<String>failure("F").flatMap(s -> success(s.length())).toString());
        assertEquals("Empty()", Result.<String>empty().flatMap(s -> success(s.length())).toString());
        assertEquals("Success(1)", Result.<String>success("S").flatMap(s -> success(s.length())).toString());
    }

    @Test
    public void testOrElse() {
        assertEquals("Success(Default)", Result.<String>failure("F").orElse(() -> success("Default")).toString());
        assertEquals("Success(Default)", Result.<String>empty().orElse(() -> success("Default")).toString());
        assertEquals("Success(S)", Result.<String>success("S").orElse(() -> success("Default")).toString());
    }

    @Test
    public void testFilter() {
        assertEquals("Failure(F)", Result.<String>failure("F").filter(s -> s.length() > 1).toString());
        assertEquals("Empty()", Result.<String>empty().filter(s -> s.length() > 1).toString());
        assertEquals("Empty()", Result.<String>success("S").filter(s -> s.length() > 1).toString());
        assertEquals("Success(Success)", Result.<String>success("Success").filter(s -> s.length() > 1).toString());

        assertEquals("Failure(F)", Result.<String>failure("F").filter(s -> s.length() > 1, "Not found").toString());
        assertEquals("Empty()", Result.<String>empty().filter(s -> s.length() > 1, "Not found").toString());
        assertEquals("Failure(Not found)", Result.<String>success("S").filter(s -> s.length() > 1, "Not found").toString());
        assertEquals("Success(Success)", Result.<String>success("Success").filter(s -> s.length() > 1, "Not found").toString());
    }

    @Test
    public void testExists() {
        assertFalse(Result.<String>failure("F").exists(s -> s.length() > 1));
        assertFalse(Result.<String>empty().exists(s -> s.length() > 1));
        assertFalse(Result.<String>success("S").exists(s -> s.length() > 1));
        assertTrue(Result.<String>success("Success").exists(s -> s.length() > 1));
    }

    @Test
    public void testMapFailure() {
        assertEquals("Failure(Failure)", Result.<String>failure("F").mapFailure("Failure").toString());
        assertEquals("Empty()", Result.<String>empty().mapFailure("Failure").toString());
        assertEquals("Success(S)", Result.<String>success("S").mapFailure("Failure").toString());
    }

    @Test
    public void testLift2() {
        Function<Integer, Function<Double, String>> f = i -> d -> String.format("%s%s", i, d);
        assertEquals("Success(10.1)", lift2(f).apply(success(1)).apply(success(0.1)).toString());
        assertEquals("Failure(F2)", lift2(f).apply(success(1)).apply(failure("F2")).toString());
        assertEquals("Empty()", lift2(f).apply(success(1)).apply(empty()).toString());
        assertEquals("Failure(F1)", lift2(f).apply(failure("F1")).apply(success(0.1)).toString());
        assertEquals("Failure(F1)", lift2(f).apply(failure("F1")).apply(failure("F2")).toString());
        assertEquals("Failure(F1)", lift2(f).apply(failure("F1")).apply(empty()).toString());
        assertEquals("Empty()", lift2(f).apply(empty()).apply(success(0.1)).toString());
        assertEquals("Empty()", lift2(f).apply(empty()).apply(failure("F2")).toString());
        assertEquals("Empty()", lift2(f).apply(empty()).apply(empty()).toString());
    }
}
