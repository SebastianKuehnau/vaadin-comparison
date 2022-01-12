import "@vaadin/vaadin-grid";
import "@vaadin/vaadin-text-field";
import '@vaadin/vaadin-combo-box';
import "@vaadin/vertical-layout";
import "@vaadin/grid/vaadin-grid-filter";
import "@vaadin/grid/vaadin-grid-sorter";
import "@vaadin/grid/vaadin-grid-filter-column";
import {html} from "lit";
import {render} from "lit-html";
import {customElement, state} from "lit/decorators.js";
import {guard} from 'lit/directives/guard.js';
import {View} from "../../views/view";
import {PersonEndpoint} from "Frontend/generated/endpoints";
import Person from "Frontend/generated/org/example/fusion/backend/Person";
import {GridColumn, GridDataProvider} from "@vaadin/grid";

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

