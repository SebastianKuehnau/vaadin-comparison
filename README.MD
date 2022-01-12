# Simple Grid

This is an example to show a grid of persons loading items from a database.

## Vaadin 8

#### **`MyUI.java`**
```java
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

private void filter() {
    personListDataProvider.setFilter(this::filterPerson);
}
```

## Flow
#### **`MainView.java`**
```java
public MainView(@Autowired PersonService personService) {

    personGrid.setColumns("lastname", "firstname", "email", "counter");
    personGrid.setSizeFull();

    var personList = personService.findAll();
    //sort lastname column
    personList.sort(Comparator.comparing(Person::getLastname));
    GridListDataView<Person> dataView = personGrid.setItems(personList);
    dataView.addFilter(this::personMatch);

    var counterList = personList.stream()
        .map(Person::getCounter)
        .distinct()
        .sorted()
        .collect(Collectors.toList());
        counterComboBox.setItems(counterList);

    HeaderRow headerRow = personGrid.appendHeaderRow();
    headerRow.getCell(personGrid.getColumnByKey("lastname")).setComponent(lastnameField);
    headerRow.getCell(personGrid.getColumnByKey("firstname")).setComponent(firstnameField);
    headerRow.getCell(personGrid.getColumnByKey("email")).setComponent(emailField);
    headerRow.getCell(personGrid.getColumnByKey("counter")).setComponent(counterComboBox);

    add(personGrid);
    setSizeFull();
}

private boolean personMatch(Person person) {
    return match(person.getFirstname(), firstnameField.getValue()) &&
        match(person.getLastname(), lastnameField.getValue()) &&
        match(person.getEmail(), emailField.getValue()) &&
        match(person.getCounter(), counterComboBox.getValue());
}

private void update() {
    personGrid.getDataProvider().refreshAll();
    counterComboBox.getDataProvider().refreshAll();
}
```

## Fusion
#### **`person-list-view-view.ts`**
```typescript
@customElement('person-list-view-view')
export class PersonListViewView extends View {

    @state()
    private personItems: Person[] = [];

    async connectedCallback() {
        super.connectedCallback();

        this.personItems = await PersonEndpoint.findAll();
    }

    private dataProvider : GridDataProvider<Person> = (params, callback) => {
        let items = this.personItems ;

        function get(path: string, item: Person) {
            // @ts-ignore
            return path.split('.').reduce((obj, property) => obj[property], item);
        }

        if (params.filters) {
            items = items.filter((item) =>
                params.filters.every((filter) => {

                    const value = get(filter.path, item);
                    const filterValueLowercase = filter.value.toString().toLowerCase();

                    if (filter.path === "counter") {
                        return filterValueLowercase === "" || value.toString() === filterValueLowercase;
                    } else {
                        return value.toString().toLowerCase().startsWith(filterValueLowercase);
                    }
                })
            );
        }

        callback(items, items.length);
    };

    render() {
        return html`
            <vaadin-vertical-layout theme="padding spacing" class="w-full h-full">
                <vaadin-grid id="grid" class="w-full h-full" .dataProvider="${guard([this.personItems], () => this.dataProvider.bind(this))}">
                    <vaadin-grid-column .headerRenderer="${this.headerFilterSortRenderer}"
                                        path="firstname"></vaadin-grid-column>
                    <vaadin-grid-column .headerRenderer="${this.headerFilterSortRenderer}"
                                        path="lastname"></vaadin-grid-column>
                    <vaadin-grid-column .headerRenderer="${this.headerFilterSortRenderer}"
                                        path="email"></vaadin-grid-column>
                    <vaadin-grid-column
                            .headerRenderer="${guard([this.personItems], () => this.headerComboBoxFilterSortRenderer.bind(this))}"
                            path="counter"></vaadin-grid-column>
                </vaadin-grid>
            </vaadin-vertical-layout>
        `;
    }

    private headerFilterSortRenderer = (root: HTMLElement, column: GridColumn) => {
        render(html`
            <div>
                <vaadin-grid-sorter class="header-cell" path="${column.path}">${this.createColumnHeader(column.path)}
                </vaadin-grid-sorter>
            </div>
            <vaadin-grid-filter path="${column.path}"></vaadin-grid-filter>
        `, root);
    };

    private headerComboBoxFilterSortRenderer = (root: HTMLElement, column: GridColumn) => {

        let comboboxItems = this.personItems
            .map(value => Number(value.counter))
            .filter((value, index,
                     categoryArray) => categoryArray.indexOf(value) === index)
            .sort((n1, n2) => n1 - n2);

        render(
            html`
                <div>
                    <vaadin-grid-sorter path="counter">${this.createColumnHeader(column.path)}</vaadin-grid-sorter>
                </div>
                <vaadin-grid-filter path="${column.path}">
                    <vaadin-combo-box slot="filter" id="filter" .items="${comboboxItems}"
                                      clear-button-visible
                                      @value-changed="${(e: CustomEvent) => {
                // @ts-ignore
                return (e.target as HTMLElement).parentElement.value = e.detail.value;
            }}">
                    </vaadin-combo-box>
                </vaadin-grid-filter>`
            , root);
    }

    private createColumnHeader(name: any) {
        return name.charAt(0).toUpperCase() + name.substr(1);
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

    public @Nonnull List<@Nonnull Integer> findDistinctCounter() {
        return personRepository.findDistinctCounter();
    }
}
```