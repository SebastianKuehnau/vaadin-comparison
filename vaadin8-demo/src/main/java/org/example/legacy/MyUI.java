package org.example.legacy;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;
import org.example.legacy.backend.Person;
import org.example.legacy.backend.PersonService;

import javax.servlet.annotation.WebServlet;
import java.util.List;
import java.util.stream.Collectors;


/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of an HTML page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Push
@SpringUI
@Theme("mytheme")
public class MyUI extends UI {

    final PersonService personService ;

    final FilterTextField lastnameFilterField = new FilterTextField();
    final FilterTextField firstnameFilterField = new FilterTextField();
    final FilterTextField emailFilterField = new FilterTextField();
    final FilterComboBox<Integer> counterFilterComboBox = new FilterComboBox<>();

    private ListDataProvider<Person> personListDataProvider;

    public MyUI(PersonService personService) {
        this.personService = personService;
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final VerticalLayout layout = new VerticalLayout();

        List<Person> personList = personService.findAll();

        personListDataProvider = DataProvider
                .ofCollection(personList);

        Grid<Person> personGrid = new Grid<>(Person.class);
        personGrid.setDataProvider(personListDataProvider);

        List<Integer> counterList = personList.stream()
                .map(Person::getCounter)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        counterFilterComboBox.setItems(counterList);

        personGrid.setColumns("lastname", "firstname", "email", "counter");

        //sort lastname column
        Grid.Column<Person, ?> personGridColumn = personGrid.getColumn("lastname");
        personGrid.sort(personGridColumn, SortDirection.ASCENDING);

        personGrid.setSizeFull();

        HeaderRow headerRow = personGrid.appendHeaderRow();
        headerRow.getCell(personGrid.getColumn("lastname")).setComponent(lastnameFilterField);
        headerRow.getCell(personGrid.getColumn("firstname")).setComponent(firstnameFilterField);
        headerRow.getCell(personGrid.getColumn("email")).setComponent(emailFilterField);
        headerRow.getCell(personGrid.getColumn("counter")).setComponent(counterFilterComboBox);

        layout.addComponent(personGrid);
        layout.setSizeFull();

        setContent(layout);
    }

    private boolean filterPerson(Person person) {
        return match(person.getFirstname(), firstnameFilterField.getValue()) &&
                match(person.getLastname(), lastnameFilterField.getValue()) &&
                match(person.getEmail(), emailFilterField.getValue()) &&
                match(person.getCounter(), counterFilterComboBox.getValue());
    }

    private boolean match(Integer value, Integer searchValue) {
        return searchValue == null || searchValue.compareTo(value) == 0;
    }

    private boolean match(String value, String searchTerm) {
        return searchTerm == null || searchTerm.isEmpty() ||
                value.toLowerCase().startsWith(searchTerm.toLowerCase());
    }

    private void filter() {
        personListDataProvider.setFilter(this::filterPerson);

    }

    public class FilterTextField extends TextField {
        public FilterTextField() {
            super();
            setWidth("100%");
            addStyleName(ValoTheme.TEXTFIELD_TINY);
            setPlaceholder("Filter");
            addValueChangeListener(event -> filter());
        }
    }

    public class FilterComboBox<T> extends ComboBox<T> {
        public FilterComboBox() {
            super();
            setWidth("100%");
            addSelectionListener(event -> filter());
            addStyleName(ValoTheme.COMBOBOX_TINY);
            setPlaceholder("Filter");
        }
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
