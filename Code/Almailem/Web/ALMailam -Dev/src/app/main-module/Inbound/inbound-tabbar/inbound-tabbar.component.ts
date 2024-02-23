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
    { url: "/main/inbound/preinbound", title: "Preinbound", screenid:3044,class: "mx-1 d-flex justify-content-center align-items-center" },
   // { url: "/main/inbound/goods-receipt", title: "Case Receipt", screenid:3046, class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/inbound/item-main", title: "GR Release", screenid:3049, class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/inbound/putaway", title: "Putaway",screenid:3051,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/inbound/readytoConfirm", title: "GR Confirmation",screenid: 3053, class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/inbound/inbound-confirm", title: "Inbound Summary",screenid:3145,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/inbound/reversal-main", title: "Reversal",screenid:3055,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/inbound/container-receipt", title: "Container Receipt",screenid:3042,  class: "mx-1 d-flex justify-content-center align-items-center" },
 ];
 link1 = [
  { url: "/main/inbound/failedorder", title: "Failed Orders",screenid: 3044, class: "mx-1 d-flex justify-content-center align-items-center" }
];
 activeLink = this.link[0].url;
 constructor(private router: Router, private auth: AuthService) { 
  this.router.events.pipe(
    filter((event: any) => event instanceof NavigationEnd),
  ).subscribe(x => {
    this.activeLink = this.router.url;
  });
}

activeUrl1: any;
ngOnInit(): void {
  this.activeLink = this.router.url;
  this.activeUrl1 = (this.router.url).split('/');
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