package org.example.flow;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.example.flow.backend.Person;
import org.example.flow.backend.PersonService;
import org.example.flow.util.SpringUtil;

@Route
@CssImport("./styles/shared-styles.css")
public class MainView extends VerticalLayout {
    
    public MainView(@Autowired PersonService personService) {

        Grid<Person> personGrid = new Grid<>(Person.class);
        personGrid.setColumns("lastname", "firstname", "email", "counter");
        personGrid.setItems(personService.findAll());
        personGrid.setSizeFull();

        add(personGrid);
        setSizeFull();
    }
}
