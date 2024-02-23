import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from 'src/app/core/core';

@Component({
  selector: 'app-delivery-mannagement-tab',
  templateUrl: './delivery-mannagement-tab.component.html',
  styleUrls: ['./delivery-mannagement-tab.component.scss']
})
export class DeliveryMannagementTabComponent implements OnInit {

 
  link = [
    { url: "/main/delivery/consignment", title: "Consignments",  screenid: 3209,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/delivery/manifest", title: "Manifest",  screenid: 3210,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/delivery/pickup", title: "Pickup", screenid: 3211,   class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/delivery/delivery", title: "Delivery",  screenid: 3212,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/delivery/redelivery", title: "Redelivery",  screenid: 3218,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/delivery/returned", title: "Returned",  screenid: 3220,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/delivery/delivered", title: "Delivered",  screenid: 3216,  class: "mx-1 d-flex justify-content-center align-items-center" },
    
   
    //{ url: "/main/productsetup/variant", title: "Variant",  class: "mx-1 d-flex justify-content-center align-items-center" },
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

