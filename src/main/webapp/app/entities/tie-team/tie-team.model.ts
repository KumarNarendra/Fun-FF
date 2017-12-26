import { BaseEntity } from './../../shared';
import {Player} from '../player';

export class TieTeam implements BaseEntity {
    constructor(
        public id?: number,
        public name?: string,
        public points?: number,
        public tiePlayers?: Player[],
        public franchiseId?: number,
        public franchiseName?: string,
    ) {
    }
}
