import { Component } from '@angular/core';
import { LoginViewComponent } from './login-view/login-view.component';
import { UserViewComponent } from './user-view/user-view.component';
import { AdminViewComponent } from './admin-view/admin-view.component';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule
  ],
  templateUrl: './app.component.html',
})
export class AppComponent {
  users: string[] = [];
  guesses: { user: string; guess: number }[] = [];
  currentUser: string = '';  // Set initial value as an empty string

  addUser(name: string): void {
    this.users.push(name);
    this.currentUser = name; // Set currentUser to the logged-in user
  }

  addGuess(guess: { user: string; guess: number }): void {
    this.guesses.push(guess);
    this.currentUser = ''; // Clear currentUser after the guess is submitted
  }
}
