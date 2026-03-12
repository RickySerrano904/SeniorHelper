export interface Lesson {
    id: number; 
    title: string;
    description: string;
    completed: boolean;
    contentBlocks?: any[];
}

export interface Answer {
    id?: number;
    text: string;
    correct: boolean;
}

export interface Question {
    id?: number;
    text: string;
    answers?: Answer[];
}

export interface Quiz {
    id?: number;
    name: string;
    questions?: Question[];
    completed: boolean;
}

export interface Module {
    id: number;
    title: string;
    description: string;
    lessons: Lesson[];
    quiz?: Quiz;
}