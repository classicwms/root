import { Component, OnInit } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from 'src/app/core/core';

@Component({
  selector: 'app-three-pl',
  templateUrl: './three-pl.component.html',
  styleUrls: ['./three-pl.component.scss']
})
export class ThreePlComponent implements OnInit {

  link = [
    { url: "/main/otherSetup/serviceid", title: "Service ",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/paymenttermid", title: "Payment Term",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/billingmodeid", title: "Billing Mode",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/billingfrequency", title: "Billing Frequency",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/billingformat", title: "Billing Format",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/paymentmode", title: "Payment Mode",  class: "mx-1 d-flex justify-content-center align-items-center" },
    // { url: "/main/otherSetup/itemgroup", title: "Item Group ID",  class: "mx-1 d-flex justify-content-center align-items-center" },
    // { url: "/main/otherSetup/subitemgroup", title: "SubItem Group ID",  class: "mx-1 d-flex justify-content-center align-items-center" },
    // { url: "/main/otherSetup/variantid", title: "Variant ID",  class: "mx-1 d-flex justify-content-center align-items-center" },
    // { url: "/main/otherSetup/uom", title: "UOM ID",  class: "mx-1 d-flex justify-content-center align-items-center" },
    // { url: "/main/otherSetup/stratergy", title: "Stratergy ID",  class: "mx-1 d-flex justify-content-center align-items-center" },
    // { url: "/main/otherSetup/row", title: "Row ID",  class: "mx-1 d-flex justify-content-center align-items-center" },
    // { url: "/main/otherSetup/shelfid", title: "Shelf ID",  class: "mx-1 d-flex justify-content-center align-items-center" },
    // { url: "/main/otherSetup/spanid", title: "Span ID",  class: "mx-1 d-flex justify-content-center align-items-center" },
    // { url: "/main/otherSetup/adhocmoduleid", title: "AdhocModule ID",  class: "mx-1 d-flex justify-content-center align-items-center" },
    // { url: "/main/otherSetup/status", title: "Satus ID",  class: "mx-1 d-flex justify-content-center align-items-center" },
    // { url: "/main/otherSetup/statussmessagesid", title: "Status Messages ID",  class: "mx-1 d-flex justify-content-center align-items-center" },
   //  { url: "/main/otherSetup/shelfid", title: "shelfid",  class: "mx-1 d-flex justify-content-center align-items-center" },
   //  { url: "/main/otherSetup/binsectionid", title: "binsectionid",  class: "mx-1 d-flex justify-content-center align-items-center" },
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
