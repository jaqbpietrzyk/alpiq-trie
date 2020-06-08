package com.alpiq.autocomplete.score

import spock.lang.Specification
import spock.lang.Title
import spock.lang.Unroll

@Title("Test weighted score calculator")
class ScoreCalculatorTest extends Specification {

    @Unroll
    def "Scoring #val between #min and #max"(min, max, val, exp) {

        expect:
        ScoreCalculator.score(val, min, max) == exp

        where:
        min | max | val || exp
        0   | 100 | 0   || 0.0
        0   | 100 | 25  || 0.25
        0   | 100 | 50  || 0.5
        0   | 100 | 100 || 1.0
        0   | 100 | -10 || 0.0
        0   | 100 | 200 || 1.0

    }
}
