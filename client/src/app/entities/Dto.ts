import { Category } from './Category';
import { Tag } from './Tag';

export interface TokenDto {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
}

export interface CreateQuizRequest {
  categories: Category[];
  tags: Tag[];
  number: number;
}

export interface FinishQuizRequest {
  id: string;
  cards: CardDto[];
}

export interface QuizStatDto {
  id: string;
  info: string;
  cards: CardStatDto[];
}

export interface CardStatDto {
  placeName: string;
  category: Category;
  guessedRight: number;
  guessedWrong: number;
}

export interface LoginRequest {
  usernameOrEmail: string;
  password: string;
}

export interface RefreshRequest {
  refreshToken: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

export interface ResendVerificationRequest {
  email: string;
}

export interface UserDto {
  username: string;
  email: string;
  emailConfirmed: boolean;
}

export interface VerifyEmailRequest {
  token: string;
}

export interface CardDto {
  placeId: number;
  category: Category;
  front: string;
  infoFront: string;
  back: string;
  infoBack: string;
  guessedRight: number;
  guessedWrong: number;
}

export interface QuizDto {
  id: string;
  cards: CardDto[];
}

export interface ForgotPasswordRequest {
  email: string;
}

export interface ResetPasswordRequest {
  token: string;
  newPassword: string;
}
