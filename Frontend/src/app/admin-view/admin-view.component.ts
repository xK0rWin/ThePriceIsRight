import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Component, NgZone } from '@angular/core';
import { Router } from '@angular/router';
import { SseService } from '../sse.service';
import { Vote } from '../models/vote.model';
import { HOST } from '../config';
import { Item } from '../models/item.model';
import { User } from '../models/user.model';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-admin-view',
  standalone: true,
  imports: [CommonModule, HttpClientModule],
  templateUrl: './admin-view.component.html',
  styleUrls: ['./admin-view.component.css']
})
export class AdminViewComponent {
  votes: Vote[] = [];
  users: User[] = []
  activeItemName: string | null;
  userName!: string;
  reveal: boolean = false;
  item?: Item; 
  itemImageUrl: string = ''
  safeUrl?: SafeResourceUrl;

  constructor(
    private router: Router,
    private http: HttpClient,
    private sseService: SseService,
    private zone: NgZone,
    private sanitizer: DomSanitizer
  ) {
    const eventSource = this.sseService.openEventSource();
    this.activeItemName = localStorage.getItem('activeItemName') ?? null;
    this.userName = localStorage.getItem('userName')!;
    this.itemImageUrl = localStorage.getItem("activeItemUrl") ?? ''
    this.safeUrl = this.sanitizer.bypassSecurityTrustResourceUrl(this.itemImageUrl);

    this.loadVotes();
    this.loadUsers();
    this.loadCurrentItem();

    eventSource.onmessage = (event) => {
      this.zone.run(() => {
        console.log(event.data)
        if (event.data == 'addVote' && this.activeItemName) {
          this.loadVotes();
        } else if (event.data == 'startVote') {
          this.http.get<Item>(HOST + "/vote/currentVote").subscribe({
            next: item => {
              localStorage.setItem("activeItemName", item.name)
              localStorage.setItem("activeItemUrl", item.url)
              this.activeItemName = localStorage.getItem("activeItemName")
              this.itemImageUrl = localStorage.getItem("activeItemUrl")!
              this.safeUrl = this.sanitizer.bypassSecurityTrustResourceUrl(this.itemImageUrl);
            },
            error: err => {
              this.activeItemName = localStorage.getItem("activeItemName")
              this.itemImageUrl = localStorage.getItem("activeItemUrl")!
              this.safeUrl = this.sanitizer.bypassSecurityTrustResourceUrl(this.itemImageUrl);
            }
          })
          this.reveal = false
          this.votes = []
          this.loadCurrentItem();
        } else if (event.data == 'newUser') {
          this.loadUsers()
        }
      });
    };
  }

  ngOnDestroy(): void {
    this.sseService.closeEventSource();
  }

  loadVotes(): void {
    if (this.activeItemName) {
      this.http.get<Vote[]>(HOST + '/vote/' + this.activeItemName).subscribe({
        next: (votes) => {
          this.votes = this.sortVotesByProximity(votes);
        },
      });
    }
  }

  loadUsers(): void {
    this.http.get<User[]>(HOST + '/player').subscribe({
      next: (users) => {
        this.users = users.slice().sort((a, b) => b.score - a.score); 
      },
    });
  }

  loadCurrentItem(): void {
    this.http.get<Item>(HOST + '/vote/currentVote').subscribe({
      next: (item) => {
        localStorage.setItem('activeItemName', item.name);
        localStorage.setItem('activeItemUrl', item.url);
        this.item = item;
      },
    });
  }

  sortVotesByProximity(votes: Vote[]): Vote[] {
    if (!this.item || this.item.price === undefined) {
      console.warn('Actual price not available for sorting votes.');
      return votes; 
    }

    return votes.sort((a, b) => {
      const diffA = Math.abs(a.price_guess - this.item!.price);
      const diffB = Math.abs(b.price_guess - this.item!.price);
      return diffA - diffB; 
    });
  }

  revealVotes(): void {
    this.reveal = true;
    this.http.post(HOST + "/vote/close", null, { responseType: 'text' as 'json', observe: 'response' }).subscribe({
      next: response => {
        if (response.status == 200) {
          this.loadUsers();
        }
      }
    })
  }
}
