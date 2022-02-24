package org.example.hilla.backend;

import java.util.List;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import dev.hilla.Endpoint;
import dev.hilla.Nonnull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Endpoint
@AnonymousAllowed
public class PersonEndpoint {

    private PersonRepository personRepository ;

    public PersonEndpoint(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public @Nonnull List<@Nonnull Person> findAll() {
        return personRepository.findAll();
    }

    public @Nonnull Long count() {
        return personRepository.count();
    }

    public @Nonnull Page<@Nonnull Person> getPage(int page, int size) {
        var dbPage = personRepository.findAll(PageRequest.of(page, size));

        return dbPage;
    }
}