package org.example.legacy.backend;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.ajbrown.namemachine.Name;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
@NoArgsConstructor
public class Person {

    @Id
    @GeneratedValue
    private Long id ;

    private String lastname ;
    private String firstname ;
    private String email ;
    private Integer counter ;

    public Person(String lastname, String firstname, String email, Integer counter) {
        this.lastname = lastname;
        this.firstname = firstname;
        this.email = email;
        this.counter = counter;
    }

    public static class Builder {
        public static Person create(Name name) {
            String email = name.getFirstName() + "." + name.getLastName() + "@gmail.com";
            return new Person(name.getLastName(), name.getFirstName(), email, 0);
        }
    }
}
