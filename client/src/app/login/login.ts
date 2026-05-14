import { Component, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../auth-service';
import { RegisterRequest } from '../entities/Dto';

enum Page {
  Login,
  Register,
}

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  authService = inject(AuthService);
  router = inject(Router);

  loading = signal(false);
  error = signal<string | null>(null);
  info = signal<string | null>(null);
  page = signal<Page>(Page.Login);

  protected readonly Page = Page;

  loginForm = new FormGroup({
    usernameOrEmail: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    password: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
  });

  registerForm = new FormGroup({
    username: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    password1: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    password2: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    email: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
  });

  login() {
    if (this.loginForm.invalid) return;

    this.loading.set(true);
    this.error.set(null);

    this.authService.login(this.loginForm.getRawValue()).subscribe({
      next: async () => {
        this.loading.set(false);
        await this.router.navigate(['']);
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Login failed');
      },
    });
  }

  register() {
    if (this.registerForm.invalid) return;

    this.loading.set(true);
    this.error.set(null);

    if (this.registerForm.getRawValue().password2 !== this.registerForm.getRawValue().password1) {
      this.error.set('Passwords do not match');
      return;
    }

    const req: RegisterRequest = {
      username: this.registerForm.getRawValue().username,
      password: this.registerForm.getRawValue().password1,
      email: this.registerForm.getRawValue().email,
    };

    this.authService.createUser(req).subscribe({
      next: () => {
        this.loading.set(false);
        this.info.set('Registration Successful, verify the email we sent to you');
        this.page.set(Page.Login);
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Registration failed');
      },
    });
  }
}
