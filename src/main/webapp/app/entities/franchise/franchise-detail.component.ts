import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { JhiEventManager, JhiDataUtils } from 'ng-jhipster';

import { Franchise } from './franchise.model';
import { FranchiseService } from './franchise.service';

@Component({
    selector: 'fafi-franchise-detail',
    templateUrl: './franchise-detail.component.html'
})
export class FranchiseDetailComponent implements OnInit, OnDestroy {

    franchise: Franchise;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: JhiEventManager,
        private dataUtils: JhiDataUtils,
        private franchiseService: FranchiseService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInFranchises();
    }

    load(id) {
        this.franchiseService.find(id).subscribe((franchise) => {
            this.franchise = franchise;
        });
    }
    byteSize(field) {
        return this.dataUtils.byteSize(field);
    }

    openFile(contentType, field) {
        return this.dataUtils.openFile(contentType, field);
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInFranchises() {
        this.eventSubscriber = this.eventManager.subscribe(
            'franchiseListModification',
            (response) => this.load(this.franchise.id)
        );
    }
}
