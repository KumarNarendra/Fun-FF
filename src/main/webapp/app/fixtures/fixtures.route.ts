import { Route } from '@angular/router';

import { UserRouteAccessService } from '../shared';
import { FixturesComponent } from './';

export const FIXTURES_ROUTE: Route = {
  path: 'fixtures',
  component: FixturesComponent,
  data: {
    authorities: [],
    pageTitle: 'fixtures.title'
  },
  canActivate: [UserRouteAccessService]
};
