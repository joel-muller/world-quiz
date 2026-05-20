import { Category } from './Category';
import { Tag } from './Tag';

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
}

export interface CreateQuizRequest {
  categories: Category[];
  tags: Tag[];
  number: number;
}

export interface FinishGameRequest {
  id: string;
}

export interface GameStatResponse {
  id: string;
  info: string;
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

export interface UserResponse {
  username: string;
  email: string;
  emailConfirmed: boolean;
}

export interface VerifyEmailRequest {
  token: string;
}
