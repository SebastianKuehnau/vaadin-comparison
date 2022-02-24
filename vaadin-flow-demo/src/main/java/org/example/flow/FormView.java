package org.example.flow;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.*;
import org.example.flow.backend.Person;
import org.example.flow.backend.PersonService;

import java.util.Optional;

@Route("person-form")
public class FormView extends VerticalLayout implements HasUrlParameter<Long> {

    private final TextField firstnameField = new TextField("Firstname");
    private final TextField lastnameField = new TextField("Lastname");
    private final TextField emailField = new TextField("E-Mail");
    private final TextField counterField = new TextField("Counter");
    private final RouterLink backLink = new RouterLink("back", MainView.class);

    private Person currentPerson ;

    private final FormLayout formLayout = new FormLayout(backLink, firstnameField, lastnameField, emailField, counterField);

    private final Binder<Person> binder = new Binder<>(Person.class);
    private final PersonService personService;

    private final Button cancelButton = new Button("cancel", event -> binder.readBean(currentPerson));
    private final Button saveButton = new Button("save", this::savePerson);

    private final HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, saveButton);;

    public FormView(PersonService personService) {
        this.personService = personService;

        binder.bind(lastnameField, Person::getLastname, Person::setLastname);
        binder.bind(firstnameField, Person::getFirstname, Person::setFirstname);

        binder.forField(emailField)
                .withValidator(new EmailValidator("please enter a valid email address"))
                .bind(Person::getEmail, Person::setEmail);

        binder.forField(counterField)
                .withConverter(s -> Integer.valueOf(s), integer -> String.valueOf(integer))
                .withValidator((integer, valueContext) -> {
                    if (integer > 0 && integer <= 10)
                        return ValidationResult.ok();

                    return ValidationResult.error("Values between 0 and 10");
                })
                .bind(Person::getCounter, Person::setCounter);

        formLayout.add(buttonLayout);
        formLayout.setColspan(backLink, 2);
        formLayout.setColspan(buttonLayout, 2);
        formLayout.setSizeUndefined();

        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);

        add(formLayout);
        setAlignSelf(Alignment.CENTER, formLayout);
        setWidthFull();
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Long id) {
        if (id == null)
            return;

        Optional<Person> personOptional = personService.findById(id);
        personOptional.ifPresent(person -> {
            binder.readBean(person);
            currentPerson = person ;
        });
    }

    private void savePerson(ClickEvent<Button> buttonClickEvent) {
        try {
            binder.writeBean(currentPerson);
            personService.save(currentPerson);
            UI.getCurrent().navigate(MainView.class);
            Notification show = Notification.show("Person saved!", 2000, Notification.Position.BOTTOM_CENTER);
            show.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }
}
