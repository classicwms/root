import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from 'src/app/core/core';



@Component({
  selector: 'app-setupstoragenew',
  templateUrl: './setupstoragenew.component.html',
  styleUrls: ['./setupstoragenew.component.scss']
})


export class SetupstoragenewComponent implements OnInit {

  link = [
    { url: "/main/productstorage/storageclass", title: "Storage Class",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/productstorage/storagetype", title: "Storage Type",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/productstorage/storagebintype", title: "Storage Bin Type",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/productstorage/stratergy", title: "Stratergy",  class: "mx-1 d-flex justify-content-center align-items-center" },
    // { url: "/main/productsetup/variant", title: "Variant",  class: "mx-1 d-flex justify-content-center align-items-center" },
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
