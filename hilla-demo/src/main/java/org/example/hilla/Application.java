package org.example.hilla;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.ajbrown.namemachine.Name;
import org.ajbrown.namemachine.NameGenerator;
import org.example.hilla.backend.Person;
import org.example.hilla.backend.PersonRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
@Theme("hilla-demo")
public class Application extends SpringBootServletInitializer implements AppShellConfigurator {

    protected static final int COUNT = 100000;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    final static Logger logger = Logger.getLogger("InitDataService");

    @Bean
    public CommandLineRunner createDemoDataIfNeeded(PersonRepository personRepository) {
        return args -> {
            logger.info("running DataGenerator");
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
