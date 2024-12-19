import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Component, EventEmitter, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { User } from '../models/user.model';
import { HOST } from '../config';

@Component({
  selector: 'app-login-view',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './login-view.component.html',
})
export class LoginViewComponent {
  userName: string = '';


  constructor(private router: Router, private http: HttpClient) {}

  onSubmit(): void {
    if (this.userName.trim()) {
      this.http.post<User>(HOST + "/vote/register", this.userName).subscribe({
        next: user => {
          localStorage.setItem("userName", user.user_name)
          this.router.navigate(['']);
        }
      })
    }
  }

}
