
import "@vaadin/vaadin-grid";
import "@vaadin/grid/src/vaadin-grid-column";
import "@vaadin/vertical-layout";
import "@vaadin/vaadin-icon";
import { html, customElement } from "lit-element";
import { View } from "../../views/view";
import {BeforeEnterObserver, PreventAndRedirectCommands, Router, RouterLocation} from "@vaadin/router";

@customElement('person-form-view')
export class PersonFormView extends View implements BeforeEnterObserver {

  async connectedCallback() {
    super.connectedCallback();
  }

  render() {
    return html`
      <vaadin-form-layout class="w-full h-full">
        <div>Hello World</div>
      </vaadin-form-layout>
    `;
  }

  onBeforeEnter(
      location: RouterLocation,
      commands: PreventAndRedirectCommands,
      router: Router) {

      console.log(location.params['id']);

  }
}

