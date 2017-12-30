import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Response } from '@angular/http';

import { Observable } from 'rxjs/Rx';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { Match } from './match.model';
import { MatchPopupService } from './match-popup.service';
import { MatchService } from './match.service';
import { Tournament, TournamentService } from '../tournament';
import { Franchise, FranchiseService } from '../franchise';
import { ResponseWrapper } from '../../shared';

@Component({
    selector: 'fafi-match-dialog',
    templateUrl: './match-dialog.component.html'
})
export class MatchDialogComponent implements OnInit {

    match: Match;
    isSaving: boolean;

    tournaments: Tournament[];

    franchises: Franchise[];

    constructor(
        public activeModal: NgbActiveModal,
        private jhiAlertService: JhiAlertService,
        private matchService: MatchService,
        private tournamentService: TournamentService,
        private franchiseService: FranchiseService,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
        this.tournamentService.query()
            .subscribe((res: ResponseWrapper) => { this.tournaments = res.json; }, (res: ResponseWrapper) => this.onError(res.json));
        this.franchiseService.query()
            .subscribe((res: ResponseWrapper) => { this.franchises = res.json; }, (res: ResponseWrapper) => this.onError(res.json));
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.match.id !== undefined) {
            this.subscribeToSaveResponse(
                this.matchService.update(this.match));
        } else {
            this.subscribeToSaveResponse(
                this.matchService.create(this.match));
        }
    }

    private subscribeToSaveResponse(result: Observable<Match>) {
        result.subscribe((res: Match) =>
            this.onSaveSuccess(res), (res: Response) => this.onSaveError());
    }

    private onSaveSuccess(result: Match) {
        this.eventManager.broadcast({ name: 'matchListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError() {
        this.isSaving = false;
    }

    private onError(error: any) {
        this.jhiAlertService.error(error.message, null, null);
    }

    trackTournamentById(index: number, item: Tournament) {
        return item.id;
    }

    trackFranchiseById(index: number, item: Franchise) {
        return item.id;
    }
}

@Component({
    selector: 'fafi-match-popup',
    template: ''
})
export class MatchPopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private matchPopupService: MatchPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.matchPopupService
                    .open(MatchDialogComponent as Component, params['id']);
            } else {
                this.matchPopupService
                    .open(MatchDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
