package com.example.application.util;

import com.example.application.backend.Person;
import com.example.application.backend.PersonRepository;
import org.ajbrown.namemachine.Name;
import org.ajbrown.namemachine.NameGenerator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class DataGenerator {

    final static Logger logger = Logger.getLogger("InitDataService");
    protected static final int COUNT = 1000000;

    @Bean
    public CommandLineRunner createDemoDataIfNeeded(PersonRepository personRepository) {
        return args -> {
            if (personRepository.count() >= COUNT)
                return ;

            logger.info("add new data");

            NameGenerator generator = new NameGenerator();

            List<Name> names = generator.generateNames(COUNT);

            List<Person> personList = names.stream()
                    .map(name -> {
                        Person person = Person.Builder.create(name);

                        Integer counter = ThreadLocalRandom.current().nextInt(10) + 1 ;
                        person.setCounter(counter) ;

                        return person;
                    })
                    .collect(Collectors.toList());

            personRepository.saveAll(personList);

            logger.info("generated " + personRepository.count() + " items");
        };
    }
}