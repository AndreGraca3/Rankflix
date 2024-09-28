import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { Media } from '../types';

@Injectable({
  providedIn: 'root',
})
export class MediaService {
  constructor(private apiService: ApiService) {}

  getMovies() {
    console.log('getMovies');
    return this.apiService.get<Media[]>('/movies');
  }
}
