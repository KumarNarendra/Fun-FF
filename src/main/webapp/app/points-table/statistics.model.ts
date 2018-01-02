import { Franchise } from '../entities/franchise';
import { Player } from '../entities/player';

export class FranchiseStandingsModel {
    constructor(
        public rank?: number,
        public franchise?: Franchise,
        public totalMatchesPlayed?: number,
        public currentForm?: boolean[],
        public totalPoints?: number,
        public matchWiseDetails?: MatchWiseDetails[],
    ) {
    }
}
class MatchWiseDetails {
    constructor(
        public matchId?: number,
        public matchResult?: boolean[],
        public matchPoints?: number,
    ) {
    }
}

export class PlayerStandingsModel {
    constructor(
        public rank?: number,
        public franchise?: Franchise,
        public player?: Player,
        public totalMatchesPlayed?: number,
        public currentForm?: boolean[],
        public totalPoints?: number,
    ) {
    }
}
