package io.deeplay.qlab;

import io.deeplay.qlab.parser.models.history.Round;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static io.deeplay.qlab.DataAnalyzerKt.unitsProfiles;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        unitsProfiles();

        /*List<Round> data = Parser.parseListRounds(new File("testData/anonymized_data.json"));

        Map<String, Set<Integer>> levelsByLocations = data.stream()
                .collect(Collectors.groupingBy(Round::getLocationName))
                .entrySet().stream()
                .collect(Collectors.toMap(k -> k.getKey(), e -> getLocationLevels(e.getValue())));

        levelsByLocations.forEach((key, value) -> System.out.println(key + "\t" + value));*/
    }

    private static Set<Integer> getLocationLevels(List<Round> rounds) {
        return rounds.stream().map(Round::getLocationLevel).collect(Collectors.toSet());
    }
}
