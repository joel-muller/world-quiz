import { Category } from './Category';

export interface Card {
  placeId: number;
  category: Category;
  front: string;
  infoFront: string;
  back: string;
  infoBack: string;
}
