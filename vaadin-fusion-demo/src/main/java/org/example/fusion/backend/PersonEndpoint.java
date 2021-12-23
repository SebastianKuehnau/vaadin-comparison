package org.example.fusion.backend;

import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.fusion.Endpoint;
import com.vaadin.fusion.Nonnull;

import org.example.fusion.util.GridFilterValue;
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

    class PageResponse<T> {
        public List<T> content;
        public long size;
    }

    public PageResponse<Person> getPage(int page, int size, List<GridFilterValue> filterValueList) {

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