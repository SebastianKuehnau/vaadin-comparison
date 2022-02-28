import { Route } from '@vaadin/router';
import './views/personlistview/person-list-view-view';

export type ViewRoute = Route & {
  title?: string;
  icon?: string;
  children?: ViewRoute[];
};

export const views: ViewRoute[] = [
  // place routes below (more info https://vaadin.com/docs/latest/fusion/routing/overview)
  {
    path: '',
    component: 'person-list-view-view',
    icon: 'la la-file',
    title: 'PersonListView',
  },
];
export const routes: ViewRoute[] = [...views];
