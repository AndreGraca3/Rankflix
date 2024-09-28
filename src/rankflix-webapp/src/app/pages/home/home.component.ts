import { Component } from '@angular/core';
import { MediaCardComponent } from '../../components/media-card/media-card.component';
import { MediaService } from '../../services/media.service';
import { Media } from '../../types';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [MediaCardComponent],
  templateUrl: './home.component.html',
})
export class HomeComponent {
  mediaItems!: Media[];

  constructor(private mediaService: MediaService) {
    mediaService.getMovies().subscribe((mediaItems) => {
      this.mediaItems = mediaItems;
    });
  }
}
