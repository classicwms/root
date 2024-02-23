import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from 'src/app/core/core';

@Component({
  selector: 'app-organisation-tab',
  templateUrl: './organisation-tab.component.html',
  styleUrls: ['./organisation-tab.component.scss']
})
export class OrganisationTabComponent implements OnInit {

  link = [
    { url: "/main/organisationsetup/company", title: "Company",screenid: 3002 ,class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/organisationsetup/plant", title: "Branch", screenid: 3003 , class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/organisationsetup/warehouse", title: "Warehouse",screenid: 3004,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/organisationsetup/floor", title: "Floor",screenid: 3005,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/organisationsetup/storage", title: "Storage Section",screenid: 3006,  class: "mx-1 d-flex justify-content-center align-items-center" },
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
