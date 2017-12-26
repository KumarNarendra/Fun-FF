import { BaseEntity } from './../../shared';

export class Match implements BaseEntity {
    constructor(
        public id?: number,
        public startDateTime?: any,
        public endDateTime?: any,
        public pointsForFranchise1?: number,
        public pointsForFranchise2?: number,
        public tournamentId?: number,
        public franchise1Id?: number,
        public franchise2Id?: number,
        public winnerId?: number,
        public tournamentName?: string,
        public franchise1Name?: string,
        public franchise2Name?: string,
        public winnerName?: string,
    ) {
    }
}
