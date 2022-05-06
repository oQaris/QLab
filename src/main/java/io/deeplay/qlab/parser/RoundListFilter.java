package io.deeplay.qlab.parser;


import io.deeplay.qlab.parser.models.Unit;
import io.deeplay.qlab.parser.models.history.Round;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class RoundListFilter {
    private static final double EPS = 1E-9;
    
    
    private static final Predicate<Round> nonZeroSizedLocations = round -> round.getMaxPositionsQuantity() != 0;
    
    
    private static final Predicate<Round> nonEmptyLocations = round ->
            round.getOurUnits().size() + round.getOpponentUnits().size() > 0;
    
    
    private static final Predicate<Round> actionsAreBool = round -> {
        Set<Integer> possibleValues = Set.of(0, 1);
        
        return getAllUnits(round).stream().allMatch(unit ->
                possibleValues.contains(unit.getEvasiveness())
                        && possibleValues.contains(unit.getAggression())
                        && possibleValues.contains(unit.getResponseAggression())
                        && possibleValues.contains(unit.getShield()));
    };
    
    
    private static final Predicate<Round> unitsAreOnExistingPositions = round ->
            getAllUnits(round).stream().allMatch(unit ->
                    unit.getLocatePosition() >= 0 && unit.getLocatePosition() < round.getMaxPositionsQuantity());
    
    
    private static final Predicate<Round> unitsAreOnDifferentPositions = round -> {
        List<Unit> units = getAllUnits(round);
        IntStream occupiedPositions = units.stream().mapToInt(Unit::getLocatePosition).distinct();
        
        return units.size() == occupiedPositions.count();
    };
    
    
    private static final Predicate<Round> zeroGoldProfitSum = round -> getAllUnits(round).stream()
            .map(Unit::getGoldProfit).reduce(0., Double::sum) < EPS;
    
    
    private static List<Unit> getAllUnits(Round round) {
        List<Unit> units = new ArrayList<>(round.getOurUnits());
        units.addAll(round.getOpponentUnits());
        
        return units;
    }
    
    
    public static List<Round> filter(List<Round> rounds) {
        return rounds.stream()
                .filter(nonZeroSizedLocations)
                .filter(nonEmptyLocations)
                .filter(actionsAreBool)
                .filter(unitsAreOnExistingPositions)
                .filter(unitsAreOnDifferentPositions)
                .filter(zeroGoldProfitSum)
                .collect(Collectors.toList());
    }
}
