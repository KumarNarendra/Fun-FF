import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Response } from '@angular/http';

import { Observable } from 'rxjs/Rx';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { TieMatch } from './tie-match.model';
import { TieMatchPopupService } from './tie-match-popup.service';
import { TieMatchService } from './tie-match.service';
import { Match, MatchService } from '../match';
import { TieTeam, TieTeamService } from '../tie-team';
import { ResponseWrapper } from '../../shared';

@Component({
    selector: 'fafi-tie-match-dialog',
    templateUrl: './tie-match-dialog.component.html'
})
export class TieMatchDialogComponent implements OnInit {

    tieMatch: TieMatch;
    isSaving: boolean;

    matches: Match[];

    tieteams: TieTeam[];

    constructor(
        public activeModal: NgbActiveModal,
        private jhiAlertService: JhiAlertService,
        private tieMatchService: TieMatchService,
        private matchService: MatchService,
        private tieTeamService: TieTeamService,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
        this.matchService.query()
            .subscribe((res: ResponseWrapper) => { this.matches = res.json; }, (res: ResponseWrapper) => this.onError(res.json));
        this.tieTeamService.query()
            .subscribe((res: ResponseWrapper) => { this.tieteams = res.json; }, (res: ResponseWrapper) => this.onError(res.json));
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.tieMatch.id !== undefined) {
            this.subscribeToSaveResponse(
                this.tieMatchService.update(this.tieMatch));
        } else {
            this.subscribeToSaveResponse(
                this.tieMatchService.create(this.tieMatch));
        }
    }

    private subscribeToSaveResponse(result: Observable<TieMatch>) {
        result.subscribe((res: TieMatch) =>
            this.onSaveSuccess(res), (res: Response) => this.onSaveError());
    }

    private onSaveSuccess(result: TieMatch) {
        this.eventManager.broadcast({ name: 'tieMatchListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError() {
        this.isSaving = false;
    }

    private onError(error: any) {
        this.jhiAlertService.error(error.message, null, null);
    }

    trackMatchById(index: number, item: Match) {
        return item.id;
    }

    trackTieTeamById(index: number, item: TieTeam) {
        return item.id;
    }
}

@Component({
    selector: 'fafi-tie-match-popup',
    template: ''
})
export class TieMatchPopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private tieMatchPopupService: TieMatchPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.tieMatchPopupService
                    .open(TieMatchDialogComponent as Component, params['id']);
            } else {
                this.tieMatchPopupService
                    .open(TieMatchDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
