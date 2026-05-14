import { Component, computed, inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../auth-service';

@Component({
  selector: 'app-dashboard',
  imports: [],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard {
  private authService = inject(AuthService);
  private router = inject(Router);

  currentUser = computed(() => {
    return this.authService.currentUser();
  });

  async logout() {
    this.authService.logout();
    await this.router.navigate(['']);
  }
}
