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
    { url: "/main/otherSetup/binclassid", title: "Bin Class ",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/binsectionid", title: "Bin Section ",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/stocktype", title: "Stock Type ",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/storageclass", title: "Storage Class ",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/storagetype", title: "Storage Type ",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/storagebintype", title: "Storage Bin ",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/stratergy", title: "Stratergy ",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/aisle", title: "Aisle ",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/row", title: "Row ",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/spanid", title: "Span ",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/shelfid", title: "Shelf ",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
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
