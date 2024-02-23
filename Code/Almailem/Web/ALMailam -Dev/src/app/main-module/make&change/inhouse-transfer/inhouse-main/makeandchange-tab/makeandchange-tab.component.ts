import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from 'src/app/core/core';

@Component({
  selector: 'app-makeandchange-tab',
  templateUrl: './makeandchange-tab.component.html',
  styleUrls: ['./makeandchange-tab.component.scss']
})
export class MakeandchangeTabComponent implements OnInit {

  link = [
    { url: "/main/make&change/inhouse-transfer", title: "Inhouse Transfer",screenid:3057,  class: "mx-1 d-flex justify-content-center align-items-center" }
 ];

activeLink = this.link[0].url;

constructor(private router: Router, private auth: AuthService) { 
 this.router.events.pipe(
   filter((event: any) => event instanceof NavigationEnd),
 ).subscribe(x => {
   this.activeLink = this.router.url;
 });
}

ngOnInit(): void {
 this.activeLink = this.router.url;
}

}
