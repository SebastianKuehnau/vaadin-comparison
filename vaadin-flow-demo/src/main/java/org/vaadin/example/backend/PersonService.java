package org.vaadin.example.backend;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonService extends JpaRepository<Person, Long> {

    @Cacheable("findAllByExamplePageable")
    <S extends Person> Page<S> findAll(Example<S> example, Pageable pageable);

    @Cacheable("findAllByExample")
    <S extends Person> List<S> findAll(Example<S> example);
}
