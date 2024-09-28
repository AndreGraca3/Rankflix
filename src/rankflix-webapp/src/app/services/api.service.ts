import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ApiService {
  constructor(private httpClient: HttpClient) {}

  get<T>(url: string) {
    return this.httpClient.get<T>(environment.API_BASE_URL + url);
  }
}
