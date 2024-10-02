import { Component } from '@angular/core';
import { MediaService } from '../../services/media.service';
import { Media } from '../../types';
import { MediaCardComponent } from '../../components/media-card/media-card.component';
import movies from '../../movies.json';
import { SelectorComponent } from "../../components/selector/selector.component";

@Component({
  selector: 'app-discover',
  standalone: true,
  imports: [MediaCardComponent, SelectorComponent],
  templateUrl: './discover.component.html',
})
export class DiscoverComponent {
  mediaItems!: Media[];

  constructor(private mediaService: MediaService) {
    /*mediaService.getMovies().subscribe((paginatedMediaItems) => {
      this.mediaItems = paginatedMediaItems.items;
    });*/

   this.mediaItems = movies.items;
  }
}
