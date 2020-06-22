package fpinjava.chapter7;

import org.junit.jupiter.api.Test;

import static fpinjava.chapter7.Either.left;
import static fpinjava.chapter7.Either.right;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EitherTest {

    @Test
    public void testGetOrElse() {
        assertEquals("Default", left("L").getOrElse(() -> "Default"));
        assertEquals("R", right("R").getOrElse(() -> "Default"));
    }

    @Test
    public void testMap() {
        assertEquals("Left(L)", Either.<String, String>left("L").map(s -> s.length()).toString());
        assertEquals("Right(1)", Either.<String, String>right("R").map(s -> s.length()).toString());
    }

    @Test
    public void testFlatMap() {
        assertEquals("Left(L)", Either.<String, String>left("L").flatMap(s -> right(s.length())).toString());
        assertEquals("Right(1)", Either.<String, String>right("R").flatMap(s -> right(s.length())).toString());
    }

    @Test
    public void testOrElse() {
        assertEquals("Right(Default)", left("L").orElse(() -> right("Default")).toString());
        assertEquals("Right(R)", right("R").orElse(() -> right("Default")).toString());
    }

}
