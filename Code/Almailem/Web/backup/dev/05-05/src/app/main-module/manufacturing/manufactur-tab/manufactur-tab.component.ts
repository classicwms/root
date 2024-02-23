import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from 'src/app/core/core';

@Component({
  selector: 'app-manufactur-tab',
  templateUrl: './manufactur-tab.component.html',
  styleUrls: ['./manufactur-tab.component.scss']
})
export class ManufacturTabComponent implements OnInit {

  
  link = [
    { url: "/main/manufacturing/orderDetails", title: "Order Details",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/manufacturing/cutting", title: "Cutting",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/manufacturing/modling", title: "Molding",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/manufacturing/fabrication", title: "Fabrication",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/manufacturing/assembly", title: "Assembly",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/manufacturing/delivery", title: "Delivery",  class: "mx-1 d-flex justify-content-center align-items-center" },
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
