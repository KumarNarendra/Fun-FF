import {NgModule, CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA} from '@angular/core';
import { RouterModule } from '@angular/router';

import { FafiSharedModule } from '../shared';

import { POINTS_TABLE_ROUTE, PointsTableComponent, StatisticsService } from './';

import { NvD3Module } from 'angular2-nvd3';

@NgModule({
    imports: [
      FafiSharedModule,
      RouterModule.forRoot([ POINTS_TABLE_ROUTE ], { useHash: true }),
        NvD3Module
    ],
    declarations: [
      PointsTableComponent,
    ],
    entryComponents: [
    ],
    providers: [
        StatisticsService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA]
})
export class FafiAppPointsTableModule {}
