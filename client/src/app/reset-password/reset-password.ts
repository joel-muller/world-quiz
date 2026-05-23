import { Component, inject, OnInit, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../auth-service';
import { ResetPasswordRequest } from '../entities/Dto';

@Component({
  selector: 'app-reset-password',
  imports: [ReactiveFormsModule],
  templateUrl: './reset-password.html',
  styleUrl: './reset-password.css',
})
export class ResetPassword implements OnInit {
  error = signal<string | null>(null);
  success = signal<string | null>(null);
  loading = signal(false);

  private authService = inject(AuthService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  token = '';

  form = new FormGroup({
    password1: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
    password2: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
  });

  ngOnInit(): void {
    const token = this.route.snapshot.paramMap.get('token');

    if (!token) {
      this.error.set('Invalid reset link');
      return;
    }

    this.token = token;
  }

  resetPassword() {
    if (this.form.invalid) return;

    const { password1, password2 } = this.form.getRawValue();

    if (password1 !== password2) {
      this.error.set('Passwords do not match');
      return;
    }

    this.loading.set(true);
    this.error.set(null);

    const request: ResetPasswordRequest = {
      token: this.token,
      newPassword: password1,
    };

    this.authService.resetPassword(request).subscribe({
      next: async () => {
        this.loading.set(false);
        this.success.set('Password reset successful');

        setTimeout(async () => {
          await this.router.navigate(['']);
        }, 1500);
      },
      error: (err) => {
        this.loading.set(false);
        this.error.set(err?.error?.message || 'Password reset failed');
      },
    });
  }
}
