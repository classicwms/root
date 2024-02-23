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
    { url: "/main/otherSetup/company", title: "Company ", screenid: 3082, class: "mx-1  d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/plant", title: "Plant ",  screenid: 3084, class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/warehouse", title: "Warehouse ",  screenid: 3083, class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/floor", title: "Floor ",  screenid: 3085, class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/storagesection", title: "Storage Section ",  screenid: 3086, class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/dockid", title: "Dock ",  screenid: 3122, class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/doorid", title: "Door ",  screenid: 3094, class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/otherSetup/vertical", title: "Vertical ", screenid: 3081, class: "mx-1 d-flex justify-content-center align-items-center" },
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

 