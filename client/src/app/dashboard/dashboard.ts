import { Component } from '@angular/core';
import { Login } from '../login/login';
import { AuthenticationService } from '../authentication-service';

@Component({
  selector: 'app-dashboard',
  imports: [Login],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard {
  secretMessage: string | null = null; // Property to store the message

  constructor(private authenticationService: AuthenticationService) {}

  ngOnInit(): void {
    this.getSecret();
  }

  getSecret(): void {
    if (this.authenticationService.isLoggedIn()) {
      this.authenticationService.getSecretMessage().subscribe({
        next: (msg) => {
          if (msg) {
            this.secretMessage = msg.reply;
          }
        },
        error: (err) => {
          console.error('Error fetching secret message', err);
        },
      });
    }
  }
}
