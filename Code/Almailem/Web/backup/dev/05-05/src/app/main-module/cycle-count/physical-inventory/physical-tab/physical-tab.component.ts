import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from 'src/app/core/core';

@Component({
  selector: 'app-physical-tab',
  templateUrl: './physical-tab.component.html',
  styleUrls: ['./physical-tab.component.scss']
})
export class PhysicalTabComponent implements OnInit {


  link = [
    { url: "/main/cycle-count/physical-main", title: "Annual Creation",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/cycle-count/variant-analysis-annual", title: "Variance Analysis",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
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
