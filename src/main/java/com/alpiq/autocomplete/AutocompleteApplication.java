package com.alpiq.autocomplete;

import com.alpiq.autocomplete.trie.CityLocation;
import com.alpiq.autocomplete.trie.Trie;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;

import java.io.IOException;

@SpringBootApplication
@Slf4j
public class AutocompleteApplication {

    private final Trie<CityLocation> trie;
    @Value("classpath:cities15000.txt")
    Resource citiesResource;

    public AutocompleteApplication(Trie<CityLocation> trie) {
        this.trie = trie;
    }

    public static void main(String[] args) {
        SpringApplication.run(AutocompleteApplication.class, args);

    }

    @EventListener(ApplicationReadyEvent.class)
    public void prepareTrie() {
        int counter = 0;
        long maxPop = Long.MIN_VALUE;
        try (LineIterator it = FileUtils.lineIterator(citiesResource.getFile(), "UTF-8")) {
            while (it.hasNext()) {
                String line = it.nextLine();
                String[] splitted = line.split("\t");
                String city = splitted[1].toLowerCase();
                String country = splitted[8].toLowerCase();
                int population = Integer.parseInt(splitted[14].toLowerCase());
                 maxPop = Math.max(population, maxPop);
                trie.insert(new CityLocation(city, country, population));
                counter++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("alpiq.autocomplete.loader loaded={} population={}", counter, maxPop);
    }

}
