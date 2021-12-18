package org.example.fusion.backend;

import java.util.List;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.fusion.Endpoint;
import com.vaadin.fusion.Nonnull;

import org.springframework.data.domain.PageRequest;

@Endpoint
@AnonymousAllowed
public class PersonEndpoint {

    private PersonRepository personRepository;

    public PersonEndpoint(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    class PageResponse<T> {
        public List<T> content;
        public long size;
    }

    @Nonnull
    public PageResponse<Person> getPage(int page, int size) {
        var dbPage = personRepository.findAll(PageRequest.of(page, size));
    
        var response = new PageResponse<Person>();
        response.content = dbPage.getContent();
        response.size = dbPage.getTotalElements();
    
        return response;
    }
}