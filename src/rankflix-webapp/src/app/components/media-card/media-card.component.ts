import { Component, Input } from '@angular/core';
import { Media } from '../../types';

@Component({
  selector: 'app-media-card',
  standalone: true,
  imports: [],
  templateUrl: './media-card.component.html',
  styleUrl: './media-card.component.css',
})
export class MediaCardComponent {
  @Input() media!: Media;
}
