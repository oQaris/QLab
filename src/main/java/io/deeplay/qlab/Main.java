package io.deeplay.qlab;


import io.deeplay.qlab.parser.RoundListFilter;
import io.deeplay.qlab.parser.Parser;
import io.deeplay.qlab.parser.models.history.Round;
import io.deeplay.qlab.util.CmdLineArgs;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        final CmdLineArgs parsedArgs = CmdLineArgs.parse(args);
        String historyPath = Objects.requireNonNullElse(parsedArgs.getHistory(), "testData/anonymized_data.json");
        
        List<Round> rounds = RoundListFilter.filter(Parser.parseRoundList(new File(historyPath)));
        
        System.out.println("Round list size: " + rounds.size());
        System.out.println();
        
        Map<String, Set<Integer>> levelsByLocations = rounds.stream()
                .collect(Collectors.groupingBy(Round::getLocationName))
                .entrySet().stream()
                .collect(Collectors.toMap(k -> k.getKey(), e -> getLocationLevels(e.getValue())));
        
        levelsByLocations.forEach((key, value) -> System.out.println(key + "\t" + value));
    }
    
    
    private static Set<Integer> getLocationLevels(List<Round> rounds) {
        return rounds.stream().map(Round::getLocationLevel).collect(Collectors.toSet());
    }
}
