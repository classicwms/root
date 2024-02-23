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

  paramdata = this.cs.encrypt({tabSelected: 'otherMaster'});
  link = [
    { url: "/main/other-masters/handling-unit", title: "Handling Unit",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/other-masters/handling-equipment", title: "Handling Equipment",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/other-masters/bom", title: "Bill of Material",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/other-masters/business-partner", title: "Bussiness Partner",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/other-masters/packing-material", title: "Packing Material",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/userman/hhtuser/" + this.paramdata, title: "HHT",  class: "mx-1 d-flex justify-content-center align-items-center" },
  

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

