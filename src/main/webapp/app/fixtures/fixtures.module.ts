import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { FafiSharedModule } from '../shared';

import { FIXTURES_ROUTE, FixturesComponent } from './';

@NgModule({
    imports: [
      FafiSharedModule,
      RouterModule.forRoot([ FIXTURES_ROUTE ], { useHash: true })
    ],
    declarations: [
      FixturesComponent,
    ],
    entryComponents: [
    ],
    providers: [
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class FafiAppFixturesModule {}
