import { Vote } from "./vote.model";

export interface Item {
    name: string;
    price: number;
    url: string;
    status: string;
    votes?: Vote[]; 
  }
  