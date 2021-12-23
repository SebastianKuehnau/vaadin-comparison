
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
import { render } from "lit";

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
            <vaadin-grid-column .headerRenderer="${this.firstnameHeaderRenderer}" path="firstname"></vaadin-grid-column>
            <vaadin-grid-column path="lastname"></vaadin-grid-column>
            <vaadin-grid-filter-column path="email"></vaadin-grid-filter-column>
          </vaadin-grid>
      </vaadin-vertical-layout>
    `;
  }

  private firstnameHeaderRenderer = (root: HTMLElement) => {
    // Create header for column
    let header = root.querySelector('div');
    if (!header) {
      header = document.createElement('div');
      header.textContent = "firstname";
      root.appendChild(header);
    }
    // Create filter control
    let filter = root.querySelector('vaadin-grid-filter');
    if (!filter) {
      filter = document.createElement('vaadin-grid-filter');
      root.appendChild(filter);
    }
    filter.path = 'firstname';

    // Create sorter control
    let sorter = root.querySelector('vaadin-grid-sorter');
    if (!sorter) {
      sorter = document.createElement('vaadin-grid-sorter');
      root.appendChild(sorter);
    }
    sorter.path = 'firstname';
  };
}
