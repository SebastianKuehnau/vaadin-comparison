package org.example.hilla.backend;

import java.util.List;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import dev.hilla.Endpoint;
import dev.hilla.Nonnull;

@Endpoint
@AnonymousAllowed
public class PersonEndpoint {

    private PersonRepository personRepository ;

    public PersonEndpoint(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public @Nonnull Long count() { return personRepository.count(); }

    public @Nonnull List<@Nonnull Person> findAll() {
        return personRepository.findAll();
    }

    public @Nonnull List<@Nonnull Integer> findDistinctCounter() {
        return personRepository.findDistinctCounter();
    }
}