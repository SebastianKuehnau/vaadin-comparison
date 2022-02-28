package org.example.hilla.backend;

import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.flow.server.auth.AnonymousAllowed;

import dev.hilla.Endpoint;
import dev.hilla.Nonnull;
import org.example.hilla.util.GridFilterValue;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
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

    class PageResponse<@Nonnull T> {
        public @Nonnull List<@Nonnull T> content;
        public @Nonnull long size;
    }

    public @Nonnull PageResponse<@Nonnull Person> getPage(int page, int size, List<GridFilterValue> filterValueList) {

        var personFilterValueMap = filterValueList.stream().
                collect(Collectors.toMap(GridFilterValue::getPath, GridFilterValue::getValue));

        var personDTO = new Person(
                personFilterValueMap.get("lastname"),
                personFilterValueMap.get("firstname"),
                personFilterValueMap.get("email"), null);

        var personExampleMatcher = ExampleMatcher
                .matching()
                .withIgnoreNullValues()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.STARTING);

        var dbPage = personRepository.findAll(Example.of(personDTO, personExampleMatcher), PageRequest.of(page, size));

        var response = new PageResponse<Person>();
        response.content = dbPage.getContent();
        response.size = dbPage.getTotalElements();

        return response;
    }
}