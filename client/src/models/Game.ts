import  { type Card } from '@/models/Card.ts'

export interface Game {
  Id: number
  Category: number
  Cards: Card[]
}
