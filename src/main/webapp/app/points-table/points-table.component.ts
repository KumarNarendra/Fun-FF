import { Component, OnInit } from '@angular/core';
import { StatisticsService } from './statistics.service';
import { ResponseWrapper } from '../shared';
import { FranchiseStandingsModel } from './statistics.model';
import { JhiAlertService } from 'ng-jhipster';

@Component({
    selector: 'fafi-points-table',
    templateUrl: './points-table.component.html',
    styleUrls: [
        'points-table.scss'
    ]
})
export class PointsTableComponent implements OnInit {

    franchiseStandings: FranchiseStandingsModel[];

    constructor(
        private statisticsService: StatisticsService,
        private jhiAlertService: JhiAlertService,
    ) {
    }

    ngOnInit() {
        this.loadFranchiseStandings();
    }

    private loadFranchiseStandings() {
        this.statisticsService.getFranchiseStandings(1).subscribe(
            (res: ResponseWrapper) => this.franchiseStandings = res.json,
            (res: ResponseWrapper) => this.onError(res.json)
        );
    }

    private onError(error) {
        this.jhiAlertService.error(error.message, null, null);
    }
}
