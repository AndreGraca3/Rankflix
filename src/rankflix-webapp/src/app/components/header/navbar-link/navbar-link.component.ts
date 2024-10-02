import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-navbar-link',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './navbar-link.component.html',
})
export class HeaderLinkComponent {
  @Input() link!: string;
  @Input() text!: string;
}
