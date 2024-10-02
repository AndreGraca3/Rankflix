import { Component, Input } from '@angular/core';
import { Media } from '../../types';
import { RouterLink } from '@angular/router';
import { NgOptimizedImage } from '@angular/common';

@Component({
  selector: 'app-media-card',
  standalone: true,
  imports: [RouterLink, NgOptimizedImage ],
  templateUrl: './media-card.component.html',
})
export class MediaCardComponent {
  @Input() media!: Media;
}
