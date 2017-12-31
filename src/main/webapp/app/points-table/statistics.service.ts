import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs/Rx';
import { SERVER_API_URL } from '../app.constants';

import { ResponseWrapper } from '../shared';
import { FranchiseStandingsModel, PlayerStandingsModel } from './statistics.model';

@Injectable()
export class StatisticsService {

    private resourceUrl = SERVER_API_URL + '/api/statistics';

    private static convertPlayerStandingsResponse(res: Response): ResponseWrapper {
        const jsonResponse = res.json();
        const result = [];
        for (let i = 0; i < jsonResponse.length; i++) {
            result.push(Object.assign(new PlayerStandingsModel(), jsonResponse[i]));
        }
        return new ResponseWrapper(res.headers, result, res.status);
    }

    private static convertResponse(res: Response): ResponseWrapper {
        const jsonResponse = res.json();
        const result = [];
        for (let i = 0; i < jsonResponse.length; i++) {
            result.push(Object.assign(new FranchiseStandingsModel(), jsonResponse[i]));
        }
        return new ResponseWrapper(res.headers, result, res.status);
    }

    constructor(private http: Http) { }

    getFranchiseStandings(seasonsId: number): Observable<ResponseWrapper> {
        return this.http.get(`${this.resourceUrl}/seasons/${seasonsId}/franchise-standings`).map((res: Response) => StatisticsService.convertResponse(res));
    }

    getPlayerStandings(seasonsId: number): Observable<ResponseWrapper> {
        return this.http.get(`${this.resourceUrl}/seasons/${seasonsId}/player-standings`).map((res: Response) => StatisticsService.convertPlayerStandingsResponse(res));
    }
}
