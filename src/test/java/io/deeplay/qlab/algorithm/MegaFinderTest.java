package io.deeplay.qlab.algorithm;

import io.deeplay.qlab.parser.models.Unit;
import io.deeplay.qlab.parser.models.input.EnemyLocation;
import io.deeplay.qlab.parser.models.output.UnitWithLocation;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MegaFinderTest {

    @Test
    void findDisposition_EmptyTest() {

        Set<Unit> units = Set.of();

        Set<EnemyLocation> locations = Set.of();

        Set<UnitWithLocation> expected = Set.of();

        assertEquals(expected, new MegaFinder(new SumSourceProfitEvaluator())
                .findDisposition(units, locations));
    }

    @Test
    void findDisposition_OneLocationTest() {

        Set<Unit> units = Set.of(new Unit("1", 1.0), new Unit("2", 2.0),
                new Unit("3", 3.0), new Unit("4", 4.0), new Unit("5", 5.0));

        Set<EnemyLocation> locations = Set.of(new EnemyLocation("LOC", 999, 3,
                List.of(new Unit("enemy1", 99.0), new Unit("enemy2", 99.0))));

        Set<UnitWithLocation> expected = Set.of(new UnitWithLocation("5", 5.0, 0, "LOC"),
                new UnitWithLocation("4", 4.0, 1, "LOC"),
                new UnitWithLocation("3", 3.0, 2, "LOC"));

        Set<UnitWithLocation> actual = new MegaFinder(new SumSourceProfitEvaluator())
                .findDisposition(units, locations);

        assertAll(
                // Один размер
                () -> assertEquals(expected.size(), actual.size()),
                // Совпадает оценка
                () -> assertEquals(new SumSourceProfitEvaluator().estimatedGoldProfit(expected),
                        new SumSourceProfitEvaluator().estimatedGoldProfit(actual)),
                // Нет одинаковых позиций
                () -> assertEquals(expected.stream().map(UnitWithLocation::getLocatePosition).distinct().count(),
                        actual.stream().map(UnitWithLocation::getLocatePosition).distinct().count()),
                // Все юниты различны
                () -> assertEquals(expected.stream().map(UnitWithLocation::getName).distinct().count(),
                        actual.stream().map(UnitWithLocation::getName).distinct().count()));
    }

    @Test
    void findDisposition_ManyLocationTest() {

        Set<Unit> units = Set.of(new Unit("1", 1.0), new Unit("2", 2.0),
                new Unit("3", 3.0), new Unit("4", 4.0), new Unit("5", 5.0));

        Set<EnemyLocation> locations = Set.of(new EnemyLocation("L1", 1, 9, List.of()),
                new EnemyLocation("L2", 2, 9, List.of()),
                new EnemyLocation("L3", 3, 9, List.of()));

        Set<UnitWithLocation> actual = new MegaFinder(new SumSourceProfitEvaluator())
                .findDisposition(units, locations);

        assertAll(
                () -> assertEquals(5, actual.size()),
                () -> assertEquals(1 + 2 + 3 + 4 + 5, new SumSourceProfitEvaluator().estimatedGoldProfit(actual)),
                () -> assertEquals(5, actual.stream().map(UnitWithLocation::getLocatePosition).distinct().count()),
                () -> assertEquals(5, actual.stream().map(UnitWithLocation::getName).distinct().count()));
    }

    private static class SumSourceProfitEvaluator implements IEvaluator {

        @Override
        public double estimatedGoldProfit(Set<UnitWithLocation> units) {
            return units.stream().mapToDouble(UnitWithLocation::getSourceGoldCount).sum();
        }
    }
}
