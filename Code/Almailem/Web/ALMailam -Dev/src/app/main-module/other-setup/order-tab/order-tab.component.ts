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
    { url: "/main/otherSetup/controlprocessid", title: "Control Process",screenid: 3142,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/controltypeid", title: "Control Type ",screenid: 3131,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/processid", title: "Process ",screenid: 3095,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/processsequence", title: "Process Sequence ",screenid: 3144,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/level", title: "Level ",screenid: 3101,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/sublevelid", title: " Sub Level ",screenid: 3143,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/inboundorderstatusid", title: "Inbound Order Status", screenid: 3129, class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/inboundordertypeid", title: "Inbound Order Type ",screenid: 3128,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/outboundorderstatusid", title: "Outbound Order Status ",screenid: 3127,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/outboundordertypeid", title: "Outbound Order Type ", screenid: 3128, class: "mx-1 d-flex justify-content-center align-items-center" },
    // { url: "/main/otherSetup/menu", title: "Menu ID",  class: "mx-1 d-flex justify-content-center align-items-center" },
    // { url: "/main/otherSetup/moduleid", title: "Module ID",  class: "mx-1 d-flex justify-content-center align-items-center" },
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

 
 isdiabled(id: any) {
  this.auth.isMenudata();
  let fileterdata = this.auth.MenuData.filter((x: any) => x.subMenuId == id && (x.view || x.delete || x.createUpdate));
  if (fileterdata.length > 0) {
    return false;
  }
  return true;
}

}
