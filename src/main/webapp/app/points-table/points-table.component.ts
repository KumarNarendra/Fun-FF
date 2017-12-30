import { Component, OnInit } from '@angular/core';
import { StatisticsService } from './statistics.service';
import { ResponseWrapper } from '../shared';
import { FranchiseStandingsModel } from './statistics.model';
import { JhiAlertService } from 'ng-jhipster';
import * as d3 from 'd3';

@Component({
    selector: 'fafi-points-table',
    templateUrl: './points-table.component.html',
    styleUrls: [
        'points-table.scss'
    ]
})
export class PointsTableComponent implements OnInit {

    franchiseStandings: FranchiseStandingsModel[];
    lineChartOptions: any;
    lineChartData: any;

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
            (res: ResponseWrapper) => {
                this.franchiseStandings = res.json;
                this.loadLineChartData(this.franchiseStandings);
            },
            (res: ResponseWrapper) => this.onError(res.json)
        );
    }

    private onError(error) {
        this.jhiAlertService.error(error.message, null, null);
    }

    private loadLineChartData(franchiseStandingsData) {
        this.lineChartOptions = {
            chart: {
                type: 'lineChart',
                height: 450,
                margin: {
                    top: 20,
                    right: 20,
                    bottom: 40,
                    left: 75
                },
                x(d) {
                    return d.x;
                },
                y(d) {
                    return d.y;
                },
                useInteractiveGuideline: true,
                xAxis: {
                    axisLabel: 'Matches',
                },
                yAxis: {
                    axisLabel: 'Points',
                },
                callback(chart) {
                }
            },
            title: {
                enable: true,
                text: 'Franchise points growth'
            },
            caption: {
                enable: true,
                html: '',
                css: {
                    'text-align': 'justify',
                    'margin': '10px 13px 0px 7px'
                }
            }
        };
        const dataArray = [];
        franchiseStandingsData.forEach((franchiseStanding) => {
            const dataCoordinates = [];
            dataCoordinates.push({x: 0, y: 0});
            let i = 0;
            let totalPoints = 0;
            franchiseStanding.matchWiseDetails.forEach((matchDetails) => {
                totalPoints = totalPoints + matchDetails.matchPoints;
                dataCoordinates.push({x: ++i, y: totalPoints});
            });
            const lineData = {
                values: dataCoordinates,
                key: franchiseStanding.franchise.name,
            };
            dataArray.push(lineData);
        });
        this.lineChartData = dataArray;
        console.log(this.lineChartData);
    }
}
