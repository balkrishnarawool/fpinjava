package fpinjava.chapter4;

import fpinjava.chapter2.Function;
import org.junit.jupiter.api.Test;

public class MemoizerTest<T, U> {

    // Curried form
    Function<Integer, Function<Integer, Function<Integer, Integer>>> f3m =
            Memoizer.memoize(x ->
                    Memoizer.memoize(y ->
                            Memoizer.memoize(z ->
                                    longCalculation(x) + longCalculation(y) - longCalculation(z))));

    // Tuple form
    Function<Tuple3<Integer, Integer, Integer>, Integer> ft =
            x -> longCalculation(x._1)
                    + longCalculation(x._2)
                    - longCalculation(x._3);
    Function<Tuple3<Integer, Integer, Integer>, Integer> ftm =
            Memoizer.memoize(ft);


    private Integer longCalculation(Integer y) {
        try {
            Thread.sleep(1_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return  y * 2;
    }

    @Test
    public void automaticMemoizationExampleCurried() {
        long startTime = System.currentTimeMillis();
        Integer result1 = f3m.apply(2).apply(3).apply(4);
        long time1 = System.currentTimeMillis() - startTime;
        startTime = System.currentTimeMillis();
        Integer result2 = f3m.apply(2).apply(3).apply(4);
        long time2 = System.currentTimeMillis() - startTime;
        System.out.println(result1);
        System.out.println(result2);
        System.out.println(time1);
        System.out.println(time2);
    }

    @Test
    public void automaticMemoizationExampleTuple() {
        long startTime = System.currentTimeMillis();
        Integer result1 = ftm.apply(new Tuple3<>(2, 3, 4));
        long time1 = System.currentTimeMillis() - startTime;
        startTime = System.currentTimeMillis();
        Integer result2 = ftm.apply(new Tuple3<>(2, 3, 4));
        long time2 = System.currentTimeMillis() - startTime;
        System.out.println(result1);
        System.out.println(result2);
        System.out.println(time1);
        System.out.println(time2);
    }
}