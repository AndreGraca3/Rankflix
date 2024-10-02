import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { FooterComponent } from './components/footer/footer.component';
import { NavbarComponent } from './components/header/navbar.component';
import NProgress from 'nprogress';
import 'nprogress/nprogress.css';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, FooterComponent, NavbarComponent],
  templateUrl: './app.component.html',
})
export class AppComponent {
  title = 'rankflix-webapp';

  constructor() {
    NProgress.configure({
      showSpinner: false,
      trickleSpeed: 200,
    });
  }
}
