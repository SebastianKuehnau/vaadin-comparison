package org.vaadin.example.util;

import org.ajbrown.namemachine.NameGenerator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.vaadin.example.backend.Person;
import org.vaadin.example.backend.PersonService;

import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class DataGenerator {

    final static Logger logger = Logger.getLogger("InitDataService");
    protected static final int COUNT = 100000;

    @Bean
    public CommandLineRunner createDemoDataIfNeeded(PersonService personService) {
        return args -> {
            if (personService.count() >= COUNT)
                return ;

            logger.info("add new data");

            var generator = new NameGenerator();
            var names = generator.generateNames(COUNT);
            var personList = names.stream()
                    .map(name -> {
                        var person = Person.Builder.create(name);

                        var counter = ThreadLocalRandom.current().nextInt(10) + 1 ;
                        person.setCounter(counter) ;

                        return person;
                    })
                    .collect(Collectors.toList());

            personService.saveAll(personList);

            logger.info("generated " + personService.count() + " items");
        };
    }
}
