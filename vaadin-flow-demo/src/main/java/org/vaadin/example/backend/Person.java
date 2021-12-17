package org.vaadin.example.backend;

import org.ajbrown.namemachine.Name;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Person {

    @Id
    @GeneratedValue
    private Long id ;

    private String lastname ;
    private String firstname ;
    private String email ;
    private Integer counter ;

    public Person() {
    }

    public Person(String lastname, String firstname, String email) {
        this.lastname = lastname;
        this.firstname = firstname;
        this.email = email;
    }

    public Person(String lastname, String firstname, String email, Integer counter) {
        this(lastname, firstname, email);
        this.counter = counter;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getCounter() {
        return counter;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }

    public static class Builder {
        public static Person create(Name name) {
            String email = name.getFirstName() + "." + name.getLastName() + "@gmail.com";
            return new Person(name.getLastName(), name.getFirstName(), email);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(lastname, person.lastname) && Objects.equals(firstname, person.firstname) && Objects.equals(email, person.email) && Objects.equals(counter, person.counter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lastname, firstname, email, counter);
    }
}
