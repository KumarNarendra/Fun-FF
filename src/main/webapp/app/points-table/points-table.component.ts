import { Component, OnInit } from '@angular/core';
import { StatisticsService } from './statistics.service';
import { ResponseWrapper } from '../shared';
import { FranchiseStandingsModel, PlayerStandingsModel } from './statistics.model';
import { JhiAlertService } from 'ng-jhipster';
import { COLORS } from '../app.constants';
import { Season, SeasonService } from '../entities/season';
import { ActiveEntityDataService } from '../shared/active-entity/active-entity.service';

@Component({
    selector: 'fafi-points-table',
    templateUrl: './points-table.component.html',
    styleUrls: [
        'points-table.scss'
    ]
})
export class PointsTableComponent implements OnInit {

    seasons: Season[];
    seasonId: number;

    franchiseStandings: FranchiseStandingsModel[];
    lineChartOptions: any;
    lineChartData: any;

    playerStandings: PlayerStandingsModel[];
    playerLineChartOptions: any;
    playerLineChartData: any;

    constructor(
        private statisticsService: StatisticsService,
        private seasonService: SeasonService,
        private jhiAlertService: JhiAlertService,
        private activeEntityDataService: ActiveEntityDataService
    ) {
    }

    ngOnInit() {
        this.seasonService.query()
            .subscribe((res: ResponseWrapper) => { this.seasons = res.json; }, (res: ResponseWrapper) => this.onError(res.json));

        this.activeEntityDataService.currentActiveEntityData.subscribe((activeEntityData) => this.seasonId = activeEntityData.activeSeasonId);
        this.loadAllStandings();
    }

    loadAllStandings() {
        this.loadFranchiseStandings();
        this.loadPlayerStandings();
    }

    private loadFranchiseStandings() {
        this.statisticsService.getFranchiseStandings(this.seasonId).subscribe(
            (res: ResponseWrapper) => {
                this.franchiseStandings = res.json;
                this.loadLineChartData(this.franchiseStandings);
            },
            (res: ResponseWrapper) => this.onError(res.json)
        );
    }

    private loadPlayerStandings() {
        this.statisticsService.getPlayerStandings(this.seasonId).subscribe(
            (res: ResponseWrapper) => {
                this.playerStandings = res.json;
                this.loadPlayerLineChartData(this.playerStandings);
            },
            (res: ResponseWrapper) => this.onError(res.json)
        );
    }

    trackSeasonById(index: number, item: Season) {
        return item.id;
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
        let j = 0;
        franchiseStandingsData.forEach((franchiseStanding) => {
            const dataCoordinates = [];
            dataCoordinates.push({ x: 0, y: 0 });
            let i = 0;
            let totalPoints = 0;
            franchiseStanding.matchWiseDetails.forEach((matchDetails) => {
                totalPoints = totalPoints + matchDetails.matchPoints;
                dataCoordinates.push({ x: ++i, y: totalPoints });
            });
            const lineData = {
                values: dataCoordinates,
                key: franchiseStanding.franchise.name,
                color: COLORS[j],
            };
            j = j + 1;
            dataArray.push(lineData);
        });
        this.lineChartData = dataArray;
    }

    private loadPlayerLineChartData(playerStandingsData) {
        this.playerLineChartOptions = {
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
        for (let index = 0; index < playerStandingsData.length && index < 10; index++) {
            const playerStanding = playerStandingsData[index];
            const dataCoordinates = [];
            dataCoordinates.push({ x: 0, y: 0 });
            let i = 0;
            let totalPoints = 0;
            playerStanding.matchWiseDetails.forEach((matchDetails) => {
                totalPoints = totalPoints + matchDetails.matchPoints;
                dataCoordinates.push({ x: ++i, y: totalPoints });
            });
            const lineData = {
                values: dataCoordinates,
                key: playerStanding.player.name,
            };
            dataArray.push(lineData);
        }
        this.playerLineChartData = dataArray;
    }

}
