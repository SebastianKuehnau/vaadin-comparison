
import "@vaadin/vaadin-grid";
import "@vaadin/grid/src/vaadin-grid-column";
import "@vaadin/vertical-layout";
import { html, customElement, state } from "lit-element";
import { View } from "../../views/view";
import { PersonEndpoint } from "Frontend/generated/endpoints";
import Person from "Frontend/generated/org/example/hilla/backend/Person";

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
            <vaadin-grid-column path="firstname"></vaadin-grid-column>
            <vaadin-grid-column path="lastname"></vaadin-grid-column>
            <vaadin-grid-column path="email"></vaadin-grid-column>
          </vaadin-grid>
      </vaadin-vertical-layout>
    `;
  }
}
