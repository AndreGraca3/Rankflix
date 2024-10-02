import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { Media, PaginatedResponse } from '../types';

@Injectable({
  providedIn: 'root',
})
export class MediaService {
  constructor(private apiService: ApiService) {}

  getMovies() {
    return this.apiService.get<PaginatedResponse<Media>>(
      '/media/movies/trending'
    );
  }
}
