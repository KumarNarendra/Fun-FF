import { BaseEntity } from './../../shared';

export const enum Games {
    'Football',
    'Chess',
    'Badminton',
    'Ludo',
    'Table Tennis',
    'Box Cricket'
}

export class Player implements BaseEntity {
    constructor(
        public id?: number,
        public name?: string,
        public basePrice?: number,
        public bidPrice?: number,
        public optedGames?: Games[],
        public points?: number,
        public franchiseId?: number,
        public franchiseName?: string,
    ) {
    }
}
