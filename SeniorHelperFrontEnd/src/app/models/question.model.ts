import { Answer } from './answer.model';

export interface Question {
    id?: number;
    text: string;
    answers?: Answer[];
}