package org.example.legacy;

import com.vaadin.data.*;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.example.legacy.backend.Person;
import org.example.legacy.backend.PersonService;

@SpringView(name = FormView.VIEW_NAME)
public class FormView extends VerticalLayout implements View {

    public static final String VIEW_NAME = "person-form";

    private Person currentPerson;

    private final TextField lastname = new TextField("Lastname");
    private final TextField firstname = new TextField("Firstname");
    private final TextField email = new TextField("E-Mail");
    private final TextField counter = new TextField("Counter");

    private final Binder<Person> binder = new Binder<>();
    private final PersonService personService;

    private final Button back = new Button("", event -> UI.getCurrent().getNavigator().navigateTo(""));
    private final Button save = new Button("Save", this::save);
    private final Button cancel = new Button("Cancel", event -> binder.readBean(currentPerson));
    private final HorizontalLayout buttonLayout = new HorizontalLayout(cancel, save);

    public FormView(PersonService personService) {

        this.personService = personService;

        back.setIcon(VaadinIcons.ARROW_LEFT);
        back.setPrimaryStyleName(ValoTheme.BUTTON_ICON_ONLY);
        back.addStyleName(ValoTheme.BUTTON_HUGE);
        addComponent(back);

        FormLayout formLayout = new FormLayout();
        formLayout.addComponents(lastname, firstname, email, counter);
        formLayout.setWidthUndefined();
        formLayout.setMargin(false);

        binder.forField(lastname)
                .bind(Person::getLastname, Person::setLastname);

        binder.forField(firstname)
                .bind(Person::getFirstname, Person::setFirstname);

        binder.forField(email)
                .withValidator(new EmailValidator("Invalid emailadress"))
                .bind(Person::getEmail, Person::setEmail);

        binder.forField(counter)
                .withConverter(s -> Integer.valueOf(s), integer -> String.valueOf(integer))
                .withValidator((value, context) ->
                        (value > 0 && value < 10) ?
                                ValidationResult.ok() :
                                ValidationResult.error("Value must be between 1 and 10."))
                .bind(Person::getCounter, Person::setCounter);

        save.addStyleName(ValoTheme.BUTTON_PRIMARY);

        buttonLayout.setMargin(false);

        VerticalLayout rootLayout = new VerticalLayout() ;
        rootLayout.addComponents(back, formLayout, buttonLayout);
        rootLayout.setComponentAlignment(back, Alignment.TOP_LEFT);
        rootLayout.setComponentAlignment(buttonLayout, Alignment.BOTTOM_RIGHT);
        rootLayout.setWidthUndefined();

        addComponent(rootLayout);
        setComponentAlignment(rootLayout, Alignment.TOP_CENTER);
    }

    private void save(Button.ClickEvent event) {
        try {
            binder.writeBean(currentPerson);
            personService.save(currentPerson);
            UI.getCurrent().getNavigator().navigateTo("");
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        try {
            Long personId = Long.valueOf(event.getParameters());
            personService.findById(personId).ifPresent(person -> {
                currentPerson = person;
                binder.readBean(currentPerson);
            });
        } catch (NumberFormatException ex) {
            event.getNavigator().navigateTo("");
        }
    }
}
