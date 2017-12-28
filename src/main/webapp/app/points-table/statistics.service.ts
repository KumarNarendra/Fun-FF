import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs/Rx';
import { SERVER_API_URL } from '../app.constants';

import { ResponseWrapper } from '../shared';
import { FranchiseStandingsModel } from './statistics.model';

@Injectable()
export class StatisticsService {

    private resourceUrl = SERVER_API_URL + '/api/statistics';

    constructor(private http: Http) { }

    getFranchiseStandings(seasonsId: number): Observable<ResponseWrapper> {
        return this.http.get(`${this.resourceUrl}/seasons/${seasonsId}/franchise-standings`).map((res: Response) => this.convertResponse(res));
    }

    private convertResponse(res: Response): ResponseWrapper {
        const jsonResponse = res.json();
        const result = [];
        for (let i = 0; i < jsonResponse.length; i++) {
            result.push(this.convertFranchiseStandingsFromServer(jsonResponse[i]));
        }
        return new ResponseWrapper(res.headers, result, res.status);
    }

    /**
     * Convert a returned JSON object to FranchiseStandingsModel.
     */
    private convertFranchiseStandingsFromServer(json: any): FranchiseStandingsModel {
        return Object.assign(new FranchiseStandingsModel(), json);
    }

}
