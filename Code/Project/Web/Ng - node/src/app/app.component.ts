import { Component } from '@angular/core';
import { Router, NavigationStart, NavigationEnd, NavigationCancel } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'Classic WMS';

  constructor(
    private router: Router,
    private spinner: NgxSpinnerService,
    // private auth: AuthService,
    // private rs: RoleAccessService
  ) { }

  ngAfterViewInit() {
    this.router.events.subscribe(event => {
      if (event instanceof NavigationStart) {
        this.spinner.show();
        // if (this.auth.userID) {
        //   console.log(event.url);
        //   console.log(this.rs.getAccessBySearch(event.url));
        // }
      } else if (
        event instanceof NavigationEnd ||
        event instanceof NavigationCancel
      ) {
        this.spinner.hide();
      }
    });
  }
}
