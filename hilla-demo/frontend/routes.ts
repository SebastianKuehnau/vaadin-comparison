import { Route } from '@vaadin/router';
import './views/personlistview/person-list-view';
import './views/personformview/person-form-view';

export type ViewRoute = Route & {
  title?: string;
  icon?: string;
  children?: ViewRoute[];
};

export const views: ViewRoute[] = [
  // place routes below (more info https://vaadin.com/docs/latest/fusion/routing/overview)
  {
    path: '',
    component: 'person-list-view',
    icon: 'la la-file',
    title: 'PersonListView',
  },
  {
    path: 'person-list',
    component: 'person-list-view',
    icon: 'la la-file',
    title: 'PersonListView',
  },
  {
    path: 'person-form/:id',
    component: 'person-form-view',
    icon: 'la la-file',
    title: 'PersonFormView',
  },
];
export const routes: ViewRoute[] = [...views];
