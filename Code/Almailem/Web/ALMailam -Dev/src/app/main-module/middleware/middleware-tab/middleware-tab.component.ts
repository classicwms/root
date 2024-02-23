import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from 'src/app/core/core';

@Component({
  selector: 'app-middleware-tab',
  templateUrl: './middleware-tab.component.html',
  styleUrls: ['./middleware-tab.component.scss']
})
export class MiddlewareTabComponent implements OnInit {

  
  link = [
    { url: "/main/middleware/salesInvoice", title: "Sales Invoice", screenid:3044, class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/middleware/salesReturn", title: "Sales Return", screenid:3044, class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/middleware/shippingorder", title: "Shipment Orders", screenid:3044, class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/middleware/periodicHeader", title: "Periodic Count", screenid:3044, class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/middleware/perpetualHeader", title: "Perpetual Count", screenid:3044, class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/middleware/stockadjustment", title: "Stock Adjustment", screenid:3044, class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/middleware/supplierinvoice", title: "Supplier Invoice", screenid:3044, class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/middleware/B2BTransferOrder", title: "B2B Transfer ", screenid:3044, class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/middleware/customer", title: "Customer Master", screenid:3044, class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/middleware/interwarehousein", title: "Interwarehouse Transfer In", screenid:3044, class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/middleware/interwarehouseout", title: "Interwarehouse Transfer Out", screenid:3044, class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/middleware/itemaster", title: "Item Master", screenid:3044, class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
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