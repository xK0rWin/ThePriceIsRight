import { Component, NgZone } from '@angular/core';
import { HttpClient, HttpClientModule, HttpResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { HOST } from '../config'; // Ensure this is set to your API endpoint
import { CommonModule } from '@angular/common';
import { Item } from '../models/item.model';
import { FormsModule } from '@angular/forms';
import { SseService } from '../sse.service';

@Component({
  selector: 'app-add-item',
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './add-item.component.html',
  styleUrls: ['./add-item.component.css']
})
export class AddItemComponent {
  item = {
    name: '',
    url: '',
    status: "OPEN",
    price: null
  };
  allItems: Item[] = []

  constructor(private http: HttpClient, private router: Router, private sseService: SseService, private zone: NgZone) {
    const eventSource = this.sseService.openEventSource();
    this.loadItems();

    eventSource.onmessage = (event) => {
      this.zone.run(() => {
        if (event.data != "addVote") {
          this.loadItems();
        }
      });
    };
  }

  onSubmit() {
    if (this.item.name && this.item.url && this.item.price) {
      this.http.post(HOST + '/vote/item', this.item).subscribe({
        next: (response) => {
          this.loadItems()
        },
        error: (error) => {
          console.error('Error adding item', error);
        }
      });
    }
  }

  ngOnDestroy(): void {
    this.sseService.closeEventSource();
  }

  loadItems() {
    this.http.get<Item[]>(HOST + '/vote/items').subscribe({
      next: items => {
        this.allItems = items
      }
    });
  }

  activateItem(item: any) {

    this.http.post<HttpResponse<string>>(HOST + '/vote/start/' + item.name, null, { responseType: 'text' as 'json', observe: 'response'}).subscribe({
      next: response => {
        this.loadItems();
      },
      error: error => {
        console.error('Error activating item', error);
      }
    });
  }

  clear() {
    this.http.get<string>(HOST + '/vote/clear').subscribe({
      next: status => {
      }
    });
  }
}
