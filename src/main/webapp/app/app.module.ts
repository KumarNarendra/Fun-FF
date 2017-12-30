import './vendor.ts';

import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { Ng2Webstorage } from 'ngx-webstorage';

import { FafiSharedModule, UserRouteAccessService } from './shared';
import { FafiAppRoutingModule } from './app-routing.module';
import { FafiHomeModule } from './home/home.module';
import { FafiAdminModule } from './admin/admin.module';
import { FafiAccountModule } from './account/account.module';
import { FafiEntityModule } from './entities/entity.module';
import { FafiGameModule } from './games/game.module';
import { customHttpProvider } from './blocks/interceptor/http.provider';
import { PaginationConfig } from './blocks/config/uib-pagination.config';

import { FafiAppAboutUsModule } from './about-us/about-us.module';
import { FafiAppContactUsModule } from './contact-us/contact-us.module';
import { FafiAppPointsTableModule } from './points-table/points-table.module';
import { FafiAppFixturesModule } from './fixtures/fixtures.module';
// jhipster-needle-angular-add-module-import JHipster will add new module here

import {
    FafiMainComponent,
    NavbarComponent,
    FooterComponent,
    ProfileService,
    PageRibbonComponent,
    ActiveMenuDirective,
    ErrorComponent
} from './layouts';

@NgModule({
    imports: [
        BrowserModule,
        FafiAppRoutingModule,
        Ng2Webstorage.forRoot({ prefix: 'jhi', separator: '-'}),
        FafiSharedModule,
        FafiHomeModule,
        FafiAdminModule,
        FafiAccountModule,
        FafiEntityModule,
        FafiAppAboutUsModule,
        FafiAppContactUsModule,
        FafiAppPointsTableModule,
        FafiGameModule,
        FafiAppFixturesModule,
        // jhipster-needle-angular-add-module JHipster will add new module here
    ],
    declarations: [
        FafiMainComponent,
        NavbarComponent,
        ErrorComponent,
        PageRibbonComponent,
        ActiveMenuDirective,
        FooterComponent
    ],
    providers: [
        ProfileService,
        customHttpProvider(),
        PaginationConfig,
        UserRouteAccessService
    ],
    bootstrap: [ FafiMainComponent ]
})
export class FafiAppModule {}
