package org.example.flow;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.IronIcon;
import com.vaadin.flow.component.icon.VaadinIcon;
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

    Logger log = LoggerFactory.getLogger(MainView.class);

    public MainView(@Autowired PersonService personService) {

        Grid<Person> personGrid = new Grid<>(Person.class);
        personGrid.setColumns("lastname", "firstname", "email", "counter");
        personGrid.addComponentColumn(this::createEditIcon).setAutoWidth(true).setFlexGrow(0);

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

    private Component createEditIcon(Person person) {
        Icon icon = new Icon("lumo", "edit");
        Button button = new Button();
        button.setIcon(icon);
        button.addThemeVariants(ButtonVariant.LUMO_ICON);
        button.addClickListener(event -> UI.getCurrent().navigate(FormView.class, person.getId()));
        return button;
    }
}
