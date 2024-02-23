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
    { url: "/main/otherSetup/controlprocessid", title: "Control Process",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/controltypeid", title: "Control Type ",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/processid", title: "Process ",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/processsequence", title: "Process Sequence ",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/level", title: "Level ",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/sublevelid", title: " Sub Level ",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/inboundorderstatusid", title: "Inbound Order Status",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/inboundordertypeid", title: "Inbound Order Type ",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/outboundorderstatusid", title: "Outbound Order Status ",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/outboundordertypeid", title: "Outbound Order Type ",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    // { url: "/main/otherSetup/menu", title: "Menu ID",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    // { url: "/main/otherSetup/moduleid", title: "Module ID",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
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
