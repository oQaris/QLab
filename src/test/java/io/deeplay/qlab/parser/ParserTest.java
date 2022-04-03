package io.deeplay.qlab.parser;

import io.deeplay.qlab.parser.models.Round;
import io.deeplay.qlab.parser.models.Unit;
import io.deeplay.qlab.parser.models.World;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParserTest {

    @Test
    void parseListRoundsTest() throws FileNotFoundException {
        List<Round> expected = List.of(new Round("aaa72c29-a55a-4a24-9a1c-94e8a9d24eda", "Factoria0", 10, 6,
                List.of(new Unit("АДАЛЬГУНД ВИТУИНДСКИ", 132.8, 0.0, 1, 1, 0, 0, 1)),
                List.of(new Unit("ГЕЛЕРИК ЛЕЙВГОТАХ", 115.5, -0.5, 5, 0, 1, 0, 0))));

        assertEquals(expected, Parser.parseListRounds("testData/listRounds.json"));
    }

    @Test
    void parseWorldTest() throws FileNotFoundException {
        World expected = new World(
                "FunnyLand",
                List.of()
        )

        assertEquals(expected, Parser.parseWorld("testData/listRounds.json"));
    }
}
