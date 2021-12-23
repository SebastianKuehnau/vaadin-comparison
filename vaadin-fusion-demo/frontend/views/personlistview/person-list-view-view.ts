
import "@vaadin/vaadin-grid";
import "@vaadin/vaadin-text-field";
import "@vaadin/grid/src/vaadin-grid-filter";
import "@vaadin/grid/src/vaadin-grid-sorter";
import "@vaadin/grid/src/vaadin-grid-filter-column";
import "@vaadin/vertical-layout";
import { html, customElement, state } from "lit-element";
import { View } from "../../views/view";
import { PersonEndpoint } from "Frontend/generated/endpoints";
import Person from "Frontend/generated/org/example/fusion/backend/Person";
import {GridColumn} from "@vaadin/grid";

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
            <vaadin-grid-column .headerRenderer="${this.headerFilterSortRenderer}" path="firstname"></vaadin-grid-column>
            <vaadin-grid-column .headerRenderer="${this.headerFilterSortRenderer}" path="lastname"></vaadin-grid-column>
            <vaadin-grid-column .headerRenderer="${this.headerFilterSortRenderer}" path="email"></vaadin-grid-column>
          </vaadin-grid>
      </vaadin-vertical-layout>
    `;
  }

  private headerFilterSortRenderer = (root: HTMLElement, column: GridColumn) => {
    let pathName = column.path;

    if (typeof pathName === "string") {

      // Create header for column
      let header = root.querySelector('div');
      if (!header) {
        header = document.createElement('div');
        header.textContent = pathName.charAt(0).toUpperCase() + pathName.substr(1);

        root.appendChild(header);
      }

      // Create filter control
      let filter = root.querySelector('vaadin-grid-filter');
      if (!filter) {
        filter = document.createElement('vaadin-grid-filter');
        root.appendChild(filter);
      }
      filter.path = pathName;

      // Create sorter control
      let sorter = root.querySelector('vaadin-grid-sorter');
      if (!sorter) {
        sorter = document.createElement('vaadin-grid-sorter');
        root.appendChild(sorter);
      }
      sorter.path = pathName;
    }
  };
}
