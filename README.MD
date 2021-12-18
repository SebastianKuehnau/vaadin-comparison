# Grid with Lazy loading

This is an example to show a grid of persons and lazy load the data from the database. 

## Vaadin 8

#### **`MyUI.java`**
```java
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
        //similar in Vaadin 8 and Flow
    }
```

## Flow
#### **`MainView.java`**
```java
public MainView(@Autowired PersonService personService) {

        Grid<Person> personGrid = new Grid<>(Person.class);
        personGrid.setColumns("lastname", "firstname", "email", "counter");

        //DataProvider with callback to send query with parameter to DB
        personGrid.setItems(query -> {
            //similar in Vaadin 8 and Flow
        });

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

  async connectedCallback() {
    super.connectedCallback();
  }

  render() {
    return html`
      <vaadin-vertical-layout class="w-full h-full">
          <vaadin-grid id="grid" class="w-full h-full" theme="no-border" .dataProvider=${this.dataProvider}>
            <vaadin-grid-column path="firstname"></vaadin-grid-column>
            <vaadin-grid-column path="lastname"></vaadin-grid-column>
            <vaadin-grid-column path="email"></vaadin-grid-column>
          </vaadin-grid>
      </vaadin-vertical-layout>
    `;
  }

  async dataProvider(params: GridDataProviderParams<Person>, callback: GridDataProviderCallback<Person>) {
    const page = await PersonEndpoint.getPage(params.page, params.pageSize);
    
    callback(page?.content, page?.size);
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

    class PageResponse<T> {
        public List<T> content;
        public long size;
    }

    public PageResponse<Person> getPage(int page, int size) {
        var dbPage = personRepository.findAll(PageRequest.of(page, size));
    
        var response = new PageResponse<Person>();
        response.content = dbPage.getContent();
        response.size = dbPage.getTotalElements();
    
        return response;
    }
}
```

