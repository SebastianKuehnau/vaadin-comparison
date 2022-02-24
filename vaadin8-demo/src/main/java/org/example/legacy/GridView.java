package org.example.legacy;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.navigator.SpringNavigator;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.example.legacy.backend.Person;
import org.example.legacy.backend.PersonService;
import org.example.legacy.util.SpringUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringView(name = "")
public class GridView extends VerticalLayout implements View {

    private final PersonService personService;
    private final SpringNavigator navigator;

    public GridView(PersonService personService, SpringNavigator navigator) {
        this.personService = personService;
        this.navigator = navigator;

        Grid<Person> personGrid = new Grid<>(Person.class);
        personGrid.setDataProvider(DataProvider.fromCallbacks(
                query -> fetchPersons(query),
                query -> Math.toIntExact(personService.count())
        ));
        personGrid.setColumns("lastname", "firstname", "email", "counter");
        personGrid.addComponentColumn(this::createEditIcon).setWidth(50d);
        personGrid.setSizeFull();

        addComponent(personGrid);
        setSizeFull();
    }

    private Component createEditIcon(Person person) {
        Button button = new Button() ;
        button.setIcon(VaadinIcons.EDIT);
        button.setPrimaryStyleName(ValoTheme.BUTTON_ICON_ONLY);
        button.addClickListener(event -> navigator.navigateTo(FormView.VIEW_NAME + "/" + person.getId()));
        return button;
    }

    private Stream<Person> fetchPersons(Query<Person, Void> query) {
        // The index of the first item to load
        final int offset = query.getOffset();

        // The number of items to load
        final int limit = query.getLimit();

        //lazy loading
        final int startPage = offset / limit ;
        final int endPAge = (offset + limit + 1) / limit ;

        final Sort sort = SpringUtil.convertSortOrders(query.getSortOrders());

        PageRequest page0Request = PageRequest.of(startPage, limit, sort);
        List<Person> page0 = personService.findAll(page0Request)
                .stream().collect(Collectors.toList());

        PageRequest page1Request = PageRequest.of(endPAge, limit, sort);
        List<Person> page1 = personService.findAll(page1Request)
                .stream().collect(Collectors.toList());

        return Stream.concat(
                page0.subList(offset % limit, page0.size()).stream(),
                page1.subList(0, limit - page0.size()).stream());
    }
}
