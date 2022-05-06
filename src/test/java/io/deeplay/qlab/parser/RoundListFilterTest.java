package io.deeplay.qlab.parser;

import io.deeplay.qlab.parser.models.Unit;
import io.deeplay.qlab.parser.models.history.Round;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoundListFilterTest {
    @Test
    void filter_NonZeroSizedLocationsTest() {
        Round okay = new Round("45987fd3", "Factoria304", 10, 6,
                List.of(new Unit("ДОРШЕ ОВМОНДАЙЛО", 102.9, 9, 1, 1, 1, 0, 0)),
                List.of(new Unit("АДАЛЬМЕР ОНКУШВИЛИ", 117.1, -0.5, 3, 0, 0, 0, 0),
                        new Unit("ДОНОТ ВАСКАЯН", 165.7, -8.5, 0, 1, 1, 1, 0))
        );
        Round zeroSizedEmpty = new Round("f11c3f18", "Factoria770", 50, 0, List.of(), List.of());
        Round zeroSizedNonEmpty = new Round("f1151918", "Factoria771", 51, 0,
                List.of(new Unit("ЛИАГОРД ОНГКЕЕЦ", 10)),
                List.of()
        );

        List<Round> input = List.of(okay, zeroSizedEmpty, zeroSizedNonEmpty);
        List<Round> expected = List.of(okay);

        assertEquals(expected, RoundListFilter.filter(input));
    }

    @Test
    void filter_NonEmptyLocationsTest() {
        Round okay = new Round("45987fd3", "Factoria304", 10, 6,
                List.of(new Unit("ДОРШЕ ОВМОНДАЙЛО", 102.9, 9, 1, 1, 1, 0, 0)),
                List.of(new Unit("АДАЛЬМЕР ОНКУШВИЛИ", 117.1, -0.5, 3, 0, 0, 0, 0),
                        new Unit("ДОНОТ ВАСКАЯН", 165.7, -8.5, 0, 1, 1, 1, 0))
        );
        Round emptyZeroSized = new Round("f11c3f18", "Factoria770", 50, 0, List.of(), List.of());
        Round emptyNonZeroSized = new Round("f1151918", "Factoria771", 51, 5, List.of(), List.of());

        List<Round> input = List.of(okay, emptyZeroSized, emptyNonZeroSized);
        List<Round> expected = List.of(okay);

        assertEquals(expected, RoundListFilter.filter(input));
    }

    @Test
    void filter_ActionsAreBoolTest() {
        Round okay = new Round("3ecb8727", "Factoria414", 50, 9,
                List.of(new Unit("ОРТАРД ТЕАИФНИК", 116.92, 0.5, 0, 0, 1, 0, 0)),
                List.of(new Unit("АДАЛЬМЕР ОНКУШВИЛИ", 117.1, -0.5, 3, 0, 0, 0, 0))
        );
        Round nonBool = new Round("f11c3f18", "Factoria770", 50, 3,
                List.of(new Unit("ОРТАРД ТЕАИФНИК", 116.92, 0.5, 0, 0, 1, 0, 0)),
                List.of(new Unit("АДАЛЬМЕР ОНКУШВИЛИ", 117.1, -0.5, 3, 0, 0, 0, -1))
        );

        List<Round> input = List.of(okay, nonBool);
        List<Round> expected = List.of(okay);

        assertEquals(expected, RoundListFilter.filter(input));
    }

    @Test
    void filter_UnitsAreOnExistingPositionsTest() {
        Round okay = new Round("3ecb8727", "Factoria414", 50, 9,
                List.of(new Unit("ОРТАРД ТЕАИФНИК", 116.92, 0.5, 0, 0, 1, 0, 0)),
                List.of(new Unit("АДАЛЬМЕР ОНКУШВИЛИ", 117.1, -0.5, 3, 0, 0, 0, 0))
        );
        Round tooBig = new Round("f11c3f18", "Factoria770", 50, 2,
                List.of(new Unit("ОРТАРД ТЕАИФНИК", 116.92, 0.5, 0, 0, 1, 0, 0)),
                List.of(new Unit("АДАЛЬМЕР ОНКУШВИЛИ", 117.1, -0.5, 3, 0, 0, 0, 1))
        );
        Round tooSmall = new Round("9e1c3b1a", "Factoria771", 50, 4,
                List.of(new Unit("ОРТАРД ТЕАИФНИК", 116.92, 0.5, -5, 0, 1, 0, 0)),
                List.of(new Unit("АДАЛЬМЕР ОНКУШВИЛИ", 117.1, -0.5, 3, 0, 0, 0, 1))
        );

        List<Round> input = List.of(okay, tooBig, tooSmall);
        List<Round> expected = List.of(okay);

        assertEquals(expected, RoundListFilter.filter(input));
    }

    @Test
    void filter_UnitsAreOnDifferentPositionsTest() {
        Round okay = new Round("3ecb8727", "Factoria414", 50, 9,
                List.of(new Unit("ОРТАРД ТЕАИФНИК", 116.92, 0.5, 0, 0, 1, 0, 0)),
                List.of(new Unit("АДАЛЬМЕР ОНКУШВИЛИ", 117.1, -0.5, 3, 0, 0, 0, 0))
        );
        Round oneTeam = new Round("f11c3f18", "Factoria770", 50, 8,
                List.of(new Unit("ОРТАРД ТЕАИФНИК", 116.92, 1, 2, 0, 1, 0, 0)),
                List.of(new Unit("АДАЛЬМЕР ОНКУШВИЛИ", 117.1, -0.5, 5, 0, 0, 0, 1),
                        new Unit("АЛЬБOГЕСТ НЕТРИОК", 118.4, -0.5, 5, 0, 0, 0, 1),
                        new Unit("ВАУМ РИСАНИОЯКА", 117.1, 0, 5, 0, 0, 0, 1))
        );
        Round differentTeams = new Round("f11c3f18", "Factoria700", 50, 5,
                List.of(new Unit("ОРТАРД ТЕАИФНИК", 116.92, 0.5, 2, 0, 1, 0, 0),
                        new Unit("ВАУМ РИСАНИОЯКА", 117.1, -0.5, 1, 0, 0, 0, 1)),
                List.of(new Unit("АДАЛЬМЕР ОНКУШВИЛИ", 117.1, 0, 2, 0, 0, 0, 1))
        );

        List<Round> input = List.of(oneTeam, okay, differentTeams);
        List<Round> expected = List.of(okay);

        assertEquals(expected, RoundListFilter.filter(input));
    }

    @Test
    void filter_ZeroGoldProfitSumTest() {
        Round okay = new Round("3ecb8727", "Factoria414", 50, 9,
                List.of(new Unit("ОРТАРД ТЕАИФНИК", 116.92, 0.5, 0, 0, 1, 0, 0)),
                List.of(new Unit("АДАЛЬМЕР ОНКУШВИЛИ", 117.1, -0.5, 3, 0, 0, 0, 0))
        );
        Round tooBig = new Round("f11c3f18", "Factoria770", 50, 8,
                List.of(new Unit("ОРТАРД ТЕАИФНИК", 116.92, 1, 2, 0, 1, 0, 0)),
                List.of(new Unit("АДАЛЬМЕР ОНКУШВИЛИ", 117.1, 0.5, 5, 0, 0, 0, 1),
                        new Unit("АЛЬБOГЕСТ НЕТРИОК", 118.4, -0.5, 5, 0, 0, 0, 1),
                        new Unit("ВАУМ РИСАНИОЯКА", 117.1, 0, 5, 0, 0, 0, 1))
        );
        Round tooSmall = new Round("f11c3f18", "Factoria700", 50, 5,
                List.of(new Unit("ОРТАРД ТЕАИФНИК", 116.92, 0, 2, 0, 1, 0, 0),
                        new Unit("ВАУМ РИСАНИОЯКА", 117.1, -0.5, 1, 0, 0, 0, 1)),
                List.of(new Unit("АДАЛЬМЕР ОНКУШВИЛИ", 117.1, 0, 2, 0, 0, 0, 1))
        );

        List<Round> input = List.of(okay, tooBig, tooSmall);
        List<Round> expected = List.of(okay);

        assertEquals(expected, RoundListFilter.filter(input));
    }
}
