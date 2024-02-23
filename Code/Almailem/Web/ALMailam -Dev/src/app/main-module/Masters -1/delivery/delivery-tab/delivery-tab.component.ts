import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from 'src/app/core/Auth/auth.service';

@Component({
  selector: 'app-delivery-tab',
  templateUrl: './delivery-tab.component.html',
  styleUrls: ['./delivery-tab.component.scss']
})
export class DeliveryTabComponent implements OnInit {

 
  link = [
    { url: "/main/delivery/driver", title: "Driver",  screenid: 3177,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/delivery/vehicle", title: "Vehicle", screenid: 3178,   class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/delivery/route", title: "Route",  screenid: 3179,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/delivery/drivervehicleassign", title: "Driver Vehicle Assignment", screenid: 3180,   class: "mx-1 d-flex justify-content-center align-items-center" },
   
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
