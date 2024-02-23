import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from 'src/app/core/core';

@Component({
  selector: 'app-warehouse-tab',
  templateUrl: './warehouse-tab.component.html',
  styleUrls: ['./warehouse-tab.component.scss']
})
export class WarehouseTabComponent implements OnInit {


  link = [
    { url: "/main/otherSetup/employeeid", title: "Employee ",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/handlingequipmentid", title: "Handling Equipment ",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/handlingunitid", title: "Handling Unit",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/palletization-level-id", title: "Palletization ",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/refdoctypeid", title: "RefDoc Type ",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/returntypeid", title: "Return Type ",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/specialstockindicator", title: "Special Stock Indicator ",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/warehousetype", title: "Warehouse Type ",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/workcenter", title: "Work Center ",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/usertype", title: "User Type ",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/barcodetypeid", title: "Barcode Type ",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/barcodesubtypeid", title: "Barcode SubType ",  class: "mx-1 d-flex justify-content-center align-items-center" },
   
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
