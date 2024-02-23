import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from 'src/app/core/core';

@Component({
  selector: 'app-perpetual-tab',
  templateUrl: './perpetual-tab.component.html',
  styleUrls: ['./perpetual-tab.component.scss']
})
export class PerpetualTabComponent implements OnInit {

 
  link = [
    { url: "/main/cycle-count/Prepetual-main/count", title: "Perpetual Creation",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/cycle-count/varianceConfirm", title: "Variance Analysis",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
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
