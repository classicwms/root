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
     { url: "/main/otherSetup/languageid", title: "Language ",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
     { url: "/main/otherSetup/country", title: "Country ",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
     { url: "/main/otherSetup/state", title: "State ",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
     { url: "/main/otherSetup/city", title: "City ",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
     { url: "/main/otherSetup/currency", title: "Currency ",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
     { url: "/main/otherSetup/dateformatid", title: "Date Format ",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
     { url: "/main/otherSetup/decimalnotationid", title: "Decimal Notation ",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
     { url: "/main/otherSetup/menu", title: "Menu",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
     { url: "/main/otherSetup/moduleid", title: "Module ",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
     { url: "/main/otherSetup/adhocmoduleid", title: "Adhoc Module ",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
     { url: "/main/otherSetup/status", title: "Status ",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
     { url: "/main/otherSetup/statussmessagesid", title: "Status Messages ",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
     { url: "/main/otherSetup/numberrange", title: "Number Range ",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    //  { url: "/main/otherSetup/shelfid", title: "shelfid",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    //  { url: "/main/otherSetup/binsectionid", title: "binsectionid",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
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
