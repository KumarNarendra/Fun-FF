import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Response } from '@angular/http';

import { Observable } from 'rxjs/Rx';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { TieTeam } from './tie-team.model';
import { TieTeamPopupService } from './tie-team-popup.service';
import { TieTeamService } from './tie-team.service';
import { Player, PlayerService } from '../player';
import { Franchise, FranchiseService } from '../franchise';
import { ResponseWrapper } from '../../shared';

@Component({
    selector: 'fafi-tie-team-dialog',
    templateUrl: './tie-team-dialog.component.html'
})
export class TieTeamDialogComponent implements OnInit {

    tieTeam: TieTeam;
    isSaving: boolean;

    players: Player[];
    franchise: Franchise;
    franchises: Franchise[];

    constructor(
        public activeModal: NgbActiveModal,
        private jhiAlertService: JhiAlertService,
        private tieTeamService: TieTeamService,
        private playerService: PlayerService,
        private franchiseService: FranchiseService,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
        this.playerService.query()
            .subscribe((res: ResponseWrapper) => { this.players = res.json; }, (res: ResponseWrapper) => this.onError(res.json));
        this.franchiseService.query()
            .subscribe((res: ResponseWrapper) => { this.franchises = res.json; }, (res: ResponseWrapper) => this.onError(res.json));
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        this.tieTeam.name = '';
        this.tieTeam.franchiseId = this.franchise.id;
        this.tieTeam.name = this.tieTeam.name.concat(this.franchise.name).concat('-');
        this.tieTeam.tiePlayers.forEach((value) => this.tieTeam.name = this.tieTeam.name.concat(',').concat(value.name));
console.log('name = ', this.tieTeam.name);
        if (this.tieTeam.id !== undefined) {
            this.subscribeToSaveResponse(
                this.tieTeamService.update(this.tieTeam));
        } else {
            this.subscribeToSaveResponse(
                this.tieTeamService.create(this.tieTeam));
        }
    }

    private subscribeToSaveResponse(result: Observable<TieTeam>) {
        result.subscribe((res: TieTeam) =>
            this.onSaveSuccess(res), (res: Response) => this.onSaveError());
    }

    private onSaveSuccess(result: TieTeam) {
        this.eventManager.broadcast({ name: 'tieTeamListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError() {
        this.isSaving = false;
    }

    private onError(error: any) {
        this.jhiAlertService.error(error.message, null, null);
    }

    trackPlayerById(index: number, item: Player) {
        return item.id;
    }

    trackFranchiseById(index: number, item: Franchise) {
        return item.id;
    }

    getSelected(selectedVals: Array<any>, option: any) {
        if (selectedVals) {
            for (let i = 0; i < selectedVals.length; i++) {
                if (option.id === selectedVals[i].id) {
                    return selectedVals[i];
                }
            }
        }
        return option;
    }
}

@Component({
    selector: 'fafi-tie-team-popup',
    template: ''
})
export class TieTeamPopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private tieTeamPopupService: TieTeamPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.tieTeamPopupService
                    .open(TieTeamDialogComponent as Component, params['id']);
            } else {
                this.tieTeamPopupService
                    .open(TieTeamDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
