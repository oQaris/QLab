package io.deeplay.qlab.algorithm.evolution;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class EvolutionAlgorithmTest {
    @Test
    void test() {
        EvolutionAlgorithm ea = new EvolutionAlgorithm(10, 2, 1, 3, 2, Functions.randFunction);
        List<String> list = Arrays.stream(new String[]{
                "A", "B", "C", "D", "E", "F", "G", "H", "K", "L"
        }).collect(Collectors.toList());
        List<List<String>> result = ea.start(list);
        System.out.println();
    }
}