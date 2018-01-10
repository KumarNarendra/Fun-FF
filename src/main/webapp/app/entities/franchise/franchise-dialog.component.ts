import { Component, OnInit, OnDestroy, ElementRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Response } from '@angular/http';

import { Observable } from 'rxjs/Rx';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager, JhiAlertService, JhiDataUtils } from 'ng-jhipster';

import { Franchise } from './franchise.model';
import { FranchisePopupService } from './franchise-popup.service';
import { FranchiseService } from './franchise.service';
import { Player, PlayerService } from '../player';
import { Season, SeasonService } from '../season';
import { ResponseWrapper } from '../../shared';

@Component({
    selector: 'fafi-franchise-dialog',
    templateUrl: './franchise-dialog.component.html'
})
export class FranchiseDialogComponent implements OnInit {

    franchise: Franchise;
    isSaving: boolean;

    seasons: Season[];

    owners: Player[];

    iconplayers: Player[];

    constructor(
        public activeModal: NgbActiveModal,
        private dataUtils: JhiDataUtils,
        private jhiAlertService: JhiAlertService,
        private franchiseService: FranchiseService,
        private playerService: PlayerService,
        private seasonService: SeasonService,
        private elementRef: ElementRef,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
        this.seasonService.query()
            .subscribe((res: ResponseWrapper) => { this.seasons = res.json; }, (res: ResponseWrapper) => this.onError(res.json));
        this.playerService
            .query({filter: 'franchise-is-null'})
            .subscribe((res: ResponseWrapper) => {
                if (!this.franchise.ownerId) {
                    this.owners = res.json;
                } else {
                    this.playerService
                        .find(this.franchise.ownerId)
                        .subscribe((subRes: Player) => {
                            this.owners = [subRes].concat(res.json);
                        }, (subRes: ResponseWrapper) => this.onError(subRes.json));
                }
            }, (res: ResponseWrapper) => this.onError(res.json));
        this.playerService
            .query({filter: 'franchise-is-null'})
            .subscribe((res: ResponseWrapper) => {
                if (!this.franchise.iconPlayerId) {
                    this.iconplayers = res.json;
                } else {
                    this.playerService
                        .find(this.franchise.iconPlayerId)
                        .subscribe((subRes: Player) => {
                            this.iconplayers = [subRes].concat(res.json);
                        }, (subRes: ResponseWrapper) => this.onError(subRes.json));
                }
            }, (res: ResponseWrapper) => this.onError(res.json));
    }

    byteSize(field) {
        return this.dataUtils.byteSize(field);
    }

    openFile(contentType, field) {
        return this.dataUtils.openFile(contentType, field);
    }

    setFileData(event, entity, field, isImage) {
        this.dataUtils.setFileData(event, entity, field, isImage);
    }

    clearInputImage(field: string, fieldContentType: string, idInput: string) {
        this.dataUtils.clearInputImage(this.franchise, this.elementRef, field, fieldContentType, idInput);
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.franchise.id !== undefined) {
            this.subscribeToSaveResponse(
                this.franchiseService.update(this.franchise));
        } else {
            this.subscribeToSaveResponse(
                this.franchiseService.create(this.franchise));
        }
    }

    private subscribeToSaveResponse(result: Observable<Franchise>) {
        result.subscribe((res: Franchise) =>
            this.onSaveSuccess(res), (res: Response) => this.onSaveError());
    }

    private onSaveSuccess(result: Franchise) {
        this.eventManager.broadcast({ name: 'franchiseListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError() {
        this.isSaving = false;
    }

    private onError(error: any) {
        this.jhiAlertService.error(error.message, null, null);
    }

    trackSeasonById(index: number, item: Season) {
        return item.id;
    }

    trackPlayerById(index: number, item: Player) {
        return item.id;
    }
}

@Component({
    selector: 'fafi-franchise-popup',
    template: ''
})
export class FranchisePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private franchisePopupService: FranchisePopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.franchisePopupService
                    .open(FranchiseDialogComponent as Component, params['id']);
            } else {
                this.franchisePopupService
                    .open(FranchiseDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
