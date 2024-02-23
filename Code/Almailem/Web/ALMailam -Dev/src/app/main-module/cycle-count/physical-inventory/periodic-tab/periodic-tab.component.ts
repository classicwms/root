import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from 'src/app/core/core';

@Component({
  selector: 'app-periodic-tab',
  templateUrl: './periodic-tab.component.html',
  styleUrls: ['./periodic-tab.component.scss']
})
export class PeriodicTabComponent implements OnInit {


  link = [
    { url: "/main/cycle-count/physical-main", title: "Assign & Count",screenid:3076,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/cycle-count/variant-analysis-annual", title: "Stock Analysis", screenid:3079,class: "mx-1 d-flex justify-content-center align-items-center" },
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
