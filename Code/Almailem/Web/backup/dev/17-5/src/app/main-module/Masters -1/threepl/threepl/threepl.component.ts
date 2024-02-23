import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from 'src/app/core/core';

@Component({
  selector: 'app-threepl',
  templateUrl: './threepl.component.html',
  styleUrls: ['./threepl.component.scss']
})
export class ThreeplComponent implements OnInit {

  link = [
    { url: "/main/threePLmaster/pricelist", title: "Price List",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/threePLmaster/billing", title: "Billing",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/threePLmaster/pricelistassign", title: "Price List Assignment",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/threePLmaster/cbm", title: "CBM Inbound",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/threePLmaster/proformainvoiceheader", title: "Proforma Invoice Header",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/threePLmaster/proformainvoiceline", title: "Proforma Invoice Line",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/threePLmaster/invoiceheader", title: " Invoice Header",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/threePLmaster/invoiceline", title: " Invoice Line",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/threePLmaster/inquiry", title: " Inquiry",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/threePLmaster/quotationheader", title: "Quotation Header",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/threePLmaster/quotationline", title: "Quotation Line",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/threePLmaster/agreement", title: "Agreement",  class: "mx-1 d-flex justify-content-center align-items-center" },
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

