import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from 'src/app/core/core';

@Component({
  selector: 'app-inbound-tabbar',
  templateUrl: './inbound-tabbar.component.html',
  styleUrls: ['./inbound-tabbar.component.scss']
})
export class InboundTabbarComponent implements OnInit {


  link = [
    { url: "/main/inbound/preinbound", title: "Preinbound",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/inbound/container-receipt", title: "Container Receipt",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/inbound/goods-receipt", title: "Case Receipt",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/inbound/item-main", title: "Good Receipt",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/inbound/putaway", title: "Putaway",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/inbound/readytoConfirm", title: "Ready to Confirm",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/inbound/inbound-confirm", title: "Confirmed",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/inbound/reversal-main", title: "Reversal",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
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
