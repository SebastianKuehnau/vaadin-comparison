package org.example.flow;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.example.flow.backend.Person;
import org.example.flow.backend.PersonService;
import org.example.flow.util.SpringUtil;

import javax.annotation.PostConstruct;

@Route
@CssImport("./styles/shared-styles.css")
public class MainView extends VerticalLayout {

    Logger log = LoggerFactory.getLogger(MainView.class);

    final Grid<Person> personGrid = new Grid<>(Person.class);
    final FilterTextField lastnameField = new FilterTextField();
    final FilterTextField firstnameField = new FilterTextField();
    final FilterTextField emailField = new FilterTextField();
    final FilterComboBox<Integer> counterComboBox = new FilterComboBox<>();
    private final PersonService personService;

    public MainView(@Autowired PersonService personService) {
        this.personService = personService;

        personGrid.setColumns("lastname", "firstname", "email", "counter");

        HeaderRow headerRow = personGrid.appendHeaderRow();
        headerRow.getCell(personGrid.getColumnByKey("lastname")).setComponent(lastnameField);
        headerRow.getCell(personGrid.getColumnByKey("firstname")).setComponent(firstnameField);
        headerRow.getCell(personGrid.getColumnByKey("email")).setComponent(emailField);
        headerRow.getCell(personGrid.getColumnByKey("counter")).setComponent(counterComboBox);

        personGrid.setSizeFull();

        add(personGrid);

        setSizeFull();
    }

    @PostConstruct
    public void init() {
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

                    var personDTO = new Person(lastnameField.getValue(), firstnameField.getValue(), emailField.getValue(), counterComboBox.getValue());

                    var personPage = personService.findAll(Example.of(personDTO, personExampleMatcher), pageRequest);

                    return personPage.stream();
                });

        counterComboBox.setItems(query -> {
            // The index of the first item to load
            int offset = query.getOffset();

            // The number of items to load
            int limit = query.getLimit();

            var sort = SpringUtil.convertSortOrders(query.getSortOrders());

            // Spring specific API to page queries
            var pageRequest = PageRequest.of((offset / limit), limit, sort);

            //DTO for filtering
            var personFilterDto = new Person(lastnameField.getValue(), firstnameField.getValue(), emailField.getValue(), counterComboBox.getValue());

            var personExampleMatcher = ExampleMatcher
                    .matching()
                    .withIgnoreNullValues()
                    .withIgnoreCase()
                    .withStringMatcher(ExampleMatcher.StringMatcher.STARTING);

            var personStream = personService.findAll(Example.of(personFilterDto, personExampleMatcher), pageRequest)
                    .stream()
                    .map(Person::getCounter)
                    .distinct()
                    .sorted();

            return personStream;
        });
    }

    private void update() {
        personGrid.getDataProvider().refreshAll();
        counterComboBox.getDataProvider().refreshAll();
    }

    public class FilterTextField extends TextField {
        public FilterTextField() {
            super(event -> update());
            setClearButtonVisible(true);
            setValueChangeMode(ValueChangeMode.EAGER);
        }
    }

    public class FilterComboBox<T> extends ComboBox<T> {
        public FilterComboBox() {
            addSelectedItemChangeListener(event -> update());
            setClearButtonVisible(true);
        }
    }
}
