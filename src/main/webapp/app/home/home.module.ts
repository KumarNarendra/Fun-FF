import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { FafiSharedModule } from '../shared';

import { HOME_ROUTE, HomeComponent } from './';
import { ActiveEntityDataService } from '../shared/active-entity/active-entity.service';

@NgModule({
    imports: [
        FafiSharedModule,
        RouterModule.forChild([ HOME_ROUTE ])
    ],
    declarations: [
        HomeComponent,
    ],
    entryComponents: [
    ],
    providers: [
        ActiveEntityDataService
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class FafiHomeModule {}
