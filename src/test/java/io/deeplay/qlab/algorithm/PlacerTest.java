package io.deeplay.qlab.algorithm;

import io.deeplay.qlab.parser.models.Unit;
import io.deeplay.qlab.parser.models.input.EnemyLocation;
import io.deeplay.qlab.parser.models.output.UnitWithLocation;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PlacerTest {
    private static final String DIFF_SIZE = "Разный размер";
    private static final String DIFF_PROFIT_ESTIMATION = "Не совпадает оценка";
    private static final String POS_COLLAPSE = "Коллапс позиций";
    private static final String UNIT_COLLAPSE = "Коллапс юнитов";

    @Test
    void findDisposition_EmptyTest() {

        Set<Unit> units = Set.of();

        Set<EnemyLocation> locations = Set.of();

        Set<UnitWithLocation> expected = Set.of();

        assertEquals(expected, new Placer(new SumSourceProfitEvaluator())
                .findDisposition(units, locations));
    }

    @Test
    void findDisposition_OneLocationTest() {

        Set<Unit> units = Set.of(new Unit("1", 1.0), new Unit("2", 2.0),
                new Unit("3", 3.0), new Unit("4", 4.0), new Unit("5", 5.0));

        Set<EnemyLocation> locations = Set.of(new EnemyLocation("LOC", 999, 3,
                List.of(new Unit("enemy1", 99.0, 0), new Unit("enemy2", 99.0, 2))));

        Set<UnitWithLocation> actual = new Placer(new SumSourceProfitEvaluator())
                .findDisposition(units, locations);

        assertAll(
                () -> assertEquals(1, actual.size(), DIFF_SIZE),
                () -> assertEquals(5, new SumSourceProfitEvaluator().evaluateGoldProfit(actual), DIFF_PROFIT_ESTIMATION),
                () -> assertEquals(1, actual.stream().map(UnitWithLocation::getLocatePosition).count(), POS_COLLAPSE),
                () -> assertEquals(1, actual.stream().map(UnitWithLocation::getName).count(), UNIT_COLLAPSE)
        );
    }

    @Test
    void findDisposition_ManyLocationTest() {

        Set<Unit> units = Set.of(new Unit("1", 1.0), new Unit("2", 2.0),
                new Unit("3", 3.0), new Unit("4", 4.0), new Unit("5", 5.0));

        Set<EnemyLocation> locations = Set.of(new EnemyLocation("L1", 1, 9, List.of()),
                new EnemyLocation("L2", 2, 9, List.of()),
                new EnemyLocation("L3", 3, 9, List.of()));

        Set<UnitWithLocation> actual = new Placer(new SumSourceProfitEvaluator())
                .findDisposition(units, locations);

        assertAll(
                () -> assertEquals(5, actual.size(), DIFF_SIZE),
                () -> assertEquals(1 + 2 + 3 + 4 + 5, new SumSourceProfitEvaluator().evaluateGoldProfit(actual),
                        DIFF_PROFIT_ESTIMATION),
                () -> assertEquals(5, actual.stream().map(UnitWithLocation::getLocatePosition).count(), POS_COLLAPSE),
                () -> assertEquals(5, actual.stream().map(UnitWithLocation::getName).count(), UNIT_COLLAPSE)
        );
    }

    private static final class SumSourceProfitEvaluator implements IEvaluator {

        @Override
        public double evaluateGoldProfit(Set<UnitWithLocation> units) {
            return units.stream().mapToDouble(UnitWithLocation::getSourceGoldCount).sum();
        }
    }
}
