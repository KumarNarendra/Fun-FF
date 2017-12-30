import { Component, OnInit } from '@angular/core';
import { ResponseWrapper } from '../shared';
import { JhiAlertService } from 'ng-jhipster';
import { Match, MatchService } from '../entities/match';

@Component({
    selector: 'fafi-fixtures',
    templateUrl: './fixtures.component.html',
    styleUrls: [
        'fixtures.scss'
    ]
})
export class FixturesComponent implements OnInit {

    matches: Match[];

    constructor(private matchService: MatchService,
                private jhiAlertService: JhiAlertService,
                ) {
    }

    ngOnInit() {
        this.loadAll();
    }

    loadAll() {
        this.matchService.query()
            .subscribe((res: ResponseWrapper) => { this.matches = res.json; }, (res: ResponseWrapper) => this.onError(res.json));
    }

    private onError(error) {
        this.jhiAlertService.error(error.message, null, null);
    }
}
