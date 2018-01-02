import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { JhiEventManager, JhiParseLinks, JhiAlertService } from 'ng-jhipster';

import { ITEMS_PER_PAGE, ResponseWrapper } from '../shared';
import { Franchise, FranchiseService } from '../entities/franchise';
import { SeasonService, Season } from '../entities/season';
import { ActiveEntityData } from '../shared/active-entity/active-entity.model';
import { ActiveEntityDataService } from '../shared/active-entity/active-entity.service';

@Component({
    selector: 'fafi-home',
    templateUrl: './home.component.html',
    styleUrls: [
        'home.scss'
    ]

})
export class HomeComponent implements OnInit {
    franchises: Franchise[];
    seasons: Season[];

    constructor(
        private jhiAlertService: JhiAlertService,
        private seasonService: SeasonService,
        private franchiseService: FranchiseService,
        private activeEntityDataService: ActiveEntityDataService
    ) {
    }

    ngOnInit() {
        this.loadAll();
    }

    loadAll() {
        this.franchiseService.query().subscribe(
            (res: ResponseWrapper) => this.franchises = res.json,
            (res: ResponseWrapper) => this.onError(res.json));

        this.seasonService.query()
            .subscribe(
            (res: ResponseWrapper) => {
                this.seasons = res.json;
                this.loadActiveEntity(this.seasons);
            },
            (res: ResponseWrapper) => this.onError(res.json));
    }

    trackId(index: number, item: Franchise) {
        return item.id;
    }

    private onError(error) {
        this.jhiAlertService.error(error.message, null, null);
    }

    private loadActiveEntity(seasons: Season[]) {
        const activeEntity: ActiveEntityData = new ActiveEntityData(0, 0);
        activeEntity.activeSeasonId = seasons[0].id;
        this.activeEntityDataService.changeActiveEntityData(activeEntity);
    }
}
