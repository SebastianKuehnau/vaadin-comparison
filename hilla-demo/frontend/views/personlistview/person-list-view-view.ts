
import "@vaadin/vaadin-grid";
import "@vaadin/grid/src/vaadin-grid-column";
import "@vaadin/vertical-layout";
import { html, customElement, property } from "lit-element";
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

    this.gridSize = (await PersonEndpoint.count()) ?? 0 ;
  }

  render() {
    return html`
      <vaadin-vertical-layout class="w-full h-full">
          <vaadin-grid id="grid" class="w-full h-full" theme="no-border" .size=${this.gridSize} .dataProvider=${this.dataProvider}>
            <vaadin-grid-column path="firstname"></vaadin-grid-column>
            <vaadin-grid-column path="lastname"></vaadin-grid-column>
            <vaadin-grid-column path="email"></vaadin-grid-column>
          </vaadin-grid>
      </vaadin-vertical-layout>
    `;
  }

  async dataProvider(params: GridDataProviderParams<Person>, callback: GridDataProviderCallback<Person | undefined>) {
    const page = await PersonEndpoint.getPage(params.page, params.pageSize);
    
    callback(page);
  }
}
