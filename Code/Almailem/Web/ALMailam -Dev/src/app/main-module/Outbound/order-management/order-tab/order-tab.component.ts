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
    { url: "/main/outbound/preoutbound", title: "Preoutbound", screenid:3059, class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/outbound/order-management", title: "Order Management", screenid:3060, class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/outbound/assignment", title: "Assignment",screenid:3061,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/outbound/pickup-main", title: "Pickup", screenid:3063, class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/outbound/quality-main", title: "Quality",screenid:3065,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/outbound/delivery-main1", title: "Ready for Delivery",screenid: 3067, class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/outbound/delivery-main", title: "Delivered",screenid:3146,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/outbound/reversal", title: "Reversal", screenid:3069, class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/outbound/cancellation", title: "Picklist Cancellation", screenid:3222, class: "mx-1 d-flex justify-content-center align-items-center" },
 ];
 link1 = [
  { url: "/main/outbound/readyForDelivery", title: "Sales Order Status Report",screenid: 3067, class: "mx-1 d-flex justify-content-center align-items-center" }
];
 activeLink = this.link[0].url;
 
 constructor(private router: Router, private auth: AuthService) { 
   this.router.events.pipe(
     filter((event: any) => event instanceof NavigationEnd),
   ).subscribe(x => {
     this.activeLink = this.router.url;
   });
 }

 activeUrl1: any;
 ngOnInit(): void {
   this.activeLink = this.router.url;
   this.activeUrl1 = (this.router.url).split('/');
 }


 isdiabled(id: any) {
  this.auth.isMenudata();
  let fileterdata = this.auth.MenuData.filter((x: any) => x.subMenuId == id && (x.view || x.delete || x.createUpdate));
  if (fileterdata.length > 0) {
    return false;
  }
  return true;
}
}
