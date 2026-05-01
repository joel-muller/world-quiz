import { Category } from './Category';
import { Tag } from './Tag';

export interface LoginRequest {
  usernameOrEmail: string;
  password: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
}

export interface RefreshRequest {
  refreshToken: string;
}

export interface RequestGame {
  categories: Category[];
  tags: Tag[];
  number: number | undefined;
}

export interface RequestUser {
  username: string;
  email: string;
  password: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
}

export interface RefreshRequest {
  refreshToken: string;
}
