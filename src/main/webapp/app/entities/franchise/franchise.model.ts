import { BaseEntity } from './../../shared';

export class Franchise implements BaseEntity {
    constructor(
        public id?: number,
        public name?: string,
        public logoPath?: string,
        public points?: number,
        public logoContentType?: string,
        public logo?: any,
        public players?: BaseEntity[],
        public seasonId?: number,
        public ownerId?: number,
        public iconPlayerId?: number,
        public seasonName?: string,
        public ownerName?: string,
        public iconPlayerName?: string,
    ) {
    }
}
