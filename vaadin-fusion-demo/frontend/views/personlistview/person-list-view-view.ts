import "@vaadin/vaadin-grid";
import "@vaadin/vaadin-text-field";
import '@vaadin/vaadin-combo-box';
import "@vaadin/grid/src/vaadin-grid-filter";
import "@vaadin/grid/src/vaadin-grid-sorter";
import "@vaadin/grid/src/vaadin-grid-filter-column";
import "@vaadin/vertical-layout";
import {html} from "lit";
import {render} from "lit-html";
import {customElement, state} from "lit/decorators.js";
import {guard} from 'lit/directives/guard.js';
import {View} from "../../views/view";
import {PersonEndpoint} from "Frontend/generated/endpoints";
import Person from "Frontend/generated/org/example/fusion/backend/Person";
import {GridColumn, GridDataProvider} from "@vaadin/grid";
// @ts-ignore
import {createArrayDataProvider} from 'Frontend/views/personlistview/filter-array-data-provider.js';
import {css} from "@vaadin/vaadin-themable-mixin";

@customElement('person-list-view-view')
export class PersonListViewView extends View {

    @state()
    private personItems: Person[] = [];

    private dataProvider: GridDataProvider<Person> = () => {
    };

    async connectedCallback() {
        super.connectedCallback();

        this.personItems = await PersonEndpoint.findAll();

        this.dataProvider = createArrayDataProvider(this.personItems);
    }

    render() {
        return html`
            <vaadin-vertical-layout class="w-full h-full">
                <vaadin-grid id="grid" class="w-full h-full" theme="no-border" .dataProvider="${this.dataProvider}">
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

