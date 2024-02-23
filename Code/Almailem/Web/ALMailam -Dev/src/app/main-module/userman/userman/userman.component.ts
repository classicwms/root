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
  link = [
    { url: "/main/userman/userrole", title: "User Role",screenid:3159,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/userman/user-profile", title: "User Profile",screenid:3160,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/userman/hhtuser", title: "HHT User",screenid:3161,  class: "mx-1 d-flex justify-content-center align-items-center" },
  

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


 
 isdiabled(id: any) {
  this.auth.isMenudata();
  let fileterdata = this.auth.MenuData.filter((x: any) => x.subMenuId == id && (x.view || x.delete || x.createUpdate));
  if (fileterdata.length > 0) {
    return false;
  }
  return true;
}


}

