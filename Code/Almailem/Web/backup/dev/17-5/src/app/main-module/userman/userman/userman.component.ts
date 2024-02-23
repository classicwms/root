import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';

@Component({
  selector: 'app-userman',
  templateUrl: './userman.component.html',
  styleUrls: ['./userman.component.scss']
})
export class UsermanComponent implements OnInit {
  paramdata = this.cs.encrypt({tabSelected: 'setupMaster'});
  link = [
    { url: "/main/userman/userrole", title: "User Role",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/userman/user-profile", title: "User Profile",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/userman/hhtuser/"+ this.paramdata, title: "HHT User",  class: "mx-1 d-flex justify-content-center align-items-center" },
  

 ];
 activeLink = this.link[0].url;
 
 constructor(private router: Router, private auth: AuthService, private cs: CommonService) { 
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

