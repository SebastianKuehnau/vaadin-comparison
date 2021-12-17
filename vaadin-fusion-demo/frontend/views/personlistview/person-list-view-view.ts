
import "@vaadin/vaadin-grid";
import "@vaadin/grid/src/vaadin-grid-column";
import "@vaadin/vertical-layout";
import { html, customElement } from "lit-element";
import { View } from "../../views/view";
import { GridDataProviderCallback, GridDataProviderParams } from "@vaadin/grid/src/vaadin-grid";
import { PersonEndpoint } from "Frontend/generated/endpoints";
import Person from "Frontend/generated/com/example/application/backend/Person";

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
