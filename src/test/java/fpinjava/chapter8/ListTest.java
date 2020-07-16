package fpinjava.chapter8;

import fpinjava.chapter1.Tuple;
import org.junit.jupiter.api.Test;

import static fpinjava.chapter7.Result.empty;
import static fpinjava.chapter7.Result.success;
import static fpinjava.chapter8.List.list;
import static fpinjava.chapter8.List.range;
import static fpinjava.chapter8.List.unfold;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ListTest {

    List<Integer> list = list(1, 2, 3, 4, 5, 6, 7, 8);

    @Test
    public void testSplitAtUsingFoldLeftEfficient() {
        Tuple<List<Integer>, List<Integer>> lists = List.splitAtUsingFoldLeftEfficient(list, 4);
        assertEquals(lists._1.toString(), list(1, 2, 3, 4).toString());
        assertEquals(lists._2.toString(), list(5, 6, 7, 8).toString());

        Tuple<List<Integer>, List<Integer>> lists2 = List.splitAtUsingFoldLeftEfficientWithTuple(list, 4);
        assertEquals(lists2._1.toString(), list(1, 2, 3, 4).toString());
        assertEquals(lists2._2.toString(), list(5, 6, 7, 8).toString());
    }

    @Test
    public void testUnfold() {
        assertEquals("[ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, NIL ]", unfold(0, i -> i < 10 ? success(new Tuple(i + 1, i + 1)) : empty()).toString());
    }

    @Test
    public void testRange() {
        assertEquals("[ 1, 2, 3, 4, 5, 6, 7, 8, 9, NIL ]", range(1, 10).toString());
    }
}
