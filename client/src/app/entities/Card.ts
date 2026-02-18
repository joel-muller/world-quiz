import { Category } from './Category';

export interface Card {
  placeId: number;
  category: Category;
  front: string;
  frontInfo: string;
  back: string;
  backInfo: string;
}
