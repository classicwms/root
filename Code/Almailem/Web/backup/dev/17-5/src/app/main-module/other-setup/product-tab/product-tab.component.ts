import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from 'src/app/core/core';

@Component({
  selector: 'app-product-tab',
  templateUrl: './product-tab.component.html',
  styleUrls: ['./product-tab.component.scss']
})
export class ProductTabComponent implements OnInit {


  link = [
    { url: "/main/otherSetup/itemtype", title: "Item Type",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/itemgroup", title: "Item Group",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/subitemgroup", title: "Sub Item Group",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/variantid", title: "Variant",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/uom", title: "UOM",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    // { url: "/main/otherSetup/stratergy", title: "Stratergy ID",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    // { url: "/main/otherSetup/row", title: "Row ID",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    // { url: "/main/otherSetup/shelfid", title: "Shelf ID",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    // { url: "/main/otherSetup/spanid", title: "Span ID",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    // { url: "/main/otherSetup/adhocmoduleid", title: "AdhocModule ID",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    // { url: "/main/otherSetup/status", title: "Satus ID",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    // { url: "/main/otherSetup/statussmessagesid", title: "Status Messages ID",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
   //  { url: "/main/otherSetup/shelfid", title: "shelfid",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
   //  { url: "/main/otherSetup/binsectionid", title: "binsectionid",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
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