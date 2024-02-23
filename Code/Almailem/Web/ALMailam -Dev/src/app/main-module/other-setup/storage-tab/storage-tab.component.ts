import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from 'src/app/core/core';

@Component({
  selector: 'app-storage-tab',
  templateUrl: './storage-tab.component.html',
  styleUrls: ['./storage-tab.component.scss']
})
export class StorageTabComponent implements OnInit {

  link = [
    { url: "/main/otherSetup/binclassid", title: "Bin Class ", screenid: 3130, class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/binsectionid", title: "Bin Section ",screenid: 3110,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/stocktype", title: "Stock Type ",screenid: 3118,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/storageclass", title: "Storage Class ", screenid: 3091, class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/storagetype", title: "Storage Type ",screenid: 3092,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/storagebintype", title: "Storage Bin Type",screenid: 3093,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/stratergy", title: "Stratergy ",screenid: 3108,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/aisle", title: "Aisle ", screenid: 3088, class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/row", title: "Row ",screenid: 3087,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/spanid", title: "Span ", screenid: 3089, class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/shelfid", title: "Shelf ",screenid: 3090,  class: "mx-1 d-flex justify-content-center align-items-center" },
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
