import { Component, inject, signal } from '@angular/core';
import { QuizService } from '../quiz-service';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RequestUser } from '../entities/Dto';

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
  quizService = inject(QuizService);

  loading = signal(false);
  error = signal<string | null>(null);
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

    this.quizService.login(this.loginForm.getRawValue()).subscribe({
      next: () => {
        this.loading.set(false);
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

    // TODO: Check if the two passwords are equal lenght
    const req: RequestUser = {
      username: this.registerForm.getRawValue().username,
      password: this.registerForm.getRawValue().password1,
      email: this.registerForm.getRawValue().email,
    };

    this.quizService.createUser(req).subscribe({
      next: () => {
        this.loading.set(false);
        this.page.set(Page.Login);
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Login failed');
      },
    });
  }
}
