import { Component, OnInit } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from 'src/app/core/core';

@Component({
  selector: 'app-enterprise',
  templateUrl: './enterprise.component.html',
  styleUrls: ['./enterprise.component.scss']
})
export class EnterpriseComponent implements OnInit {
  link = [
    { url: "/main/otherSetup/company", title: "Company ",  class: "mx-1 hovertab  d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/plant", title: "Plant ",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/warehouse", title: "Warehouse ",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/floor", title: "Floor ",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/storagesection", title: "Storage Section ",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/dockid", title: "Dock ",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/doorid", title: "Door ",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/vertical", title: "Vertical ",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
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

}

 