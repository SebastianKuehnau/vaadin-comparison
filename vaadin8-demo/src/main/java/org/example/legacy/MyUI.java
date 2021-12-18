package org.example.legacy;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final VerticalLayout layout = new VerticalLayout();
        
        Grid<Person> personGrid = new Grid<>(Person.class);

        personGrid.setDataProvider(DataProvider.fromCallbacks(
                query -> fetchPersons(query),
                query -> Math.toIntExact(fetchPersons(query).count())
        ));
        personGrid.setColumns("lastname", "firstname", "email", "counter");
        personGrid.setSizeFull();

        layout.addComponent(personGrid);
        layout.setSizeFull();

        setContent(layout);
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
        //Person personFilterDto = new Person(lastnameField.getValue(), firstnameField.getValue(), emailField.getValue(), counterComboBox.getValue());

        long startTime = System.currentTimeMillis() ;

        Page<Person> personPage = personService.findAll(pageRequest);

        log.info("Grid Query time is " + (System.currentTimeMillis() - startTime));

        return personPage.stream();
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
