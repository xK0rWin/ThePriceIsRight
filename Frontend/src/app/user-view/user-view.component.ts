import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule, HttpResponse } from '@angular/common/http';
import { Component, Input, Output, EventEmitter, NgZone } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { SseService } from '../sse.service';
import { HOST } from '../config';
import { Item } from '../models/item.model';

@Component({
  selector: 'app-user-view',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './user-view.component.html',
  styleUrls: ['./user-view.component.css']
})
export class UserViewComponent {
  votingActive: boolean = true
  itemImageUrl: string = ''
  activeItemName: string | null
  userName!: string
  message: string = ''
  hasSubmitted: boolean = false
  priceGuess: number | null = null

  constructor(private router: Router, private http: HttpClient, private sseService: SseService, private zone: NgZone) {
    const eventSource = this.sseService.openEventSource();
    this.activeItemName = localStorage.getItem("activeItemName")
    this.itemImageUrl = localStorage.getItem("activeItemUrl") ?? ""
    this.userName = localStorage.getItem("userName")!

    this.http.get<Item>(HOST + "/vote/currentVote").subscribe({
      next: item => {
        localStorage.setItem("activeItemName", item.name)
        localStorage.setItem("activeItemUrl", item.url)
        this.activeItemName = localStorage.getItem("activeItemName")
        this.itemImageUrl = localStorage.getItem("activeItemUrl")!
        this.votingActive = true
        this.hasSubmitted = false
      },
      error: err => {
        this.activeItemName = localStorage.getItem("activeItemName")
        this.itemImageUrl = localStorage.getItem("activeItemUrl")!
        this.votingActive = false
      }
    })

    eventSource.onmessage = (event) => {
      this.zone.run(() => {
        this.message = ""
        if (event.data != "addVote") {
          this.http.get<Item>(HOST + "/vote/currentVote").subscribe({
            next: item => {
              localStorage.setItem("activeItemName", item.name)
              localStorage.setItem("activeItemUrl", item.url)
              this.activeItemName = localStorage.getItem("activeItemName")
              this.itemImageUrl = localStorage.getItem("activeItemUrl")!
              this.votingActive = true
              this.hasSubmitted = false
            },
            error: err => {
              this.activeItemName = localStorage.getItem("activeItemName")
              this.itemImageUrl = localStorage.getItem("activeItemUrl")!
              this.votingActive = false
            }
          })
        }
      })
    }
  }

  ngOnDestroy() : void {
    this.sseService.closeEventSource();
  }

  onSubmit(): void {
    if (this.priceGuess !== null) {
      this.http.post<HttpResponse<Number>>(HOST + "/vote", {"price_guess": this.priceGuess, "user_name": this.userName, "item_name": this.activeItemName}, { responseType: 'text' as 'json', observe: 'response' }).subscribe({
        next: response => {
          if (response.status == 200) {
            this.hasSubmitted = true
          } else {
            this.message = "Sorry, you already voted"
          }
        }
      })
    }
  }

}
