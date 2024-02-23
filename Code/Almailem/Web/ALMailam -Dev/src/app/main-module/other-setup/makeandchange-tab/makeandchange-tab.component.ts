import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from 'src/app/core/core';

@Component({
  selector: 'app-makeandchange-tab',
  templateUrl: './makeandchange-tab.component.html',
  styleUrls: ['./makeandchange-tab.component.scss']
})
export class MakeandchangeTabComponent implements OnInit {

  
  link = [
    { url: "/main/otherSetup/cyclecounttypeid", title: "Cycle Count Type ", screenid: 3124, class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/approvalprocessid", title: "Approval Process ", screenid: 3103, class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/approvalid", title: "Approval ",screenid: 3132,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/movementtypeid", title: "Movement Type ",screenid: 3115,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/submovementtypeid", title: "Sub Movement Type ",screenid: 3116,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/transfertypeid", title: "Transfer Type ",screenid: 3117,  class: "mx-1 d-flex justify-content-center align-items-center" },
    // { url: "/main/otherSetup/storagebintype", title: "StorageBin ID",  class: "mx-1 d-flex justify-content-center align-items-center" },
    // { url: "/main/otherSetup/storageclass", title: "StorageClass ID",  class: "mx-1 d-flex justify-content-center align-items-center" },
    // { url: "/main/otherSetup/storagetype", title: "StorageType ID",  class: "mx-1 d-flex justify-content-center align-items-center" },
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


 
 isdiabled(id: any) {
  this.auth.isMenudata();
  let fileterdata = this.auth.MenuData.filter((x: any) => x.subMenuId == id && (x.view || x.delete || x.createUpdate));
  if (fileterdata.length > 0) {
    return false;
  }
  return true;
}

}
