package org.example.flow;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.example.flow.backend.Person;
import org.example.flow.backend.PersonService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Collectors;

@Route
@CssImport("./styles/shared-styles.css")
public class MainView extends VerticalLayout {

    final FilterTextField lastnameField = new FilterTextField();
    final FilterTextField firstnameField = new FilterTextField();
    final FilterTextField emailField = new FilterTextField();
    final FilterComboBox<Integer> counterComboBox = new FilterComboBox<>();

    final Grid<Person> personGrid = new Grid<>(Person.class);

    public MainView(@Autowired PersonService personService) {
        
        personGrid.setColumns("lastname", "firstname", "email", "counter");
        personGrid.setSizeFull();

        var personList = personService.findAll();
        GridListDataView<Person> dataView = personGrid.setItems(personList);
        dataView.addFilter(this::personMatch);

        var counterList = personList.stream()
                .map(Person::getCounter)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        counterComboBox.setItems(counterList);

        HeaderRow headerRow = personGrid.appendHeaderRow();
        headerRow.getCell(personGrid.getColumnByKey("lastname")).setComponent(lastnameField);
        headerRow.getCell(personGrid.getColumnByKey("firstname")).setComponent(firstnameField);
        headerRow.getCell(personGrid.getColumnByKey("email")).setComponent(emailField);
        headerRow.getCell(personGrid.getColumnByKey("counter")).setComponent(counterComboBox);

        add(personGrid);
        setSizeFull();
    }

    private boolean personMatch(Person person) {

        return match(person.getFirstname(), firstnameField.getValue()) &&
                match(person.getLastname(), lastnameField.getValue()) &&
                match(person.getEmail(), emailField.getValue()) &&
                match(person.getCounter(), counterComboBox.getValue());
    }

    private boolean match(Integer value, Integer searchValue) {
        return searchValue == null || searchValue.compareTo(value) == 0;
    }

    private boolean match(String value, String searchTerm) {
        return searchTerm == null || searchTerm.isEmpty() ||
                value.toLowerCase().startsWith(searchTerm.toLowerCase());
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
