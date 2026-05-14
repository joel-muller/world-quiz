import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../auth-service';
import { VerifyEmailRequest } from '../entities/Dto';

@Component({
  selector: 'app-verify',
  imports: [],
  templateUrl: './verify.html',
  styleUrl: './verify.css',
})
export class Verify implements OnInit {
  error = signal<string | null>(null);

  private authService: AuthService = inject(AuthService);
  private route: ActivatedRoute = inject(ActivatedRoute);
  private router: Router = inject(Router);

  ngOnInit(): void {
    const token = this.route.snapshot.paramMap.get('token')!;

    if (!token) {
      this.error.set('Invalid verification link');
      return;
    }

    const request: VerifyEmailRequest = { token };

    this.authService.verifyEmail(request).subscribe({
      next: async () => {
        this.authService.logout();
        await this.router.navigate(['']);
      },
      error: (err) => {
        this.error.set(err?.error?.message || 'Verification failed');
      },
    });
  }
}
