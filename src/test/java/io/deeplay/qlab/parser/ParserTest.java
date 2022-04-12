package io.deeplay.qlab.parser;

import io.deeplay.qlab.parser.models.Unit;
import io.deeplay.qlab.parser.models.history.Round;
import io.deeplay.qlab.parser.models.input.EnemyLocation;
import io.deeplay.qlab.parser.models.input.InputData;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParserTest {

    @Test
    void parseListRoundsTest() throws FileNotFoundException {
        List<Round> expected = List.of(new Round("aaa72c29-a55a-4a24-9a1c-94e8a9d24eda", "Factoria0", 10, 6,
                List.of(new Unit("АДАЛЬГУНД ВИТУИНДСКИ", 132.8, 0.0, 1, 1, 0, 0, 1)),
                List.of(new Unit("ГЕЛЕРИК ЛЕЙВГОТАХ", 115.5, -0.5, 5, 0, 1, 0, 0))));

        assertEquals(expected, Parser.parseListRounds(new File("testData/listRounds.json")));
    }

    @Test
    void parseInputTest() throws IOException {
        String data = Files.readString(Path.of("testData/world.json"));
        InputData expected = new InputData("FunnyLand",
                List.of(new EnemyLocation("Factoria15", 10, 6,
                        List.of(new Unit("Иван Факов", 86.25, 2)))),
                List.of(new Unit("РИНО КОТОНИЧ", 294.25),
                        new Unit("ЭТЕЛЬСКА АДАЛЬРИСИЧ", 204.5),
                        new Unit("МЕРСТЕН БРОНДРЕАКА", 119.75)));

        assertEquals(expected, Parser.parseInput(data));
    }
}
