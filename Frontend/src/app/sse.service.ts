import { Injectable } from '@angular/core';
import { HOST } from './config';

@Injectable({
    providedIn: 'root'
})
export class SseService {

    private eventSource: EventSource | undefined;

  constructor() {}

  openEventSource(): EventSource {
    if (!this.eventSource) {
      this.eventSource = new EventSource(HOST + "/vote/sse");
    }

    return this.eventSource;
  }

  closeEventSource(): void {
    if (this.eventSource) {
      this.eventSource.close();
      this.eventSource = undefined;
    }
  }
}
