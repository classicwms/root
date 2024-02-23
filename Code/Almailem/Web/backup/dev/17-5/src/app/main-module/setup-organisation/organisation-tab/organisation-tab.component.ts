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
    { url: "/main/organisationsetup/company", title: "Company",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/organisationsetup/plant", title: "Plant",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/organisationsetup/warehouse", title: "Warehouse",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/organisationsetup/floor", title: "Floor",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/organisationsetup/storage", title: "Storage Section",  class: "mx-1 d-flex justify-content-center align-items-center" },
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
