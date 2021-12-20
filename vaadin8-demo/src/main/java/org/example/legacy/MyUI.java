package org.example.legacy;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.HeaderRow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.example.legacy.backend.Person;
import org.example.legacy.backend.PersonService;
import org.example.legacy.util.SpringUtil;

import javax.servlet.annotation.WebServlet;
import java.util.stream.Stream;


/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of an HTML page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@SpringUI
@Theme("mytheme")
public class MyUI extends UI {

    @Autowired
    PersonService personService ;

    Logger log = LogManager.getLogger(MyUI.class);

    private final Grid<Person> personGrid = new Grid<>(Person.class);
    private final TextField lastnameFilterField = new FilterTextField("Lastname");
    private final TextField firstnameFilterField = new FilterTextField("Firstname");
    private final TextField emailFilterField = new FilterTextField("Email");
    private final ComboBox<Integer> counterFilterField = new ComboBox<>("");

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final VerticalLayout layout = new VerticalLayout();

        personGrid.setDataProvider(DataProvider.fromCallbacks(
                query -> fetchPersons(query),
                query -> Math.toIntExact(fetchPersons(query).count())
        ));
        personGrid.setColumns("lastname", "firstname", "email", "counter");
        personGrid.setSizeFull();

        counterFilterField.setItems(personService.findDistinctCounter());
        counterFilterField.addValueChangeListener(event -> personGrid.getDataProvider().refreshAll());

        HeaderRow headerRow = personGrid.appendHeaderRow();
        headerRow.getCell("lastname").setComponent(lastnameFilterField);
        headerRow.getCell("firstname").setComponent(firstnameFilterField);
        headerRow.getCell("email").setComponent(emailFilterField);
        headerRow.getCell("counter").setComponent(counterFilterField);

        layout.addComponent(personGrid);
        layout.setSizeFull();

        setContent(layout);
    }

    public class FilterTextField extends TextField {
        public FilterTextField(String placeholder) {
            addValueChangeListener(event -> personGrid.getDataProvider().refreshAll());
            setValueChangeMode(ValueChangeMode.EAGER);
            setPlaceholder(placeholder);
        }
    }

    private Stream<Person> fetchPersons(Query<Person, Void> query) {
        // The index of the first item to load
        int offset = query.getOffset();

        // The number of items to load
        int limit = query.getLimit();

        Sort sort = SpringUtil.convertSortOrders(query.getSortOrders());

        // Spring specific API to page queries
        PageRequest pageRequest = PageRequest.of((offset / limit), limit, sort);

        //DTO for filtering
        Person personFilterDto = new Person(lastnameFilterField.getValue(), firstnameFilterField.getValue(), emailFilterField.getValue(), counterFilterField.getValue());

        long startTime = System.currentTimeMillis();

        ExampleMatcher personExampleMatcher = ExampleMatcher
                .matching()
                .withIgnoreNullValues()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.STARTING);

        Page<Person> personPage = personService.findAll(Example.of(personFilterDto, personExampleMatcher), pageRequest);

        log.info("Grid Query time is " + (System.currentTimeMillis() - startTime));

        return personPage.stream();
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
