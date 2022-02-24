
import "@vaadin/vaadin-grid";
import "@vaadin/grid/src/vaadin-grid-column";
import "@vaadin/vertical-layout";
import "@vaadin/vaadin-icon";
import "@vaadin/vaadin-button";
import { html } from "lit";
import { customElement, property, query } from 'lit/decorators.js';
import { View } from "../../views/view";
import {Grid, GridDataProviderCallback, GridDataProviderParams, GridItemModel} from "@vaadin/grid";
import { PersonEndpoint } from "Frontend/generated/endpoints";
import Person from "Frontend/generated/org/example/hilla/backend/Person";
import {GridColumnElement} from "@vaadin/vaadin-grid";
import { render } from "lit";
import { router } from "./../../index";

@customElement('person-list-view')
export class PersonListView extends View {

  @query('#grid')
  private grid!: Grid;

  @property({ type: Number })
  private gridSize = 0;

  private gridDataProvider = this.getGridData.bind(this);

  async connectedCallback() {
    super.connectedCallback();

    this.gridSize = (await PersonEndpoint.count()) ?? 0;
  }

  render() {
    return html`
      <vaadin-vertical-layout class="w-full h-full">
          <vaadin-grid id="grid" class="w-full h-full" theme="no-border" .size=${this.gridSize} .dataProvider=${this.gridDataProvider}>
            <vaadin-grid-column path="firstname"></vaadin-grid-column>
            <vaadin-grid-column path="lastname"></vaadin-grid-column>
            <vaadin-grid-column path="email"></vaadin-grid-column>
            <vaadin-grid-column .renderer="${this.iconRenderer}"></vaadin-grid-column>
          </vaadin-grid>
      </vaadin-vertical-layout>
    `;
  }

  iconRenderer(root: HTMLElement,
               column: GridColumnElement,
               model: GridItemModel<Person>) {

    render(html`
      <a href=${router.urlForPath('person-form/' + model.item.id)}>
        <vaadin-icon icon="lumo:edit"></vaadin-icon>
      </a>`, root);
  };

  private async getGridData(params: GridDataProviderParams<Person>,
                     callback: GridDataProviderCallback<Person | undefined>) {

    const page = await PersonEndpoint.getPage(params.page, params.pageSize);

    callback(page);
  }
}

