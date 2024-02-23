import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';

@Component({
  selector: 'app-othermaster-tab',
  templateUrl: './othermaster-tab.component.html',
  styleUrls: ['./othermaster-tab.component.scss']
})
export class OthermasterTabComponent implements OnInit {

  link = [
    { url: "/main/other-masters/handling-unit", title: "Handling Unit",screenid: 3032 ,class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/other-masters/handling-equipment", title: "Handling Equipment",screenid: 3034,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/other-masters/bom", title: "Bill of Material",screenid: 3036,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/other-masters/business-partner", title: "Business Partner",screenid: 3038,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/other-masters/packing-material", title: "Packing Material",screenid: 3040,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/other-masters/dock", title: "Dock",screenid: 3149,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/other-masters/workcenter", title: "Work Center", screenid: 3151, class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/other-masters/cyclecountschedular", title: "Cycle Count Scheduler",screenid: 3153,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/other-masters/numberrange", title: "Number Range Item", screenid: 3155, class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/other-masters/numberrangestoragebin", title: "Number Range Storage Bin", screenid: 3157, class: "mx-1 d-flex justify-content-center align-items-center" },

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

