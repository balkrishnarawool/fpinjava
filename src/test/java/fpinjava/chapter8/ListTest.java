package fpinjava.chapter8;

import fpinjava.chapter1.Tuple;
import org.junit.jupiter.api.Test;

import static fpinjava.chapter8.List.list;
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

}
