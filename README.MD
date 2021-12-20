# Simple Grid

This is an example to show a grid of persons loading items from a database.

## Vaadin 8

#### **`MyUI.java`**
```java
@Override
protected void init(VaadinRequest vaadinRequest) {
    final VerticalLayout layout = new VerticalLayout();

    Grid<Person> personGrid = new Grid<>(Person.class);

    personGrid.setItems(personService.findAll());
    personGrid.setColumns("lastname", "firstname", "email", "counter");
    personGrid.setSizeFull();

    layout.addComponent(personGrid);
    layout.setSizeFull();

    setContent(layout);
}
```

## Flow
#### **`MainView.java`**
```java
public MainView(@Autowired PersonService personService) {

    Grid<Person> personGrid = new Grid<>(Person.class);
    personGrid.setColumns("lastname", "firstname", "email", "counter");
    personGrid.setItems(personService.findAll());
    personGrid.setSizeFull();

    add(personGrid);
    setSizeFull();
}
```

## Fusion
#### **`person-list-view-view.ts`**
```typescript
@customElement('person-list-view-view')
export class PersonListViewView extends View {

    @state()
    private items: Person[] = [];

    async connectedCallback() {
        super.connectedCallback();
        this.items = await PersonEndpoint.findAll();
    }

    render() {
        return html`
          <vaadin-vertical-layout class="w-full h-full">
              <vaadin-grid id="grid" class="w-full h-full" theme="no-border" .items="${this.items}">
                <vaadin-grid-column path="firstname"></vaadin-grid-column>
                <vaadin-grid-column path="lastname"></vaadin-grid-column>
                <vaadin-grid-column path="email"></vaadin-grid-column>
              </vaadin-grid>
          </vaadin-vertical-layout>
        `;
    }
}
```

#### **`PersonEndpoint.java`**
```java
@Endpoint
@AnonymousAllowed
public class PersonEndpoint {

    private PersonRepository personRepository ;

    public PersonEndpoint(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public @Nonnull List<@Nonnull Person> findAll() {
        return personRepository.findAll();
    }
}
```