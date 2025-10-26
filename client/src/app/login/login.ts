import { Component } from '@angular/core';
import { AuthenticationService } from '../authentication-service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  registerView: boolean = false;
  username: string = '';
  password: string = '';
  usernameRegister: string = '';
  passwordRegister: string = '';
  alert?: string;

  constructor(private authenticationService: AuthenticationService) {}

  openRegisterView() {
    this.registerView = true;
    this.clearUsernamePasswordFields();
  }

  closerRegisterView() {
    this.registerView = false;
    this.clearUsernamePasswordFields();
  }

  clearUsernamePasswordFields() {
    this.username = '';
    this.usernameRegister = '';
    this.password = '';
    this.passwordRegister = '';
  }

  login() {
    this.authenticationService.login(this.username, this.password).subscribe({
      next: () => {
        this.alert = 'Login successful!';
      },
      error: (err) => {
        this.alert = 'Login failed! ' + (err.error?.message || 'An unexpected error occurred.');
        console.error('Login error:', err);
      },
    });
  }

  register() {
    this.authenticationService.register(this.usernameRegister, this.passwordRegister).subscribe({
      next: (response) => {
        this.alert = response.message;
      },
      error: (err) => {
        this.alert =
          'Registration failed! ' + (err.error?.message || 'An unexpected error occurred.');
        console.error('Registration error:', err);
      },
    });
  }
}
