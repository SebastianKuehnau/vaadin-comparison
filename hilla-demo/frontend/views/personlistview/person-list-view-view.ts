
import "@vaadin/vaadin-grid";
import "@vaadin/grid/src/vaadin-grid-filter-column";
import "@vaadin/text-field"
import "@vaadin/vertical-layout";
import { html } from "lit";
import { customElement, property } from 'lit/decorators.js';
import { View } from "../../views/view";
import { GridDataProviderCallback, GridDataProviderParams } from "@vaadin/vaadin-grid";
import { PersonEndpoint } from "Frontend/generated/endpoints";
import Person from "Frontend/generated/org/example/hilla/backend/Person";

@customElement('person-list-view-view')
export class PersonListViewView extends View {

  @property({ type: Number })
  private gridSize = 0;

  async connectedCallback() {
    super.connectedCallback();

    this.gridSize = (await PersonEndpoint.count()) ?? 0;
  }

  render() {
    return html`
      <vaadin-vertical-layout class="w-full h-full">
          <vaadin-grid id="grid" class="w-full h-full" theme="no-border" .size=${this.gridSize} .dataProvider=${this.dataProvider}>
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
