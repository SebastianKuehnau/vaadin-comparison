# Filter Lazy Grid

This is an example to show a grid of persons loading and filter items from a database.

## Vaadin 8
#### **`MyUI.java`**
```java
@Override
protected void init(VaadinRequest vaadinRequest) {
    final VerticalLayout layout = new VerticalLayout();

    personGrid.setDataProvider(DataProvider.fromCallbacks(
    query -> fetchPersons(query),
    query -> Math.toIntExact(fetchPersons(query).count())));
    personGrid.setColumns("lastname", "firstname", "email", "counter");
    personGrid.setSizeFull();

    counterFilterField.setItems(personService.findDistinctCounter());
    counterFilterField.addStyleName(ValoTheme.COMBOBOX_TINY);
    counterFilterField.addValueChangeListener(event -> personGrid.getDataProvider().refreshAll());

    HeaderRow headerRow = personGrid.appendHeaderRow();
    headerRow.getCell("lastname").setComponent(lastnameFilterField);
    headerRow.getCell("firstname").setComponent(firstnameFilterField);
    headerRow.getCell("email").setComponent(emailFilterField);
    headerRow.getCell("counter").setComponent(counterFilterField);

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
    Person personFilterDto = new Person(lastnameFilterField.getValue(), firstnameFilterField.getValue(), emailFilterField.getValue(), counterFilterField.getValue());

    long startTime = System.currentTimeMillis();

    ExampleMatcher personExampleMatcher = ExampleMatcher
        .matching()
        .withIgnoreNullValues()
        .withIgnoreCase()
        .withStringMatcher(ExampleMatcher.StringMatcher.STARTING);

    Page<Person> personPage = personService.findAll(Example.of(personFilterDto, personExampleMatcher), pageRequest);

    return personPage.stream();
}
```

## Flow
#### **`MainView.java`**
```java
public MainView(@Autowired PersonService personService) {
    this.personService = personService;

    personGrid.setColumns("lastname", "firstname", "email", "counter");

    HeaderRow headerRow = personGrid.appendHeaderRow();
    headerRow.getCell(personGrid.getColumnByKey("lastname")).setComponent(lastnameField);
    headerRow.getCell(personGrid.getColumnByKey("firstname")).setComponent(firstnameField);
    headerRow.getCell(personGrid.getColumnByKey("email")).setComponent(emailField);
    headerRow.getCell(personGrid.getColumnByKey("counter")).setComponent(counterComboBox);

    personGrid.setSizeFull();

    add(personGrid);

    setSizeFull();
}

@PostConstruct
public void init() {
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
        
            var personDTO = new Person(lastnameField.getValue(), firstnameField.getValue(), emailField.getValue(), counterComboBox.getValue());
        
            var personPage = personService.findAll(Example.of(personDTO, personExampleMatcher), pageRequest);
        
            return personPage.stream();
        });

    counterComboBox.setItems(query -> {
        //same as for personGrid;
    });
}

public class FilterTextField extends TextField {
    public FilterTextField() {
        super(event -> {
            personGrid.getDataProvider().refreshAll();
            counterComboBox.getDataProvider().refreshAll();
        });
        setClearButtonVisible(true);
        setValueChangeMode(ValueChangeMode.EAGER);
    }
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
            <vaadin-grid-filter-column path="firstname"></vaadin-grid-filter-column>
            <vaadin-grid-filter-column path="lastname"></vaadin-grid-filter-column>
            <vaadin-grid-filter-column path="email"></vaadin-grid-filter-column>
            <vaadin-grid-filter-column path="counter"></vaadin-grid-filter-column>
          </vaadin-grid>
      </vaadin-vertical-layout>
    `;
    }

    async dataProvider(params: GridDataProviderParams<Person>, callback: GridDataProviderCallback<Person>) {
        const page = await PersonEndpoint.getPage(params.page, params.pageSize, params.filters);

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

    public @Nonnull List<@Nonnull Person> findAll() {
        return personRepository.findAll();
    }

    class PageResponse<T> {
        public List<T> content;
        public long size;
    }

    public PageResponse<Person> getPage(int page, int size, List<GridFilterValue> filterValueList) {

        var personFilterValueMap = filterValueList.stream().
                collect(Collectors.toMap(GridFilterValue::getPath, GridFilterValue::getValue));

        var personDTO = new Person(
                personFilterValueMap.get("lastname"),
                personFilterValueMap.get("firstname"),
                personFilterValueMap.get("email"), null);

        var personExampleMatcher = ExampleMatcher
                .matching()
                .withIgnoreNullValues()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.STARTING);

        var dbPage = personRepository.findAll(Example.of(personDTO, personExampleMatcher), PageRequest.of(page, size));

        var response = new PageResponse<Person>();
        response.content = dbPage.getContent();
        response.size = dbPage.getTotalElements();

        return response;
    }
}
```