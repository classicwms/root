import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from 'src/app/core/core';

@Component({
  selector: 'app-perpeatual-tab',
  templateUrl: './perpeatual-tab.component.html',
  styleUrls: ['./perpeatual-tab.component.scss']
})
export class PerpeatualTabComponent implements OnInit {


  link = [
    { url: "/main/cycle-count/Prepetual-main/count", title: "Assign & Count",screenid:3071,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/cycle-count/varianceConfirm", title: "Stock Analysis", screenid:3074,class: "mx-1 d-flex justify-content-center align-items-center" },
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
