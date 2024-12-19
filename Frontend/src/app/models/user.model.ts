import { Vote } from "./vote.model";

export interface User {
    user_name: string;
    votes?: Vote[];
    score: number;
  }