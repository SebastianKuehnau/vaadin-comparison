package org.example.legacy;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringNavigator;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.UI;
import org.example.legacy.backend.PersonService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.annotation.WebServlet;


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

    @Autowired
    SpringViewProvider springViewProvider ;

    @Autowired
    SpringNavigator springNavigator ;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        //Navigator navigator = new Navigator(this, this);
        //navigator.addProvider(springViewProvider);
        springNavigator.init(this, this);
        springNavigator.addProvider(springViewProvider);
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
