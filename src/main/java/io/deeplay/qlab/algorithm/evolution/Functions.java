package io.deeplay.qlab.algorithm.evolution;

import java.util.List;
import java.util.function.Function;

public class Functions {
    public static Function<List<List<String>>, Double> randFunction = x -> (double) -x.stream().mapToInt(List::size).sum();
}
