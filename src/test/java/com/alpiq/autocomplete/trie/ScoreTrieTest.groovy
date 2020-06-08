package com.alpiq.autocomplete.trie

import com.alpiq.autocomplete.score.ScoreCalculator
import spock.lang.Shared
import spock.lang.Specification

class Car implements Word {

    def model
    double popularity

    Car(def model, double popularity) {
        this.model = model
        this.popularity = popularity
    }

    @Override
    String getWord() {
        return model
    }

    @Override
    double calculateScore() {
        return ScoreCalculator.score(popularity, 0, 100)
    }
}


class ScoreTrieTest extends Specification {
    @Shared
    ScoreTrie<Car> trie = new ScoreTrie<>()

    def "setupSpec"() {
        def cars = [new Car("foo", 100), new Car("foo123", 10), new Car("foo bar", 60), new Car("bar", 30)]
        for (car in cars) {
            trie.insert(car)
        }
    }

    def "test unmatched word"() {
        given: "unmatched word"
        def word = "unmatched"

        when: "search for a car"
        def result = trie.searchByPrefix(word)

        then: "no car found"
        result.size() == 0
    }

    def "test bar model"() {
        given: "ba word"
        def word = "ba"

        when: "search for a car"
        def result = trie.searchByPrefix(word)

        then: "bar car found"
        result.size() == 1
        result[0].score > 0 && result[0].score < 1
        result[0].value.model == "bar"

    }

    def "test all foo models"() {
        given: "f word"
        def word = "f"

        when: "search for a car"
        def result = trie.searchByPrefix(word)

        then: "foo* cars found"
        result.size() == 3
        result[0].value.model == "foo"
        result[1].value.model == "foo bar"
        result[2].value.model == "foo123"

    }
}
