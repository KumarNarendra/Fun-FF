import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { ActiveEntityData } from './active-entity.model';

@Injectable()
export class ActiveEntityDataService {

    private activeEntityDataSource = new BehaviorSubject<ActiveEntityData>(new ActiveEntityData(0, 0));
    currentActiveEntityData = this.activeEntityDataSource.asObservable();

    constructor() {
    }

    changeActiveEntityData(activeEntityData: ActiveEntityData) {
        this.activeEntityDataSource.next(activeEntityData);
    }
}
