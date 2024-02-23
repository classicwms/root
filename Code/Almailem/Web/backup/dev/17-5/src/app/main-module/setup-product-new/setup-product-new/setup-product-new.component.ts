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
    { url: "/main/productsetup/itemtype", title: "Item Type",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/productsetup/itemgroup", title: "Item Group",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/productsetup/batch", title: "Batch/Serial",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/productsetup/variant", title: "Variant",  class: "mx-1 d-flex justify-content-center align-items-center" },
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
