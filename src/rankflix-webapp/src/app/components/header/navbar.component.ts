import { Component } from '@angular/core';
import { HeaderLinkComponent } from './navbar-link/navbar-link.component';
import { Router, RouterLink } from '@angular/router';
import { SearchBarComponent } from '../search-bar/search-bar.component';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [HeaderLinkComponent, RouterLink, SearchBarComponent],
  templateUrl: './navbar.component.html',
})
export class NavbarComponent {}
