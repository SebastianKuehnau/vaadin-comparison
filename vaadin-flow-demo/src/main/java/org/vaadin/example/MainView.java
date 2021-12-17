package org.vaadin.example;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.vaadin.example.backend.Person;
import org.vaadin.example.backend.PersonService;
import org.vaadin.example.util.SpringUtil;

@Route
@CssImport("./styles/shared-styles.css")
public class MainView extends VerticalLayout {

    Logger log = LoggerFactory.getLogger(MainView.class);

    public MainView(@Autowired PersonService personService) {

        Grid<Person> personGrid = new Grid<>(Person.class);
        personGrid.setColumns("lastname", "firstname", "email", "counter");

        //DataProvider with callback to send query with parameter to DB
        personGrid.setItems(
                // First callback fetches items based on a query
                query -> {
                    // The index of the first item to load
                    int offset = query.getOffset();

                    // The number of items to load
                    int limit = query.getLimit();

                    var sort = SpringUtil.convertSortOrders(query.getSortOrders());

                    // Spring specific API to page queries
                    var pageRequest = PageRequest.of((offset / limit), limit, sort);

                    var personExampleMatcher = ExampleMatcher
                            .matching()
                            .withIgnoreNullValues()
                            .withIgnoreCase()
                            .withStringMatcher(ExampleMatcher.StringMatcher.STARTING);

                    long startTime = System.currentTimeMillis() ;

                    var personPage = personService.findAll(pageRequest);

                    log.info("Grid Query time is " + (System.currentTimeMillis() - startTime));

                    return personPage.stream();
                });

        personGrid.setSizeFull();

        add(personGrid);

        setSizeFull();
    }
}
