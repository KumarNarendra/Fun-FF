import {Franchise} from '../entities/franchise';

export class FranchiseStandingsModel {
    constructor(
        public rank?: number,
        public franchise?: Franchise,
        public matchesPlayed?: number,
        public currentForm?: boolean[],
        public points?: number,
    ) {
    }
}
