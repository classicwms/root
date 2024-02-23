import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from 'src/app/core/core';

@Component({
  selector: 'app-order-tab',
  templateUrl: './order-tab.component.html',
  styleUrls: ['./order-tab.component.scss']
})
export class OrderTabComponent implements OnInit {

  
  link = [
    { url: "/main/outbound/preoutbound", title: "Preoutbound",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/outbound/order-management", title: "Order Management",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/outbound/assignment", title: "Assignment",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/outbound/pickup-main", title: "Pickup",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/outbound/quality-main", title: "Quality",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/outbound/delivery-main1", title: "Ready for Delivery",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/outbound/delivery-main", title: "Delivered",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/outbound/reversal", title: "Reversal",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
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
