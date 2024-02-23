import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from 'src/app/core/Auth/auth.service';


@Component({
  selector: 'app-setup-product-new',
  templateUrl: './setup-product-new.component.html',
  styleUrls: ['./setup-product-new.component.scss']
})
export class SetupProductNewComponent implements OnInit {
  link = [
    { url: "/main/productsetup/itemtype", title: "Item Type",screenid:3008,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/productsetup/itemgroup", title: "Item Group",screenid:3009,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/productsetup/batch", title: "Batch/Serial",screenid:3010,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/productsetup/variant", title: "Variant", screenid:3011, class: "mx-1 d-flex justify-content-center align-items-center" },
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
