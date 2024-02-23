import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from 'src/app/core/core';

@Component({
  selector: 'app-setup-tab',
  templateUrl: './setup-tab.component.html',
  styleUrls: ['./setup-tab.component.scss']
})
export class SetupTabComponent implements OnInit {
  link = [
     { url: "/main/otherSetup/languageid", title: "Language ", screenid: 3097,  class: "mx-1 d-flex justify-content-center align-items-center" },
     { url: "/main/otherSetup/country", title: "Country ", screenid: 3133,  class: "mx-1 d-flex justify-content-center align-items-center" },
     { url: "/main/otherSetup/state", title: "State ", screenid: 3134,  class: "mx-1 d-flex justify-content-center align-items-center" },
     { url: "/main/otherSetup/city", title: "City ", screenid: 3135,  class: "mx-1 d-flex justify-content-center align-items-center" },
     { url: "/main/otherSetup/currency", title: "Currency ", screenid: 3104,  class: "mx-1 d-flex justify-content-center align-items-center" },
     { url: "/main/otherSetup/dateformatid", title: "Date Format ", screenid: 3111,  class: "mx-1 d-flex justify-content-center align-items-center" },
     { url: "/main/otherSetup/decimalnotationid", title: "Decimal Notation ", screenid: 3112,  class: "mx-1 d-flex justify-content-center align-items-center" },
     { url: "/main/otherSetup/menu", title: "Menu", screenid: 3109,  class: "mx-1 d-flex justify-content-center align-items-center" },
     { url: "/main/otherSetup/moduleid", title: "Module ", screenid: 3099,  class: "mx-1 d-flex justify-content-center align-items-center" },
     { url: "/main/otherSetup/adhocmoduleid", title: "Adhoc Module ", screenid: 3100,  class: "mx-1 d-flex justify-content-center align-items-center" },
     { url: "/main/otherSetup/status", title: "Status ", screenid: 3140,  class: "mx-1 d-flex justify-content-center align-items-center" },
     { url: "/main/otherSetup/statussmessagesid", title: "Status Messages ", screenid: 3113,  class: "mx-1 d-flex justify-content-center align-items-center" },
     { url: "/main/otherSetup/numberrange", title: "Number Range ", screenid: 3162,  class: "mx-1 d-flex justify-content-center align-items-center" },
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
