package io.deeplay.qlab;

import io.deeplay.qlab.parser.Parser;
import io.deeplay.qlab.parser.RoundListFilter;
import io.deeplay.qlab.parser.Writer;
import io.deeplay.qlab.parser.models.history.Round;
import io.deeplay.qlab.util.CmdLineArgs;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {
        final CmdLineArgs parsedArgs = CmdLineArgs.parse(args);
        String historyPath = Objects.requireNonNullElse(parsedArgs.getHistory(), "testData/anonymized_data.json");
        String filteredPath = parsedArgs.getFiltered();

        System.out.printf("Parsing from %s...%n", historyPath);
        List<Round> rounds = Parser.parseRoundList(new File(historyPath));

        System.out.printf("%nRound list size: %d%n", rounds.size());
        rounds = RoundListFilter.filter(rounds);
        System.out.printf("Filtered round list size: %d%n", rounds.size());

        Map<String, Set<Integer>> levelsByLocations = rounds.stream()
                .collect(Collectors.groupingBy(Round::getLocationName))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> getLocationLevels(e.getValue())));

        System.out.printf("%nLevel locations:%n");
        levelsByLocations.forEach((key, value) -> System.out.printf("%s\t%s%n", key, value));

        if (filteredPath != null) {
            System.out.printf("%nWriting to %s...%n", filteredPath);
            Writer.writeRoundList(rounds, new File(filteredPath));
            System.out.println("Done");
        }
    }

    private static Set<Integer> getLocationLevels(List<Round> rounds) {
        return rounds.stream().map(Round::getLocationLevel).collect(Collectors.toSet());
    }
}
